// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.serialisation;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.uima.cas.Feature;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.DocumentAnnotation;

import uk.gov.dstl.baleen.types.BaleenAnnotation;
import uk.gov.dstl.baleen.uima.UimaMonitor;
import uk.gov.dstl.baleen.uima.UimaSupport;
import uk.gov.dstl.baleen.uima.utils.UimaTypesUtils;

/**
 * This class is responsible for deserialisation of the JCas from a JSON Map
 *
 * <p>For full string deserialisation use {@link JsonJCasConverter}
 */
@SuppressWarnings("unchecked")
public class JCasDeserialiser {

  private final UimaMonitor monitor;
  private final Collection<Class<? extends BaleenAnnotation>> whiteList;
  private final Collection<Class<? extends BaleenAnnotation>> blackList;

  /**
   * Construct a JCasDeserialiser using the given {@link UimaSupport}, {@link UimaMonitor} and,
   * optional, white and black lists to filter by.
   *
   * <p>NB: a null or empty filter list implies no filtering.
   *
   * @param support the {@link UimaSupport} to use
   * @param monitor the {@link UimaMonitor} to use
   * @param whiteList given annotation classes (optional)
   * @param blackList given annotation classes (optional)
   */
  public JCasDeserialiser(
      final UimaMonitor monitor,
      final Collection<Class<? extends BaleenAnnotation>> whiteList,
      final Collection<Class<? extends BaleenAnnotation>> blackList) {
    this.monitor = monitor;
    this.whiteList = whiteList;
    this.blackList = blackList;
  }

  private UimaMonitor getMonitor() {
    return monitor;
  }

  /**
   * Deserialise the given JSON map by populating the given JCas.
   *
   * @param jCas to populate
   * @param input to deserialise
   * @throws IOException if there is an error while deserialising.
   */
  public void deseralize(final JCas jCas, final Map<String, Object> input) {

    // Read top level
    jCas.setDocumentText((String) input.getOrDefault(JsonJCas.DOCUMENT_TEXT, ""));
    jCas.setDocumentLanguage((String) input.getOrDefault(JsonJCas.DOCUMENT_LANGUAGE, ""));

    // Read Document annotations
    final DocumentAnnotation documentAnnotation = UimaSupport.getDocumentAnnotation(jCas);
    final Map<String, Object> daNode =
        (Map<String, Object>) input.get(JsonJCas.DOCUMENT_ANNOTATION);
    processDocumentAnnotation(jCas, documentAnnotation, daNode);

    final List<Map<String, Object>> annotationsNode =
        (List<Map<String, Object>>) input.get(JsonJCas.ANNOTATIONS);
    final List<ReferencedFeatures> featuresToDereference =
        processAnnotations(jCas, annotationsNode);

    // Here we need to do hydrate the references

    final Map<Long, BaleenAnnotation> annotationIndex = buildAnnotationIndex(jCas);
    featuresToDereference.forEach(r -> r.rehydrate(jCas, annotationIndex));
  }

  private void processDocumentAnnotation(
      final JCas jCas, final DocumentAnnotation da, final Map<String, Object> map) {
    da.setDocType((String) map.getOrDefault(JsonJCas.DA_DOCUMENT_TYPE, ""));
    da.setDocumentClassification((String) map.getOrDefault(JsonJCas.DA_CLASSIFICATION, ""));
    da.setLanguage((String) map.getOrDefault(JsonJCas.DA_LANGUAGE, ""));
    da.setSourceUri((String) map.getOrDefault(JsonJCas.DA_SOURCE_URI, ""));
    da.setTimestamp(((Number) map.getOrDefault(JsonJCas.DA_TIMESTAMP, 0)).longValue());

    da.setDocumentCaveats(
        UimaTypesUtils.toArray(
            jCas, (Collection<String>) map.getOrDefault(JsonJCas.DA_CAVEATS, null)));
    da.setDocumentReleasability(
        UimaTypesUtils.toArray(
            jCas, (Collection<String>) map.getOrDefault(JsonJCas.DA_RELEASABILITY, null)));
  }

  private List<ReferencedFeatures> processAnnotations(
      final JCas jCas, final List<Map<String, Object>> annotations) {
    // We need to convert in the data back into annotations
    // but we have a complication in that some fields are references (ie hold the internalId when
    // then need to hold the actual Annotation)
    // so we record that information as we go, leaving the field blank and then we need to reprocess
    // those entities to 'rehydrate them'
    final List<ReferencedFeatures> featuresToDereference = new LinkedList<>();

    for (final Map<String, Object> a : annotations) {
      final String typeName = (String) a.get(JsonJCas.ANNOTATION_CLASS);

      try {
        // If you want to use the actual type in the JSON you can use ANNOTATION_TYPE
        // and then TypeUtils.getType(typeName, typeSystem) but it's very slow...
        final Class<?> typeClazz = getClass().getClassLoader().loadClass(typeName);

        if (typeClazz == null || !BaleenAnnotation.class.isAssignableFrom(typeClazz)) {
          // Not a type we know.. can't deserialise
          continue;
        }

        if (CollectionUtils.isNotEmpty(whiteList) && !isTypeInList(whiteList, typeClazz)) {
          // Not in whiltelist, ignore
          continue;
        }

        if (CollectionUtils.isNotEmpty(blackList) && isTypeInList(blackList, typeClazz)) {
          // In blacklist, ignore
          continue;
        }

        final BaleenAnnotation annotation =
            (BaleenAnnotation) typeClazz.getConstructor(JCas.class).newInstance(jCas);

        populateFeaturesForAnnotation(jCas, annotation, a, featuresToDereference);

        annotation.addToIndexes(jCas);

      } catch (final Exception e) {
        getMonitor().warn("Unable to process annotations", e);
      }
    }

    return featuresToDereference;
  }

