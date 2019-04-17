// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils.select;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.Validate;

import uk.gov.dstl.baleen.uima.utils.SelectorPart;

/** Abstract node implementation */
public abstract class AbstractNode<T> implements Node<T> {

  /** The item */
  protected final T item;

  /** the parent of this node */
  private final AbstractNode<T> parent;

  /** the children of this node */
  private final List<AbstractNode<T>> children = new ArrayList<>();

  /**
   * Constructor for the Structure Node.
   *
   * @param parent the parent node
   * @param annotation the structure
   */
  protected AbstractNode(AbstractNode<T> parent, T annotation) {
    this.parent = parent;
    this.item = annotation;
  }

  /**
   * Add child node, to be used during construction of the hierarchy
   *
   * @param node the child node
   */
  protected void addChild(AbstractNode<T> node) {
    children.add(node);
  }

  @Override
  public Nodes<T> getChildren() {
    return new Nodes<>(children);
  }

  @Override
  public Nodes<T> getChildren(Class<?> type) {
    return new Nodes<>(children.stream().filter(c -> type.equals(c.getType())).collect(toList()));
  }

  @Override
  public T getItem() {
    return item;
  }

  @Override
  public AbstractNode<T> getParent() {
    return parent;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Class<? extends T> getType() {
    if (item == null) {
      return null;
    } else {
      return (Class<? extends T>) item.getClass();
    }
  }

  @Override
  public String getTypeName() {
    if (item == null) {
      return "Root";
    } else {
      return getType().getSimpleName();
    }
  }

  @Override
  public List<SelectorPart> toPath() {
    LinkedList<SelectorPart> path = new LinkedList<>();
    toPath(path, Object.class);
    return path;
  }

  @Override
  public List<SelectorPart> toPath(Class<? extends T> type) {
    LinkedList<SelectorPart> path = new LinkedList<>();
    toPath(path, type);
    return path;
  }

  /**
   * Internal method to build the path of the annotation
   *
   * @param path
   */
  private void toPath(LinkedList<SelectorPart> path, Class<?> filter) {
    if (item != null) {
      int index = getTypeIndex() + 1;
      if (filter.isInstance(item)) {
        path.addFirst(new SelectorPart(item.getClass(), index));
      }
      if (hasParent()) {
        parent.toPath(path, filter);
      }
    }
  }

  @Override
  public int getTypeIndex() {
    if (!hasParent()) {
      return 0;
    }
    Nodes<T> siblings = parent.getChildren(getType());
    return siblings.indexOf(this);
  }

  @Override
  public int getSiblingIndex() {
    if (!hasParent()) {
      return 0;
    }
    Nodes<T> siblings = parent.getChildren();
    return siblings.indexOf(this);
  }

  @Override
  public boolean hasParent() {
    return parent != null;
  }

  @Override
  public String toString() {
    return "<" + getTypeName() + ">" + text();
  }

  @Override
  public Nodes<T> select(String query) {
    return Selector.select(query, this);
  }

  @Override
  public String attr(String attributeKey) {
    return attributes().getOrDefault(attributeKey, "");
  }

  @Override
  public boolean hasText() {
    return !text().isEmpty();
  }

  @Override
  public boolean is(Evaluator<T> eval) {
    return eval.matches(this.root(), this);
  }

  /**
   * Get this node's root node; that is, its topmost ancestor. If this node is the top ancestor,
   * returns {@code this}.
   *
   * @return topmost ancestor.
   */
  private AbstractNode<T> root() {
    AbstractNode<T> node = this;
    while (node.parent != null) {
      node = node.parent;
    }
    return node;
  }

  @Override
  public AbstractNode<T> nextSibling() {
    if (parent == null) {
      return null;
    }
    Nodes<T> siblings = parent.getChildren();
    Integer index = indexInList(this, siblings);
    Validate.notNull(index);
    if (siblings.size() > index + 1) {
      return (AbstractNode<T>) siblings.get(index + 1);
    } else {
      return null;
    }
  }

  @Override
  public AbstractNode<T> previousSibling() {
    if (parent == null) {
      return null;
    }
    Nodes<T> siblings = parent.getChildren();
    Integer index = indexInList(this, siblings);
    Validate.notNull(index);
    if (index > 0) {
      return (AbstractNode<T>) siblings.get(index - 1);
    } else {
      return null;
    }
  }

  @Override
  public Nodes<T> getParents() {
    Nodes<T> parents = new Nodes<>();
    AbstractNode<T> node = this;
    while (node.hasParent()) {
      node = node.getParent();
      parents.add(node);
    }
    return parents;
  }

  @Override
  public boolean hasAttr(String attributeKey) {
    return attributes().containsKey(attributeKey.toLowerCase());
  }

  @Override
  public Nodes<T> getSiblings() {
    if (parent == null) {
      return new Nodes<>(0);
    }

    Nodes<T> elements = parent.getChildren();
    Nodes<T> siblings = new Nodes<>(elements.size() - 1);
    for (Node<T> el : elements) {
      if (el != this) {
        siblings.add(el);
      }
    }
    return siblings;
  }

  /**
   * Get the index of the search node in the given list.
   *
   * <p>Search by identity
   *
   * @param search the node to search for
   * @param nodes the node to search in
   * @return the index
   */
  private Integer indexInList(Node<T> search, List<? extends Node<T>> nodes) {
    Validate.notNull(search);
    Validate.notNull(nodes);

    for (int i = 0; i < nodes.size(); i++) {
      Node<T> node = nodes.get(i);
      if (node == search) {
        return i;
      }
    }
    return null;
  }

  @Override
  public Nodes<T> getAllNodes() {
    return Collector.collect(new Evaluator.AllNodes<>(), this);
  }

  @Override
  public boolean hasClass(String className) {
    return getClasses().contains(className.toLowerCase());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (item == null ? 0 : item.hashCode());
    return result;
  }

  @Override
  @SuppressWarnings("rawtypes")
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
    AbstractNode<?> other = (AbstractNode) obj;
    if (item == null) {
      if (other.item != null) {
        return false;
      }
    } else if (!item.equals(other.item)) {
      return false;
    }
    return true;
  }
}
