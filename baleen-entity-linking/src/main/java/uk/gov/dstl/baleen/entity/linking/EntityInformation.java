// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;

/**
 * Information about an entity.
 *
 * @param <T> The type of Entity
 */
public class EntityInformation<T extends Entity> {

  private final ReferenceTarget referenceTarget;
  private final Collection<T> mentions;
  private final Collection<Sentence> sentences;

  /**
   * Constructor
   *
   * @param referenceTarget The referenceTarget relating to the Entity
   */
  public EntityInformation(ReferenceTarget referenceTarget) {
    this.referenceTarget = referenceTarget;
    sentences = ImmutableList.of();
    mentions = ImmutableList.of();
  }

  /**
   * Constructor
   *
   * @param referenceTarget The referenceTarget
   * @param mentions A Collection of type T
   * @param sentences A collection of sentences
   */
  public EntityInformation(
      ReferenceTarget referenceTarget, Collection<T> mentions, Collection<Sentence> sentences) {
    this.referenceTarget = referenceTarget;
    this.sentences = ImmutableList.copyOf(sentences);
    this.mentions = ImmutableList.copyOf(mentions);
  }

  /**
   * Get referenceTarget
   *
   * @return referenceTarget
   */
  public ReferenceTarget getReferenceTarget() {
    return referenceTarget;
  }

  /**
   * Get Mentions
   *
   * @return mentions
   */
  public Collection<T> getMentions() {
    return mentions;
  }

  /**
   * Get Sentences
   *
   * @return sentences
   */
  public Collection<Sentence> getSentences() {
    return sentences;
  }
}
