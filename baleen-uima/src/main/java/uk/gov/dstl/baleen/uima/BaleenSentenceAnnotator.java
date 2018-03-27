// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima;

import java.util.Collection;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.language.Sentence;

/**
 * A helper class to process each sentences at a time.
 *
 * <p>Pre and Post extract options are provided for any full document setup/teardown required.
 *
 * @baleen.javadoc
 */
public abstract class BaleenSentenceAnnotator extends BaleenAnnotator {

  @Override
  protected final void doProcess(final JCas jCas) throws AnalysisEngineProcessException {

    try {
      preExtract(jCas);

      Collection<Sentence> sentences = JCasUtil.select(jCas, Sentence.class);
      for (Sentence s : sentences) {
        doProcessSentence(jCas, s);
      }
    } finally {
      postExtract(jCas);
    }
  }

  /**
   * Perform any pre-process actions required.
   *
   * <p>The default implementation will do nothing.
   *
   * @param jCas to pre process
   * @throws AnalysisEngineProcessException the analysis engine process exception
   */
  protected void preExtract(final JCas jCas) throws AnalysisEngineProcessException {
    // Do nothing
  }

  /**
   * Perform any post-process actions required.
   *
   * <p>(Called even after exception thrown.)
   *
   * <p>The default implementation will do nothing.
   *
   * @param jCas to post process
   * @throws AnalysisEngineProcessException the analysis engine process exception
   */
  protected void postExtract(final JCas jCas) throws AnalysisEngineProcessException {
    // Do nothing
  }

  /**
   * Process a sentence
   *
   * @param sentence the sentence to process
   * @throws AnalysisEngineProcessException the analysis engine process exception
   */
  protected abstract void doProcessSentence(final JCas jCas, final Sentence sentence)
      throws AnalysisEngineProcessException;
}
