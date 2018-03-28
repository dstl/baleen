// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import com.google.common.collect.Maps;

import uk.gov.dstl.baleen.types.BaleenAnnotation;
import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.uima.utils.select.Node;

/**
 * Create the Hierarchy within the document of all the Structure annotations.
 *
 * <p>The structure is built by first using the offset of the Structure annotation and then using
 * the depth.
 */
public class CoveringStructureHierarchy extends StructureHierarchy {

  /** the internal index of the covering of baleen annotations */
  private final Map<Annotation, Collection<Structure>> coveringIndex;

  /**
   * Private constructor accepting the root node.
   *
   * <p>Use {@link #build(JCas)} to create an instance
   *
   * @param root the root
   */
  private CoveringStructureHierarchy(
      Node<Structure> root, Map<Annotation, Collection<Structure>> coveringIndex) {
    super(root);
    this.coveringIndex = coveringIndex;
  }

  /**
   * Get the selector path of the structure annotation covering the given annotation
   *
   * @param annotation the annotation
   * @return the selector path in this hierarchy covering the given annotation
   */
  public SelectorPath generatePath(Annotation annotation) {
    Optional<Structure> covering = getCoveringStructure(annotation);
    Node<Structure> node = getRoot();
    if (covering.isPresent()) {
      node = getIndex().get(covering.get());
    }
    return new SelectorPath(node.toPath());
  }

  /**
   * Get the structure covering the given annotation
   *
   * @param annotation the annotation
   * @return optional containing the covering annotation
   */
  public Optional<Structure> getCoveringStructure(Annotation annotation) {
    return coveringIndex.get(annotation).stream().max(Comparator.comparingInt(Structure::getDepth));
  }

  /**
   * Build the covering structure hierarchy for the given jCas, using only the structural classes
   * provided.
   *
   * <p>The structure is built by first using the offset of the Structure annotation and then using
   * the depth.
   *
   * @param jCas the jCas
   * @param structuralClasses the structural classes
   * @return the StructureHierachy
   */
  public static CoveringStructureHierarchy build(
      JCas jCas, Set<Class<? extends Structure>> structuralClasses) {
    Node<Structure> root = StructureHierarchy.build(jCas, structuralClasses).getRoot();
    Map<Annotation, Collection<Structure>> covering = buildCovering(jCas, structuralClasses);
    return new CoveringStructureHierarchy(root, covering);
  }

  /**
   * Build the covering index of baleen annotations by structure annotations (lazily) filtered to
   * only the given structural classes
   *
   * @param jCas the jCas
   * @param structuralClasses the structural classes
   * @return the covering index
   */
  private static Map<Annotation, Collection<Structure>> buildCovering(
      JCas jCas, Set<Class<? extends Structure>> structuralClasses) {
    return Maps.transformValues(
        JCasUtil.indexCovering(jCas, BaleenAnnotation.class, Structure.class),
        s -> TypeUtils.filterAnnotations(s, structuralClasses));
  }
}
