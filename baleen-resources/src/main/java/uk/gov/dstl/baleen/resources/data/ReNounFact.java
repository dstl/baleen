// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.resources.data;

import java.util.Objects;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

/**
 * Data class for Fact details.
 *
 * <p>A ReNoun Fact is a triple of the for (subject, attribute, object) where the attribute is from
 * a defined list on noun based relation attributes.
 *
 * <p>The other attributes can be used in different circumstances but do not affect the equality.
 *
 * @baleen.javadoc
 * @see http://emnlp2014.org/papers/pdf/EMNLP2014038.pdf
 */
public class ReNounFact {

  /** Field to store the subject for the fact for scoring */
  public static final String SUBJECT_FIELD = "subject";

  /** Field to store the attribute for the fact for scoring */
  public static final String ATTRIBUTE_FIELD = "attribute";

  /** Field to store the object for the fact for scoring */
  public static final String OBJECT_FIELD = "object";

  /** The field to store a fact's score */
  public static final String SCORE_FIELD = "score";

  /** Field to store the pattern for the fact for scoring */
  public static final String PATTERN_FIELD = "pattern";

  /** Field to store the sentence the fact was extracted from if known */
  public static final String SENTENCE_FIELD = "sentence";

  private String subject;
  private String attribute;
  private String object;
  private Double score;
  private String pattern;
  private String sentence;

  /**
   * Construct the fact from a mongo document
   *
   * @param document to construct from
   */
  public ReNounFact(Document document) {
    subject = document.getString(SUBJECT_FIELD);
    object = document.getString(OBJECT_FIELD);
    attribute = document.getString(ATTRIBUTE_FIELD);
    score = document.getDouble(SCORE_FIELD);
    pattern = document.getString(PATTERN_FIELD);
    sentence = document.getString(SENTENCE_FIELD);
  }

  /**
   * Constructor for the fact
   *
   * @param subject
   * @param attribute
   * @param object
   */
  public ReNounFact(String subject, String attribute, String object) {
    this.subject = subject;
    this.attribute = attribute;
    this.object = object;
  }

  /**
   * Constructor for the fact with known pattern
   *
   * @param subject
   * @param attribute
   * @param object
   * @param pattern that extracted this fact
   */
  public ReNounFact(String subject, String attribute, String object, String pattern) {
    this.subject = subject;
    this.attribute = attribute;
    this.object = object;
    this.pattern = pattern;
  }

  /** @return the attribute */
  public String getAttribute() {
    return attribute;
  }

  /** @return the subject */
  public String getSubject() {
    return subject;
  }

  /** @return the object */
  public String getObject() {
    return object;
  }

  /** @return the score (if present) of this fact */
  public Double getScore() {
    return score;
  }

  /**
   * Set the score to the given value
   *
   * @param score
   */
  public void setScore(Double score) {
    this.score = score;
  }

  /** @return the pattern that extracted this fact, if known */
  public String getPattern() {
    return pattern;
  }

  /**
   * Set the pattern that extracted this fact, if known
   *
   * @param pattern
   */
  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  /** @return the sentence this fact was extracted form if know */
  public String getSentence() {
    return sentence;
  }

  /**
   * Set the sentence this fact was extracted from
   *
   * @param sentence
   */
  public void setSentence(String sentence) {
    this.sentence = sentence;
  }

  /**
   * Save this fact to the given mongo collection
   *
   * @param collection to save to
   */
  public void save(MongoCollection<Document> collection) {
    collection.insertOne(
        new Document()
            .append(SUBJECT_FIELD, subject)
            .append(ATTRIBUTE_FIELD, attribute)
            .append(OBJECT_FIELD, object)
            .append(SCORE_FIELD, score)
            .append(PATTERN_FIELD, pattern)
            .append(SENTENCE_FIELD, sentence));
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder
        .append("ReNounFact (")
        .append(subject)
        .append(", ")
        .append(attribute)
        .append(", ")
        .append(object)
        .append(") ");
    if (score != null) {
      builder.append(", score=").append(score).append(", ");
    }
    if (pattern != null) {
      builder.append(", pattern=").append(pattern);
    }
    if (sentence != null) {
      builder.append(", sentence=").append(sentence);
    }
    return builder.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ReNounFact)) {
      return false;
    }
    ReNounFact fact = (ReNounFact) o;
    return Objects.equals(subject, fact.subject)
        && Objects.equals(object, fact.object)
        && Objects.equals(attribute, fact.attribute);
  }

  @Override
  public int hashCode() {
    return Objects.hash(subject, object, attribute);
  }
}
