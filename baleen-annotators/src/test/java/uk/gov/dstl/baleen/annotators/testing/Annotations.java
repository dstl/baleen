// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.testing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.common.Organisation;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.common.Quantity;
import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.baleen.types.language.Dependency;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.types.semantic.Temporal;

public class Annotations {
  private static final String WEIGHT = "weight";

  private Annotations() {
    // Singleton
  }

  public static Location createLocation(
      JCas jCas, int begin, int end, String value, String geojson) {
    Location l = new Location(jCas);
    l.setValue(value);
    l.setBegin(begin);
    l.setEnd(end);
    if (geojson != null) {
      l.setGeoJson(geojson);
    }
    l.addToIndexes();
    return l;
  }

  public static Quantity createWeightQuantity(
      JCas jCas,
      int begin,
      int end,
      String value,
      double quantity,
      String unit,
      double normalizedQuantity) {
    Quantity q = new Quantity(jCas);
    q.setBegin(begin);
    q.setEnd(end);
    q.setConfidence(1.0);
    q.setValue(value);
    q.setQuantity(quantity);
    q.setUnit(unit);
    q.setNormalizedQuantity(normalizedQuantity);
    q.setNormalizedUnit("kg");
    q.setSubType(WEIGHT);
    q.addToIndexes();
    return q;
  }

  public static Quantity createDistanceQuantity(
      JCas jCas,
      int begin,
      int end,
      String value,
      int quantity,
      String unit,
      double normalizedQuantity) {
    Quantity q = new Quantity(jCas);
    q.setBegin(begin);
    q.setEnd(end);
    q.setConfidence(1.0);
    q.setValue(value);
    q.setQuantity(quantity);
    q.setUnit(unit);
    q.setNormalizedQuantity(normalizedQuantity);
    q.setNormalizedUnit("m");
    q.setSubType("length");
    q.addToIndexes();
    return q;
  }

  public static Coordinate createCoordinate(JCas jCas, int begin, int end, String value) {
    Coordinate c = new Coordinate(jCas);
    c.setBegin(begin);
    c.setEnd(end);
    c.setValue(value);
    c.addToIndexes();
    return c;
  }

  public static ReferenceTarget createReferenceTarget(JCas jCas) {
    ReferenceTarget rt = new ReferenceTarget(jCas);
    rt.addToIndexes();
    return rt;
  }

  @SafeVarargs
  public static <T extends Entity> ReferenceTarget createReferenceTarget(JCas jCas, T... entities) {
    ReferenceTarget rt = new ReferenceTarget(jCas);
    rt.addToIndexes();
    Arrays.stream(entities).forEach(e -> e.setReferent(rt));
    return rt;
  }

  public static Person createPerson(JCas jCas, int begin, int end, String value) {
    Person p = new Person(jCas);
    p.setValue(value);
    p.setBegin(begin);
    p.setEnd(end);
    p.addToIndexes();
    return p;
  }

  public static Organisation createOrganisation(JCas jCas, int begin, int end, String value) {
    Organisation p = new Organisation(jCas);
    p.setValue(value);
    p.setBegin(begin);
    p.setEnd(end);
    p.addToIndexes();
    return p;
  }

  public static Temporal createTemporal(JCas jCas, int begin, int end, String value) {
    Temporal d2 = new Temporal(jCas);
    d2.setValue(value);
    d2.setBegin(begin);
    d2.setEnd(end);
    d2.addToIndexes();
    return d2;
  }

  public static Metadata createMetadata(JCas jCas, String key, String value) {
    Metadata md2 = new Metadata(jCas);
    md2.setKey(key);
    md2.setValue(value);
    md2.addToIndexes();
    return md2;
  }

  public static Entity createEntity(JCas jCas, int begin, int end, String value) {
    Entity e = new Entity(jCas);
    e.setBegin(begin);
    e.setEnd(end);
    if (value != null) {
      e.setValue(e.getCoveredText());
    }
    e.addToIndexes();
    return e;
  }

  public static Dependency createDependency(JCas jCas, WordToken gov, WordToken dep, String type) {
    Dependency d = new Dependency(jCas);
    d.setDependencyType(type);
    d.setBegin(dep.getBegin());
    d.setEnd(dep.getEnd());
    d.setGovernor(gov);
    d.setDependent(dep);
    d.addToIndexes();
    return d;
  }

  public static Sentence createSentence(JCas jCas, int begin, int end) {
    Sentence s = new Sentence(jCas);
    s.setBegin(begin);
    s.setEnd(end);
    s.addToIndexes();
    return s;
  }

  public static List<WordToken> createWordTokens(JCas jCas) {
    return createWordTokens(jCas, "(?=\\.)| |$");
  }

  public static List<Sentence> createSentences(JCas jCas) {
    return createSentences(jCas, "(?<=\\.)( |$)");
  }

  public static List<WordToken> createWordTokens(JCas jCas, String regex) {
    List<WordToken> words = new ArrayList<>();
    String documentText = jCas.getDocumentText();
    Matcher matcher = Pattern.compile(regex).matcher(documentText);
    int begin = 0;
    int end = 0;
    while (matcher.find()) {
      end = matcher.start();
      WordToken wt = new WordToken(jCas);
      wt.setBegin(begin);
      wt.setEnd(end);
      wt.addToIndexes();
      words.add(wt);
      begin = matcher.end();
    }
    return words;
  }

  public static List<Sentence> createSentences(JCas jCas, String regex) {
    List<Sentence> sentences = new ArrayList<>();
    String documentText = jCas.getDocumentText();
    Matcher matcher = Pattern.compile(regex).matcher(documentText);
    int begin = 0;
    int end = 0;
    while (matcher.find()) {
      end = matcher.start();
      Sentence s = new Sentence(jCas);
      s.setBegin(begin);
      s.setEnd(end);
      s.addToIndexes();
      sentences.add(s);
      begin = matcher.end();
    }
    return sentences;
  }
}
