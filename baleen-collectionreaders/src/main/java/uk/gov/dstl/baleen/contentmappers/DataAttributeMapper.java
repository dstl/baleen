// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmappers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.contentmanipulators.ParagraphMarkedClassification;
import uk.gov.dstl.baleen.contentmanipulators.helpers.MarkupUtils;
import uk.gov.dstl.baleen.contentmappers.helpers.AnnotationCollector;
import uk.gov.dstl.baleen.contentmappers.helpers.ContentMapper;

/**
 * Create Baleen types based on the data attributes of tags.
 *
 * <p>This will create both the annotations, and set any values on the annotation based on the tags
 * data-baleen- tags on the element.
 *
 * <p>Use MarkupUtils.additionallyAnnotateAsType and MarkupUtils.add/setAttribute within {@link
 * MarkupUtils} in order to pass information to this mapper.
 *
 * <p>Note the full Java type should be passed to additionallyAnnotateAsType. The attributes should
 * have the same key as the Java bean fields would be (eg if the UIMA value setter is
 * 'setSomething', the attribute key is 'something'). Only the first value is considered, and only
 * simple types (boolean, int, double, string) are used.
 *
 * <p>See {@link ParagraphMarkedClassification} for an example.
 *
 * <p>This is a useful helper tool for simple cases, but consider writing a full contentmapper to
 * deal with important tags, and depending on the complexity of the annotation introducing new
 * information carrying elements to the HTML DOM.
 */
public class DataAttributeMapper implements ContentMapper {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataAttributeMapper.class);

  @Override
  public void map(JCas jCas, Element element, AnnotationCollector collector) {
    Set<String> types = MarkupUtils.getTypes(element);

    for (String type : types) {
      try {

        // Look for the annotation type by reflection
        Object annotation =
            getClass()
                .getClassLoader()
                .loadClass(type)
                .getConstructor(JCas.class)
                .newInstance(jCas);

        if (!(annotation instanceof Annotation)) {
          LOGGER.error("Type is not an annotation {}, ignoring", type);
          continue;
        }

        // For each settings look if we have have an attribute defined on the element

        Arrays.stream(annotation.getClass().getMethods())
            .filter(
                m ->
                    m.getName().startsWith("set")
                        && m.getName().length() > 3
                        && m.getParameterCount() == 1)
            .forEach(m -> setMethodValue(type, element, annotation, m));

        // Add the annotation to the jCas
        collector.add((Annotation) annotation);

      } catch (Exception e) {
        LOGGER.error("Unable to create annotation of type {}", type, e);
      }
    }
  }

  /**
   * Sets the value of field by calling the method.
   *
   * @param type the type
   * @param element the element
   * @param annotation the annotation
   * @param m the method
   */
  private void setMethodValue(String type, Element element, Object annotation, Method m) {
    String fieldName = m.getName().substring("set".length());
    fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
    List<String> attributes = MarkupUtils.getAttributes(element, fieldName);
    if (attributes != null && !attributes.isEmpty()) {
      // We only try the first value... (even if it doesn't work)
      try {
        assignAttribute(annotation, m, attributes.get(0));
      } catch (Exception e) {
        LOGGER.warn(
            "Unable to set annotation values of type {} from attribute {}", type, fieldName, e);
      }
    }
  }

  /**
   * Convert an attribute value to something we can call on a method
   *
   * @param annotation
   * @param method to call
   * @param string
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  private void assignAttribute(Object annotation, Method m, String string)
      throws IllegalAccessException, InvocationTargetException {
    Class<?> parameterType = m.getParameterTypes()[0];

    // We only deal with the simple UIMA types
    // int, string, double, boolean
    if (parameterType.equals(int.class)) {
      int value = Integer.parseInt(string);
      m.invoke(annotation, value);

    } else if (parameterType.equals(double.class)) {
      double value = Double.parseDouble(string);
      m.invoke(annotation, value);
    } else if (parameterType.equals(boolean.class)) {
      boolean value = Boolean.parseBoolean(string);
      m.invoke(annotation, value);
    } else if (parameterType.equals(String.class)) {
      m.invoke(annotation, string);
    }
  }
}
