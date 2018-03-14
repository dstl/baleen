// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.metadata.PublishedId;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.UimaMonitor;
import uk.gov.dstl.baleen.uima.UimaSupport;

/**
 * Helper class for converting a CAS object into a single document (i.e. with entities and relations
 * embedded) for persistence using the following schema:
 *
 * <pre>
 * {
 * content,
 * language,
 * externalId,
 * dateAccessed,
 * sourceUri,
 * docType,
 * classification,
 * caveats: [],
 * releasability: [],
 * publishedId: [],
 * metadata: {
 * key1: value1,
 * key2: value2,
 * ...
 * },
 * entities: [
 * {
 * externalId,
 * value,
 * confidence,
 * type,
 * begin,
 * end,
 * ...
 * }
 * ],
 * relations: [
 * {
 * ...
 * }
 * ]
 * }</pre>
 *
 * The protective marking set on the DocumentAnnotation is used as the classification of the
 * document, and ProtectiveMarking annotations are ignored. Events are not currently supported.
 *
 * <p>Use of this class ensures consistency of formats across databases, e.g. Elasticsearch and
 * ActiveMQ
 */
public class SingleDocumentConsumerFormat {

  /** Private constructor for utility class */
  private SingleDocumentConsumerFormat() {
    // Do nothing
  }

  /**
   * Convert the provided jCas object into a standardised representation
   *
   * @param jCas
   * @param contentHashAsId Should a hash of the content be used to generate the ID? If false, then
   *     a hash of the Source URI is used instead.
   * @param fields An instance of IEntityConverterFields to be used
   * @param monitor
   * @param support
   * @return Standardised representation of jCas
   */
  public static Map<String, Object> formatCas(
      JCas jCas,
      IEntityConverterFields fields,
      boolean contentHashAsId,
      UimaMonitor monitor,
      UimaSupport support) {
    Set<String> stopFeatures = new HashSet<>();
    stopFeatures.add("uima.cas.AnnotationBase:sofa");
    stopFeatures.add("uk.gov.dstl.baleen.types.BaleenAnnotation:internalId");

    Map<String, Object> output = new HashMap<>();
    EntityRelationConverter entityRelationConverter =
        new EntityRelationConverter(
            monitor, false, support.getDocumentHistory(jCas), stopFeatures, fields);

    // Content and language
    output.put("content", jCas.getDocumentText());
    if (!Strings.isNullOrEmpty(jCas.getDocumentLanguage())) {
      output.put("language", jCas.getDocumentLanguage());
    }

    // Document Annotations
    DocumentAnnotation da = support.getDocumentAnnotation(jCas);
    output.putAll(createDocumentAnnotationMap(da));

    String id = ConsumerUtils.getExternalId(da, contentHashAsId);
    output.put("externalId", id);

    // Metadata Annotations
    Collection<PublishedId> publishedIds = JCasUtil.select(jCas, PublishedId.class);
    if (!publishedIds.isEmpty()) {
      output.put("publishedId", createPublishedIdList(publishedIds));
    }

    Collection<Metadata> metadata = JCasUtil.select(jCas, Metadata.class);
    if (!metadata.isEmpty()) {
      output.put("metadata", createMetadataMap(metadata));
    }

    // Entities
    List<Map<String, Object>> entitiesList = new ArrayList<>();
    Collection<Entity> entities = JCasUtil.select(jCas, Entity.class);

    for (Entity ent : entities) {
      entitiesList.add(entityRelationConverter.convertEntity(ent));
    }
    output.put("entities", entitiesList);

    // Relations
    List<Map<String, Object>> relationsList = new ArrayList<>();
    Collection<Relation> relations = JCasUtil.select(jCas, Relation.class);

    for (Relation rel : relations) {
      relationsList.add(entityRelationConverter.convertRelation(rel));
    }
    output.put("relations", relationsList);

    return output;
  }

  /** Create a map containing information from the DocumentAnnotation object */
  public static Map<String, Object> createDocumentAnnotationMap(DocumentAnnotation da) {
    Map<String, Object> map = new HashMap<>();

    if (!Strings.isNullOrEmpty(da.getSourceUri())) {
      map.put("sourceUri", da.getSourceUri());
    }
    map.put("dateAccessed", da.getTimestamp());
    if (!Strings.isNullOrEmpty(da.getDocType())) {
      map.put("docType", da.getDocType());
    }
    if (!Strings.isNullOrEmpty(da.getDocumentClassification())) {
      map.put("classification", da.getDocumentClassification().toUpperCase());
    }
    if (da.getDocumentCaveats() != null) {
      String[] caveats = da.getDocumentCaveats().toArray();
      if (caveats.length > 0) {
        map.put("caveats", caveats);
      }
    }
    if (da.getDocumentReleasability() != null) {
      String[] rels = da.getDocumentReleasability().toArray();
      if (rels.length > 0) {
        map.put("releasability", rels);
      }
    }

    return map;
  }

  /**
   * Create a map of all metadata objects in a collection. Duplicate key values will be converted
   * into a list of objects.
   */
  public static Map<String, Object> createMetadataMap(Collection<Metadata> md) {
    Map<String, Object> metadata = new HashMap<>();

    for (Metadata m : md) {
      String key = m.getKey().replaceAll("\\.", "_");

      if (metadata.containsKey(key)) {
        List<Object> list = new ArrayList<>();

        Object o = metadata.get(key);
        if (o instanceof List) {
          list.addAll((List<?>) o);
        }
        list.add(m.getValue());

        metadata.put(key, m.getValue());
      } else {
        metadata.put(key, m.getValue());
      }
    }

    return metadata;
  }

  /** Create a list of PublishedId values from a collection of PublishedIds */
  public static List<String> createPublishedIdList(Collection<PublishedId> publishedIds) {
    List<String> pids = new ArrayList<>();

    publishedIds.forEach(x -> pids.add(x.getValue()));

    return pids;
  }
}
