// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import uk.gov.dstl.baleen.annotators.templates.TemplateAnnotator;
import uk.gov.dstl.baleen.consumers.utils.SourceUtils;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.templates.TemplateField;
import uk.gov.dstl.baleen.types.templates.TemplateRecord;
import uk.gov.dstl.baleen.uima.BaleenConsumer;

/**
 * Abstract Template Record Consumer that converts TemplateRecord annotations and the TemplateField
 * annotations covered by them, to a more convenient record-centric form.
 *
 * <p>Each entry in the file is an "object" with a <code>kind</code> field of <code>NAMED</code> or
 * <code>DEFAULT</code> and a <code>fields</code> field consisting of a dictionary / map of name and
 * value pairs from the TemplateField annotations. In the case of <code>NAMED</code> records, there
 * will be an additional <code>name</code> field.
 *
 * <p>Subclasses of this consumer should be used with {@link TemplateAnnotator}.
 */
public abstract class AbstractTemplateRecordConsumer extends BaleenConsumer {

  @Override
  protected void doProcess(final JCas jCas) throws AnalysisEngineProcessException {
    Multimap<String, ExtractedRecord> records = HashMultimap.create();
    Collection<TemplateRecord> recordAnnotations = JCasUtil.select(jCas, TemplateRecord.class);

    HashSet<TemplateField> allFields = new HashSet<>(JCasUtil.select(jCas, TemplateField.class));

    for (TemplateRecord recordAnnotation : recordAnnotations) {
      Collection<TemplateField> fieldAnnotations =
          JCasUtil.selectCovered(TemplateField.class, recordAnnotation);
      List<TemplateField> recordFieldAnnotations =
          fieldAnnotations
              .stream()
              .filter(f -> recordAnnotation.getSource().equals(f.getSource()))
              .collect(Collectors.toList());
      allFields.removeAll(recordFieldAnnotations);
      Collection<ExtractedField> fieldValues = makeFieldValues(recordFieldAnnotations);
      records.put(
          recordAnnotation.getSource(),
          new ExtractedRecord(recordAnnotation.getName(), fieldValues));
    }

    Multimap<String, TemplateField> remainingFields = HashMultimap.create();
    for (TemplateField templateField : allFields) {
      remainingFields.put(templateField.getSource(), templateField);
    }

    for (String source : remainingFields.keySet()) {
      records.put(source, new ExtractedRecord(makeFieldValues(remainingFields.get(source))));
    }

    String documentSourceName = SourceUtils.getDocumentSourceBaseName(jCas, getSupport());
    writeRecords(jCas, documentSourceName, records.asMap());
  }

  /**
   * Makes the field name/value pairs from a collection of field annotations.
   *
   * @param fieldAnnotations the field annotations
   * @return the field value name/value pairs
   */
  private static Collection<ExtractedField> makeFieldValues(
      Collection<TemplateField> fieldAnnotations) {
    Collection<ExtractedField> fieldValues = new ArrayList<>();
    for (TemplateField templateField : fieldAnnotations) {
      fieldValues.add(new ExtractedField(templateField.getName(), templateField.getValue()));
    }
    return fieldValues;
  }

  protected abstract void writeRecords(
      JCas jCas, String documentSourceName, Map<String, Collection<ExtractedRecord>> records)
      throws AnalysisEngineProcessException;

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(TemplateRecord.class, TemplateField.class), Collections.emptySet());
  }
}
