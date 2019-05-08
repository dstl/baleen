// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils;

import java.util.*;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import uk.gov.dstl.baleen.types.BaleenAnnotation;
import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.uima.utils.select.ItemHierarchy;
import uk.gov.dstl.baleen.uima.utils.select.Node;

/**
 * A hierarchy builder for annotations within the document.
 *
 * <p>The hierarchy is build using the offset order and depth of structure annotations. Non
 * structure annotations are given maximum depth.
 *
 * @see Structure
 */
public class AnnotationHierarchyBuilder {

  private AnnotationHierarchyBuilder() {}

  /**
   * Build the hierarchy for the given jCas, using the types provided.
   *
   * <p>The structure is built by first using the offset of all annotations and then the depth of
   * the Structure annotations, non structure annotations have maximum depth.
   *
   * @param jCas the jCas
   * @param annotationClasses the annotations classes to use
   * @return the Structure base AnnotationHierachy
   */
  public static <T extends Annotation> ItemHierarchy<T> build(
      JCas jCas, Set<Class<? extends T>> annotationClasses) {
    return new ItemHierarchy<>(buildRoot(jCas, annotationClasses));
  }

  /**
   * Build the structure hierarchy, by first collecting all the annotations.
   *
   * @param jCas the jCas
   * @param annotationTypes the annotations classes to use
   * @return the root structure node
   */
  protected static <T extends Annotation> Node<T> buildRoot(
      JCas jCas, Set<Class<? extends T>> annotationTypes) {
    return build(
        TypeUtils.filterAnnotations(
            JCasUtil.select(jCas, BaleenAnnotation.class), annotationTypes));
  }

  /**
   * Build the structure hierarchy, first by sorting the structure, by offset and depth, then using
   * a deque to recursively create the structure.
   *
   * @param annotations the list of all structural annotations
   * @return the root structure node
   */
  protected static <T extends Annotation> AnnotationNode<T> build(final List<T> annotations) {
    Collections.sort(
        annotations,
        (s1, s2) -> {
          int compare = Integer.compare(s1.getBegin(), s2.getBegin());
          if (compare == 0) {
            compare = Integer.compare(s2.getEnd(), s1.getEnd());
          }
          if (compare == 0) {
            compare = Integer.compare(getDepth(s1), getDepth(s2));
          }
          return compare;
        });

    final AnnotationNode<T> parent = new AnnotationNode<>(null, null);
    final Deque<AnnotationNode<T>> deque = new ArrayDeque<>();
    deque.push(parent);

    annotations.forEach(s -> build(deque, s));

    return parent;
  }

  private static int getDepth(Annotation s) {
    if (s instanceof Structure) {
      return ((Structure) s).getDepth();
    }
    return Integer.MAX_VALUE;
  }

  /**
   * Recursively build the structure hierarchy
   *
   * @param deque the deque of nodes
   * @param s the current annotation
   */
  private static <T extends Annotation> void build(Deque<AnnotationNode<T>> deque, T s) {
    final AnnotationNode<T> parent = getParent(deque, s);
    final AnnotationNode<T> node = new AnnotationNode<>(parent, s);
    parent.addChild(node);
    deque.push(node);
  }

  /**
   * Get the parent of the given element from the deque
   *
   * @param deque the deque
   * @param s the annotation
   * @return the parent annotation
   */
  private static <T extends Annotation> AnnotationNode<T> getParent(
      Deque<AnnotationNode<T>> deque, T s) {

    while (testNodePassed(deque.peek(), s)) {
      deque.pop();
    }
    return deque.peek();
  }

  private static <T extends Annotation> boolean testNodePassed(AnnotationNode<T> node, T s) {
    int begin = s.getBegin();
    int end = AnnotationNode.getEnd(node);
    return end < begin || end == begin && getDepth(node.getItem()) >= getDepth(s);
  }
}
