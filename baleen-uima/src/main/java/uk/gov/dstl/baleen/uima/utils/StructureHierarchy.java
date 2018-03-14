// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils;

import static uk.gov.dstl.baleen.uima.utils.StructureUtil.filterAnnotations;

import java.util.List;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.uima.utils.select.ItemHierarchy;
import uk.gov.dstl.baleen.uima.utils.select.Node;

/**
 * A hierarchy within the document of the structure annotations.
 *
 * <p>The hierarchy is build using offset and depth.
 */
public class StructureHierarchy extends ItemHierarchy<Structure> {

  protected StructureHierarchy(Node<Structure> root) {
    super(root);
  }

  /**
   * Build the structure hierarchy for the given jCas, using only the structural classes provided.
   *
   * <p>The structure is built by first using the offset of the Structure annotation and then using
   * the depth.
   *
   * @param jCas the jCas
   * @param types the structural classes
   * @return the Structure base AnnotationHierachy
   */
  public static StructureHierarchy build(JCas jCas, Set<Class<? extends Structure>> types) {
    final List<Structure> structures =
        filterAnnotations(JCasUtil.select(jCas, Structure.class), types);
    return new StructureHierarchy(AnnotationHierarchyBuilder.build(structures));
  }
}
