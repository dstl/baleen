// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.structure.Header;
import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.types.structure.TableHeader;

public class StructureUtilsTest {

  @Test
  public void testGetAllStructureClasses() throws ResourceInitializationException {
    Set<Class<? extends Structure>> structureClasses = StructureUtil.getStructureClasses();
    assertNotNull(structureClasses);
    assertEquals(38, structureClasses.size());
    assertFalse(structureClasses.contains(Structure.class));
  }

  @Test
  public void testWhereNameEndMatches() throws UIMAException, BaleenException {
    Set<Class<? extends Structure>> c = StructureUtil.getStructureClasses("Header", "TableHeader");
    assertEquals(2, c.size());
    assertTrue(c.contains(Header.class));
    assertTrue(c.contains(TableHeader.class));
  }
}
