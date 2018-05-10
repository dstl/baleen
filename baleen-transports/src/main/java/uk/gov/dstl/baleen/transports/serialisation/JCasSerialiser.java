// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.serialisation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.DocumentAnnotation;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import uk.gov.dstl.baleen.types.BaleenAnnotation;
import uk.gov.dstl.baleen.uima.UimaMonitor;
import uk.gov.dstl.baleen.uima.UimaSupport;
import uk.gov.dstl.baleen.uima.utils.FeatureUtils;

/**
 * This class is responsible for serialisation of the JCas to a JSON Map.
 *
 * <p>For full string serialisation use {@link JsonJCasConverter}
 */
public class JCasSerialiser {

  private final Collection<Class<? extends BaleenAnnotation>> whiteList;

  private final Collection<Class<? extends BaleenAnnotation>> blackList;

  private final UimaMonitor monitor;

  private final Set<String> stopFeatures;

  /**
   * Construct a JCasSerialiser using the given {@link UimaSupport}, {@link UimaMonitor} and,
   * optional, white and black lists to filter by.
   *
   * <p>NB: a null or empty filter list implies no filtering.
   *
   * @param monitor the {@link UimaMonitor} to use
   * @param whiteList given annotation classes (optional)
   * @param blackList given annotation classes (optional)
   */
  public JCasSerialiser(
      final UimaMonitor monitor,
      final Collection<Class<? extends BaleenAnnotation>> whiteList,
      final Collection<Class<? extends BaleenAnnotation>> blackList) {
    this.monitor = monitor;
    this.whiteList = whiteList;
    this.blackList = blackList;

    // Features to ignore
    stopFeatures = new HashSet<>();
    stopFeatures.add("uima.cas.AnnotationBase:sofa");
  }

  private UimaMonitor getMonitor() {
    return monitor;
  }

  /**
   * Serialise the JCas to a JSON map
   *
   * @param jCas to serialise
   * @return a JSON map representation
   * @throws IOException if the serialisation cannot be performed
   */
  public Map<String, Object> serialise(final JCas jCas) {
    final Map<String, Object> output = new HashMap<>();
    final DocumentAnnotation da = UimaSupport.getDocumentAnnotation(jCas);

    // Content and language
    output.put(JsonJCas.DOCUMENT_TEXT, jCas.getDocumentText());

    if (!Strings.isNullOrEmpty(jCas.getDocumentLanguage())) {
      output.put(JsonJCas.DOCUMENT_LANGUAGE, jCas.getDocumentLanguage());
    }

    // Document Annotation
    final Map<String, Object> documentAnnotation = serialiseDocumentAnnotation(da);
    output.put(JsonJCas.DOCUMENT_ANNOTATION, documentAnnotation);

    // Output all annotations
    final List<Map<String, Object>> annotationList = serialiseAnnotations(jCas);
    output.put(JsonJCas.ANNOTATIONS, annotationList);

    return output;
  }

  private Map<String, Object> serialiseDocumentAnnotation(final DocumentAnnotation da) {
    final Map<String, Object> map = new HashMap<>();

    map.put(JsonJCas.DA_DOCUMENT_TYPE, da.getDocType());
    map.put(JsonJCas.DA_LANGUAGE, da.getLanguage());

    map.put(JsonJCas.DA_SOURCE_URI, da.getSourceUri());
    map.put(JsonJCas.DA_CLASSIFICATION, da.getDocumentClassification());
    final String[] caveats =
        da.getDocumentCaveats() != null ? da.getDocumentCaveats().toArray() : new String[0];
    map.put(JsonJCas.DA_CAVEATS, caveats);
    final String[] rels =
        da.getDocumentReleasability() != null
            ? da.getDocumentReleasability().toArray()
            : new String[0];
    map.put(JsonJCas.DA_RELEASABILITY, rels);

    return map;
  }

  private List<Map<String, Object>> serialiseAnnotations(final JCas jCas) {
    final Collection<BaleenAnnotation> baleenAnnotations =
        new ArrayList<>(JCasUtil.select(jCas, BaleenAnnotation.class));

    filterByWhiteList(baleenAnnotations);
    filterByBlackList(baleenAnnotations);

    final List<Map<String, Object>> annotationList = new ArrayList<>(baleenAnnotations.size());
    for (final BaleenAnnotation a : baleenAnnotations) {
      annotationList.add(convertBaleenAnnotation(a));
    }

    return annotationList;
  }

