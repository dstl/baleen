// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.relations;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.relations.helpers.AbstractTypedRelationshipAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.grammar.DependencyGraph;
import uk.gov.dstl.baleen.uima.utils.Offset;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;

/**
 * Extract simple relations between all entities that occur in the same sentence of the document and
 * have a dependency.
 *
 * <p>This is a more restricted set of relations than given by {@link SentenceRelationshipAnnotator}
 * but still aimed at producing a high recall baseline for relation extraction.
 *
 * <p>An optional type, subType to assign can be supplied.
 *
 * @baleen.javadoc
 */
public class DependencyRelationshipAnnotator extends AbstractTypedRelationshipAnnotator {

  /**
   * Use only the given types to construct relations.
   *
   * <p>Do not set for all types.
   *
   * @baleen.config Person,Location,...
   */
  public static final String PARAM_TYPE_NAMES = "typeNames";

  @ConfigurationParameter(name = PARAM_TYPE_NAMES, mandatory = false)
  protected String[] typeNames;

  /** The type classes. */
  protected Set<Class<? extends Entity>> typeClasses;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);
    typeClasses = TypeUtils.getTypeClasses(Entity.class, typeNames);
  }

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
  protected void extract(JCas jCas) throws AnalysisEngineProcessException {

    Map<Sentence, List<Entity>> languageCovered =
        JCasUtil.indexCovered(jCas, uk.gov.dstl.baleen.types.language.Sentence.class, Entity.class);

    Map<uk.gov.dstl.baleen.types.structure.Sentence, List<Entity>> structureCovered =
        JCasUtil.indexCovered(
            jCas, uk.gov.dstl.baleen.types.structure.Sentence.class, Entity.class);

    Map<Offset, List<Entity>> sentences = cleanSentencesByOffset(languageCovered, structureCovered);

    addRelationsToIndex(
        sentences.entrySet().stream()
            .flatMap(e -> createDependantRelations(jCas, e.getValue(), e.getKey())));
  }

  protected Stream<Relation> createDependantRelations(
      final JCas jCas, final Collection<Entity> collection, final Offset offset) {

    final List<Relation> relations = new LinkedList<>();

    List<Entity> entities = TypeUtils.filterAnnotations(collection, typeClasses);

    final ListIterator<Entity> outer = entities.listIterator();
    while (outer.hasNext()) {
      final Entity source = outer.next();

      final ListIterator<Entity> inner = entities.listIterator(outer.nextIndex());
      while (inner.hasNext()) {
        final Entity target = inner.next();

        List<WordToken> sources = JCasUtil.selectCovered(WordToken.class, source);
        List<WordToken> targets = JCasUtil.selectCovered(WordToken.class, target);
        List<WordToken> shortestPath =
            dependencyGraph.shortestPath(sources, targets, offset.getEnd() - offset.getBegin());

        if (shortestPath.isEmpty()) {
          continue;
        }

        List<WordToken> modifiedPath = new ArrayList<>(shortestPath);
        modifiedPath.removeAll(sources);
        modifiedPath.removeAll(targets);

        String value =
            modifiedPath.stream().map(WordToken::getCoveredText).collect(Collectors.joining(" "));

        Relation relation =
            createRelation(
                jCas,
                source,
                target,
                offset.getBegin(),
                offset.getEnd(),
                type,
                subType,
                value,
                confidence);
        relation.setSentenceDistance(0);

        addWordDistance(jCas, source, target, relation);
        relation.setDependencyDistance(modifiedPath.size());

        relations.add(relation);
      }
    }

    return relations.stream();
  }

  private void addWordDistance(
      final JCas jCas, final Entity source, final Entity target, Relation relation) {
    List<WordToken> wordTokens =
        JCasUtil.selectCovered(jCas, WordToken.class, source.getEnd(), target.getBegin());
    int words = wordTokens.size();
    relation.setWordDistance(words);
  }

  private Map<Offset, List<Entity>> cleanSentencesByOffset(
      Map<Sentence, List<Entity>> languageCovered,
      Map<uk.gov.dstl.baleen.types.structure.Sentence, List<Entity>> structureCovered) {
    HashMap<Offset, List<Entity>> sentences = new HashMap<>();
    languageCovered.forEach((s, c) -> sentences.put(new Offset(s.getBegin(), s.getEnd()), c));
    structureCovered.forEach((s, c) -> sentences.put(new Offset(s.getBegin(), s.getEnd()), c));
    return sentences;
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(
            Entity.class,
            uk.gov.dstl.baleen.types.structure.Sentence.class,
            Sentence.class,
            WordToken.class),
        ImmutableSet.of(Relation.class));
  }
}
