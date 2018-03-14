// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.relations;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import uk.gov.dstl.baleen.annotators.relations.helpers.AbstractInteractionBasedRelationshipAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.language.Dependency;
import uk.gov.dstl.baleen.types.language.Interaction;
import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.grammar.DependencyGraph;
import uk.gov.dstl.baleen.uima.grammar.DependencyGraph.TraversePredicate;
import uk.gov.dstl.baleen.uima.utils.AnnotationUtils;

/**
 * Unsupervised (originally Biomedical) Relationship Extractor.
 *
 * <p>A relationship extraction algorithm based on dependency parsing.
 *
 * <p>The algorithm works by taking an interaction word and then looking for entities that are
 * connected to it (in the way of a dependency graph, within a specified distance).
 *
 * <p>Formally this is defined as two conditions in the paper:
 *
 * <ul>
 *   <li>RD1: dependency distance between interaction word and each entity should be <= 4.
 *   <li>RD2: if the interaction word is a verb it should be between the two entities (within the
 *       text of the sentence)
 * </ul>
 *
 * We modified this approach as it links a lot of entities, because the dependency graph fully
 * connects all words.
 *
 * <p>We also use a dependency distance of 1 - that effectively produces a more generalised
 * subject-verb-object mapping. We suspect that a total distance subject-object of 3 would also
 * work, but the current implementation requires symmetry.
 *
 * <p>In a paper on Unsupervised Events, the dependency is divided into trees but looks for verbs
 * that are connected to other verbs. In a simple case this focuses to subject-(one or more
 * verb-verb)-object relations, rather than subject-verb-noun-verb-object. We found the latter
 * connected to many unrelated parts of the sentence, whereas the former at least provided something
 * akin to an actionable connection.
 *
 * <p>Note this requires the following annotations: Sentence, WordToken, PhraseChunk (required for
 * dependency), Dependency, Entity, Interaction
 *
 * @baleen.javadoc
 */
public class UbmreDependency extends AbstractInteractionBasedRelationshipAnnotator {

  /**
   * The maximum distance (dependency links) between an entity and an interaction word within which
   * they are considered connected.
   *
   * @baleen.config 3
   */
  public static final String KEY_DEPENDENCY_DISTANCE = "distance";

  @ConfigurationParameter(name = KEY_DEPENDENCY_DISTANCE, defaultValue = "3")
  private Integer maxDependencyDistance;

  private DependencyGraph dependencyGraph;

  @Override
  protected void preExtract(JCas jCas) {
    super.preExtract(jCas);

    dependencyGraph = DependencyGraph.build(jCas);
  }

  @Override
  protected void postExtract(JCas jCas) {
    super.postExtract(jCas);

    dependencyGraph = null;
  }

  @Override
  protected void extract(JCas jCas) {

    final Map<WordToken, Collection<Interaction>> tokenToInteraction =
        JCasUtil.indexCovered(jCas, WordToken.class, Interaction.class);
    final Map<Entity, Collection<Dependency>> entityToDependency =
        JCasUtil.indexCovered(jCas, Entity.class, Dependency.class);
    final Map<Interaction, Collection<WordToken>> interactionToDependencies =
        JCasUtil.indexCovered(jCas, Interaction.class, WordToken.class);

    final Collection<Entity> entities = JCasUtil.select(jCas, Entity.class);

    // This is the complex part. We are looking to find all entities close to interaction words
    // in 'dependency space'.
    // We allow a entity to traverse the graph until they get to a a verb, then in effect they
    // can go verb to verb. If they want to go verb to noun that's ok (our interaction word
    // could be a noun) but they can't then go back to a verb (since that would joint two
    // disconnected verb trees).

    final Multimap<Interaction, Entity> interactionToEntities = HashMultimap.create();
    for (final Entity entity : entities) {
      dependencyGraph.traverse(
          maxDependencyDistance,
          entityToDependency.getOrDefault(entity, Collections.emptyList()),
          traverseToVerb(tokenToInteraction, interactionToEntities, entity));
    }

    // Now we can create all the relations

    final Stream<Relation> relations =
        interactionToEntities
            .asMap()
            .entrySet()
            .stream()
            .flatMap(
                e -> {
                  final Interaction i = e.getKey();

                  final boolean interactionIsVerb =
                      interactionToDependencies
                          .getOrDefault(i, Collections.emptyList())
                          .stream()
                          .anyMatch(p -> p.getPartOfSpeech().startsWith("V"));

                  final Collection<Entity> c = e.getValue();
                  return createMeshedRelations(jCas, i, c, 1.0f)
                      .filter(Objects::nonNull)
                      .filter(
                          r ->
                              // Filter applies RD2: If a verb then we interaction should be between
                              // the two
                              // entities
                              !interactionIsVerb
                                  || AnnotationUtils.isInBetween(r, r.getSource(), r.getTarget()));
                });

    addRelationsToIndex(relations);
  }

  /**
   * Traverse within the verb structure.
   *
   * @param tokenToInteraction the token to interaction
   * @param interactionToEntities the interaction to entities
   * @param entity the entity
   * @return the traverse predicate
   */
  private TraversePredicate traverseToVerb(
      final Map<WordToken, Collection<Interaction>> tokenToInteraction,
      final Multimap<Interaction, Entity> interactionToEntities,
      final Entity entity) {
    return (d, f, t, h) -> {
      if (f == null) // When starting allow it to carry on
      return true;

      if ("punct".equalsIgnoreCase(d.getDependencyType())) // Don't traverse punctuation
      return false;

      final boolean previousVerb =
          h.stream()
              .map(WordToken::getPartOfSpeech)
              .anyMatch(s -> s.startsWith("V")); // Can we traverse to this node
      if (carryOn(previousVerb, t)) {
        final Collection<Interaction> interactions = tokenToInteraction.get(t);
        if (interactions != null
            && !interactions
                .isEmpty()) // We've reached an interaction word, store our connection to it
        interactions.forEach(i -> interactionToEntities.put(i, entity));

        return true;
      } else {
        return false;
      }
    };
  }

  private boolean carryOn(boolean previousIsVerb, WordToken token) {
    // If you've not hit a verb you can go anywhere n-n-n or
    // If you have hit a verb you can only move to another non-verb
    // v-n-v is not allowed as its another verb subtree
    return !previousIsVerb || !token.getPartOfSpeech().startsWith("V");
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(
            Sentence.class,
            WordToken.class,
            PhraseChunk.class,
            Interaction.class,
            Entity.class,
            Dependency.class),
        ImmutableSet.of(Relation.class));
  }
}
