// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.uima.grammar;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.BiMap;

import uk.gov.dstl.baleen.types.language.WordToken;

/** A match of {@link DependencyNode}s to {@link WordToken}'s in a dependency graph. */
public class DependencyMatch {

  private final BiMap<DependencyNode, WordToken> matched;

  /**
   * Construct the dependency match from the given map.
   *
   * @param matched map of node to word token
   */
  public DependencyMatch(BiMap<DependencyNode, WordToken> matched) {
    this.matched = matched;
  }

  /**
   * Get the matched {@link WordToken} to the given {@link DependencyNode}
   *
   * @param node to match
   * @return the matched {@link WordToken}
   */
  public WordToken getMatched(DependencyNode node) {
    return matched.get(node);
  }

  /**
   * Get the matched {@link DependencyNode} to the given {@link WordToken} if it exists.
   *
   * @param wordToken to match
   * @return {@link Optional} of the matched node
   */
  public Optional<DependencyNode> getMatched(WordToken wordToken) {
    return Optional.ofNullable(matched.inverse().get(wordToken));
  }

  /**
   * Find the node with the given id and return the matched word token
   *
   * @param id to look for
   * @return optional of the found matched word token
   */
  public Optional<WordToken> getMatchedToId(String id) {
    return matched.entrySet().stream()
        .filter(e -> id.equals(e.getKey().getId()))
        .map(Map.Entry::getValue)
        .findFirst();
  }
}
