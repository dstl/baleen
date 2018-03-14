// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.patterns.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class InteractionTypeDefinitionTest {
  @Test
  public void testProperties() {
    InteractionTypeDefinition itd = new InteractionTypeDefinition("type", "subtype", "pos");

    assertEquals("type", itd.getType());
    assertEquals("subtype", itd.getSubType());
    assertEquals("pos", itd.getPos());
  }

  @Test
  public void testEquals() {
    InteractionTypeDefinition itd1 = new InteractionTypeDefinition("type", "subtype", "pos");
    InteractionTypeDefinition itd1a = new InteractionTypeDefinition("type", "subtype", "pos");
    InteractionTypeDefinition itd2 = new InteractionTypeDefinition("type2", "subtype", "pos");
    InteractionTypeDefinition itd3 = new InteractionTypeDefinition("type", "subtype2", "pos");
    InteractionTypeDefinition itd4 = new InteractionTypeDefinition("type", "subtype", "pos2");

    InteractionTypeDefinition itdn1 = new InteractionTypeDefinition(null, "subtype", "pos");
    InteractionTypeDefinition itdn1a = new InteractionTypeDefinition(null, "subtype", "pos");
    InteractionTypeDefinition itdn2 = new InteractionTypeDefinition("type", null, "pos");
    InteractionTypeDefinition itdn2a = new InteractionTypeDefinition("type", null, "pos");
    InteractionTypeDefinition itdn3 = new InteractionTypeDefinition("type", "subtype", null);
    InteractionTypeDefinition itdn3a = new InteractionTypeDefinition("type", "subtype", null);

    assertEquals(itd1, itd1a);
    assertNotEquals(itd1, itd2);
    assertNotEquals(itd1, itd3);
    assertNotEquals(itd1, itd4);

    assertNotEquals(itd1, null);
    assertNotEquals(itd1, "type");

    assertNotEquals(itd1, itdn1);
    assertEquals(itdn1, itdn1a);
    assertNotEquals(itd1, itdn2);
    assertEquals(itdn2, itdn2a);
    assertNotEquals(itd1, itdn3);
    assertEquals(itdn3, itdn3a);

    assertEquals(itd1.hashCode(), itd1a.hashCode());
    assertNotEquals(itd1.hashCode(), itd2.hashCode());
  }
}
