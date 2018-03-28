// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.structural;

import java.util.Set;
import java.util.stream.Stream;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.relations.helpers.AbstractTypedRelationshipAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.uima.utils.AnnotationHierarchyBuilder;
import uk.gov.dstl.baleen.uima.utils.StructureUtil;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;
import uk.gov.dstl.baleen.uima.utils.select.Nodes;

/**
 * Extract relation using the provided structural queries.
 *
 * <p>The structural query supplied is run over the whole document, to identify structural features
 * containing the required annotations. Then the source and target queries are applied to find the
 * source and target within the structural annotation. A relation is then created for matched
 * elements with the type information supplied.
 *
 * <p>A confidence to assign can be supplied.
 *
 * @baleen.javadoc
 */
public class StructuralRelation extends AbstractTypedRelationshipAnnotator {

  /**
   * A list of structural types which will be considered during record path analysis.
   *
   * <p>Leave blank for all types.
   *
   * @baleen.config Paragraph,TableCell,ListItem,Aside, ...
   */
  public static final String PARAM_TYPE_NAMES = "types";

  /** The type names. */
  @ConfigurationParameter(name = PARAM_TYPE_NAMES, mandatory = false)
  private String[] typeNames;

  /**
   * The query to isolate the related entities
   *
   * @baleen.config
   */
  public static final String PARAM_QUERY = "query";

  @ConfigurationParameter(name = PARAM_QUERY, mandatory = true)
  private String query;

  /**
   * The source entity sub query used to find the source in the result of the query
   *
   * @baleen.config
   */
  public static final String PARAM_SOURCE_QUERY = "sourceQuery";

  @ConfigurationParameter(name = PARAM_SOURCE_QUERY, mandatory = true)
  private String sourceQuery;

  /**
   * The target entity sub query used to find the source in the result of the query
   *
   * @baleen.config
   */
  public static final String PARAM_TARGET_QUERY = "targetQuery";

  @ConfigurationParameter(name = PARAM_TARGET_QUERY, mandatory = true)
  private String targetQuery;

  /** The structural classes. */
  protected Set<Class<? extends Structure>> structuralClasses;

  /** The annotation classes. */
  protected Set<Class<? extends Annotation>> annotationClasses;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);
    annotationClasses =
        ImmutableSet.<Class<? extends Annotation>>builder()
            .addAll(StructureUtil.getStructureClasses(typeNames))
            .addAll(TypeUtils.getAnnotationClasses(Entity.class))
            .build();
  }

  @Override
  protected void extract(JCas jCas) throws AnalysisEngineProcessException {

    Nodes<Annotation> select =
        AnnotationHierarchyBuilder.build(jCas, annotationClasses).select(query);

    addRelationsToIndex(
        select
            .stream()
            .flatMap(
                match -> {
                  Nodes<Annotation> sourceAnnotation = match.select(sourceQuery);
                  Nodes<Annotation> targetAnnotation = match.select(targetQuery);
                  if (sourceAnnotation.isEmpty() || targetAnnotation.isEmpty()) {
                    return Stream.empty();
                  }

                  Entity sourceEntity = (Entity) sourceAnnotation.first().getItem();
                  Entity targetEntity = (Entity) targetAnnotation.first().getItem();

                  int begin = match.getItem().getBegin();
                  int end = match.getItem().getEnd();

                  return Stream.of(
                      createRelation(
                          jCas,
                          sourceEntity,
                          targetEntity,
                          begin,
                          end,
                          type,
                          subType,
                          type,
                          confidence));
                }));
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(ImmutableSet.of(Entity.class), ImmutableSet.of(Relation.class));
  }
}
