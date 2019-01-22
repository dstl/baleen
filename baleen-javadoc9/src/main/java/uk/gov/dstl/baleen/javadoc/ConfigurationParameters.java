// NCA (c) Crown Copyright 2018
package uk.gov.dstl.baleen.javadoc;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;

import com.sun.source.doctree.DocTree;

/** Adds information about configuration parameters to the Javadoc. */
public class ConfigurationParameters extends AbstractBaleenTaglet {
  public static final String NAME = "baleen.config";

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
    return list.isEmpty()
        ? null
        : "<p>This constant holds a configuration parameter name, which can be used to configure this Baleen component.</p>";
  }
}
