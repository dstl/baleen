// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.jcas.tcas.Annotation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.uima.utils.select.AbstractNode;
import uk.gov.dstl.baleen.uima.utils.select.Node;

/**
 * A node in the structure hierarchy of the document
 *
 * <p>All nodes except the root node contain a structure annotation and gives access to the parent
 * and child structure nodes.
 */
public class AnnotationNode<T extends Annotation> extends AbstractNode<T> {

  private static final Set<String> nonAttributes =
      ImmutableSet.of("getElementClass", "getElementId", "getTypeIndexID");

  /** lazily created attribute map */
  private Map<String, String> attributes;

  /**
   * Constructor for the Structure Node.
   *
   * @param parent the parent node
   * @param annotation the structure
   */
  protected AnnotationNode(AnnotationNode<T> parent, T annotation) {
    super(parent, annotation);
  }

  /**
   * Add child node, to be used during construction of the hierarchy
   *
   * @param node the child node
   */
  protected void addChild(AnnotationNode<T> node) {
    super.addChild(node);
  }

  @Override
  public String getTypeName() {
    if (getItem() == null) {
      return "Root";
    } else {
      return getItem().getType().getShortName();
    }
  }

  @Override
  public String text() {
    if (getItem() == null) {
      return "";
    } else {
      return getItem().getCoveredText();
    }
  }

  @Override
  public Map<String, String> attributes() {
    if (attributes != null) {
      return attributes;
    }

    attributes = new HashMap<>();
    T annotation = getItem();
    if (annotation != null) {

      if (annotation instanceof Structure) {
        addAttribute(attributes, "class", ((Structure) annotation).getElementClass());
        addAttribute(attributes, "id", ((Structure) annotation).getElementId());
      }

      for (Method method : annotation.getClass().getDeclaredMethods()) {
        String name = method.getName();
        if (name.startsWith("get")
            && method.getParameterTypes().length == 0
            && !nonAttributes.contains(name)) {
          String key = name.substring(3).toLowerCase();
          try {
            addAttribute(attributes, key, method.invoke(annotation));
          } catch (IllegalAccessException
              | IllegalArgumentException
              | InvocationTargetException e) {
            throw new RuntimeException(e);
          }
        }
      }
    }
    return attributes;
  }

  /**
   * Add attribute to the map if not null
   *
   * @param attributes the attributes map
   * @param key the attribute key
   * @param value the value
   */
  private void addAttribute(Map<String, String> attributes, String key, Object value) {
    if (value != null) {
      attributes.put(key, value.toString());
    }
  }

  @Override
  public String ownText() {
    StringBuilder sb = new StringBuilder(text());
    List<Node<T>> reversed = ImmutableList.copyOf(getChildren()).reverse();
    for (Node<T> s : reversed) {
      sb.replace(getBegin(s) - getBegin(this), getEnd(s) - getBegin(this), "");
    }
    return sb.toString();
  }

  @Override
  public List<String> getClasses() {
    T annotation = getItem();
    if (annotation instanceof Structure) {
      String classes = ((Structure) annotation).getElementClass();
      if (StringUtils.isNotBlank(classes)) {
        return Arrays.asList(classes.split("\\s"))
            .stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());
      }
    }
    return ImmutableList.of();
  }

  @Override
  public String id() {
    T annotation = getItem();
    if (annotation instanceof Structure) {
      String id = ((Structure) annotation).getElementId();
      if (id != null) {
        return id;
      }
    }
    return "";
  }

  /**
   * Get the begin offset of the contained structure node (or 0).
   *
   * @return the begin
   */
  public static int getBegin(Node<? extends Annotation> node) {
    return node.getItem() == null ? 0 : node.getItem().getBegin();
  }

  /**
   * Get the end offset of the contained structure node (or int max).
   *
   * @return the end
   */
  public static int getEnd(Node<? extends Annotation> node) {
    return node.getItem() == null ? Integer.MAX_VALUE : node.getItem().getEnd();
  }
}
