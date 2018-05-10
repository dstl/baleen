// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.analysis.convertors;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.impl.AnalysisEngineDescription_impl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;

import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.consumers.analysis.data.AnalysisConstants;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenDocument;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenDocumentMetadata;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenEntity;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenMention;
import uk.gov.dstl.baleen.consumers.analysis.data.BaleenRelation;
import uk.gov.dstl.baleen.consumers.analysis.data.LatLon;
import uk.gov.dstl.baleen.consumers.utils.DefaultFields;
import uk.gov.dstl.baleen.consumers.utils.EntityRelationConverter;
import uk.gov.dstl.baleen.consumers.utils.IEntityConverterFields;
import uk.gov.dstl.baleen.resources.SharedIdGenerator;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.metadata.PublishedId;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.types.semantic.Temporal;
import uk.gov.dstl.baleen.uima.UimaMonitor;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;
import uk.gov.dstl.baleen.uima.utils.UimaTypesUtils;

public class AnalysisMockData {

  public static final String PUBLISHED_ID1 = "pId1";

  public static final int DOC_TIMESTAMP = 123456789;

  public static final String LANGUAGE = "lang";

  public static final String SOURCE = "source";

  public static final String CLASSIFICATION = "class";

  public static final String RELEASABILITY = "rel";

  public static final String CAVEAT = "caveat";

  public static final String DOCTYPE = "doctype";

  public static final String TEXT = "1234567890abcdefghijklmnopqrstuvwxyz";

  public static final String BALEEN_DOC_ID = "12345abc";

  private final String docId;

  private EntityRelationConverter erc;

  private JCas jCas;

  private Map<String, BaleenMention> mentions;

  private HashMap<String, BaleenRelation> relations;

  private BaleenDocument document;

  private HashMap<String, BaleenEntity> entities;

  private UimaMonitor monitor;

  private SharedIdGenerator idGenerator;

