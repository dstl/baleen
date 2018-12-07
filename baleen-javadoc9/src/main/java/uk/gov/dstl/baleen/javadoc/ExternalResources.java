// NCA (c) Crown Copyright 2018
package uk.gov.dstl.baleen.javadoc;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;

import com.sun.source.doctree.DocTree;

/** Adds information about external resources to the Javadoc. */
public class ExternalResources extends AbstractBaleenTaglet {
  public static final String NAME = "baleen.resource";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Set<Location> getAllowedLocations() {
    return Set.of(Location.FIELD);
  }

  @Override
  public String toString(List<? extends DocTree> list, Element element) {
    return list.isEmpty() ? null : "<p>This constant holds an external resource key.</p>";
  }
}
