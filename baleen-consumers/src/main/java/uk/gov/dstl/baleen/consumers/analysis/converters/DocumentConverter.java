// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.analysis.converters;

import static org.springframework.util.StringUtils.isEmpty;

import java.time.DateTimeException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.logging.log4j.util.Strings;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;

import uk.gov.dstl.baleen.consumers.analysis.data.AnalysisConstants;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenDocument;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenDocumentMetadata;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.metadata.PublishedId;
import uk.gov.dstl.baleen.uima.utils.UimaTypesUtils;

/**
 * Converts from a JCas to a BaleenDocument POJO.
 *
 * <p>This standardises the output of the document centric aspect of a JCas.
 *
 * <p>It encompasses metadata, documentation annotation information, and the content itself.
 *
 * <p>In an effort to help users of baleen output it adds a document title and date (falling back to
 * information from the documentation annotation). This is 'best guess', but at least a standardised
 * best guess.
 */
public class DocumentConverter {

  /**
   * Convert the metadata, information, published ids to a BaleenDocument.
   *
   * @param jCas the jCas
   * @param baleenDocumentId the document id which Baleen generates (eg using the hash of content)
   * @param documentId the documentId
   * @param da the documentation annotation
   * @return The converted POJO
   */
  public BaleenDocument convert(
      final JCas jCas,
      final String documentId,
      final String baleenDocumentId,
      final DocumentAnnotation da) {

    final BaleenDocument document = new BaleenDocument();
    final Map<String, Object> properties = document.getProperties();

    document.setContent(jCas.getDocumentText());
    document.setExternalId(documentId);
    document.setBaleenId(baleenDocumentId);

    addDocumentAnnotationToProperties(properties, da);

    addMetadataToProperties(document.getMetadata(), jCas);

    addPublishedIdsToDocument(properties, jCas);

    // Add title and data from best place we can
    addDocumentTitle(document);
    addDocumentDate(document, da);
    return document;
  }

  private void addDocumentTitle(final BaleenDocument document) {
    final Map<String, Object> properties = document.getProperties();
    final String title = findDocumentTitle(properties, document.getMetadata());
    properties.put(AnalysisConstants.DOCUMENT_TITLE, title);
  }

  private void addDocumentDate(final BaleenDocument document, final DocumentAnnotation da) {
    final Map<String, Object> properties = document.getProperties();
    final Date date = findDocumentDate(document.getMetadata(), da);
    properties.put(AnalysisConstants.DOCUMENT_DATE, date);
  }

  private void addPublishedIdsToDocument(final Map<String, Object> properties, final JCas jCas) {

    final List<BaleenDocument.PublishedId> publishedIds = new ArrayList<>();
    for (final PublishedId pid : JCasUtil.select(jCas, PublishedId.class)) {

      publishedIds.add(new BaleenDocument.PublishedId(pid.getPublishedIdType(), pid.getValue()));
    }
    properties.put(AnalysisConstants.PUBLISHED_IDS, publishedIds);
  }

  private Date findDocumentDate(
      final List<BaleenDocumentMetadata> documentMetadata, final DocumentAnnotation da) {
    final Optional<Date> date = findDateFromMetadata(documentMetadata);
    return date.orElse(new Date(da.getTimestamp()));
  }

  private void addDocumentAnnotationToProperties(
      final Map<String, Object> properties, final DocumentAnnotation da) {
    properties.put(AnalysisConstants.DOCUMENT_TYPE, da.getDocType());
    properties.put(AnalysisConstants.CAVEATS, UimaTypesUtils.toList(da.getDocumentCaveats()));
    properties.put(AnalysisConstants.CLASSIFICATION, da.getDocumentClassification());
    properties.put(
        AnalysisConstants.RELEASABILITY, UimaTypesUtils.toList(da.getDocumentReleasability()));
    properties.put(AnalysisConstants.LANGUAGE, da.getLanguage());
    properties.put(AnalysisConstants.HASH, da.getHash());
    properties.put(AnalysisConstants.SOURCE, da.getSourceUri());
    properties.put(AnalysisConstants.TIMESTAMP, new Date(da.getTimestamp()));
  }

  private void addMetadataToProperties(final List<BaleenDocumentMetadata> list, final JCas jCas) {
    for (final Metadata metadata : JCasUtil.select(jCas, Metadata.class)) {
      String key = metadata.getKey();
      if (key.contains(".")) {
        // Field names can't contain a "." in Mongo, so replace with a _
        key = key.replaceAll("\\.", "_");
      }

      final String value = metadata.getValue();
      if (!Strings.isEmpty(key) && !Strings.isEmpty(value)) {
        list.add(new BaleenDocumentMetadata(key, value));
      }
    }
  }

