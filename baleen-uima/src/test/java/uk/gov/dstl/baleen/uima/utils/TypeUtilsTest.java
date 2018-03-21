// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.types.semantic.Temporal;
import uk.gov.dstl.baleen.types.structure.Header;
import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.types.structure.TableHeader;
import uk.gov.dstl.baleen.uima.testing.JCasSingleton;

public class TypeUtilsTest {
  @Test
  public void testPerson() throws UIMAException {
    JCas jCas = JCasSingleton.getJCasInstance();
    Class<?> c = TypeUtils.getType("Person", jCas);

    assertEquals(Person.class, c);
  }

  @Test
  public void testTemporal() throws UIMAException {
    JCas jCas = JCasSingleton.getJCasInstance();
    Class<?> c = TypeUtils.getType("Temporal", jCas);

    assertEquals(Temporal.class, c);
  }

  @Test
  public void testRelation() throws UIMAException {
    JCas jCas = JCasSingleton.getJCasInstance();
    Class<?> c = TypeUtils.getType("Relation", jCas);

    assertEquals(Relation.class, c);
  }

  @Test
  public void testMissing() throws UIMAException {
    JCas jCas = JCasSingleton.getJCasInstance();
    Class<?> c = TypeUtils.getType("Missing", jCas);

    assertEquals(null, c);
  }

  @Test
  public void testPersonEntity() throws UIMAException, BaleenException {
    JCas jCas = JCasSingleton.getJCasInstance();
    Class<? extends Entity> c = TypeUtils.getEntityClass("Person", jCas);

    assertEquals(Person.class, c);
  }

  @Test
  public void testTemporalEntity() throws UIMAException, BaleenException {
    JCas jCas = JCasSingleton.getJCasInstance();
    Class<? extends Entity> c = TypeUtils.getEntityClass("Temporal", jCas);

    assertEquals(Temporal.class, c);
  }

  @Test
  public void testWhereNameEndMatches() throws UIMAException, BaleenException {
    Set<Class<? extends Structure>> c =
        TypeUtils.getTypeClasses(Structure.class, "Header", "TableHeader");
    assertEquals(2, c.size());
    assertTrue(c.contains(Header.class));
    assertTrue(c.contains(TableHeader.class));
  }

  @Test
  public void testRelationEntity() throws UIMAException {
    JCas jCas = JCasSingleton.getJCasInstance();

    try {
      TypeUtils.getEntityClass("Relation", jCas);
      fail("Expected exception not found");
    } catch (BaleenException e) {
      // Do nothing - exception expected
    }
  }

  @Test
  public void testMissingEntity() throws UIMAException {
    JCas jCas = JCasSingleton.getJCasInstance();

    try {
      TypeUtils.getEntityClass("Missing", jCas);
      fail("Expected exception not found");
    } catch (BaleenException e) {
      // Do nothing - exception expected
    }
  }

  @Test
  public void testCanGetTypeClass() throws ResourceInitializationException {
    Set<Class<? extends Entity>> typeClasses =
        TypeUtils.getTypeClasses(Entity.class, new String[] {Person.class.getSimpleName()});
    assertEquals(1, typeClasses.size());
    assertTrue(typeClasses.contains(Person.class));
  }
}
