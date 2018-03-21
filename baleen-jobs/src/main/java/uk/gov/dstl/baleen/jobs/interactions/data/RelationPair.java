// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.jobs.interactions.data;

/**
 * Captures the source and target (UIMA) types of a relationship.
 *
 * <p>Has sensible hashcode, equals and toString implementations.
 */
public class RelationPair {

  private final String source;

  private final String target;

  /**
   * Instantiates a new relation pair.
   *
   * @param pattern the pattern
   */
  public RelationPair(PatternReference pattern) {
    this(pattern.getSourceType(), pattern.getTargetType());
  }

  /**
   * Instantiates a new relation pair.
   *
   * @param source the source
   * @param target the target
   */
  public RelationPair(String source, String target) {
    this.source = source;
    this.target = target;
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

  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * result + (source == null ? 0 : source.hashCode());
    result = PRIME * result + (target == null ? 0 : target.hashCode());
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
    final RelationPair other = (RelationPair) obj;
    if (source == null) {
      if (other.source != null) {
        return false;
      }
    } else if (!source.equals(other.source)) {
      return false;
    }
    if (target == null) {
      if (other.target != null) {
        return false;
      }
    } else if (!target.equals(other.target)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return String.format("RP[%s,%s]", source, target);
  }
}
