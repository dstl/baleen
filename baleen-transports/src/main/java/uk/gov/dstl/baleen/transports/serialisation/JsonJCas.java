// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.serialisation;

import org.apache.uima.cas.Feature;

import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;

/** Constants for use in JSON serialisation of the JCas */
public final class JsonJCas {
  // Top level
  protected static final String ANNOTATIONS = "annotations";
  protected static final String DOCUMENT_ANNOTATION = "da";
  protected static final String DOCUMENT_TEXT = "text";
  protected static final String DOCUMENT_LANGUAGE = "lang";

  // Document annotations
  protected static final String DA_DOCUMENT_TYPE = "docType";
  protected static final String DA_LANGUAGE = "lang";
  protected static final String DA_SOURCE_URI = "sourceUri";
  protected static final String DA_CLASSIFICATION = "classification";
  protected static final String DA_TIMESTAMP = "timestamp";
  protected static final String DA_CAVEATS = "caveats";
  protected static final String DA_RELEASABILITY = "releasability";

  protected static final String ANNOTATION_TYPE = "type";
  protected static final String ANNOTATION_CLASS = "class";

  private static final String REFERENCE_INDICATOR = "_";

  private JsonJCas() {
    // Singleton
  }

  protected static String makeReference(final String shortName) {
    return REFERENCE_INDICATOR + shortName;
  }

  protected static boolean isReference(final String shortName) {
    return shortName.startsWith(REFERENCE_INDICATOR);
  }

  protected static String getNameFromReference(final String shortName) {
    return shortName.substring(REFERENCE_INDICATOR.length());
  }

  protected static String normalizeFeatureName(final Feature f) {
    return ConsumerUtils.toCamelCase(f.getShortName());
  }
}
