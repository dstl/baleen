// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import java.io.File;
import java.util.Collections;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Sets the document type based on the file location, stripping out a 'root' directory specified by
 * the user
 *
 * @baleen.javadoc
 */
public class DocumentTypeByLocation extends BaleenAnnotator {
  /**
   * The root directory to strip out of the path before setting the document type
   *
   * @baleen.config
   */
  public static final String PARAM_BASE_DIRECTORY = "baseDirectory";

  @ConfigurationParameter(name = PARAM_BASE_DIRECTORY, defaultValue = "")
  private String baseDirectory;

  @Override
  public void doProcess(JCas aJCas) throws AnalysisEngineProcessException {
    DocumentAnnotation da = getDocumentAnnotation(aJCas);

    File f = new File(da.getSourceUri());

    String type = f.getParentFile().getAbsolutePath();
    if (baseDirectory != null && type.startsWith(baseDirectory))
      type = type.substring(baseDirectory.length());

    // remove leading and trailing slashes and backslashes using a regular expression
    type = type.replaceAll("^\\\\+|^\\/+|\\\\+$|\\/+$", "");
    da.setDocType(type);
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        Collections.emptySet(), ImmutableSet.of(DocumentAnnotation.class));
  }
}
