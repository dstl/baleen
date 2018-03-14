// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.grammar;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.language.WordToken;

/** A node in the parse tree. */
public final class ParseTreeNode {

  private static final Logger LOGGER = LoggerFactory.getLogger(ParseTreeNode.class);

  private final PhraseChunk chunk;

  private ParseTreeNode parent;

  private final List<ParseTreeNode> children = new LinkedList<>();

  private final List<WordToken> words = new LinkedList<>();

  /**
   * Instantiates a node from a chunk
   *
   * @param chunk the chunk
   */
  public ParseTreeNode(PhraseChunk chunk) {
    this.chunk = chunk;
  }

  /**
   * Instantiates a new node based on a set of children.
   *
   * @param children the children
   */
  public ParseTreeNode(List<ParseTreeNode> children) {
    this.chunk = null;
    addAllChildren(children);
  }

  /**
   * Traverse children (down)
   *
   * @param consumer the consumer
   */
  public void traverseChildren(Consumer<List<ParseTreeNode>> consumer) {
    if (children != null && !children.isEmpty()) {
      consumer.accept(children);
      children.forEach(c -> c.traverseChildren(consumer));
    }
  }

  /**
   * Traverse parent (up the tree)
   *
   * @param consumer the consumer
   */
  public void traverseParent(BiPredicate<ParseTreeNode, ParseTreeNode> consumer) {
    if (parent != null) {
      final boolean test = consumer.test(parent, this);
      if (test) {
        parent.traverseParent(consumer);
      }
    }
  }

  /**
   * Checks if is root.
   *
   * @return true, if is root
   */
  public boolean isRoot() {
    return chunk == null;
  }

  /**
   * Gets the chunk.
   *
   * @return the chunk
   */
  public PhraseChunk getChunk() {
    return chunk;
  }

  /**
   * Sets the parent.
   *
   * @param parent the new parent
   */
  public void setParent(ParseTreeNode parent) {
    this.parent = parent;
  }

  /**
   * Gets the children.
   *
   * @return the children
   */
  public List<ParseTreeNode> getChildren() {
    return children;
  }

  /**
   * Checks for children.
   *
   * @return true, if successful
   */
  public boolean hasChildren() {
    return !children.isEmpty();
  }

  /**
   * Gets the parent.
   *
   * @return the parent
   */
  public ParseTreeNode getParent() {
    return parent;
  }

  /**
   * Adds a child.
   *
   * @param child the child
   */
  public void addChild(ParseTreeNode child) {
    children.add(child);
    child.setParent(this);
  }

  /**
   * Adds all children.
   *
   * @param children the children
   */
  private void addAllChildren(List<ParseTreeNode> children) {
    children.forEach(this::addChild);
  }

  /**
   * Adds the words which are at this node (not its children)
   *
   * @param word the word
   */
  public void addWords(Collection<WordToken> word) {
    if (word != null) {
      words.addAll(word);
    }
  }

  /**
   * Gets the words.
   *
   * @return the words
   */
  public List<WordToken> getWords() {
    return words;
  }

  @Override
  public String toString() {
    if (chunk != null) {
      return String.format("%s [%s]", chunk.getCoveredText(), chunk.getChunkType());
    } else {
      return "No chunk";
    }
  }

  /**
   * Contains word.
   *
   * @param filter the filter
   * @return true, if successful
   */
  public boolean containsWord(Predicate<WordToken> filter) {

    if (words != null && !words.isEmpty()) {

      final boolean result = words.stream().anyMatch(filter);
      if (result) {
        return true;
      }
    }

    if (children != null) {
      return children.stream().anyMatch(c -> c.containsWord(filter));
    }
    return false;
  }

  /**
   * Log the tree in an indent (pretty) fashion
   *
   * @param indent the indent
   */
  public void log(String indent) {
    String wordString = "";
    if (words != null) {
      wordString =
          getWords().stream().map(WordToken::getCoveredText).collect(Collectors.joining(","));
    }
    String chunkString = "";
    if (chunk != null) {
      chunkString = chunk.getChunkType();
    }
    String parentString = "";
    if (parent != null) {
      parentString = getParent().toString();
    }

    LOGGER.info("{}{}: {}: {}", indent, chunkString, wordString, parentString);
    if (children != null) {
      String childIndent = "\t" + indent;
      for (ParseTreeNode c : children) {
        c.log(childIndent);
      }
    }
  }
}
