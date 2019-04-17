// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.renoun;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.relations.helpers.AbstractTypedRelationshipAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.language.Dependency;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.grammar.DependencyGraph;
import uk.gov.dstl.baleen.uima.grammar.DependencyParseException;
import uk.gov.dstl.baleen.uima.grammar.DependencyTree;
import uk.gov.dstl.baleen.uima.utils.AnnotationUtils;

/**
 * An abstract dependency tree based relation extractor for ReNoun.
 *
 * @baleen.javadoc
 * @see http://emnlp2014.org/papers/pdf/EMNLP2014038.pdf
 */
public abstract class AbstractReNounRelationshipAnnotator
    extends AbstractTypedRelationshipAnnotator {

  private static final Set<String> ATTRIBUTE_DEPENDENCIES =
      ImmutableSet.of("nn", "amod", "appos", "cc", "conj", "name", "compound");

  /**
   * Noun attributes to search for as a list in the config
   *
   * @baleen.config false
   */
  public static final String PARAM_ONTOLOGY_ATTRIBUTES = "attributes";

  @ConfigurationParameter(name = PARAM_ONTOLOGY_ATTRIBUTES, mandatory = false)
  private String[] attributeArray = new String[0];

  /**
   * Noun attributes to search for as a list, one per line in the given file
   *
   * @baleen.config false
   */
  public static final String PARAM_ONTOLOGY_ATTRIBUTES_FILE = "attributesFile";

  @ConfigurationParameter(name = PARAM_ONTOLOGY_ATTRIBUTES_FILE, mandatory = false)
  private String attributesFile;

  /**
   * Additionally check if the noun attribute is coreferenced by the target. This is done for seed
   * fact generation in the ReNoun paper, but left optional here.
   *
   * @baleen.config false
   */
  public static final String PARAM_REQUIRE_COREFERENCE = "requireCoreference";

  @ConfigurationParameter(name = PARAM_REQUIRE_COREFERENCE, defaultValue = "false")
  private boolean requireCoreference;

  private Supplier<Stream<DependencyTreeMatcher>> patterns;

  private Set<String> attributes;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);

    try {
      attributes = createAttributes();
      patterns = createPatternsSupplier();

    } catch (DependencyParseException | IOException e) {
      throw new ResourceInitializationException(e);
    }
    // @formatter:on

  }

  /** @return supplier a stream of patterns */
  protected abstract Supplier<Stream<DependencyTreeMatcher>> createPatternsSupplier()
      throws DependencyParseException;

  /**
   * @return supplier a stream of attribute strings
   * @throws IOException
   */
  protected Set<String> createAttributes() throws IOException {
    if (attributesFile != null) {
      return new HashSet<>(
          Files.readAllLines(new File(attributesFile).toPath(), StandardCharsets.UTF_8));
    } else {
      return new HashSet<>(Arrays.asList(attributeArray));
    }
  }

  @Override
  protected void extract(JCas jCas) throws AnalysisEngineProcessException {

    Map<WordToken, List<Entity>> entities =
        JCasUtil.indexCovering(jCas, WordToken.class, Entity.class);

    DependencyGraph dependencyGraph = DependencyGraph.build(jCas);

    patterns.get().forEach(seed -> processTree(seed, jCas, dependencyGraph, entities));
  }

  private void processTree(
      DependencyTreeMatcher matcher,
      JCas jCas,
      DependencyGraph graph,
      Map<WordToken, List<Entity>> entities) {

    List<DependencyTreeMatcher.Matching> matches = matcher.match(graph);

    for (DependencyTreeMatcher.Matching matching : matches) {

      WordToken sourceToken = matching.getSubject();
      WordToken targetToken = matching.getObject();
      WordToken valueToken = matching.getAttribute();

      Optional<Entity> sourceCheck = getEntity(entities, sourceToken);
      Optional<Entity> targetCheck = getEntity(entities, targetToken);

      if (!sourceCheck.isPresent() || !targetCheck.isPresent() || sourceCheck.equals(targetCheck)) {
        continue;
      }

      Entity source = sourceCheck.get();
      Entity target = targetCheck.get();

      Optional<String> attribute = hasAttribute(graph, valueToken, source, target);

      if (attribute.isPresent()) {

        Optional<Relation> relation =
            createRelation(jCas, entities, valueToken, source, target, attribute.get());

        if (relation.isPresent()) {
          recordRelation(matcher.getDependencyTree(), relation.get());
          addToJCasIndex(relation.get());
        }
      }
    }
  }

  private Optional<Relation> createRelation(
      JCas jCas,
      Map<WordToken, List<Entity>> entities,
      WordToken valueToken,
      Entity source,
      Entity target,
      String attribute) {

    if (checkCoreference(entities, valueToken, target)) {
      return Optional.empty();
    }
    int begin = Math.min(source.getBegin(), target.getBegin());
    int end = Math.max(source.getEnd(), target.getEnd());
    return Optional.of(createRelation(jCas, source, target, begin, end, attribute));
  }

  /**
   * Override to separately record the relation, say for scoring
   *
   * @param dependencyTree the tree matched
   * @param relation created
   */
  protected void recordRelation(DependencyTree dependencyTree, Relation relation) {
    // DO NOTHING
  }

  private Optional<Entity> getEntity(Map<WordToken, List<Entity>> entities, WordToken token) {
    List<Entity> collection = entities.get(token);
    if (collection.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(collection.iterator().next());
  }

  private boolean checkCoreference(
      Map<WordToken, List<Entity>> entities, WordToken valueToken, Entity target) {
    if (requireCoreference) {
      Optional<Entity> entity = getEntity(entities, valueToken);
      return !entity
          .map(Entity::getReferent)
          .filter(Predicates.equalTo(target.getReferent()))
          .isPresent();
    }
    return false;
  }

  private Optional<String> hasAttribute(
      DependencyGraph graph, WordToken rootToken, Entity source, Entity target) {

    Set<Dependency> governors = graph.getGovernors(rootToken);
    Stream<WordToken> dependants =
        governors
            .stream()
            .filter(d -> ATTRIBUTE_DEPENDENCIES.contains(d.getDependencyType()))
            .map(Dependency::getDependent);

    Stream<String> sordedDependentWords =
        Stream.concat(dependants, Stream.of(rootToken))
            .filter(wt -> !AnnotationUtils.isCovering(source, wt))
            .filter(wt -> !AnnotationUtils.isCovering(target, wt))
            .sorted(Comparator.comparing(WordToken::getBegin))
            .map(WordToken::getCoveredText);

    if (attributes.isEmpty()) {
      String candidateAttribute = sordedDependentWords.collect(Collectors.joining(" "));
      if (StringUtils.isEmpty(candidateAttribute)) {
        return Optional.empty();
      } else {
        return Optional.of(candidateAttribute);
      }
    }

    List<String> dependentWords = sordedDependentWords.collect(Collectors.toList());
    for (int i = 1; i <= dependentWords.size(); i++) {
      for (int j = 0; j <= dependentWords.size() - i; j++) {
        String attributeCandidate = joining(dependentWords.subList(j, j + i));
        if (attributes.contains(attributeCandidate)) {
          return Optional.of(attributeCandidate);
        }
      }
    }
    return Optional.empty();
  }

  private String joining(List<String> valueTokens) {
    return valueTokens.stream().collect(Collectors.joining(" "));
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(Entity.class, WordToken.class, Dependency.class),
        ImmutableSet.of(Relation.class));
  }
}
