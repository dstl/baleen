// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.grammar;

import uk.gov.dstl.baleen.types.language.Dependency;
import uk.gov.dstl.baleen.types.language.WordToken;

/** An edge between two words (from and to) defined by a dependency. */
public class Edge {
  private final WordToken from;
  private final Dependency dependency;
  private final WordToken to;

  /**
   * Instantiates a new edge.
   *
   * @param from the from
   * @param dependency the dependency
   * @param to the to
   */
  public Edge(WordToken from, Dependency dependency, WordToken to) {
    this.from = from;
    this.dependency = dependency;
    this.to = to;
  }

  /**
   * Gets the from word.
   *
   * @return the from word
   */
  public WordToken getFrom() {
    return from;
  }

  /**
   * Gets the dependency.
   *
   * @return the dependency
   */
  public Dependency getDependency() {
    return dependency;
  }

  /**
   * Gets the to word
   *
   * @return the to word
   */
  public WordToken getTo() {
    return to;
  }

  /**
   * Gets the other word (other side)
   *
   * @param token the token
   * @return the other
   */
  public WordToken getOther(WordToken token) {
    return token.equals(to) ? from : to;
  }

  /**
   * Checks if this is the to word.
   *
   * @param token the token
   * @return true, if is to
   */
  public boolean isTo(WordToken token) {
    return token.equals(to);
  }

  /**
   * Checks if this is the from word?
   *
   * @param token the token
   * @return true, if is from
   */
  public boolean isFrom(WordToken token) {
    return token.equals(from);
  }

  @Override
  public String toString() {
    return to.getCoveredText() + " - " + from.getCoveredText();
  }
}
