// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;

import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;

public class JCasSerializationTester {

  public static final String EMPTY_JSON =
      "{\"annotations\":[],\"text\":null,\"lang\":\"x-unspecified\",\"da\":{\"sourceUri\":null,\"docType\":null,\"releasability\":[],\"lang\":\"x-unspecified\",\"classification\":null,\"caveats\":[]}}";

  public static final String TEST_JSON =
      "{\"annotations\":[{\"internalId\":0,\"linking\":null,\"end\":1,\"type\":\"uk.gov.dstl.baleen.types.semantic.ReferenceTarget\",\"begin\":0,\"class\":\"uk.gov.dstl.baleen.types.semantic.ReferenceTarget\"},{\"internalId\":1,\"gender\":\"Male\",\"isNormalised\":false,\"confidence\":0.5,\"_referent\":0,\"end\":8,\"subType\":null,\"title\":null,\"type\":\"uk.gov.dstl.baleen.types.common.Person\",\"begin\":2,\"value\":\"Peter\",\"class\":\"uk.gov.dstl.baleen.types.common.Person\"},{\"internalId\":2,\"geoJson\":\"{ \\\"type\\\": \\\"Feature\\\", \\\"geometry\\\": {\\\"type\\\":\\\"Point\\\",\\\"coordinates\\\": [125.6, 10.1]},\\\"properties\\\": {\\\"name\\\": \\\"Dinagat Islands\\\"}}\",\"isNormalised\":false,\"confidence\":0.9,\"end\":12,\"subType\":null,\"type\":\"uk.gov.dstl.baleen.types.semantic.Location\",\"begin\":10,\"value\":\"Dinagat Islands\",\"class\":\"uk.gov.dstl.baleen.types.semantic.Location\"}],\"text\":\"This is a test\",\"lang\":\"x-unspecified\",\"da\":{\"sourceUri\":null,\"docType\":null,\"releasability\":[],\"lang\":\"x-unspecified\",\"classification\":\"CLASS\",\"caveats\":[]}}";

  private static final String GEO_JSON =
      "{ \"type\": \"Feature\", \"geometry\": {\"type\":\"Point\",\"coordinates\": [125.6, 10.1]},\"properties\": {\"name\": \"Dinagat Islands\"}}";

  private JCas in;

  private JCas out;

  public JCasSerializationTester() throws UIMAException {
    out = JCasFactory.createJCas();
    in = JCasFactory.createJCas();

    in.setDocumentText("This is a test");
    final DocumentAnnotation inDA = (DocumentAnnotation) in.getDocumentAnnotationFs();
    inDA.setDocumentClassification("CLASS");

    final Location l = new Location(in);
    l.setBegin(10);
    l.setEnd(12);
    l.setGeoJson(GEO_JSON);
    l.setValue("Dinagat Islands");
    l.setConfidence(0.9d);
    l.addToIndexes(in);

    final ReferenceTarget rt = new ReferenceTarget(in);
    rt.setBegin(0);
    rt.setEnd(1);
    rt.addToIndexes(in);

    final Person p = new Person(in);
    p.setBegin(2);
    p.setEnd(8);
    p.setGender("Male");
    p.setValue("Peter");
    p.setConfidence(0.5d);
    p.setReferent(rt);
    p.addToIndexes(in);
  }

  public JCas getIn() {
    return in;
  }

  public JCas getOut() {
    return out;
  }

  public void assertCompleteMatch() {
    assertTopLevel();
    assertLocationMatches();
    assertPersonMatches();
  }

  public void assertTopLevel() {
    // Top level jCas
    assertEquals(in.getDocumentText(), out.getDocumentText());
    assertEquals(in.getDocumentLanguage(), out.getDocumentLanguage());

    // Doc annotations
    final DocumentAnnotation outDa = (DocumentAnnotation) out.getDocumentAnnotationFs();
    assertNotNull(outDa);

    final DocumentAnnotation inDa = (DocumentAnnotation) out.getDocumentAnnotationFs();
    assertEquals(inDa.getDocumentClassification(), outDa.getDocumentClassification());
  }

  public void assertPersonMatches() {
    final Person inPerson = JCasUtil.selectSingle(in, Person.class);
    final Person outPerson = JCasUtil.selectSingle(out, Person.class);
    assertEquals(inPerson.getGender(), outPerson.getGender());
    assertEquals(inPerson.getBegin(), outPerson.getBegin());
    assertEquals(inPerson.getEnd(), outPerson.getEnd());
    assertEquals(inPerson.getValue(), outPerson.getValue());
    assertEquals(0, inPerson.getConfidence(), outPerson.getConfidence());

    // Check that person to entity is deferenced and its the same as the one we get...
    final ReferenceTarget inRt = JCasUtil.selectSingle(in, ReferenceTarget.class);
    final ReferenceTarget outRtFromJCas = JCasUtil.selectSingle(out, ReferenceTarget.class);
    final ReferenceTarget outRt = outPerson.getReferent();
    assertNotNull(outRt);
    assertEquals(inRt.getBegin(), outRt.getBegin());
    assertEquals(inRt.getEnd(), outRt.getEnd());
    assertSame(outRt, outRtFromJCas);
  }

  public void assertLocationMatches() {
    final Location inLocation = JCasUtil.selectSingle(in, Location.class);
    final Location outLocation = JCasUtil.selectSingle(out, Location.class);
    assertEquals(inLocation.getGeoJson(), outLocation.getGeoJson());
    assertEquals(inLocation.getBegin(), outLocation.getBegin());
    assertEquals(inLocation.getEnd(), outLocation.getEnd());
    assertEquals(inLocation.getValue(), outLocation.getValue());
    assertEquals(0, inLocation.getConfidence(), outLocation.getConfidence());
  }

  public void assertSerialised(String result) {
    assertEquals(ignoreInternalIds(TEST_JSON), ignoreInternalIds(result));
  }

  private String ignoreInternalIds(String testJson) {
    return testJson
        .replaceAll("\\\"internalId\\\":\\d", "\"internalId\":0")
        .replaceAll("\\\"_referent\\\":\\d", "\"_referent\":0");
  }
}
