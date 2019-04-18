// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.relations.helpers;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;

/**
 * A base class for relationship extractors of a particular type.
 *
 * <p>Implementations should override {@link #extract(JCas) extract}, and potentially {@link
 * #preExtract(JCas) preExtract} and {@link #postExtract(JCas) postExtract}, which both allow for
 * creation and clean up of objects related to extraction.
 */
public abstract class AbstractTypedRelationshipAnnotator extends AbstractRelationshipAnnotator {

  /**
   * The relation type to use
   *
   * @baleen.config
   */
  public static final String PARAM_TYPE = "type";

  @ConfigurationParameter(name = PARAM_TYPE, defaultValue = "")
  protected String type;

  /**
   * The relation subType to use
   *
   * @baleen.config
   */
  public static final String PARAM_SUB_TYPE = "subType";

  @ConfigurationParameter(name = PARAM_SUB_TYPE, defaultValue = "")
  protected String subType;

  /**
   * Creates the relation using the defined type and subType.
   *
   * <p>(With default confidence value.)
   *
   * @param jCas the jcas
   * @param source the source the source entity
   * @param target the target the target entity
   * @param begin the begin of the relation
   * @param end the end of the relation
   * @param value the value of the relation
   * @return the relation
   */
  protected Relation createRelation(
      JCas jCas, Entity source, Entity target, int begin, int end, String value) {
    return super.createRelation(jCas, source, target, begin, end, type, subType, value);
  }

  /**
   * Creates the relation using the defined type and subType.
   *
   * <p>(With default confidence value.)
   *
   * @param jCas the jcas
   * @param source the source the source entity
   * @param target the target the target entity
   * @param begin the begin of the relation
   * @param end the end of the relation
   * @param value the value of the relation
   * @param assignedConfidence the confidence of the relation
   * @return the relation
   */
  protected Relation createRelation(
      JCas jCas,
      Entity source,
      Entity target,
      int begin,
      int end,
      String value,
      Float assignedConfidence) {
    return super.createRelation(
        jCas, source, target, begin, end, type, subType, value, assignedConfidence);
  }

  /**
   * Creates the relations of the same type between from all the entities on the source list to all
   * the entities on the target list using the defined type and subType.
   *
   * @param jCas the j cas
   * @param sources the sources
   * @param targets the targets
   * @param begin the begin of the relation
   * @param end the end of the relation
   * @param value the value of the relation
   * @param confidence the confidence of the relation
   * @return the stream of relations
   */
  protected Stream<Relation> createPairwiseRelations(
      JCas jCas,
      List<Entity> sources,
      List<Entity> targets,
      int begin,
      int end,
      String value,
      Float confidence) {
    return super.createPairwiseRelations(
        jCas, sources, targets, begin, end, type, subType, value, confidence);
  }

  /**
   * Creates the relations between all the entities provided (but not between an entity and itself)
   * using the defined type and subType.
   *
   * @param jCas the j cas
   * @param collection the collection of entities to related
   * @param begin the begin of the relation
   * @param end the end of the relation
   * @param value the value of the relation
   * @param confidence the confidence of the relation
   * @return the stream of relations
   */
  protected Stream<Relation> createMeshedRelations(
      JCas jCas,
      Collection<Entity> collection,
      int begin,
      int end,
      String value,
      Float confidence) {
    return super.createMeshedRelations(
        jCas, collection, begin, end, type, subType, value, confidence);
  }

  /**
   * Creates the relations between all the entities provided (but not between an entity and itself)
   * using the defined type and subType.
   *
   * @param jCas the j cas
   * @param collection the collection of entities to related
   * @param begin the begin of the relation
   * @param end the end of the relation
   * @param value the value of the relation
   * @return the stream of relations
   */
  protected Stream<Relation> createMeshedRelations(
      JCas jCas, Collection<Entity> collection, int begin, int end, String value) {
    return super.createMeshedRelations(
        jCas, collection, begin, end, type, subType, value, confidence);
  }
}