  /**
   * Convert from an BaleenAnnotation to a map.
   *
   * @param entity the entity to convert
   * @return a map containing the entity's fields (and history is required)
   */
  private Map<String, Object> convertBaleenAnnotation(final BaleenAnnotation annotation) {
    final Map<String, Object> map = Maps.newHashMap();
    convertFeatures(map, annotation);
    return map;
  }

  private void filterByBlackList(final Collection<BaleenAnnotation> baleenAnnotations) {

    if (blackList == null || blackList.isEmpty()) {
      return;
    }

    final Iterator<BaleenAnnotation> iterator = baleenAnnotations.iterator();
    while (iterator.hasNext()) {
      final BaleenAnnotation next = iterator.next();

      for (final Class<? extends BaleenAnnotation> clazz : blackList) {
        if (isSubClass(clazz, next)) {
          iterator.remove();
        }
      }
    }
  }

  private void filterByWhiteList(final Collection<BaleenAnnotation> baleenAnnotations) {
    if (whiteList == null || whiteList.isEmpty()) {
      return;
    }

    final Iterator<BaleenAnnotation> iterator = baleenAnnotations.iterator();
    while (iterator.hasNext()) {
      final BaleenAnnotation next = iterator.next();

      for (final Class<? extends BaleenAnnotation> clazz : whiteList) {
        if (!isSubClass(clazz, next)) {
          iterator.remove();
        }
      }
    }
  }

  private boolean isSubClass(
      final Class<? extends BaleenAnnotation> clazz, final BaleenAnnotation next) {
    return clazz.isInstance(next);
  }

  private void convertFeatures(final Map<String, Object> map, final BaleenAnnotation base) {
    for (final Feature f : base.getType().getFeatures()) {
      if (stopFeatures.contains(f.getName())) {
        continue;
      }

      try {
        convertFeature(map, base, f);
      } catch (final Exception e) {
        getMonitor()
            .warn(
                "Couldn't output {} to map. Type '{}' isn't supported.",
                f.getName(),
                f.getRange().getShortName(),
                e);
      }
    }
    map.put(JsonJCas.ANNOTATION_TYPE, base.getType().getName());
    map.put(JsonJCas.ANNOTATION_CLASS, base.getClass().getName());

    // We don't output covered text... as we can't set it.
  }

  private void convertFeature(
      final Map<String, Object> map, final BaleenAnnotation base, final Feature f) {
    final String name = JsonJCas.normalizeFeatureName(f);
    if (f.getRange().isPrimitive()) {
      getMonitor().trace("Converting primitive feature to an object");
      map.put(name, FeatureUtils.featureToObject(f, base));

    } else if (f.getRange().isArray()
        && f.getRange().getComponentType() != null
        && f.getRange().getComponentType().isPrimitive()) {
      getMonitor().trace("Converting primitive feature to an array");
      map.put(name, FeatureUtils.featureToList(f, base));
    } else {

      // If we have a non-primitive then
      getMonitor()
          .trace(
              "Feature is not a primitive type / array of primitives - will try to treat the feature as an annotation");
      if (f.getRange().isArray()) {
        getMonitor()
            .trace("Feature is an array - attempting converstion to an array of annotations");
        final FSArray fArr = (FSArray) base.getFeatureValue(f);
        if (fArr != null) {
          map.put(JsonJCas.makeReference(name), getInternalIds(fArr));
        }
      } else {
        getMonitor().trace("Feature is singular - attempting conversion to a single annotation");
        final FeatureStructure ent = base.getFeatureValue(f);
        if (ent == null) {
          // Ignore null entities
        } else if (ent instanceof BaleenAnnotation) {
          map.put(JsonJCas.makeReference(name), ((BaleenAnnotation) ent).getInternalId());
        } else {
          getMonitor().trace("Unable to persist feature {}", name);
        }
      }
    }
  }

  private List<Long> getInternalIds(final FSArray annotationArray) {
    final List<Long> entities = new ArrayList<>();

    for (int x = 0; x < annotationArray.size(); x++) {
      final FeatureStructure featureStructure = annotationArray.get(x);
      if (featureStructure != null && featureStructure instanceof BaleenAnnotation) {
        final BaleenAnnotation ent = (BaleenAnnotation) featureStructure;
        entities.add(ent.getInternalId());
      }
    }

    return entities;
  }
}
