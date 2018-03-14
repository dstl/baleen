// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Maps;

import uk.gov.dstl.baleen.core.history.BaleenHistory;
import uk.gov.dstl.baleen.core.history.memory.InMemoryBaleenHistory;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.metadata.ProtectiveMarking;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UimaSupportTest {

  private static final String PIPELINE = null;

  @Mock private UimaMonitor monitor;

  private BaleenHistory history;

  private JCas jCas;

  private Location location;

  private Metadata md;

  @Before
  public void setUp() throws UIMAException {
    jCas = JCasSingleton.getJCasInstance();
    jCas.setDocumentText("Dave saw is some of London");

    location = new Location(jCas);
    location.setBegin(20);
    location.setEnd(26);
    location.setValue("London");
    location.addToIndexes();

    md = new Metadata(jCas);
    md.setKey("k");
    md.setValue("v");
    md.addToIndexes();

    history = new InMemoryBaleenHistory();
    history.initialize(new CustomResourceSpecifier_impl(), Maps.newHashMap());
  }

  @Test
  public void testAddAnnotationArray() {
    UimaSupport support = new UimaSupport(PIPELINE, UimaSupportTest.class, history, monitor, false);

    Person withValue = new Person(jCas);
    withValue.setBegin(0);
    withValue.setEnd(4);
    withValue.setValue("David");

    Person withoutValue = new Person(jCas);
    withoutValue.setBegin(0);
    withoutValue.setEnd(4);

    support.add(new ProtectiveMarking(jCas), withValue, withoutValue);

    assertEquals(1, JCasUtil.select(jCas, ProtectiveMarking.class).size());
    List<Person> persons = new ArrayList<Person>(JCasUtil.select(jCas, Person.class));
    assertEquals(2, persons.size());

    // Check value is set / not overridden
    assertNotEquals(persons.get(0).getCoveredText(), persons.get(0).getValue());
    assertEquals(persons.get(1).getCoveredText(), persons.get(1).getValue());

    // Check Id set
    assertNotEquals(persons.get(0).getInternalId(), persons.get(1).getInternalId());

    // Check had history of addition
    assertFalse(
        support.getDocumentHistory(jCas).getHistory(persons.get(1).getInternalId()).isEmpty());
  }

  @Test
  public void testRemoveAnnotationArray() {
    UimaSupport support = new UimaSupport(PIPELINE, UimaSupportTest.class, history, monitor, false);

    support.remove(location, md);

    assertEquals(0, JCasUtil.select(jCas, Location.class).size());
    assertEquals(0, JCasUtil.select(jCas, Metadata.class).size());

    // Location should not have history and id
    assertNotNull(location.getInternalId());
    assertFalse(support.getDocumentHistory(jCas).getHistory(location.getInternalId()).isEmpty());
  }

  @Test
  public void testMergeWithNewAnnotationAnnotationArray() {
    UimaSupport support = new UimaSupport(PIPELINE, UimaSupportTest.class, history, monitor, false);

    Location l = new Location(jCas);
    l.setBegin(0);
    l.setEnd(0);

    Metadata md2 = new Metadata(jCas);
    md2.setBegin(0);
    md2.setEnd(0);

    support.mergeWithNew(l, location);
    support.mergeWithNew(md2, md);

    List<Location> locations = new ArrayList<>(JCasUtil.select(jCas, Location.class));
    List<Metadata> mds = new ArrayList<>(JCasUtil.select(jCas, Metadata.class));

    assertEquals(1, locations.size());
    assertEquals(l, locations.get(0));

    assertEquals(1, mds.size());
    assertEquals(md2, mds.get(0));

    assertFalse(support.getDocumentHistory(jCas).getHistory(l.getInternalId()).isEmpty());
  }

  @Test
  public void testMergeWithExistingAnnotationAnnotationArray() {
    UimaSupport support = new UimaSupport(PIPELINE, UimaSupportTest.class, history, monitor, false);

    Location l = new Location(jCas);
    l.setBegin(0);
    l.setEnd(0);
    l.addToIndexes();

    Metadata md2 = new Metadata(jCas);
    md2.setBegin(0);
    md2.setEnd(0);
    md2.addToIndexes();

    support.mergeWithExisting(l, location);
    support.mergeWithExisting(md2, md);

    List<Location> locations = new ArrayList<>(JCasUtil.select(jCas, Location.class));
    List<Metadata> mds = new ArrayList<>(JCasUtil.select(jCas, Metadata.class));

    assertEquals(1, locations.size());
    assertEquals(l, locations.get(0));

    assertEquals(1, mds.size());
    assertEquals(md2, mds.get(0));

    assertFalse(support.getDocumentHistory(jCas).getHistory(l.getInternalId()).isEmpty());
  }

  @Test
  public void testMergeWithMergeDifferentReferent() {
    UimaSupport support = new UimaSupport(PIPELINE, UimaSupportTest.class, history, monitor, true);

    ReferenceTarget rt1 = new ReferenceTarget(jCas);
    rt1.addToIndexes();

    ReferenceTarget rt2 = new ReferenceTarget(jCas);
    rt2.addToIndexes();

    Location locationRT1 = new Location(jCas);
    locationRT1.setBegin(0);
    locationRT1.setEnd(0);
    locationRT1.setReferent(rt1);
    locationRT1.addToIndexes();

    Location locationRT2 = new Location(jCas);
    locationRT2.setBegin(0);
    locationRT2.setEnd(0);
    locationRT2.setReferent(rt2);
    locationRT2.addToIndexes();

    Location locationRT2Again = new Location(jCas);
    locationRT2Again.setBegin(1);
    locationRT2Again.setEnd(1);
    locationRT2Again.setReferent(rt2);
    locationRT2Again.addToIndexes();

    Metadata md2 = new Metadata(jCas);
    md2.setBegin(0);
    md2.setEnd(0);
    md2.addToIndexes();

    support.mergeWithExisting(locationRT2Again, location, locationRT1, locationRT2);
    support.mergeWithExisting(md2, md);

    List<Location> locations = new ArrayList<>(JCasUtil.select(jCas, Location.class));
    List<Metadata> mds = new ArrayList<>(JCasUtil.select(jCas, Metadata.class));

    assertEquals(1, locations.size());
    assertEquals(locationRT2Again, locations.get(0));

    assertEquals(1, mds.size());
    assertEquals(md2, mds.get(0));

    assertFalse(
        support.getDocumentHistory(jCas).getHistory(locationRT2Again.getInternalId()).isEmpty());
  }

  @Test
  public void testMergeWithDontMergeDifferentReferent() {
    UimaSupport support = new UimaSupport(PIPELINE, UimaSupportTest.class, history, monitor, false);

    ReferenceTarget rt1 = new ReferenceTarget(jCas);
    rt1.addToIndexes();

    ReferenceTarget rt2 = new ReferenceTarget(jCas);
    rt2.addToIndexes();

    Location locationRT1 = new Location(jCas);
    locationRT1.setBegin(0);
    locationRT1.setEnd(0);
    locationRT1.setReferent(rt1);
    locationRT1.addToIndexes();

    Location locationRT2 = new Location(jCas);
    locationRT2.setBegin(0);
    locationRT2.setEnd(0);
    locationRT2.setReferent(rt2);
    locationRT2.addToIndexes();

    Location locationRT2Again = new Location(jCas);
    locationRT2Again.setBegin(1);
    locationRT2Again.setEnd(1);
    locationRT2Again.setReferent(rt2);
    locationRT2Again.addToIndexes();

    Metadata md2 = new Metadata(jCas);
    md2.setBegin(0);
    md2.setEnd(0);
    md2.addToIndexes();

    support.mergeWithExisting(locationRT2Again, location, locationRT1, locationRT2);
    support.mergeWithExisting(md2, md);

    List<Location> locations = new ArrayList<>(JCasUtil.select(jCas, Location.class));
    List<Metadata> mds = new ArrayList<>(JCasUtil.select(jCas, Metadata.class));

    assertEquals(3, locations.size());
    assertFalse(locations.contains(locationRT2));
    assertTrue(locations.contains(locationRT2Again));
    assertTrue(locations.contains(location));
    assertTrue(locations.contains(locationRT1));

    assertEquals(1, mds.size());
    assertEquals(md2, mds.get(0));

    assertTrue(support.getDocumentHistory(jCas).getHistory(locationRT1.getInternalId()).isEmpty());
    assertFalse(
        support.getDocumentHistory(jCas).getHistory(locationRT2Again.getInternalId()).isEmpty());
  }

  @Test
  public void testMergeWithRelation() {
    UimaSupport support = new UimaSupport(PIPELINE, UimaSupportTest.class, history, monitor, false);

    Person p1 = new Person(jCas);
    p1.setBegin(0);
    p1.setEnd(0);
    p1.addToIndexes();

    Person p2 = new Person(jCas);
    p2.setBegin(0);
    p2.setEnd(0);
    p2.addToIndexes();

    Person p3 = new Person(jCas);
    p3.setBegin(1);
    p3.setEnd(1);
    p3.addToIndexes();

    Relation r = new Relation(jCas);
    r.setBegin(0);
    r.setEnd(1);
    r.setSource(p2);
    r.setTarget(p3);
    r.addToIndexes();

    support.mergeWithExisting(p1, p2);

    List<Person> people = new ArrayList<>(JCasUtil.select(jCas, Person.class));
    List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));

    assertEquals(2, people.size());
    assertEquals(p1, people.get(0));
    assertEquals(p3, people.get(1));

    assertEquals(1, relations.size());
    assertEquals(r, relations.get(0));
    assertEquals(p1, relations.get(0).getSource());
    assertEquals(p3, relations.get(0).getTarget());
  }

  @Test
  public void testGetDocumentAnnotation() {
    UimaSupport support = new UimaSupport(PIPELINE, UimaSupportTest.class, history, monitor, false);
    assertNotNull(support.getDocumentAnnotation(jCas));
  }

  @Test
  public void testGetRelations() {
    UimaSupport support = new UimaSupport(PIPELINE, UimaSupportTest.class, history, monitor, false);

    Person p1 = new Person(jCas);
    p1.setBegin(0);
    p1.setEnd(0);
    p1.addToIndexes();

    Person p2 = new Person(jCas);
    p2.setBegin(0);
    p2.setEnd(0);
    p2.addToIndexes();

    Person p3 = new Person(jCas);
    p3.setBegin(1);
    p3.setEnd(1);
    p3.addToIndexes();

    Relation r1 = new Relation(jCas);
    r1.setBegin(0);
    r1.setEnd(1);
    r1.setSource(p2);
    r1.setTarget(p3);
    r1.addToIndexes();

    Relation r2 = new Relation(jCas);
    r2.setBegin(0);
    r2.setEnd(0);
    r2.setSource(p1);
    r2.setTarget(p2);
    r2.addToIndexes();

    List<Relation> relations = new ArrayList<>(support.getRelations(p1));

    assertEquals(1, relations.size());
    assertEquals(r2, relations.get(0));

    relations = new ArrayList<>(support.getRelations(p2));

    assertEquals(2, relations.size());
    assertEquals(r1, relations.get(0));
    assertEquals(r2, relations.get(1));

    relations = new ArrayList<>(support.getRelations(p3));

    assertEquals(1, relations.size());
    assertEquals(r1, relations.get(0));
  }
}
