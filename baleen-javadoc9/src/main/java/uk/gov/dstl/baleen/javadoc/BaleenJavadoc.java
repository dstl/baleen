// NCA (c) Crown Copyright 2018
package uk.gov.dstl.baleen.javadoc;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeMap;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;

import org.apache.commons.lang.ObjectUtils;

import com.sun.source.doctree.DocTree;

/** Provide a class level summary of Baleen configuration parameters and external resources */
public class BaleenJavadoc extends AbstractBaleenTaglet {
  public static final String NAME = "baleen.javadoc";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Set<Location> getAllowedLocations() {
    return Set.of(Location.TYPE);
  }

  @Override
  public String toString(List<? extends DocTree> list, Element element) {
    if (list.isEmpty()) return null;

    return processExternalResources(element) + processConfigurationParameters(element);
  }

  protected String processConfigurationParameters(Element element) {
    StringBuilder cpText =
        new StringBuilder(
            "<dt><b>Configuration Parameters:</b></dt><dd><table><tr style=\"text-align: left\"><th>Parameter</th><th>Description</th><th>Default Value(s)</th></tr>");
    Map<String, String> rows =
        new TreeMap<>(); // Use a TreeMap to store the rows so they are sorted (by key)

    for (VariableElement field : getFields(element, ConfigurationParameters.NAME)) {
      Entry<String, String> entry = createParameterRow(field);
      if (entry != null) {
        rows.put(entry.getKey(), entry.getValue());
      }
    }

    if (rows.isEmpty()) {
      cpText.setLength(0);
    } else {
      for (String s : rows.values()) {
        cpText.append(s);
      }

      cpText.append("</table></dd>");
    }

    return cpText.toString();
  }

  protected String processExternalResources(Element element) {
    StringBuilder erText = new StringBuilder("<dt><b>External Resources:</b></dt><dd><ul>");

    List<String> resources = new ArrayList<>();
    for (VariableElement field : getFields(element, ExternalResources.NAME)) {
      resources.addAll(createResourceItem(field));
    }

    if (resources.isEmpty()) {
      return "";
    } else {
      for (String s : resources) {
        erText.append(wrapWithTag("li", s, null));
      }
    }

    erText.append("</ul></dd>");
    return erText.toString();
  }

  private Entry<String, String> createParameterRow(VariableElement field) {
    List<String> tagletValues = getTagletValues(field, ConfigurationParameters.NAME);
    if (tagletValues.isEmpty()) return null;

    String name = wrapWithTag("td", field.getConstantValue(), "padding-right: 20px");
    String desc =
        wrapWithTag(
            "td",
            removeJavadocAnnotations(elementUtils.getDocComment(field)),
            "padding-right: 20px");

    StringJoiner defaultValues = new StringJoiner("<br />");
    for (String value : tagletValues) {
      defaultValues.add(value);
    }
    String values = wrapWithTag("td", defaultValues.toString(), null);

    String row = wrapWithTag("tr", name + desc + values, null);

    return new AbstractMap.SimpleEntry<>(ObjectUtils.toString(field.getConstantValue()), row);
  }

  private List<String> createResourceItem(VariableElement field) {
    List<String> resources = getTagletValues(field, ExternalResources.NAME);

    if (resources.isEmpty()) {
      return Collections.emptyList();
    }

    List<String> ret = new ArrayList<>();

    String pkg = elementUtils.getPackageOf(field).getQualifiedName().toString();
    int levels = pkg.length() - pkg.replaceAll("\\.", "").length() + 1;

    StringBuilder linkLevels = new StringBuilder();
    for (int i = 0; i < levels; i++) {
      linkLevels.append("../");
    }

    for (String resource : resources) {
      ret.add(
          "<a href=\""
              + linkLevels.toString()
              + resource.replaceAll("\\.", "/")
              + ".html\">"
              + resource
              + "</a> (key = "
              + field.getConstantValue()
              + ")");
    }

    return ret;
  }

  protected static String wrapWithTag(String tag, Object content, String style) {
    return "<"
        + tag
        + (style != null ? " style=\"" + style + "\"" : "")
        + ">"
        + content
        + "</"
        + tag
        + ">";
  }

  protected static String removeJavadocAnnotations(String s) {
    return s.replaceAll("@baleen\\..*", "").replaceAll("\n{2,}", "\n").trim();
  }
}
