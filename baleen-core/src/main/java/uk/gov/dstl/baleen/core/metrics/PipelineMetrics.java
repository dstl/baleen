// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.metrics;

import com.codahale.metrics.Timer;

/** Class to do pipeline level metrics, for example how long documents take to process */
public class PipelineMetrics {
  private String pipelineName;
  private Timer.Context documentTimerContext;

  private static final String DOCUMENT_TIMER = "documentProcessingTime";

  /**
   * Construct a new PipelineMetrics instance
   *
   * @param pipelineName Name of the pipeline
   */
  public PipelineMetrics(String pipelineName) {
    this.pipelineName = pipelineName;
  }

  /** Return the name of the pipeline associated with this PipelineMetrics instance */
  public String getPipelineName() {
    return pipelineName;
  }

  /** Start timing the processing of a document */
  public void startDocumentProcess() {
    finishDocumentProcess(); // Check we've finished the timing of the previous document
    documentTimerContext =
        MetricsFactory.getInstance().getTimer(pipelineName, DOCUMENT_TIMER).time();
  }

  /** Finish timing the processing of a document */
  public void finishDocumentProcess() {
    if (documentTimerContext != null) {
      documentTimerContext.stop();
      documentTimerContext = null;
    }
  }
}
