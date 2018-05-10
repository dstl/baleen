// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph.value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.gov.dstl.baleen.graph.coerce.Id;
import uk.gov.dstl.baleen.graph.coerce.ToString;
import uk.gov.dstl.baleen.graph.coerce.ValueCoercer;

public class ValueCoercerTest {

  @Test
  public void idTest() {
    ValueCoercer id = new Id();
    assertEquals("test", id.coerce("test"));
    assertEquals(1, id.coerce(1));
    Object object = new Object();
    assertEquals(object, id.coerce(object));
  }

  @Test
  public void stringTest() {
    ValueCoercer id = new ToString();
    assertEquals("test", id.coerce("test"));
    assertEquals("1", id.coerce(1));
    assertTrue(id.coerce(new Object()) instanceof String);
  }
}
