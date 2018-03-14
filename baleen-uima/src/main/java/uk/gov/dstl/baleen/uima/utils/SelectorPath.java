// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Joiner;

import uk.gov.dstl.baleen.core.utils.BuilderUtils;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.types.structure.Structure;

/**
 * A selector path describes a location in a structure hierarchy.
 *
 * <p>Based on a CSS selector-like syntax for selecting structural annotations.
 *
 * <p>Currently only the (<code>&gt;</code> operator) is supported, and the optional <code>
 * nth-of-type(n)</code> pseudo selector. It is required that structural annotations have a <code>
 * depth</code> feature such that nesting can be approximated.
 *
 * <p>Example selectors:
 *
 * <ul>
 *   <li><code>Section > Heading</code>
 *   <li><code>Heading:nth-of-type(2)</code>
 *   <li><code>Section:nth-of-type(1) &gt; Paragraph</code>
 *   <li><code>
 *       Table:nth-of-type(2) &gt; TableBody &gt; TableRow:nth-of-type(3) &gt; TableCell:nth-of-type(2) &gt; Paragraph:nth-of-type(1)
 *       </code>
 * </ul>
 */
public class SelectorPath {

  /** The separator of path elements */
  private static final String SEPARATOR = " > ";

  /** The joiner for to string */
  private static final Joiner JOINER = Joiner.on(SEPARATOR);

  /** The path of selector parts */
  private final List<SelectorPart> path;

  /**
   * Constructor for a selector path
   *
   * @param path the path
   */
  public SelectorPath(List<SelectorPart> path) {
    this.path = path;
  }

  /**
   * Get the path as a list of selector parts
   *
   * @return the path
   */
  public List<SelectorPart> getParts() {
    return path;
  }

  /**
   * Trim the path to the depth required
   *
   * @param depth the depth
   * @return the path up to the depth given, if the path is shorter the whole path is returned
   */
  public SelectorPath toDepth(int depth) {
    return new SelectorPath(path.subList(0, Math.min(depth, path.size())));
  }

  /**
   * Get the remaining path after the given path to get this path
   *
   * @param from
   * @return the remaining path having remove the matching elements from the given path
   */
  public SelectorPath from(SelectorPath from) {
    int i = 0;
    List<SelectorPart> remaining = new ArrayList<>(path);
    while (!remaining.isEmpty()
        && from.getDepth() > i
        && from.path.get(i).equals(remaining.get(0))) {
      i++;
      remaining.remove(0);
    }
    return new SelectorPath(remaining);
  }

  /**
   * Get the depth of this path
   *
   * @return the depth of the path
   */
  public int getDepth() {
    return path.size();
  }

  /**
   * Test if this path contains any parts
   *
   * @return true if this path is empty
   */
  public boolean isEmpty() {
    return path.isEmpty();
  }

  /**
   * Get the part of this path for the supplied index
   *
   * @param i the index
   * @return the part at the given index
   */
  public SelectorPart get(int i) {
    return path.get(i);
  }

  /**
   * Step one part of the path
   *
   * @return new path of remaining parts
   */
  public SelectorPath step() {
    return new SelectorPath(path.subList(Math.min(getDepth(), 1), getDepth()));
  }

  @Override
  public String toString() {
    return JOINER.join(path);
  }

  /**
   * Parses the selector string and returns an ordered List of {@link SelectorPart} objects that
   * represent each clause between <code>&gt;<code> symbols.
   *
   * @param value the value
   * @return the list
   * @throws InvalidParameterException the invalid parameter exception
   */
  public static SelectorPath parse(String value) throws InvalidParameterException {
    List<SelectorPart> selectorParts = new ArrayList<>();
    if (value == null) {
      return new SelectorPath(selectorParts);
    }

    String[] parts = value.split("\\s*>\\s*");
    for (String part : parts) {
      if (StringUtils.isNotEmpty(part)) {
        int colon = part.indexOf(':');
        if (colon != -1) {
          String[] typeAndQualifier = part.split(":");
          selectorParts.add(new SelectorPart(getType(typeAndQualifier[0]), typeAndQualifier[1]));
        } else {
          selectorParts.add(new SelectorPart(getType(part)));
        }
      }
    }
    return new SelectorPath(selectorParts);
  }

  /**
   * Gets the {@link Structure} type for the given type name, within the given packages.
   *
   * @param typeName the type name
   * @param packages the packages
   * @return the type
   * @throws InvalidParameterException the invalid parameter exception
   */
  private static Class<Structure> getType(String typeName) throws InvalidParameterException {
    return BuilderUtils.getClassFromString(typeName, Structure.class.getPackage().getName());
  }
}
