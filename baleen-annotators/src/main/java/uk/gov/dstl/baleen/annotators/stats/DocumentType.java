// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.stats;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.tokenize.SimpleTokenizer;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.core.utils.ConfigUtils;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Set the document type parameter on a document using an OpenNLP DocCat model
 *
 * <p>An OpenNLP DocCat (Document Categorisation) model, specified by the user, is used to
 * categorise the document. A minimum confidence threshold can be specified, and categorisations
 * under that threshold will not be used.
 *
 * @baleen.javadoc
 */
public class DocumentType extends BaleenAnnotator {

  /**
   * The minimum confidence to have in a categorisation before it is used
   *
   * @baleen.config 0.7
   */
  public static final String PARAM_CONFIDENCE_THRESHOLD = "confidenceThreshold";

  @ConfigurationParameter(name = PARAM_CONFIDENCE_THRESHOLD, defaultValue = "0.7")
  private String thresholdString;

  // Parse the threshold config parameter into this variable to avoid issues with parameter types
  private Float threshold;

  /**
   * The model to use for document categorisation
   *
   * @baleen.config doctype.bin
   */
  public static final String PARAM_MODEL = "model";

  @ConfigurationParameter(name = PARAM_MODEL, defaultValue = "doctype.bin")
  private String modelFile = null;

  private DocumentCategorizerME doccat = null;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    threshold = ConfigUtils.stringToFloat(thresholdString, 0.7f);

    DoccatModel model;

    try (InputStream modelIn = new FileInputStream(modelFile)) {
      model = new DoccatModel(modelIn);
      doccat = new DocumentCategorizerME(model);
    } catch (IOException e) {
      getMonitor()
          .error(
              "Couldn't load OpenNLP DocCat model '{}' - annotator unable to initialise",
              modelFile);
      throw new ResourceInitializationException(e);
    }
  }

  @Override
  public void doProcess(JCas jCas) throws AnalysisEngineProcessException {
    DocumentAnnotation da = getDocumentAnnotation(jCas);

    if (Strings.isNullOrEmpty(da.getDocType())) {
      double[] outcomes =
          doccat.categorize(SimpleTokenizer.INSTANCE.tokenize(jCas.getDocumentText()));
      String cat = doccat.getBestCategory(outcomes);

      double max = -Double.MAX_VALUE;
      for (double d : outcomes) {
        if (d > max) {
          max = d;
        }
      }

      if (threshold != null && max > threshold) {
        da.setDocType(cat);
      }

    } else {
      getMonitor()
          .warn("A DocType annotation already exists. A second annotation will not be added.");
    }
  }

  @Override
  public void doDestroy() {
    doccat = null;
    threshold = null;
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        Collections.emptySet(), ImmutableSet.of(DocumentAnnotation.class));
  }
}
