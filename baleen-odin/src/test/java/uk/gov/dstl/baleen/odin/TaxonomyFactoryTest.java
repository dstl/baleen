// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.odin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.fit.factory.JCasFactory;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Event;

@SuppressWarnings("unchecked")
public class TaxonomyFactoryTest {

  private TaxonomyFactory taxonomyFactory;
  private Collection<Object> taxonomy;
  private TypeSystem typeSystem;

  @Before
  public void setup() throws UIMAException {
    String[] includedTypes = {Entity.class.getName(), Event.class.getName()};
    typeSystem = JCasFactory.createJCas().getTypeSystem();
    taxonomyFactory = new TaxonomyFactory(typeSystem, includedTypes);
    taxonomy = taxonomyFactory.create();
  }

  @Test
  public void testEntityAndEventAreIncludedInTaxonomy() {
    assertEquals(2, taxonomy.size());
  }

  @Test
  public void testEntityTypeContainsAll16SubTypes() throws CASRuntimeException, UIMAException {
    Object next = taxonomy.iterator().next();
    assertTrue(next instanceof Map<?, ?>);
    Map<String, Collection<Object>> map = (Map<String, Collection<Object>>) next;
    assertEquals(
        "Value in taxonomy for Entity should match the type system",
        getEntityChildrenCount(),
        map.get("Entity").size());
  }

  private int getEntityChildrenCount() throws CASRuntimeException, UIMAException {
    Type type = typeSystem.getType(Entity.class.getName());
    return typeSystem.getDirectSubtypes(type).size();
  }

  @Test
  public void testEventHasNoChildren() {
    Iterator<Object> iterator = taxonomy.iterator();
    iterator.next();
    Object next = iterator.next();
    assertTrue(next instanceof Map<?, ?>);
    Map<String, Collection<Object>> map = (Map<String, Collection<Object>>) next;
    assertEquals("Value in taxonomy for Event should have no children", 0, map.get("Event").size());
  }

  @Test
  public void testShortNamesAllowed() throws UIMAException {

    Collection<Object> shortNamed =
        new TaxonomyFactory(
                new String[] {Entity.class.getSimpleName(), Event.class.getSimpleName()})
            .create();
    Object next = shortNamed.iterator().next();
    assertTrue(next instanceof Map<?, ?>);
    Map<String, Collection<Object>> map = (Map<String, Collection<Object>>) next;
    assertTrue("There is a value in taxonomy for Entity", map.get("Entity").size() > 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMissingType() throws UIMAException {
    new TaxonomyFactory(new String[] {"Missing"}).create();
  }
}
