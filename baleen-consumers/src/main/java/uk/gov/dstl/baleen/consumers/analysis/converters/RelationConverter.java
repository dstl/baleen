// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.analysis.converters;

import java.util.Map;
import java.util.stream.Collectors;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.consumers.MongoRelations;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenMention;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenRelation;
import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.consumers.utils.EntityRelationConverter;
import uk.gov.dstl.baleen.resources.SharedIdGenerator;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.UimaMonitor;

/**
 * Create BaleenRelation POJOs from the semantic UIMA relations in a JCas.
 *
 * <p>Other than a transformation from UIMA feature structure to POJO there is little difference
 * between the input and output. Previous version of Baleen output relations in a very basic form
 * (leading to the {@link MongoRelations} consumer.
 */
public class RelationConverter {
  private final EntityRelationConverter converter;
  private final SharedIdGenerator idGenerator;
  private final UimaMonitor uimaMonitor;

  /**
   * Create new instance
   *
   * @param uimaMonitor for logging
   * @param idGenerator to generate ids
   * @param converter the converter to use
   */
  public RelationConverter(
      UimaMonitor uimaMonitor,
      final SharedIdGenerator idGenerator,
      final EntityRelationConverter converter) {
    this.uimaMonitor = uimaMonitor;
    this.idGenerator = idGenerator;
    this.converter = converter;
  }

  /**
   * Convert UIMA relations to BaleenRelation POJOs.
   *
   * @param jCas the jCas to read from
   * @param documentId the document to assign ownership to
   * @param mentions the mentions to use to populate the relation
   * @return map of relationId to BaleenRelation
   */
  public Map<String, BaleenRelation> convert(
      final JCas jCas,
      final String documentId,
      final String baleenDocumentId,
      final Map<String, BaleenMention> mentions) {

    return JCasUtil.select(jCas, Relation.class)
        .stream()
        .map(e -> convertRelation(documentId, baleenDocumentId, e, mentions))
        .collect(
            Collectors.toMap(
                BaleenRelation::getExternalId,
                m -> m,
                (u, v) -> {
                  uimaMonitor.debug("Ignoring duplicate externalId {}", u.getExternalId());
                  return u;
                }));
  }

  private BaleenRelation convertRelation(
      final String documentId,
      final String baleenDocumentId,
      final Relation relation,
      final Map<String, BaleenMention> mentions) {
    final BaleenRelation br = new BaleenRelation();

    final String baleenExternalId = ConsumerUtils.getExternalId(relation);
    final String externalId = idGenerator.generateForExternalId(baleenExternalId);

    br.setDocId(documentId);
    br.setBaleenDocId(baleenDocumentId);

    br.setExternalId(externalId);
    br.setBaleenId(baleenExternalId);
    br.setType(relation.getRelationshipType());
    br.setSubType(relation.getRelationSubType());
    br.setValue(relation.getValue());
    br.setBegin(relation.getBegin());
    br.setEnd(relation.getEnd());

    // Look up source and target
    final Entity source = relation.getSource();
    if (source != null) {
      final String sourceId = idGenerator.generateForExternalId(source.getExternalId());
      br.setSource(mentions.get(sourceId));
    }
    final Entity target = relation.getTarget();
    if (target != null) {
      final String targetId = idGenerator.generateForExternalId(target.getExternalId());
      br.setTarget(mentions.get(targetId));
    }
    // Use Relation converter to create a map, throw away that we have above
    final Map<String, Object> map = converter.convertRelation(relation);

    map.remove("externalId");
    map.remove("begin");
    map.remove("end");
    map.remove("type");
    // Keep value, type, etc on
    // Rename the types/subtypes
    map.put("type", br.getType());
    map.put("subType", br.getSubType());

    br.getProperties().putAll(map);
    return br;
  }
}
