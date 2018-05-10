// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.serialisation;

import java.util.Collection;
import java.util.Collections;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CommonArrayFS;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.BooleanArray;
import org.apache.uima.jcas.cas.ByteArray;
import org.apache.uima.jcas.cas.DoubleArray;
import org.apache.uima.jcas.cas.FloatArray;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.LongArray;
import org.apache.uima.jcas.cas.ShortArray;
import org.apache.uima.jcas.cas.StringArray;

import uk.gov.dstl.baleen.types.BaleenAnnotation;

/** A utility class for the creation of new Features during deserialisation. */
// TODO Roll into FeatureUtils
public class NewFeatureUtils {

  private NewFeatureUtils() {
    // PRIVATE CONSTRUCTOR
  }

  /**
   * Set the primitve value for the given feature on the given annotation
   *
   * @param annotation to add the feature
   * @param feature to add
   * @param value to add
   */
  public static void setPrimitive(
      final BaleenAnnotation annotation, final Feature feature, final Object value) {

    // Nothing to do
    if (value == null) {
      return;
    }

    // We could do more type conversion here (got double -> want integer)
    // but fail fast is better - this is a common / standard type system after all.

    switch (feature.getRange().getName()) {
      case CAS.TYPE_NAME_STRING:
        annotation.setStringValue(feature, value.toString()); // Can be stored as different type
        break;
      case CAS.TYPE_NAME_INTEGER:
        annotation.setIntValue(feature, ((Number) value).intValue());
        break;
      case CAS.TYPE_NAME_FLOAT:
        annotation.setFloatValue(feature, ((Number) value).floatValue());
        break;
      case CAS.TYPE_NAME_BOOLEAN:
        annotation.setBooleanValue(feature, (boolean) value);
        break;
      case CAS.TYPE_NAME_BYTE:
        annotation.setByteValue(feature, (byte) value);
        break;
      case CAS.TYPE_NAME_SHORT:
        annotation.setShortValue(feature, ((Number) value).shortValue());
        break;
      case CAS.TYPE_NAME_LONG:
        annotation.setLongValue(feature, ((Number) value).longValue());
        break;
      case CAS.TYPE_NAME_DOUBLE:
        annotation.setDoubleValue(feature, ((Number) value).doubleValue());
        break;
      default:
        break;
    }
  }

  /**
   * Set the array value for the given feature on the given annotation
   *
   * @param annotation to add the feature
   * @param feature to add
   * @param value to add
   */
  public static void setPrimitiveArray(
      final JCas jCas,
      final BaleenAnnotation annotation,
      final Feature feature,
      final Object value) {
    final Type componentType = feature.getRange().getComponentType();

    Collection<?> list;
    if (value instanceof Collection) {
      list = (Collection<?>) value;
    } else {
      list = Collections.singletonList(value);
    }

    CommonArrayFS fs = getCommonArrayFS(jCas, componentType, list);

    if (fs != null) {
      annotation.setFeatureValue(feature, fs);
    }
  }

  private static CommonArrayFS getCommonArrayFS(JCas jCas, Type componentType, Collection<?> list) {
    CommonArrayFS fs = null;

    switch (componentType.getName()) {
      case CAS.TYPE_NAME_STRING:
        fs = getStringArray(jCas, list);
        break;
      case CAS.TYPE_NAME_INTEGER:
        fs = getIntegerArray(jCas, list);
        break;
      case CAS.TYPE_NAME_FLOAT:
        fs = getFloatArray(jCas, list);
        break;
      case CAS.TYPE_NAME_BOOLEAN:
        fs = getBooleanArray(jCas, list);
        break;
      case CAS.TYPE_NAME_BYTE:
        fs = getByteArray(jCas, list);
        break;
      case CAS.TYPE_NAME_SHORT:
        fs = getShortArray(jCas, list);
        break;
      case CAS.TYPE_NAME_LONG:
        fs = getLongArray(jCas, list);
        break;
      case CAS.TYPE_NAME_DOUBLE:
        fs = getDoubleArray(jCas, list);
        break;
      default:
        break;
    }
    return fs;
  }

  private static DoubleArray getDoubleArray(JCas jCas, Collection<?> list) {
    final DoubleArray a = new DoubleArray(jCas, list.size());
    int i = 0;
    for (final Object s : list) {
      a.set(i++, ((Number) s).doubleValue());
    }
    return a;
  }

  private static LongArray getLongArray(JCas jCas, Collection<?> list) {
    final LongArray a = new LongArray(jCas, list.size());
    int i = 0;
    for (final Object s : list) {
      a.set(i++, ((Number) s).longValue());
    }
    return a;
  }

  private static ShortArray getShortArray(JCas jCas, Collection<?> list) {
    final ShortArray a = new ShortArray(jCas, list.size());
    int i = 0;
    for (final Object s : list) {
      a.set(i++, ((Number) s).shortValue());
    }
    return a;
  }

  private static ByteArray getByteArray(JCas jCas, Collection<?> list) {
    final ByteArray a = new ByteArray(jCas, list.size());
    int i = 0;
    for (final Object s : list) {
      a.set(i++, (Byte) s);
    }
    return a;
  }

  private static BooleanArray getBooleanArray(JCas jCas, Collection<?> list) {
    final BooleanArray a = new BooleanArray(jCas, list.size());
    int i = 0;
    for (final Object s : list) {
      a.set(i++, (Boolean) s);
    }
    return a;
  }

  private static FloatArray getFloatArray(JCas jCas, Collection<?> list) {
    final FloatArray a = new FloatArray(jCas, list.size());
    int i = 0;
    for (final Object s : list) {
      a.set(i++, ((Number) s).floatValue());
    }
    return a;
  }

  private static IntegerArray getIntegerArray(JCas jCas, Collection<?> list) {
    final IntegerArray a = new IntegerArray(jCas, list.size());
    int i = 0;
    for (final Object s : list) {
      a.set(i++, ((Number) s).intValue());
    }
    return a;
  }

  private static StringArray getStringArray(JCas jCas, Collection<?> list) {
    final StringArray a = new StringArray(jCas, list.size());
    int i = 0;
    for (final Object s : list) {
      a.set(i++, s.toString());
    }
    return a;
  }
}
