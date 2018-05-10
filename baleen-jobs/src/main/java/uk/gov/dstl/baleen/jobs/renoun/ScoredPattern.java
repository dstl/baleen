// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.jobs.renoun;

import org.bson.Document;

/**
 * Data class to store the pattern score.
 *
 * <p>The Frequency is the number of facts extracted by the pattern.
 *
 * <p>The coherence is a measure of the 'closeness' of the attributes of the facts that the pattern
 * extracts.
 *
 * @baleen.javadoc
 * @see http://emnlp2014.org/papers/pdf/EMNLP2014038.pdf
 */
public class ScoredPattern {

  /** Field to store the pattern for the fact for scoring */
  /* NB should match ReNounRelationshipAnnotator */
  public static final String PATTERN_FACT_FIELD = "pattern";

  /** Frequency of the scored pattern */
  public static final String FREQUENCY_KEY = "frequency";

  /** Coherence of the scored pattern */
  public static final String COHERENCE_KEY = "coherence";

  private String pattern;
  private int frequency;
  private double coherence;

  /**
   * Construct the Scored pattern from the given document
   *
   * @param document (mongo) to construct from
   */
  public ScoredPattern(Document document) {

    pattern = document.getString(PATTERN_FACT_FIELD);
    frequency = document.getInteger(FREQUENCY_KEY);
    coherence = document.getDouble(COHERENCE_KEY);
  }

  /** @return the pattern that is scored */
  public String getPattern() {
    return pattern;
  }

  /** @return the frequency of the scored pattern */
  public int getFrequency() {
    return frequency;
  }

  /** @return the coherence of the scored pattern */
  public double getCoherence() {
    return coherence;
  }
}
