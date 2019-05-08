// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.structural;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.core.utils.ConfigUtils;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;
import uk.gov.dstl.baleen.uima.utils.StructureHierarchy;
import uk.gov.dstl.baleen.uima.utils.StructureUtil;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;
import uk.gov.dstl.baleen.uima.utils.select.Nodes;

/**
 * Extract entities using the provided structural query.
 *
 * <p>The structural query supplied is run over the whole document, the text of the matched items
 * are then annotated with the supplied Entity class.
 *
 * <p>A confidence to assign can be supplied.
 *
 * @baleen.javadoc
 */
public class StructuralEntity extends BaleenAnnotator {

  /**
   * A list of structural types which will be considered during record path analysis.
   *
   * @baleen.config Paragraph,TableCell,ListItem,Aside, ...
   */
  public static final String PARAM_TYPE_NAMES = "types";

  /** The type names. */
  @ConfigurationParameter(name = PARAM_TYPE_NAMES, mandatory = false)
  private String[] typeNames;

  /**
   * The entity type to use for matched entities
   *
   * @baleen.config uk.gov.dstl.baleen.types.semantic.Entity
   */
  public static final String PARAM_TYPE = "type";

  @ConfigurationParameter(
      name = PARAM_TYPE,
      defaultValue = "uk.gov.dstl.baleen.types.semantic.Entity")
  private String type;

  /**
   * The entity subType to use for matched entities
   *
   * @baleen.config
   */
  public static final String PARAM_SUB_TYPE = "subType";

  @ConfigurationParameter(name = PARAM_SUB_TYPE, defaultValue = "")
  private String subType;

  /**
   * The confidence to assign to matched entities
   *
   * @baleen.config 1.0
   */
  public static final String PARAM_CONFIDENCE = "confidence";

  @ConfigurationParameter(name = PARAM_CONFIDENCE, defaultValue = "1.0")
  private String confidenceString;

  /**
   * The query identifying the entities to annotate
   *
   * @baleen.config
   */
  public static final String PARAM_QUERY = "query";

  @ConfigurationParameter(name = PARAM_QUERY, mandatory = true)
  private String query;

  // Parse the confidence config parameter into this variable to avoid issues
  // with parameter types
  private Float confidence;

  /** The entity constructor. */
  private Constructor<? extends Entity> constructor;

  /** The structural classes. */
  protected Set<Class<? extends Structure>> structuralClasses;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);
    structuralClasses = StructureUtil.getStructureClasses(typeNames);
    confidence = ConfigUtils.stringToFloat(confidenceString, 1.0f);
    try {
      final Class<? extends Entity> et =
          TypeUtils.getEntityClass(
              type, JCasFactory.createJCas(TypeSystemSingleton.getTypeSystemDescriptionInstance()));
      constructor = et.getConstructor(JCas.class);
    } catch (UIMAException | BaleenException | NoSuchMethodException | SecurityException e) {
      throw new ResourceInitializationException(e);
    }
  }

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {

    Nodes<Structure> select = StructureHierarchy.build(jCas, structuralClasses).select(query);

    select.forEach(
        node -> {
          if (node.hasText()) {
            Entity ret;
            try {
              ret = constructor.newInstance(jCas);
              ret.setBegin(node.getItem().getBegin());
              ret.setEnd(node.getItem().getEnd());
              ret.setValue(node.text());
              ret.setConfidence(confidence);
              if (!Strings.isNullOrEmpty(subType)) {
                ret.setSubType(subType);
              }
              addToJCasIndex(ret);
            } catch (Exception e) {
              throw new RuntimeException("Can not create entity type " + type, e);
            }
          }
        });
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        Collections.emptySet(), ImmutableSet.of(constructor.getDeclaringClass()));
  }
}
