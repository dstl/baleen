// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import java.util.Collections;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Sets the document type based on a user provided parameter
 *
 * @baleen.javadoc
 */
public class DocumentTypeByParameter extends BaleenAnnotator {
  /**
   * The value to set the document type to
   *
   * @baleen.config
   */
  public static final String PARAM_TYPE = "type";

  @ConfigurationParameter(name = PARAM_TYPE, defaultValue = "")
  private String type;

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
    if (!Strings.isNullOrEmpty(type)) {
      getDocumentAnnotation(jCas).setDocType(type);
    }
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        Collections.emptySet(), ImmutableSet.of(DocumentAnnotation.class));
  }
}
