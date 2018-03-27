// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.util;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

public class StringArgumentsHandlerTest {

  @Test
  public void testArgumentsArePutInMap() throws Exception {
    Map<String, String> keyValuePairs =
        new StringArgumentsHandler(new String[] {"key1", "value1", "key2", "value2"})
            .createStringsMap();

    assertEquals("key1 should map to value1", "value1", keyValuePairs.get("key1"));
    assertEquals("key2 should map to value2", "value2", keyValuePairs.get("key2"));
  }

  @Test(expected = Exception.class)
  public void testExceptionIsThrownIfInvalidNumberOfArgumentsAreProvided() throws Exception {
    new StringArgumentsHandler(new String[] {"invalid", "number of", "arguments"})
        .createStringsMap();
  }
}
