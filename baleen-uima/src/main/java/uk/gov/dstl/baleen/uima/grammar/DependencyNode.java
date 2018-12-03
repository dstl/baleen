// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.uima.grammar;

import org.apache.commons.lang.StringUtils;

import uk.gov.dstl.baleen.types.language.WordToken;

/** A node in a {@link DependencyTree} */
public class DependencyNode {

  private String id;

  private String type;

  private String content;

  /**
   * Construct a dependency node.
   *
   * @param id (optional) identifier for the node
   * @param type the part of speech for the node
   * @param content specification, (an optional regular expression to check matches against)
   */
  public DependencyNode(String id, String type, String content) {
    this.id = id;
    this.type = type;
    this.content = content;
  }

  /** @return the (optional) id */
  public String getId() {
    return id;
  }

  /**
   * Set the id of the given node.
   *
   * @param newId (can be null to clear)
   */
  void setId(String newId) {
    id = newId;
  }

  /** @return the type of this node */
  public String getType() {
    return type;
  }

  /**
   * @return the optional content definition (may be a regular expression) empty content matches any
   *     content.
   */
  String getContent() {
    return content;
  }

  /** Delexicalize the node, removing the content */
  public void delexicalize() {
    content = null;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (content != null) {
      builder.append(content);
    }
    builder.append("_");
    builder.append(type);
    if (id != null) {
      builder.append(":");
      builder.append(id);
    }
    return builder.toString();
  }

  /**
   * Create a node form a given text description using _ and : separators: e.g.
   *
   * <pre>
   * _NN
   * visit.*_VB
   * example_NN:myid
   * </pre>
   *
   * @param code to parse
   * @return the constructed {@link DependencyNode}
   */
  public static DependencyNode create(String code) {
    int splitPoint = code.lastIndexOf('_');
    String content = code.substring(0, splitPoint);
    String rest = code.substring(splitPoint + 1);
    int idPoint = rest.lastIndexOf(':');
    String type;
    String id;
    if (idPoint > -1) {
      type = rest.substring(0, idPoint);
      id = rest.substring(idPoint + 1);
    } else {
      type = rest;
      id = null;
    }
    return new DependencyNode(id, type, content);
  }

  /**
   * Check if the given word token satisfies the constrains of this dependency node.
   *
   * <p>To match, the word token must have the same root part of speech (eg NN will also match NNP,
   * NNS etc.) and the covered text must satisfy the content regular expression, if defined.
   *
   * @param wt
   * @return true if this matches
   */
  public boolean matches(WordToken wt) {
    return wt.getPartOfSpeech().startsWith(getType())
        && (StringUtils.isEmpty(getContent()) || wt.getCoveredText().matches(getContent()));
  }

  /**
   * Check if the node matches this dependency node.
   *
   * <p>To match, the word token must have the same root part of speech (eg NN will also match NNP,
   * NNS etc.) and the covered text must satisfy the content regular expression, if defined.
   *
   * @param wt
   * @return true if this matches
   */
  public boolean matches(DependencyNode other) {
    if (this == other) {
      return true;
    }
    if (other == null) {
      return false;
    }
    if (getType() == null) {
      if (other.getType() != null) {
        return false;
      }
    } else if (!getType().equals(other.getType())) {
      return false;
    }
    if (StringUtils.isEmpty(getContent())) {
      if (!StringUtils.isEmpty(other.getContent())) {
        return false;
      }
    } else if (!getContent().equals(other.getContent())) {
      return false;
    }
    return true;
  }
}
