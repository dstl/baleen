// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.jobs.interactions.data;

import net.sf.extjwnl.data.POS;

/**
 * A simple representation of a word (as Lemma and Part of Speech).
 *
 * <p>Has sensible hashcode, equals and toString implementations.
 */
public class Word {

  private final String lemma;

  private final POS pos;

  /**
   * Instantiates a new word.
   *
   * @param lemma the lemma
   * @param pos the pos
   */
  public Word(String lemma, POS pos) {
    this.lemma = lemma;
    this.pos = pos;
  }

  /**
   * Gets the lemma.
   *
   * @return the lemma
   */
  public String getLemma() {
    return lemma;
  }

  /**
   * Gets the pos.
   *
   * @return the pos
   */
  public POS getPos() {
    return pos;
  }

  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * result + (lemma == null ? 0 : lemma.hashCode());
    result = PRIME * result + (pos == null ? 0 : pos.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Word other = (Word) obj;
    if (lemma == null) {
      if (other.lemma != null) {
        return false;
      }
    } else if (!lemma.equals(other.lemma)) {
      return false;
    }
    if (pos != other.pos) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return String.format("%s [%s]", lemma, pos);
  }
}