  private boolean isTypeInList(
      Collection<Class<? extends BaleenAnnotation>> typeList, Class<?> typeClazz) {
    for (final Class<? extends BaleenAnnotation> clazz : typeList) {
      if (clazz.isAssignableFrom(typeClazz)) {
        return true;
      }
    }
    return false;
  }

  private void populateFeaturesForAnnotation(
      final JCas jCas,
      final BaleenAnnotation annotation,
      final Map<String, Object> map,
      final List<ReferencedFeatures> featuresToDereference) {
    for (final Feature f : annotation.getType().getFeatures()) {
      try {
        populateFeature(jCas, map, annotation, f, featuresToDereference);
      } catch (final Exception e) {
        getMonitor()
            .warn(
                "Couldn't populate {} to map. Type '{}' isn't supported.",
                f.getName(),
                f.getRange().getShortName(),
                e);
      }
    }
  }

  private void populateFeature(
      final JCas jCas,
      final Map<String, Object> map,
      final BaleenAnnotation annotation,
      final Feature f,
      final List<ReferencedFeatures> featuresToDereference) {

    final String name = JsonJCas.normalizeFeatureName(f);

    if (f.getRange().isPrimitive()) {
      // Straight primitive...
      NewFeatureUtils.setPrimitive(annotation, f, map.get(name));
    } else if (f.getRange().isArray()
        && f.getRange().getComponentType() != null
        && f.getRange().getComponentType().isPrimitive()) {
      NewFeatureUtils.setPrimitiveArray(jCas, annotation, f, map.get(name));
    } else {
      // Not a primitive or an array of primitives... looks if its references to other annotators
      final String reference = JsonJCas.makeReference(name);
      if (map.containsKey(reference) && map.get(reference) != null) {

        // TODO: Save annotation, f and a Long / Collection<Long> (depending on if an array) to be
        // resolved later
        featuresToDereference.add(new ReferencedFeatures(annotation, f, map.get(reference)));
      }
    }
  }

  private Map<Long, BaleenAnnotation> buildAnnotationIndex(final JCas jCas) {
    return JCasUtil.select(jCas, BaleenAnnotation.class)
        .stream()
        .collect(Collectors.toMap(BaleenAnnotation::getInternalId, v -> v));
  }

  private static class ReferencedFeatures {

    private final Object references;
    private final BaleenAnnotation annotation;
    private final Feature feature;

    public ReferencedFeatures(
        final BaleenAnnotation annotation, final Feature feature, final Object references) {
      this.annotation = annotation;
      this.references = references;
      this.feature = feature;
    }

    public void rehydrate(final JCas jCas, final Map<Long, BaleenAnnotation> annotations) {
      // We need to consider if the feature is an array and it this.value is an array.
      if (feature.getRange().isArray()) {
        rehydrateArray(jCas, annotations);
      } else {
        rehydrateSingle(annotations);
      }
    }

    private void rehydrateArray(final JCas jCas, final Map<Long, BaleenAnnotation> annotations) {
      Collection<Integer> list;
      if (references instanceof Collection) {
        list = (Collection<Integer>) references;
      } else {
        // Should never happen, but convert to list
        list = Collections.singletonList((Integer) references);
      }

      if (list == null || list.isEmpty()) {
        return;
      }

      // Note here we aren't checking that we have the right feature structure type!
      final List<BaleenAnnotation> derefernces =
          list.stream()
              .map(i -> annotations.get((long) i))
              .filter(Objects::nonNull)
              .collect(Collectors.toList());

      if (derefernces.isEmpty()) {
        return;
      }

      final FSArray fsArray = UimaTypesUtils.toFSArray(jCas, derefernces);
      annotation.setFeatureValue(feature, fsArray);
    }

    private void rehydrateSingle(final Map<Long, BaleenAnnotation> annotations) {
      Integer reference;
      if (references instanceof Collection) {
        // Take the first, should never happen!
        reference = ((Collection<Integer>) references).iterator().next();
      } else {
        reference = (Integer) references;
      }

      final BaleenAnnotation derefernced = annotations.get((long) reference);
      if (derefernced == null) {
        return;
      }

      // We've not type checked the feature structures are compatible...
      annotation.setFeatureValue(feature, derefernced);
    }
  }
}
