// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.triage;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import cc.mallet.pipe.Pipe;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.mallet.MaximumIndex;
import uk.gov.dstl.baleen.mallet.TopicModelPipe;
import uk.gov.dstl.baleen.mallet.TopicWords;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * An annotator to infer a Topic Model for a document based on the provided model.
 *
 * <p>Topic description is stored as a Metadata annotation.
 *
 * @see {@link uk.gov.dstl.baleen.jobs.triage.TopicModelTrainer}
 */
public class TopicModel extends BaleenAnnotator {

  /**
   * Connection to Stopwords Resource
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedStopwordResource
   */
  public static final String KEY_STOPWORDS = "stopwords";

  @ExternalResource(key = KEY_STOPWORDS)
  protected SharedStopwordResource stopwordResource;

  /**
   * The stoplist to use. If the stoplist matches one of the enum's provided in {@link
   * uk.gov.dstl.baleen.resources.SharedStopwordResource.StopwordList}, then that list will be
   * loaded.
   *
   * <p>Otherwise, the string is taken to be a file path and that file is used. The format of the
   * file is expected to be one stopword per line.
   *
   * @baleen.config DEFAULT
   */
  public static final String PARAM_STOPLIST = "stoplist";

  @ConfigurationParameter(name = PARAM_STOPLIST, defaultValue = "DEFAULT")
  protected String stoplist;

  /**
   * Metadata key used to store the topic model
   *
   * @baleen.config topic
   */
  public static final String PARAM_METADTA_KEY = "key";

  @ConfigurationParameter(name = PARAM_METADTA_KEY, defaultValue = "topic")
  private String metadataKey;

  /**
   * Number of iterations
   *
   * @baleen.config 200
   */
  public static final String PARAM_ITERATIONS = "iterations";

  @ConfigurationParameter(name = PARAM_ITERATIONS, defaultValue = "200")
  private int iterations;

  /**
   * Number of thining iterations
   *
   * @baleen.config 10
   */
  public static final String PARAM_THINING = "thining";

  @ConfigurationParameter(name = PARAM_THINING, defaultValue = "10")
  private int thining;

  /**
   * Number of burn in iterations
   *
   * @baleen.config 5
   */
  public static final String PARAM_BURN_IN = "burnIn";

  @ConfigurationParameter(name = PARAM_BURN_IN, defaultValue = "5")
  private int burnIn;

  /**
   * Model file path
   *
   * @baleen.config e.g. /path/to/myModel.mallet
   */
  public static final String PARAM_MODEL = "modelFile";

  @ConfigurationParameter(name = PARAM_MODEL, mandatory = true)
  private String modelPath;

  private ParallelTopicModel model;
  private TopicWords topicWords;
  private Pipe pipe;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);

    try {
      model = ParallelTopicModel.read(new File(modelPath));
      pipe = new TopicModelPipe(stopwordResource.getStopwords(stoplist), model.getAlphabet());
      topicWords = new TopicWords(model);
    } catch (Exception e) {
      throw new ResourceInitializationException();
    }
  }

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {

    InstanceList testing = new InstanceList(pipe);
    testing.addThruPipe(new Instance(jCas.getDocumentText(), null, "from jcas", null));

    TopicInferencer inferencer = model.getInferencer();

    double[] topicDistribution =
        inferencer.getSampledDistribution(testing.get(0), iterations, thining, burnIn);

    int topicIndex = new MaximumIndex(topicDistribution).find();

    List<String> inferedTopic = topicWords.forTopic(topicIndex);

    Metadata md = new Metadata(jCas);
    md.setKey(metadataKey);
    md.setValue(inferedTopic.toString());
    addToJCasIndex(md);
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(Metadata.class));
  }
}
