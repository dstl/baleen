// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.analysis.data;

/** Analysis related Constants. */
public class AnalysisConstants {

  // DocumentAnnotation based properties

  /** The Constant DOCUMENT_TYPE. */
  public static final String DOCUMENT_TYPE = "type";

  /** The Constant SOURCE. */
  public static final String SOURCE = "source";

  /** The Constant LANGUAGE. */
  public static final String LANGUAGE = "language";

  /** The Constant TIMESTAMP. */
  public static final String TIMESTAMP = "timestamp";

  /** The Constant CLASSIFICATION. */
  public static final String CLASSIFICATION = "classification";

  /** The Constant CAVEATS. */
  public static final String CAVEATS = "caveats";

  /** The Constant RELEASABILITY. */
  public static final String RELEASABILITY = "releasability";

  /** The Constant HASH. */
  public static final String HASH = "hash";

  // Document annotation properties

  /** The Constant PUBLISHED_IDS. */
  public static final String PUBLISHED_IDS = "publishedIds";

  // Document metadata properties (or fallback)

  /** The Constant DOCUMENT_DATE. */
  public static final String DOCUMENT_DATE = "documentDate";

  /** The Constant DOCUMENT_TITLE. */
  public static final String DOCUMENT_TITLE = "documentTitle";

  // Relation properties

  /** The Constant WORD_DISTANCE. */
  public static final String WORD_DISTANCE = "wordDistance";

  /** The Constant DEPENDENCY_DISTANCE. */
  public static final String DEPENDENCY_DISTANCE = "dependencyDistance";

  /** The Constant NORMAL_SENTENCE_DISTANCE. */
  public static final String NORMAL_SENTENCE_DISTANCE = "sentenceDistanceNormalized";

  /** The Constant NORMAL_WORD_DISTANCE. */
  public static final String NORMAL_WORD_DISTANCE = "wordDistanceNormalized";

  /** The Constant NORMAL_DEPENDENCY_DISTANCE. */
  public static final String NORMAL_DEPENDENCY_DISTANCE = "dependencyDistanceNormalized";

  // Mention properties (inherited by entities)
  // these are just a few which are actually used in the conversion, the rest are ignored.

  /** The Constant START_TIMESTAMP. */
  public static final String START_TIMESTAMP = "timestampStart";

  /** The Constant STOP_TIMESTAMP. */
  public static final String STOP_TIMESTAMP = "timestampStop";

  /** The Constant GEOJSON. */
  public static final String GEOJSON = "geoJson";

  /** The Constant POI. */
  public static final String POI = "poi";

  // Entity, relation, mention
  public static final String BALEEN_ID = "baleenId";
  public static final String BALEEN_DOC_ID = "baleenDocId";

  /** The Constant TEMPORAL_PRECISION. */
  public static final String TEMPORAL_PRECISION = "precision";
}
