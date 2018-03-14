// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.templates;

import java.util.Collection;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.templates.TemplateField;
import uk.gov.dstl.baleen.types.templates.TemplateRecord;

/**
 * Using previously created record and template definitions and annotations, remove records that are
 * not valid.
 *
 * <p>Each YAML configuration file for records can contain multiple definitions for records.
 * Template fields can be considered required to make a record valid.
 *
 * <p>This annotator (or cleaner) removes records which do not contain all required fields.
 *
 * <p>This can be configured to only remove invalid records of a specified source and/or from a list
 * of specified records. Example configuration:
 *
 * <pre>
 * ...
 * annotators:
 * - class templates.TemplateAnnotator:
 * ...
 * - class templates.TemplateValidator:
 * source: athleteReportDefinitions
 * records:
 * - athleteDetails
 * - athletePerformance
 *
 * </pre>
 */
public class TemplateValidator extends AbstractTemplateAnnotator {

  /**
   * A specific source file that the records should be from.
   *
   * <p>If not specified all sources are used
   *
   * @baleen.config myRecords.yaml
   */
  public static final String PARAM_SOURCE = "source";

  /**
   * A specific list of records to be validated.
   *
   * <p>If not specified all records are validated
   *
   * @baleen.config myRecords.yaml
   */
  public static final String PARAM_RECORDS = "records";

  /** The source names. */
  @ConfigurationParameter(name = PARAM_SOURCE, mandatory = false)
  private String source;

  /** The records names. */
  @ConfigurationParameter(name = PARAM_RECORDS, mandatory = false)
  private String[] records;

  @Override
  protected void doProcess(final JCas jCas) throws AnalysisEngineProcessException {
    Collection<TemplateRecord> recordAnnotations = JCasUtil.select(jCas, TemplateRecord.class);
    for (Entry<String, TemplateRecordConfiguration> entry : recordDefinitions.entries()) {
      if (shouldProcessDefinition(entry.getKey(), entry.getValue())) {
        doProcessRecordDefinition(entry.getKey(), entry.getValue(), recordAnnotations);
      }
    }
  }

  /**
   * Check if this definition should be processed based on configuration
   *
   * @param source the source
   * @param recordDefinition the record definition
   * @return
   */
  private boolean shouldProcessDefinition(
      String recordSource, TemplateRecordConfiguration recordDefinition) {
    if (StringUtils.isNotBlank(source) && !source.equals(recordSource)) {
      return false;
    }
    if (ArrayUtils.isNotEmpty(records)
        && !ArrayUtils.contains(records, recordDefinition.getName())) {
      return false;
    }
    return true;
  }

  /**
   * Removes invalid records.
   *
   * @param source the source
   * @param recordDefinition the RecordDefinitionConfiguration
   * @param recordAnnotations the record annotations
   */
  protected void doProcessRecordDefinition(
      String source,
      TemplateRecordConfiguration recordDefinition,
      Collection<TemplateRecord> recordAnnotations) {

    getRecordsForRecordDefinition(recordAnnotations, source, recordDefinition.getName())
        .forEach(
            r -> {
              Collection<TemplateField> fieldAnnotations = getTemplateFieldsForRecord(source, r);
              Set<String> fieldsPresent = getNamesOfFieldsPresent(fieldAnnotations);

              Optional<String> missingRequired =
                  streamNamesOfRequiredFields(recordDefinition)
                      .filter(required -> !fieldsPresent.contains(required))
                      .findFirst();

              if (missingRequired.isPresent()) {
                getMonitor()
                    .info(
                        "Removing invalid record {} - {} from as missing require field {}",
                        source,
                        recordDefinition.getName(),
                        missingRequired.get());
                removeFromJCasIndex(r);
                removeFromJCasIndex(fieldAnnotations);
              }
            });
  }

  /**
   * Stream the names of the required fields for the given record definition.
   *
   * @param recordDefinition the record definition
   * @return stream of the names of the require fields
   */
  private Stream<String> streamNamesOfRequiredFields(TemplateRecordConfiguration recordDefinition) {
    return recordDefinition
        .getFields()
        .stream()
        .filter(TemplateFieldConfiguration::isRequired)
        .map(TemplateFieldConfiguration::getName);
  }

  /**
   * Get the names of the fields given
   *
   * @param fieldAnnotations the field annotations
   * @return the names of the given fields
   */
  private Set<String> getNamesOfFieldsPresent(Collection<TemplateField> fieldAnnotations) {
    return fieldAnnotations.stream().map(TemplateField::getName).collect(Collectors.toSet());
  }

  /**
   * Get the template fields for the given source and record.
   *
   * @param source the source
   * @param record the record
   * @return
   */
  private Collection<TemplateField> getTemplateFieldsForRecord(
      String source, TemplateRecord record) {
    return JCasUtil.selectCovered(TemplateField.class, record)
        .stream()
        .filter(t -> source.equals(t.getSource()))
        .collect(Collectors.toList());
  }

  /**
   * Get the records for the given source and record definition name
   *
   * @param records all the records
   * @param source the source
   * @param name the name of the record
   * @return a stream of the records
   */
  private Stream<TemplateRecord> getRecordsForRecordDefinition(
      Collection<TemplateRecord> records, String source, String name) {
    return records.stream().filter(r -> source.equals(r.getSource()) && name.equals(r.getName()));
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(TemplateField.class, TemplateRecord.class), Collections.emptySet());
  }
}
