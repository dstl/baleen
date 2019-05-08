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
import uk.gov.dstl.baleen.uima.utils.OffsetUtil;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;

/**
 * Extract simple relations between all entities that occur in the same sentence of the document.
 *
 * <p>With {@link DocumentRelationshipAnnotator}, this aims to capture all possible relations
 * defined in the text in order to provide a high recall baseline for relation extraction.
 *
 * <p>An optional type, subType to assign can be supplied.
 *
 * @baleen.javadoc
 */
public class SentenceRelationshipAnnotator extends AbstractTypedRelationshipAnnotator {

  /**
   * The valid values for the strategy
   *
   * <p>SENTENCE - The whole sentence
   *
   * <p>BETWEEN - The tokens between the entities
   *
   * <p>DEPENDENCY - The dependent words between the two entities, (NB Requires dependency annotator
   * ((e.g. MaltParser, ClearNlp) to run previously
   */
  public enum ValueStrategy {
    /** The whole sentence */
    SENTENTCE,
    /** The tokens between the entities */
    BETWEEN,
    /**
     * The dependent words between the two entities, (NB Requires dependency annotator ((e.g.
     * MaltParser, ClearNlp) to run previously
     */
    DEPENDENCY
  }

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

  /**
   * Declare the value strategy to be used.
   *
   * <p>Do not set for all types.
   *
   * @baleen.config SENTENCE, BETWEEN, DEPENDENCY
   */
  public static final String PARAM_VALUE_STRATEGY = "valueStrategy";

  @ConfigurationParameter(name = PARAM_VALUE_STRATEGY, defaultValue = "SENTENTCE")
  private String valueStrategyString;

  protected ValueStrategy valueStrategy;

  /** The type classes. */
  protected Set<Class<? extends Entity>> typeClasses;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);
    typeClasses = TypeUtils.getTypeClasses(Entity.class, typeNames);
    valueStrategy = ValueStrategy.valueOf(valueStrategyString);
  }

  private DependencyGraph dependencyGraph;

  @Override
  protected void preExtract(JCas jCas) {
    super.preExtract(jCas);
    if (ValueStrategy.DEPENDENCY.equals(valueStrategy)) {
      dependencyGraph = DependencyGraph.build(jCas);
    }
  }

  @Override
  protected void postExtract(JCas jCas) {
    super.postExtract(jCas);
    dependencyGraph = null;
  }

  @Override
  protected void extract(JCas jCas) throws AnalysisEngineProcessException {

    Map<Sentence, List<Entity>> languageCovered =
        JCasUtil.indexCovered(jCas, Sentence.class, Entity.class);

    Map<uk.gov.dstl.baleen.types.structure.Sentence, List<Entity>> structureCovered =
        JCasUtil.indexCovered(
            jCas, uk.gov.dstl.baleen.types.structure.Sentence.class, Entity.class);

    Map<Offset, List<Entity>> sentences = cleanSentencesByOffset(languageCovered, structureCovered);

    addRelationsToIndex(
        sentences.entrySet().stream()
            .flatMap(e -> createMeshedRelations(jCas, e.getValue(), e.getKey())));
  }

  private String getValue(JCas jCas, Offset offset, Entity source, Entity target) {
    switch (valueStrategy) {
      case DEPENDENCY:
        Optional<List<WordToken>> shortestPath = getShortestPath(offset, source, target);
        return shortestPath
            .map(
                words ->
                    words.stream().map(WordToken::getCoveredText).collect(Collectors.joining(" ")))
            .orElse("");
      case BETWEEN:
        if (OffsetUtil.overlaps(
            source.getBegin(), source.getEnd(), target.getBegin(), target.getEnd())) {
          return "";
        }
        int begin = Math.min(source.getEnd(), target.getEnd());
        int end = Math.max(source.getBegin(), target.getBegin());
        return OffsetUtil.getText(jCas, begin, end).trim();
      case SENTENTCE:
      default:
        return OffsetUtil.getText(jCas, offset).trim();
    }
  }

  private Optional<List<WordToken>> getShortestPath(Offset offset, Entity source, Entity target) {
    if (dependencyGraph == null) {
      return Optional.empty();
    }

    List<WordToken> sources = JCasUtil.selectCovered(WordToken.class, source);
    List<WordToken> targets = JCasUtil.selectCovered(WordToken.class, target);
    List<WordToken> shortestPath =
        dependencyGraph.shortestPath(sources, targets, offset.getEnd() - offset.getBegin());

    if (shortestPath.isEmpty()) {
      return Optional.empty();
    }

    shortestPath.removeAll(sources);
    shortestPath.removeAll(targets);
    return Optional.of(shortestPath);
  }

  protected Stream<Relation> createMeshedRelations(
      final JCas jCas, final Collection<Entity> collection, final Offset offset) {

    final List<Relation> relations = new LinkedList<>();

    List<Entity> entities = TypeUtils.filterAnnotations(collection, typeClasses);

    final ListIterator<Entity> outer = entities.listIterator();
    while (outer.hasNext()) {
      final Entity source = outer.next();

      final ListIterator<Entity> inner = entities.listIterator(outer.nextIndex());
      while (inner.hasNext()) {
        final Entity target = inner.next();

        String value = getValue(jCas, offset, source, target);
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
        addDependencyDistance(offset, source, target, relation);

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

  private void addDependencyDistance(
      final Offset offset, final Entity source, final Entity target, Relation relation) {
    relation.setDependencyDistance(
        getShortestPath(offset, source, target).map(List::size).orElse(Integer.valueOf(-1)));
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