  public AnalysisMockData() {
    try {
      monitor = new UimaMonitor("test", this.getClass());
      idGenerator = new SharedIdGenerator();
      idGenerator.initialize(new AnalysisEngineDescription_impl(), Collections.emptyMap());
      docId = toId(BALEEN_DOC_ID);
      createERC();
      createJCas();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String getDocumentId() {
    return docId;
  }

  private void createERC() {
    final IEntityConverterFields fields = new DefaultFields();

    final HashSet<String> stopFeatures = new HashSet<>();
    stopFeatures.add("uima.cas.AnnotationBase:sofa");
    stopFeatures.add("uk.gov.dstl.baleen.types.BaleenAnnotation:internalId");

    erc = new EntityRelationConverter(monitor, stopFeatures, fields, false);
  }

  private void createJCas() throws UIMAException {
    jCas = JCasSingleton.getJCasInstance();
    jCas.setDocumentText(TEXT);

    final DocumentAnnotation da = getDocumentAnnotation();
    da.setDocType(DOCTYPE);
    da.setDocumentCaveats(UimaTypesUtils.toArray(jCas, Arrays.asList(CAVEAT)));
    da.setDocumentReleasability(UimaTypesUtils.toArray(jCas, Arrays.asList(RELEASABILITY)));
    da.setDocumentClassification(CLASSIFICATION);
    da.setSourceUri(SOURCE);
    da.setLanguage(LANGUAGE);
    da.setTimestamp(DOC_TIMESTAMP);

    final PublishedId publishedId = new PublishedId(jCas);
    publishedId.setPublishedIdType("ref");
    publishedId.setValue(PUBLISHED_ID1);
    publishedId.addToIndexes();

    final Metadata metadata = Annotations.createMetadata(jCas, "key", "value");

    // Create entities

    final ReferenceTarget rt1 = new ReferenceTarget(jCas);
    rt1.setBegin(12);
    rt1.setEnd(13);
    rt1.addToIndexes();

    final ReferenceTarget rt2 = new ReferenceTarget(jCas);
    rt1.setBegin(12);
    rt1.setEnd(13);
    rt1.addToIndexes();

    // Fake entities
    final Person e1 = Annotations.createPerson(jCas, 1, 10, "Jon");
    e1.setConfidence(0.5);
    e1.setGender("male");
    e1.addToIndexes();

    final Person e2 = Annotations.createPerson(jCas, 0, 10, "Jonathon");
    final Person e3 = Annotations.createPerson(jCas, 0, 10, "John");
    final Person e3Duplicate = Annotations.createPerson(jCas, 0, 10, "John");

    Annotations.createReferenceTarget(jCas, e1, e2);
    Annotations.createReferenceTarget(jCas, e3, e3Duplicate);

    final Location e4 =
        Annotations.createLocation(
            jCas, 0, 10, "value4", "{ \"type\": \"Point\",  \"coordinates\": [-1, 3] }");

    final Temporal e5 = Annotations.createTemporal(jCas, 0, 10, "2018-01-01 Midnight");
    e5.setPrecision("EXACT");
    e5.setTemporalType("DATE");
    e5.setTimestampStart(1000);
    e5.setTimestampStop(1100);

    final BaleenMention m1 =
        new BaleenMention(
            docId,
            BALEEN_DOC_ID,
            toId(e1.getExternalId()),
            e1.getExternalId(),
            toId(rt1.getExternalId()),
            rt1.getExternalId(),
            e1.getBegin(),
            e1.getEnd(),
            e1.getType().getShortName(),
            e1.getValue());
    final BaleenMention m2 =
        new BaleenMention(
            docId,
            BALEEN_DOC_ID,
            toId(e2.getExternalId()),
            e2.getExternalId(),
            toId(rt1.getExternalId()),
            rt1.getExternalId(),
            e2.getBegin(),
            e2.getEnd(),
            e2.getType().getShortName(),
            e2.getValue());
    final BaleenMention m3 =
        new BaleenMention(
            docId,
            BALEEN_DOC_ID,
            toId(e3.getExternalId()),
            e3.getExternalId(),
            toId(rt2.getExternalId()),
            rt2.getExternalId(),
            e3.getBegin(),
            e3.getEnd(),
            e3.getType().getShortName(),
            e3.getValue());
    final BaleenMention m4 =
        new BaleenMention(
            docId,
            BALEEN_DOC_ID,
            toId(e4.getExternalId()),
            e4.getExternalId(),
            toId("ent1"),
            "ent1",
            e4.getBegin(),
            e4.getEnd(),
            e4.getType().getShortName(),
            e4.getValue());
    final BaleenMention m5 =
        new BaleenMention(
            docId,
            BALEEN_DOC_ID,
            toId(e5.getExternalId()),
            e5.getExternalId(),
            toId("ent2"),
            "ent2",
            e5.getBegin(),
            e5.getEnd(),
            e5.getType().getShortName(),
            e5.getValue());

    m1.getProperties().put("gender", "male");
    m4.getProperties().put("geoJson", e4.getGeoJson());
    m4.getProperties().put(AnalysisConstants.POI, Arrays.asList(new LatLon(3, -1)));
    m5.getProperties().put("precision", e5.getPrecision());
    m5.getProperties().put("temporalType", e5.getTemporalType());
    m5.getProperties().put("timestampStart", e5.getTimestampStart());
    m5.getProperties().put("timestampStop", e5.getTimestampStop());

    mentions = new HashMap<>();
    mentions.put(m1.getExternalId(), m1);
    mentions.put(m2.getExternalId(), m2);
    mentions.put(m3.getExternalId(), m3);
    mentions.put(m4.getExternalId(), m4);
    mentions.put(m5.getExternalId(), m5);

    // Create baleen entities

    entities = new HashMap<>();

    final BaleenEntity be1 =
        new BaleenEntity(
            docId,
            BALEEN_DOC_ID,
            toId(rt1.getExternalId()),
            rt1.getExternalId(),
            e1.getTypeName(),
            e2.getValue());
    final BaleenEntity be2 =
        new BaleenEntity(
            docId,
            BALEEN_DOC_ID,
            toId(rt2.getExternalId()),
            rt2.getExternalId(),
            e1.getTypeName(),
            e2.getValue());
    final BaleenEntity be3 =
        new BaleenEntity(
            docId,
            BALEEN_DOC_ID,
            toId(e4.getExternalId()),
            e4.getExternalId(),
            e4.getTypeName(),
            e4.getValue());
    final BaleenEntity be4 =
        new BaleenEntity(
            docId,
            BALEEN_DOC_ID,
            toId(e5.getExternalId()),
            e5.getExternalId(),
            e5.getTypeName(),
            e5.getValue());

    be1.getMentionIds().add(m1.getExternalId());
    be1.getMentionIds().add(m2.getExternalId());
    be2.getMentionIds().add(m3.getExternalId());
    be3.getMentionIds().add(m4.getExternalId());
    be4.getMentionIds().add(m5.getExternalId());

    be1.getProperties().putAll(m1.getProperties());
    be1.getProperties().putAll(m2.getProperties());
    be2.getProperties().putAll(m3.getProperties());
    be3.getProperties().putAll(m4.getProperties());
    be4.getProperties().putAll(m5.getProperties());

    entities.put(be1.getExternalId(), be1);
    entities.put(be2.getExternalId(), be2);
    entities.put(be3.getExternalId(), be3);
    entities.put(be4.getExternalId(), be4);

    // Create relations

    final Relation r1 = new Relation(jCas);
    r1.setBegin(2);
    r1.setEnd(4);
    r1.setRelationshipType("r1Type");
    r1.setRelationSubType("r1SubType");
    r1.setValue("r1Value");
    r1.setSource(e1);
    r1.setTarget(e4);
    r1.addToIndexes();

    final Relation r2 = new Relation(jCas);
    r2.setBegin(2);
    r2.setEnd(4);
    r2.setRelationshipType("r2Type");
    r2.setRelationSubType("r2SubType");
    r2.setValue("r2Value");
    r2.setSource(e2);
    r2.setTarget(e5);
    r2.addToIndexes();

    final Relation r2Duplicate = new Relation(jCas);
    r2Duplicate.setBegin(2);
    r2Duplicate.setEnd(4);
    r2Duplicate.setRelationshipType("r2Type");
    r2Duplicate.setRelationSubType("r2SubType");
    r2Duplicate.setValue("r2Value");
    r2Duplicate.setSource(e2);
    r2Duplicate.setTarget(e5);
    r2Duplicate.addToIndexes();

    final BaleenRelation br1 =
        new BaleenRelation(
            docId,
            BALEEN_DOC_ID,
            toId(r1.getExternalId()),
            r1.getExternalId(),
            r1.getRelationshipType(),
            r1.getRelationSubType(),
            "Relation",
            m1,
            m4);
    final BaleenRelation br2 =
        new BaleenRelation(
            docId,
            BALEEN_DOC_ID,
            toId(r2.getExternalId()),
            r2.getExternalId(),
            r2.getRelationshipType(),
            r2.getRelationSubType(),
            "Relation",
            m2,
            m5);

    relations = new HashMap<>();
    relations.put(br1.getExternalId(), br1);
    relations.put(br2.getExternalId(), br2);

    // Create baleen doc

    document = new BaleenDocument();
    document.setContent(jCas.getDocumentText());
    document.setExternalId(docId);
    document.getMetadata().add(new BaleenDocumentMetadata(metadata.getKey(), metadata.getValue()));
    final Map<String, Object> properties = document.getProperties();
    properties.put(AnalysisConstants.CAVEATS, Arrays.asList(CAVEAT));
    properties.put(AnalysisConstants.CLASSIFICATION, da.getDocumentClassification());
    properties.put(AnalysisConstants.DOCUMENT_TYPE, da.getDocType());
    properties.put(AnalysisConstants.HASH, da.getHash());
    properties.put(AnalysisConstants.RELEASABILITY, Arrays.asList(RELEASABILITY));
    properties.put(AnalysisConstants.LANGUAGE, da.getLanguage());
    properties.put(AnalysisConstants.TIMESTAMP, new Date(DOC_TIMESTAMP));
    properties.put(AnalysisConstants.SOURCE, SOURCE);
    properties.put(
        AnalysisConstants.PUBLISHED_IDS,
        Arrays.asList(
            new BaleenDocument.PublishedId(
                publishedId.getPublishedIdType(), publishedId.getValue())));
  }

  public String toId(final String externalId) {
    return idGenerator.generateForExternalId(externalId);
  }

  public EntityRelationConverter getErc() {
    return erc;
  }

  public BaleenDocument getDocument() {
    return document;
  }

  public JCas getJCas() {
    return jCas;
  }

  public Map<String, BaleenMention> getMentions() {
    return mentions;
  }

  public HashMap<String, BaleenRelation> getRelations() {
    return relations;
  }

  public DocumentAnnotation getDocumentAnnotation() {
    return (DocumentAnnotation) jCas.getDocumentAnnotationFs();
  }

  public Map<String, BaleenEntity> getEntities() {
    return entities;
  }

  public UimaMonitor getMonitor() {
    return monitor;
  }

  public SharedIdGenerator getIdGenerator() {
    return idGenerator;
  }
}
