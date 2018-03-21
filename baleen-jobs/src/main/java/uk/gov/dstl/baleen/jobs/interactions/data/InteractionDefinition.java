// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.jobs.interactions.data;

/** A interaction definition having a type, trigger word and between a source and target type. */
public class InteractionDefinition {

  private final String type;

  private final String subType;

  private final Word word;

  private final String source;

  private final String target;

  /**
   * Instantiates a new interaction definition.
   *
   * @param type the type (highlevel relation type, mapped to taxonomy)
   * @param subType the sub type (typically quite detailed, perhaps the lemma)
   * @param word the word (lemma)
   * @param source the source type
   * @param target the target type
   */
  public InteractionDefinition(
      String type, String subType, Word word, String source, String target) {
    this.type = type;
    this.subType = subType;
    this.word = word;
    this.source = source;
    this.target = target;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * Gets the sub type.
   *
   * @return the sub type
   */
  public String getSubType() {
    return subType;
  }

  /**
   * Gets the source.
   *
   * @return the source
   */
  public String getSource() {
    return source;
  }

  /**
   * Gets the target.
   *
   * @return the target
   */
  public String getTarget() {
    return target;
  }

  /**
   * Gets the word.
   *
   * @return the word
   */
  public Word getWord() {
    return word;
  }

  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * result + (source == null ? 0 : source.hashCode());
    result = PRIME * result + (subType == null ? 0 : subType.hashCode());
    result = PRIME * result + (target == null ? 0 : target.hashCode());
    result = PRIME * result + (type == null ? 0 : type.hashCode());
    result = PRIME * result + (word == null ? 0 : word.hashCode());
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
    InteractionDefinition other = (InteractionDefinition) obj;
    if (source == null) {
      if (other.source != null) {
        return false;
      }
    } else if (!source.equals(other.source)) {
      return false;
    }
    if (subType == null) {
      if (other.subType != null) {
        return false;
      }
    } else if (!subType.equals(other.subType)) {
      return false;
    }
    if (target == null) {
      if (other.target != null) {
        return false;
      }
    } else if (!target.equals(other.target)) {
      return false;
    }
    if (type == null) {
      if (other.type != null) {
        return false;
      }
    } else if (!type.equals(other.type)) {
      return false;
    }
    if (word == null) {
      if (other.word != null) {
        return false;
      }
    } else if (!word.equals(other.word)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return word.getLemma();
  }
}
