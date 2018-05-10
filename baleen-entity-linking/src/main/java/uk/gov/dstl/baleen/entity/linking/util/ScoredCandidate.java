// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.util;

import java.util.Map;

import uk.gov.dstl.baleen.entity.linking.Candidate;

/** A Candidate decorator to add a score value */
public class ScoredCandidate implements Candidate, Comparable<ScoredCandidate> {

  private final Candidate delegate;
  private final int score;

  /**
   * Decorate the given candidate and add the score
   *
   * @param candidate
   * @param score
   */
  public ScoredCandidate(Candidate candidate, int score) {
    delegate = candidate;
    this.score = score;
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public String getName() {
    return delegate.getName();
  }

  @Override
  public Map<String, String> getKeyValuePairs() {
    return delegate.getKeyValuePairs();
  }

  @Override
  public int compareTo(ScoredCandidate o) {
    return Integer.compare(score, o.score);
  }

  @Override
  public String toString() {
    return new StringBuilder().append(getId()).append(":").append(score).toString();
  }

  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * result + (delegate == null ? 0 : delegate.hashCode());
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
    if (!(obj instanceof Candidate)) {
      return false;
    }
    if (delegate == null) {
      return false;
    }
    Candidate other = (Candidate) obj;
    return delegate.getId().equals(other.getId());
  }
}
