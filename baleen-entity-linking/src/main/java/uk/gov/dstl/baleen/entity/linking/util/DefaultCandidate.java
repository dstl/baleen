// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.util;

import java.util.Map;

import uk.gov.dstl.baleen.entity.linking.Candidate;

/** A simple Candidate implementation */
public class DefaultCandidate implements Candidate {

  private final Map<String, String> keyValuePairs;
  private final String id;
  private final String name;

  /**
   * @param id The candidate id
   * @param keyValuePairs A map of keys to values about the Candidate
   */
  public DefaultCandidate(String id, String name, Map<String, String> keyValuePairs) {
    this.id = id;
    this.name = name;
    this.keyValuePairs = keyValuePairs;
  }

  @Override
  public Map<String, String> getKeyValuePairs() {
    return keyValuePairs;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id: " + id);
    sb.append(", Name: " + name);
    keyValuePairs
        .entrySet()
        .forEach(
            entry -> {
              sb.append("\nKey: " + entry.getKey() + " ");
              sb.append("Value: " + entry.getValue());
            });

    return sb.toString();
  }

  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * result + (id == null ? 0 : id.hashCode());
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
    Candidate other = (Candidate) obj;
    if (id == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!id.equals(other.getId())) {
      return false;
    }
    return true;
  }
}
