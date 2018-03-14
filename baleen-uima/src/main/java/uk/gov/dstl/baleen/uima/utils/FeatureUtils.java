// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CommonArrayFS;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation;
import org.slf4j.LoggerFactory;

/** Some commonly used functions to handle CAS Features */
public class FeatureUtils {

  private FeatureUtils() {
    // Singleton
  }

  /**
   * Convert a UIMA feature to a Java object of the correct type
   *
   * @param f UIMA CAS Feature
   * @param a UIMA CAS Annotation
   * @return Java object, or null if unable to convert
   */
  public static Object featureToObject(Feature f, Annotation a) {
    Object ret = null;

    switch (f.getRange().getName()) {
      case CAS.TYPE_NAME_STRING:
        ret = StringToObject.convertStringToObject(a.getStringValue(f));
        break;
      case CAS.TYPE_NAME_INTEGER:
        ret = a.getIntValue(f);
        break;
      case CAS.TYPE_NAME_FLOAT:
        ret = a.getFloatValue(f);
        break;
      case CAS.TYPE_NAME_BOOLEAN:
        ret = a.getBooleanValue(f);
        break;
      case CAS.TYPE_NAME_BYTE:
        ret = a.getByteValue(f);
        break;
      case CAS.TYPE_NAME_SHORT:
        ret = a.getShortValue(f);
        break;
      case CAS.TYPE_NAME_LONG:
        ret = a.getLongValue(f);
        break;
      case CAS.TYPE_NAME_DOUBLE:
        ret = a.getDoubleValue(f);
        break;
      default:
        ret = null;
    }

    return ret;
  }

  /**
   * Convert a UIMA feature array to an array of Java objects of the correct type, parsing Strings
   * to objects where possible
   *
   * @param f UIMA CAS Feature
   * @param a UIMA CAS Annotation
   * @return Array of Java objects, or null if unable to convert
   */
  public static Object[] featureToArray(Feature f, Annotation a) {
    Object[] ret;

    if (a.getFeatureValue(f) == null) {
      ret = new Object[0];
    } else {
      ret = toArray(f, a);
    }

    return ret;
  }

  /**
   * Convert a UIMA feature array to a List of Java objects of the correct type, parsing Strings to
   * objects where possible
   *
   * @param f UIMA CAS Feature
   * @param a UIMA CAS Annotation
   * @return List of Java objects, or empty if unable to convert
   */
  public static List<Object> featureToList(Feature f, Annotation a) {
    if (a.getFeatureValue(f) == null) {
      return Collections.emptyList();
    }

    return Arrays.asList(toArray(f, a));
  }

  private static Object[] toArray(Feature f, Annotation a) {
    Object[] ret;

    try {
      CommonArrayFS array = (CommonArrayFS) a.getFeatureValue(f);
      ret = new Object[array.size()];
      int index = 0;
      for (String s : array.toStringArray()) {
        ret[index] = StringToObject.convertStringToObject(s);
        index++;
      }

      return ret;
    } catch (ClassCastException cce) {
      LoggerFactory.getLogger(FeatureUtils.class)
          .debug("Couldn't cast feature value to array", cce);
      return new Object[0];
    }
  }
}
