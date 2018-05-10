// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.relations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
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
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.utils.Offset;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;

/**
 * Extract simple relations between all entities that occur in different sentences of the document.
 *
 * <p>With the {@link SentenceRelationshipAnnotator}, this aims to capture all possible relations
 * defined in the text in order to provide a high recall baseline of relation extraction.
 *
 * <p>An optional type and subType to assign can be supplied and the extraction can be limited to
 * certain types using the typeNames parameter.
 *
 * @baleen.javadoc
 */
public class DocumentRelationshipAnnotator extends AbstractTypedRelationshipAnnotator {

  /**
   * The max distance between sentences to consider.
   *
   * <p>Defaults to 10, Use -1 to disable (include everything in the doc)
   *
   * @baleen.config 10
   */
  public static final String PARAM_THRESHOLD = "threshold";

  @ConfigurationParameter(name = PARAM_THRESHOLD, defaultValue = "10")
  protected int sentenceThreshold;

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

  @Override
  protected void extract(JCas jCas) throws AnalysisEngineProcessException {

    Map<uk.gov.dstl.baleen.types.language.Sentence, Collection<Entity>> languageCovered =
        JCasUtil.indexCovered(jCas, uk.gov.dstl.baleen.types.language.Sentence.class, Entity.class);

    Map<uk.gov.dstl.baleen.types.structure.Sentence, Collection<Entity>> structureCovered =
        JCasUtil.indexCovered(
            jCas, uk.gov.dstl.baleen.types.structure.Sentence.class, Entity.class);

    SortedMap<Offset, Collection<Entity>> sentences =
        cleanSentencesByOffset(languageCovered, structureCovered);

    addRelationsToIndex(createRelations(jCas, sentences));
  }

  private Stream<Relation> createRelations(
      JCas jCas, SortedMap<Offset, Collection<Entity>> sentences) {
    final List<Relation> relations = new LinkedList<>();

    ArrayList<Entry<Offset, Collection<Entity>>> sentenceEntities =
        new ArrayList<>(sentences.entrySet());
    final ListIterator<Entry<Offset, Collection<Entity>>> outer = sentenceEntities.listIterator();
    while (outer.hasNext()) {
      final Entry<Offset, Collection<Entity>> source = outer.next();
      int begin = source.getKey().getBegin();
      int outerIndex = outer.nextIndex();
      final ListIterator<Entry<Offset, Collection<Entity>>> inner =
          sentenceEntities.listIterator(outerIndex);
      while (inner.hasNext()
          && sentenceThreshold > 0
          && inner.nextIndex() - outerIndex < sentenceThreshold) {
        final Entry<Offset, Collection<Entity>> target = inner.next();
        int innerIndex = inner.nextIndex();

        int end = target.getKey().getEnd();
        Offset offset = new Offset(begin, end);

        relations.addAll(
            createSentenceRelation(
                jCas, source.getValue(), target.getValue(), offset, innerIndex - outerIndex));
      }
    }
    return relations.stream();
  }

  protected List<Relation> createSentenceRelation(
      final JCas jCas,
      final Collection<Entity> sentence1entities,
      final Collection<Entity> sentence2entities,
      final Offset offset,
      final int distance) {

    final List<Relation> relations = new LinkedList<>();

    for (Entity source : sentence1entities) {
      for (Entity target : sentence2entities) {
        Relation relation =
            createRelation(
                jCas,
                source,
                target,
                offset.getBegin(),
                offset.getEnd(),
                type,
                subType,
                "",
                confidence);
        relation.setSentenceDistance(distance);
        relation.setWordDistance(-1);
        relation.setDependencyDistance(-1);
        relations.add(relation);
      }
    }

    return relations;
  }

  private SortedMap<Offset, Collection<Entity>> cleanSentencesByOffset(
      Map<Sentence, Collection<Entity>> languageCovered,
      Map<uk.gov.dstl.baleen.types.structure.Sentence, Collection<Entity>> structureCovered) {
    SortedMap<Offset, Collection<Entity>> sentences = new TreeMap<>();
    languageCovered.forEach(
        (s, c) ->
            sentences.put(
                new Offset(s.getBegin(), s.getEnd()), TypeUtils.filterAnnotations(c, typeClasses)));
    structureCovered.forEach(
        (s, c) ->
            sentences.put(
                new Offset(s.getBegin(), s.getEnd()), TypeUtils.filterAnnotations(c, typeClasses)));
    return sentences;
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(
            Entity.class,
            uk.gov.dstl.baleen.types.structure.Sentence.class,
            uk.gov.dstl.baleen.types.language.Sentence.class),
        ImmutableSet.of(Relation.class));
  }
}
