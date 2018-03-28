// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.collectionreaders.re3d;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/** A serializable Java Bean representation of a re3d document. */
public class Re3dDocument {

  /** The id. */
  @JsonProperty("_id")
  private String id;

  /** The title. */
  private String title;

  /** The text. */
  private String text;

  /** The source name. */
  private String sourceName;

  /** The source url. */
  private String sourceUrl;

  /** The word count. */
  private int wordCount;

  /** The sentence count. */
  private int sentenceCount;

  /** The entities. */
  @JsonIgnore private final List<Re3dEntity> entities = new LinkedList<>();

  /** The relations. */
  @JsonIgnore private final List<Re3dRelation> relations = new LinkedList<>();

  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(final String id) {
    this.id = id;
  }

  /**
   * Gets the title.
   *
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Sets the title.
   *
   * @param title the new title
   */
  public void setTitle(final String title) {
    this.title = title;
  }

  /**
   * Gets the text.
   *
   * @return the text
   */
  public String getText() {
    return text;
  }

  /**
   * Sets the text.
   *
   * @param text the new text
   */
  public void setText(final String text) {
    this.text = text;
  }

  /**
   * Gets the source name.
   *
   * @return the source name
   */
  public String getSourceName() {
    return sourceName;
  }

  /**
   * Sets the source name.
   *
   * @param sourceName the new source name
   */
  public void setSourceName(final String sourceName) {
    this.sourceName = sourceName;
  }

  /**
   * Gets the source url.
   *
   * @return the source url
   */
  public String getSourceUrl() {
    return sourceUrl;
  }

  /**
   * Sets the source url.
   *
   * @param sourceUrl the new source url
   */
  public void setSourceUrl(final String sourceUrl) {
    this.sourceUrl = sourceUrl;
  }

  /**
   * Gets the word count.
   *
   * @return the word count
   */
  public int getWordCount() {
    return wordCount;
  }

  /**
   * Sets the word count.
   *
   * @param wordCount the new word count
   */
  public void setWordCount(final int wordCount) {
    this.wordCount = wordCount;
  }

  /**
   * Gets the sentence count.
   *
   * @return the sentence count
   */
  public int getSentenceCount() {
    return sentenceCount;
  }

  /**
   * Sets the sentence count.
   *
   * @param sentenceCount the new sentence count
   */
  public void setSentenceCount(final int sentenceCount) {
    this.sentenceCount = sentenceCount;
  }

  /**
   * Adds the entity.
   *
   * @param e the entity
   */
  public void addEntity(final Re3dEntity e) {
    entities.add(e);
  }

  /**
   * Gets the entities.
   *
   * @return the entities
   */
  @JsonIgnore
  public List<Re3dEntity> getEntities() {
    return entities;
  }

  /**
   * Adds the entity.
   *
   * @param e the entity
   */
  public void addRelation(final Re3dRelation r) {
    relations.add(r);
  }

  /**
   * Gets the relations.
   *
   * @return the relations
   */
  @JsonIgnore
  public List<Re3dRelation> getRelations() {
    return relations;
  }
}
