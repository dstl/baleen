// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.triage;

import java.util.Collections;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import cc.mallet.classify.Classification;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.jobs.triage.MalletClassifierTrainer;
import uk.gov.dstl.baleen.jobs.triage.MaxEntClassifierTrainer;
import uk.gov.dstl.baleen.mallet.FileObject;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * An annotator to classify a document based on the provided model.
 *
 * <p>The assigned class is stored as a Metadata annotation, the key can be provided optionally.
 *
 * @see MaxEntClassifierTrainer
 * @see MalletClassifierTrainer
 */
public class MalletClassifier extends BaleenAnnotator {

  /**
   * Metadata key used to store the topic model
   *
   * @baleen.config topic
   */
  public static final String PARAM_METADTA_KEY = "key";

  @ConfigurationParameter(name = PARAM_METADTA_KEY, defaultValue = "label")
  private String metadataKey;

  /**
   * Model file path
   *
   * @baleen.config e.g. /path/to/myModel.mallet
   */
  public static final String PARAM_MODEL = "modelFile";

  @ConfigurationParameter(name = PARAM_MODEL, mandatory = true)
  private String modelPath;

  private cc.mallet.classify.Classifier classifierModel;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);

    try {
      classifierModel = new FileObject<cc.mallet.classify.Classifier>(modelPath).object();
    } catch (Exception e) {
      throw new ResourceInitializationException();
    }
  }

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {

    InstanceList instances = new InstanceList(classifierModel.getInstancePipe());
    instances.addThruPipe(new Instance(jCas.getDocumentText(), "", "from jcas", null));

    Classification classify = classifierModel.classify(instances.get(0));

    Metadata md = new Metadata(jCas);
    md.setKey(metadataKey);
    md.setValue(classify.getLabeling().getBestLabel().toString());
    addToJCasIndex(md);
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(Metadata.class));
  }
}
