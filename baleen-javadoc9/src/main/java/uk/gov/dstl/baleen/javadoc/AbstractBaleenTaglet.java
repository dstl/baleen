// NCA (c) Crown Copyright 2018
package uk.gov.dstl.baleen.javadoc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Taglet;

/**
 * An abstract class implementing common functionality between the different Baleen Javadoc taglets.
 */
public abstract class AbstractBaleenTaglet implements Taglet {

  protected Types typeUtils;
  protected Elements elementUtils;

  @Override
  public void init(DocletEnvironment env, Doclet doclet) {
    typeUtils = env.getTypeUtils();
    elementUtils = env.getElementUtils();
  }

  @Override
  public boolean isInlineTag() {
    return false;
  }

  /** Return a list of fields for this element, including fields inherited fields */
  protected List<VariableElement> getFields(Element element, String javadocTag) {
    List<VariableElement> fields = new ArrayList<>();

    if (!element.getKind().isClass()) return fields;

    TypeElement currElement = (TypeElement) element;
    while (currElement != null) {
      currElement
          .getEnclosedElements()
          .stream()
          .filter(e -> e.getKind().isField()) // Check that it's a field
          .filter(
              e -> { // Check that it has the correct Javadoc tag
                String javadoc = elementUtils.getDocComment(e);
                return javadoc != null && javadoc.contains("@" + javadocTag + " ");
              })
          .forEach(e -> fields.add((VariableElement) e));

      currElement = (TypeElement) typeUtils.asElement(currElement.getSuperclass());
    }

    return fields;
  }

  protected String getTagletValue(Element element, String javadocTag) {
    String javadoc = elementUtils.getDocComment(element);
    if (javadoc == null) return "";

    Pattern pTag = Pattern.compile("@" + Pattern.quote(javadocTag) + "\\s+([^\\r\\n]*)");
    Matcher m = pTag.matcher(javadoc);
    if (m.find()) {
      return m.group(1);
    }

    return "";
  }

  protected List<String> getTagletValues(Element element, String javadocTag) {
    List<String> values = new ArrayList<>();

    String javadoc = elementUtils.getDocComment(element);
    if (javadoc != null) {
      Pattern pTag = Pattern.compile("@" + Pattern.quote(javadocTag) + "\\s+([^\\r\\n]*)");
      Matcher m = pTag.matcher(javadoc);
      while (m.find()) {
        values.add(m.group(1));
      }
    }

    return values;
  }
}
