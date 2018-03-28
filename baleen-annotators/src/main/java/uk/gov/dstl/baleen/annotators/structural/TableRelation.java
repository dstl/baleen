// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.structural;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.relations.helpers.AbstractTypedRelationshipAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.exceptions.BaleenRuntimeException;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.types.structure.TableCell;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;

/**
 * Extract relations form {@link Table}s.
 *
 * <p>This annotator looks for tables with columns matching the provided source and target headings.
 * The rows of those tables are processed to add a relation per row of the provided type (and
 * subType) between the entity in the source columns and the entity in the target column. If the
 * entities do not exist already they are created with the provided source type and target type.
 *
 * @baleen.javadoc
 */
public class TableRelation extends AbstractTypedRelationshipAnnotator {

  /**
   * Is the regular expression case sensitive?
   *
   * @baleen.config false
   */
  public static final String PARAM_CASE_SENSITIVE = "caseSensitive";

  @ConfigurationParameter(name = PARAM_CASE_SENSITIVE, defaultValue = "false")
  private boolean caseSensitive = false;

  /**
   * The regular expression to search for the source column
   *
   * @baleen.config
   */
  public static final String SOURCE_PATTERN = "sourcePattern";

  @ConfigurationParameter(name = SOURCE_PATTERN, mandatory = true)
  private String sourcePattern;

  /**
   * The regular expression to search for the target column
   *
   * @baleen.config
   */
  public static final String TARGET_PATTERN = "targetPattern";

  @ConfigurationParameter(name = TARGET_PATTERN, mandatory = true)
  private String targetPattern;

  /**
   * The source entity type to use
   *
   * @baleen.config uk.gov.dstl.baleen.types.semantic.Entity
   */
  public static final String SOURCE_TYPE = "sourceType";

  @ConfigurationParameter(
    name = SOURCE_TYPE,
    defaultValue = "uk.gov.dstl.baleen.types.semantic.Entity"
  )
  private String sourceType;

  /**
   * The target entity type to use
   *
   * @baleen.config uk.gov.dstl.baleen.types.semantic.Entity
   */
  public static final String TARGET_TYPE = "targetType";

  @ConfigurationParameter(
    name = TARGET_TYPE,
    defaultValue = "uk.gov.dstl.baleen.types.semantic.Entity"
  )
  private String targetType;

  private Constructor<? extends Entity> sourceConstructor = null;
  private Constructor<? extends Entity> targetConstructor = null;
  private Pattern source;
  private Pattern target;

  @Override
  public void doInitialize(final UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);

    try {

      getMonitor().debug("The source regular expression is \"{}\"", sourcePattern);
      getMonitor().debug("The target regular expression is \"{}\"", targetPattern);
      if (!caseSensitive) {
        sourcePattern = "(?i)" + sourcePattern;
        targetPattern = "(?i)" + targetPattern;
      }

      source = Pattern.compile(sourcePattern);
      target = Pattern.compile(targetPattern);
      sourceConstructor =
          TypeUtils.getEntityClass(
                  sourceType,
                  JCasFactory.createJCas(TypeSystemSingleton.getTypeSystemDescriptionInstance()))
              .getConstructor(JCas.class);
      targetConstructor =
          TypeUtils.getEntityClass(
                  targetType,
                  JCasFactory.createJCas(TypeSystemSingleton.getTypeSystemDescriptionInstance()))
              .getConstructor(JCas.class);

    } catch (UIMAException | BaleenException | NoSuchMethodException | SecurityException e) {
      throw new ResourceInitializationException(e);
    }
  }

  @Override
  protected void extract(JCas jCas) throws AnalysisEngineProcessException {

    Stream<List<TableCell>> rows =
        new Tables(jCas).withColumn(source).withColumn(target).getFilteredRows();

    addRelationsToIndex(
        rows.map(
            row -> {
              TableCell sourceCell = row.get(0);
              TableCell targetCell = row.get(1);

              Entity sourceEntity = getEntity(jCas, sourceCell, sourceConstructor);
              Entity targetEntity = getEntity(jCas, targetCell, targetConstructor);

              int begin = Math.min(sourceCell.getBegin(), targetCell.getBegin());
              int end = Math.max(sourceCell.getEnd(), targetCell.getEnd());

              return createRelation(jCas, sourceEntity, targetEntity, begin, end, null);
            }));
  }

  private Entity getEntity(JCas jCas, TableCell cell, Constructor<? extends Entity> type) {
    List<? extends Entity> covered = JCasUtil.selectCovered(type.getDeclaringClass(), cell);
    if (!covered.isEmpty()) {
      return covered.get(0);
    } else {
      Entity entity;
      try {
        entity = type.newInstance(jCas);
        entity.setBegin(cell.getBegin());
        entity.setEnd(cell.getEnd());
        entity.setValue(cell.getCoveredText());
        if (!Strings.isNullOrEmpty(subType)) {
          entity.setSubType(subType);
        }
        addToJCasIndex(entity);
        return entity;
      } catch (InstantiationException
          | IllegalAccessException
          | IllegalArgumentException
          | InvocationTargetException e) {
        throw new BaleenRuntimeException("Can not create entity type " + type.getName(), e);
      }
    }
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(
            sourceConstructor.getDeclaringClass(), targetConstructor.getDeclaringClass()),
        ImmutableSet.of(
            sourceConstructor.getDeclaringClass(),
            targetConstructor.getDeclaringClass(),
            Relation.class));
  }
}
