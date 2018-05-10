// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class IgnoreEmptyKeyMapDecoratorTest {

  @Test
  public void testValueAddedIfKeyExists() {
    Map<String, String> delegate = new HashMap<>();
    delegate.put("key", "value");
    Map<String, String> ignoreEmptyKeyMap = new IgnoreEmptyKeyMapDecorator<>(delegate);
    ignoreEmptyKeyMap.put("key", "newValue");

    assertEquals("key should be mapped to newValue", "newValue", ignoreEmptyKeyMap.get("key"));

    assertEquals("key should be mapped to newValue", "newValue", ignoreEmptyKeyMap.get("key"));
  }

  @Test
  public void testValueNotAddedIfKeyIsNullOrEmpty() {
    Map<String, String> delegate = new HashMap<>();
    Map<String, String> ignoreEmptyKeyMap = new IgnoreEmptyKeyMapDecorator<>(delegate);

    assertNull("key should not be mapped", ignoreEmptyKeyMap.put("", "newValue"));
    assertNull("key should not be mapped", ignoreEmptyKeyMap.put(null, "newValue"));
  }

  @Test
  public void testMapFunctions() {
    Map<String, String> delegate = new HashMap<>();
    delegate.put("key", "value");
    Map<String, String> ignoreEmptyKeyMap = new IgnoreEmptyKeyMapDecorator<>(delegate);
    assertTrue("should contain value", ignoreEmptyKeyMap.containsValue("value"));
    ignoreEmptyKeyMap.clear();
    assertEquals("Size should now be 0", 0, ignoreEmptyKeyMap.size());
    assertFalse("Should not contain key", ignoreEmptyKeyMap.containsKey("key"));
  }

  @Test
  public void testRemove() {
    Map<String, String> delegate = new HashMap<>();
    delegate.put("key", "value");
    Map<String, String> ignoreEmptyKeyMap = new IgnoreEmptyKeyMapDecorator<>(delegate);
    ignoreEmptyKeyMap.remove("key");
    assertEquals("Size should now be 0", 0, ignoreEmptyKeyMap.size());
  }

  @Test
  public void testKeySet() {
    Map<String, String> delegate = new HashMap<>();
    delegate.put("key", "value");
    Map<String, String> ignoreEmptyKeyMap = new IgnoreEmptyKeyMapDecorator<>(delegate);
    Set<String> keySet = ignoreEmptyKeyMap.keySet();
    assertEquals("Should have 1 key", 1, keySet.size());
  }

  @Test
  public void testValues() {
    Map<String, String> delegate = new HashMap<>();
    delegate.put("key", "value");
    Map<String, String> ignoreEmptyKeyMap = new IgnoreEmptyKeyMapDecorator<>(delegate);
    Collection<String> keySet = ignoreEmptyKeyMap.values();
    assertEquals("Should have 1 value", 1, keySet.size());
  }
}
