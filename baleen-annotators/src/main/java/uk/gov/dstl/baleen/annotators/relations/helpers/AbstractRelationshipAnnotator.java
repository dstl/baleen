// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.relations.helpers;

import java.util.*;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.annotators.patterns.data.RelationWrapper;
import uk.gov.dstl.baleen.core.utils.ConfigUtils;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;
import uk.gov.dstl.baleen.uima.utils.ComparableEntitySpanUtils;

/**
 * A base class for relationship extractors.
 *
 * <p>Implementations should override {@link #extract(JCas) extract}, and potentially {@link
 * #preExtract(JCas) preExtract} and {@link #postExtract(JCas) postExtract}, which both allow for
 * creation and clean up of objects related to extraction.
 */
public abstract class AbstractRelationshipAnnotator extends BaleenAnnotator {

  /**
   * The confidence to assign to the relation
   *
   * @baleen.config 1.0
   */
  public static final String PARAM_CONFIDENCE = "confidence";

  @ConfigurationParameter(name = PARAM_CONFIDENCE, defaultValue = "0.1")
  private String confidenceString;

  // Parse the confidence config parameter into this variable to avoid issues
  // with parameter types
  protected Float confidence;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);
    confidence = ConfigUtils.stringToFloat(confidenceString, 0.1f);
  }

  @Override
  protected final void doProcess(final JCas jCas) throws AnalysisEngineProcessException {

    try {
      preExtract(jCas);

      extract(jCas);

    } finally {
      postExtract(jCas);
    }
  }

  /**
   * Extract relations from the jCas.
   *
   * <p>It is the overridders responsibility to add these to the jCas Index (addRelationsToIndex)
   *
   * @param jCas the j cas
   * @throws AnalysisEngineProcessException
   */
  protected abstract void extract(JCas jCas) throws AnalysisEngineProcessException;

  /**
   * Called before extract().
   *
   * @param jCas the jcas
   */
  protected void preExtract(final JCas jCas) {
    // Do nothing
  }

  /**
   * Called after extract (including on exception).
   *
   * @param jCas the jcas
   */
  protected void postExtract(final JCas jCas) {
    // Do nothing
  }

  /**
   * Adds a stream of relations to index.
   *
   * @param relations the relations
   */
  protected void addRelationsToIndex(final Stream<Relation> relations) {
    if (relations != null) {
      relations
          .filter(Objects::nonNull)
          // Only add events aren't in the same
          // Prevents overlapping spans since that makes no sense
          .filter(
              r ->
                  r.getSource().getInternalId() != r.getTarget().getInternalId()
                      && !ComparableEntitySpanUtils.overlaps(r.getSource(), r.getTarget()))
          // Discard anything which has no relationship type

          // TODO: Is this sensible?
          // These are direct connection between A and B for the
          // dependency graph (you can't be more connected than that)
          // but then you have no relationship text to work with.
          .filter(
              r -> r.getRelationshipType() != null || !StringUtils.isBlank(r.getRelationshipType()))
          .forEach(this::addToJCasIndex);
    }
  }

  /**
   * Creates the relation.
   *
   * <p>Note this does not add the relations to the jCas index. This allows them to be discarded.
   *
   * @param jCas the jcas
   * @param source the source the source entity
   * @param target the target the target entity
   * @param begin the begin of the relation
   * @param end the end of the relation
   * @param type the type of the relation
   * @param subType the sub type of the relation
   * @param value the value of the relation
   * @param assignedConfidence the confidence of the relation
   * @return the relation
   */
  protected Relation createRelation(
      final JCas jCas,
      final Entity source,
      final Entity target,
      int begin,
      int end,
      String type,
      String subType,
      String value,
      Float assignedConfidence) {
    final Relation r = new Relation(jCas);
    r.setBegin(begin);
    r.setEnd(end);
    r.setRelationshipType(type);
    r.setRelationSubType(subType);
    r.setSource(source);
    r.setTarget(target);
    r.setValue(value);
    if (assignedConfidence == null) {
      r.setConfidence(confidence);
    } else {
      r.setConfidence(assignedConfidence);
    }
    return r;
  }

  /**
   * Creates the relation.
   *
   * <p>(With default confidence value.)
   *
   * @param jCas the jcas
   * @param source the source the source entity
   * @param target the target the target entity
   * @param begin the begin of the relation
   * @param end the end of the relation
   * @param type the type of the relation
   * @param subType the sub type of the relation
   * @param value the value of the relation
   * @return the relation
   */
  protected Relation createRelation(
      final JCas jCas,
      final Entity source,
      final Entity target,
      int begin,
      int end,
      String type,
      String subType,
      String value) {
    return createRelation(jCas, source, target, begin, end, type, subType, value, null);
  }

  /**
   * Creates the relations of the same type between from all the entities on the source list to all
   * the entities on the target list.
   *
   * @param jCas the j cas
   * @param sources the sources
   * @param targets the targets
   * @param begin the begin of the relation
   * @param end the end of the relation
   * @param type the type of the relation
   * @param subType the sub type of the relation
   * @param value the value of the relation
   * @param confidence the confidence of the relation
   * @return the stream of relations
   */
  protected Stream<Relation> createPairwiseRelations(
      final JCas jCas,
      final List<Entity> sources,
      final List<Entity> targets,
      int begin,
      int end,
      String type,
      String subType,
      String value,
      Float confidence) {
    return sources.stream()
        .flatMap(
            l ->
                targets.stream()
                    .map(
                        r ->
                            createRelation(
                                jCas, l, r, begin, end, type, subType, value, confidence)));
  }

  /**
   * Creates the relations between all the entities provided (but not between an entity and itself).
   *
   * @param jCas the j cas
   * @param collection the collection of entities to related
   * @param begin the begin of the relation
   * @param end the end of the relation
   * @param type the type of the relation
   * @param subType the sub type of the relation
   * @param value the value of the relation
   * @param confidence the confidence of the relation
   * @return the stream of relations
   */
  protected Stream<Relation> createMeshedRelations(
      final JCas jCas,
      final Collection<Entity> collection,
      int begin,
      int end,
      String type,
      String subType,
      String value,
      Float confidence) {

    final List<Relation> relations = new LinkedList<>();

    List<Entity> entities;
    if (collection instanceof List) {
      entities = (List<Entity>) collection;
    } else {
      entities = new ArrayList<>(collection);
    }

    final ListIterator<Entity> outer = entities.listIterator();
    while (outer.hasNext()) {
      final Entity source = outer.next();

      final ListIterator<Entity> inner = entities.listIterator(outer.nextIndex());
      while (inner.hasNext()) {
        final Entity target = inner.next();

        relations.add(
            createRelation(jCas, source, target, begin, end, type, subType, value, confidence));
      }
    }

    return relations.stream();
  }

  /**
   * Make the stream distinct (no relations of the same type, between the same entities).
   *
   * @param stream the stream
   * @return the stream
   */
  protected Stream<Relation> distinct(final Stream<Relation> stream) {
    return stream
        .filter(Objects::nonNull)
        .map(RelationWrapper::new)
        .distinct()
        .map(RelationWrapper::getRelation);
  }
}
