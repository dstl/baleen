// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class Neo4JValueCoercerTest {

  @Test
  public void checkOnlyValueTypesReturned() {
    Neo4JValueCoercer neo4jValueCoercer = new Neo4JValueCoercer();
    assertTrue(neo4jValueCoercer.coerce(new Object()) instanceof String);
    assertEquals(1l, neo4jValueCoercer.coerce(Long.valueOf(1l)));
    assertEquals(1.0, neo4jValueCoercer.coerce(Double.valueOf(1.0)));
    assertEquals("1", neo4jValueCoercer.coerce(Integer.valueOf(1)));
    assertEquals(1l, neo4jValueCoercer.coerce(1l));
    assertEquals(1.0, neo4jValueCoercer.coerce(1.0));
    assertEquals("1", neo4jValueCoercer.coerce(1));
    assertEquals("test", neo4jValueCoercer.coerce("test"));
    assertEquals("[1, test]", neo4jValueCoercer.coerce(ImmutableList.of(1, "test")));
  }
}