  /**
   * Finds the best title for a document, falling back to information from the documentation
   * annotation
   *
   * @param properties Properties of a Baleen Document
   * @param documentMetadata Metadata of a Baleen Document
   * @return A title of the document
   */
  public String findDocumentTitle(
      final Map<String, Object> properties, final List<BaleenDocumentMetadata> documentMetadata) {

    final Optional<String> optional = extractTitleFromMetadata(documentMetadata);
    if (optional.isPresent()) {
      return optional.get();
    }

    // Published Id?
    if (properties.get(AnalysisConstants.PUBLISHED_IDS) != null) {
      // We 'know' if is a Collection< BaleenDocument.PublishedId> because its created above
      @SuppressWarnings("unchecked")
      final String title =
          lookInPublishedIds(
              (Collection<BaleenDocument.PublishedId>)
                  properties.get(AnalysisConstants.PUBLISHED_IDS));
      if (!isEmpty(title)) {
        return title;
      }
    }

    // Fall back to last bit of path
    if (properties.get(AnalysisConstants.SOURCE) != null) {
      final String title =
          extractTitleFromSource((String) properties.get(AnalysisConstants.SOURCE));
      if (!isEmpty(title)) {
        return title;
      }
    }

    return "Untitled";
  }

  private Optional<String> extractTitleFromMetadata(
      final List<BaleenDocumentMetadata> documentMetadata) {
    // Take the first, we could try and guess the best... but it would be a guess
    return getStringFromMetadata(documentMetadata, "title", "DC.Title", "documentTitle")
        .findFirst();
  }

  private Stream<String> getStringFromMetadata(
      final List<BaleenDocumentMetadata> documentMetadata, final String... keys) {
    return getKeysFromMetadata(documentMetadata, keys)
        .filter(String.class::isInstance)
        .map(String.class::cast);
  }

  private Optional<Date> findDateFromMetadata(final List<BaleenDocumentMetadata> documentMetadata) {
    return getKeysFromMetadata(
            documentMetadata, "date", "DC.Date", "Last-Modified", "documentDate", "timestamp")
        .filter(o -> Date.class.isInstance(o) || String.class.isInstance(o))
        .map(
            o -> {
              if (o instanceof Date) {
                return o;
              } else {
                // Type of string
                try {
                  final OffsetDateTime dt = OffsetDateTime.parse((String) o);
                  return new Date(dt.toInstant().toEpochMilli());
                } catch (final DateTimeException e) {
                  // Ignore
                  return null;
                }
              }
            })
        .filter(Objects::nonNull)
        .map(Date.class::cast)
        .findFirst();
  }

  private Stream<Object> getKeysFromMetadata(
      final List<BaleenDocumentMetadata> documentMetadata, final String... keys) {
    final Set<Object> ordered = new LinkedHashSet<>();

    // try ignoring case, but leave in order original of keys

    for (final String key : keys) {
      documentMetadata
          .stream()
          .filter(e -> e.getKey().equalsIgnoreCase(key))
          .filter(e -> e.getValue() != null)
          .forEach(e -> ordered.add(e.getValue()));
    }

    return ordered.stream();
  }

  private String extractTitleFromSource(final String source) {
    if (source != null && !source.isEmpty()) {
      // Is this a path? (best guess)?
      if (source.contains("/")) {
        final String title = extractFromLastPathSegment(source, '/');
        if (!isEmpty(title)) {
          return title;
        }
      }

      if (source.contains("\\")) {
        final String title = extractFromLastPathSegment(source, '\\');
        if (!isEmpty(title)) {
          return title;
        }
      }
    }
    return null;
  }

  private String extractFromLastPathSegment(final String source, final char seperator) {
    int lastIndex;
    if (source.charAt(source.length() - 1) == seperator) {
      lastIndex = source.length() - 2;
    } else {
      lastIndex = source.length() - 1;
    }
    final int index = source.lastIndexOf(seperator, lastIndex);
    if (index < source.length() - 1) {
      final String filename = source.substring(index + 1, lastIndex + 1);

      final int extensionIndex = filename.indexOf('.');
      // doesn't start with it and exists
      if (extensionIndex > 0) {
        return filename.substring(0, extensionIndex);
      } else {
        return filename;
      }
    }
    return null;
  }

  private String lookInPublishedIds(final Collection<BaleenDocument.PublishedId> publishedIds) {
    for (final BaleenDocument.PublishedId s : publishedIds) {
      if (!isEmpty(s.getId())) {
        return s.getId();
      }
    }
    return null;
  }
}
