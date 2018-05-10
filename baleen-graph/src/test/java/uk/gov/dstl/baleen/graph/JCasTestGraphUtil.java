// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph;

import java.util.Date;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.DocumentAnnotation;

import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.metadata.PublishedId;
import uk.gov.dstl.baleen.types.semantic.Event;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.types.semantic.Relation;

public class JCasTestGraphUtil {

  public static final String CONTENT =
      "This is a test document. John Smith is related to Jane Doe. He lives at Dinagat Islands.";
  public static final String LIVES_TYPE = "lives";
  public static final String RELATED_TYPE = "related";

  public static final String GEO_JSON =
      "{ \"type\": \"Feature\", \"geometry\": {\"type\":\"Point\",\"coordinates\": [125.6, 10.1]},\"properties\": {\"name\": \"Dinagat Islands\"}}";

  public static void populateJcas(final JCas jCas) {

    jCas.setDocumentText(CONTENT);
    final DocumentAnnotation da = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
    da.setDocumentClassification("CLASS");
    da.setDocType("MANUAL");
    da.setSourceUri("http://test.com");
    da.setLanguage("en");
    da.setTimestamp(new Date().getTime());
    da.setDocumentCaveats(new StringArray(jCas, 2));
    da.setDocumentCaveats(0, "GITHUB");
    da.setDocumentCaveats(1, "CAVEAT");

    final Metadata m1 = new Metadata(jCas);
    m1.setKey("test");
    m1.setValue("1");
    m1.addToIndexes(jCas);

    final Metadata m2 = new Metadata(jCas);
    m2.setKey("test");
    m2.setValue("2");
    m2.addToIndexes(jCas);

    final PublishedId pId = new PublishedId(jCas);
    pId.setPublishedIdType("test");
    pId.setValue("12");
    pId.addToIndexes(jCas);

    ReferenceTarget target = new ReferenceTarget(jCas);
    target.setLinking("testLinking");
    target.addToIndexes(jCas);

    final Person js = new Person(jCas);
    js.setBegin(25);
    js.setEnd(35);
    js.setGender("Male");
    js.setValue("John Smith");
    js.setConfidence(0.9d);
    js.setReferent(target);
    js.addToIndexes(jCas);

    final Person jd = new Person(jCas);
    jd.setBegin(50);
    jd.setEnd(58);
    jd.setGender("Female");
    jd.setValue("Jane Doe");
    jd.setConfidence(0.8d);
    jd.addToIndexes(jCas);

    final Person he = new Person(jCas);
    he.setBegin(60);
    he.setEnd(62);
    he.setGender("Male");
    he.setValue("He");
    he.setConfidence(0.9d);
    he.setReferent(target);
    he.addToIndexes(jCas);

    final Location l = new Location(jCas);
    l.setBegin(72);
    l.setEnd(87);
    l.setGeoJson(GEO_JSON);
    l.setValue("Dinagat Islands");
    l.setConfidence(0.9d);
    l.addToIndexes(jCas);

    final Relation related = new Relation(jCas);
    related.setBegin(36);
    related.setEnd(49);
    related.setValue("is related to");
    related.setRelationshipType(RELATED_TYPE);
    related.setSource(js);
    related.setTarget(jd);
    related.addToIndexes(jCas);

    final Relation lives = new Relation(jCas);
    lives.setBegin(63);
    lives.setEnd(71);
    lives.setValue("lives at");
    lives.setRelationshipType(LIVES_TYPE);
    lives.setSource(js);
    lives.setTarget(l);
    lives.addToIndexes(jCas);

    final Event event = new Event(jCas);
    event.setBegin(0);
    event.setEnd(10);
    event.setValue("test event");
    event.setEventType(new StringArray(jCas, 1));
    event.setEventType(0, "MEETING");
    event.setEntities(new FSArray(jCas, 2));
    event.setEntities(0, js);
    event.setEntities(1, jd);
    event.setArguments(new StringArray(jCas, 2));
    event.setArguments(0, "argument");
    event.setArguments(1, "Other");
    event.addToIndexes(jCas);
  }
}
