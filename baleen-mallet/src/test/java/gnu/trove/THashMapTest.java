// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class THashMapTest {

  private THashMap<String, String> tHashMap;

  private static final String HELLO = "hello";
  private static final String WORLD = "world";

  @Mock private gnu.trove.map.hash.THashMap<String, String> delegate;
  @Mock private ObjectInput objectInput;
  @Mock private ObjectOutput objectOutput;

  @Before
  public void setUp() throws Exception {
    tHashMap = new THashMap<>(delegate);
  }

  @Test
  public void testClone() throws CloneNotSupportedException {
    THashMap<String, String> t2 = tHashMap.clone();
    assertEquals(t2, new THashMap<>());
  }

  @Test
  public void testCapacity() {
    tHashMap.capacity();
    verify(delegate).capacity();
  }

  @Test
  public void testTHashMapSetup() {
    tHashMap.setUp(1);
    verify(delegate).setUp(1);
  }

  @Test
  public void testPut() {
    tHashMap.put(HELLO, WORLD);
    verify(delegate).put(HELLO, WORLD);
  }

  @Test
  public void testContains() {
    tHashMap.contains(HELLO);
    verify(delegate).contains(HELLO);
  }

  @Test
  public void testContainsKey() {
    tHashMap.containsKey(WORLD);
    verify(delegate).containsKey(WORLD);
  }

  @Test
  public void testPutIfAbsent() {
    tHashMap.putIfAbsent(HELLO, WORLD);
    verify(delegate).putIfAbsent(HELLO, WORLD);
  }

  @Test
  public void testIsEmpty() {
    tHashMap.isEmpty();
    verify(delegate).isEmpty();
  }

  @Test
  public void testSize() {
    tHashMap.size();
    verify(delegate).size();
  }

  @Test
  public void testEquals() {
    assertEquals(tHashMap, new THashMap<>());
    assertNotEquals(tHashMap, tHashMap.toString());
  }

  @Test
  public void testHashCode() {
    assertEquals(tHashMap.hashCode(), new THashMap<>(delegate).hashCode());
  }

  @Test
  public void testCompact() {
    tHashMap.compact();
    verify(delegate).compact();
  }

  @Test
  public void testAutoCompactionFactor() {
    tHashMap.setAutoCompactionFactor(1.0F);
    verify(delegate).setAutoCompactionFactor(1.0F);
  }

  @Test
  public void testGetAutoCompactionFactor() {
    tHashMap.getAutoCompactionFactor();
    verify(delegate).getAutoCompactionFactor();
  }

  @Test
  public void testTempDisableAutoCompaction() {
    tHashMap.tempDisableAutoCompaction();
    verify(delegate).tempDisableAutoCompaction();
  }

  @Test
  public void testReEnableAutoCompaction() {
    tHashMap.reenableAutoCompaction(true);
    verify(delegate).reenableAutoCompaction(true);
  }

  @Test
  public void testGet() {
    tHashMap.get(HELLO);
    verify(delegate).get(HELLO);
  }

  @Test
  public void testClear() {
    tHashMap.clear();
    verify(delegate).clear();
  }

  @Test
  public void testRemove() {
    tHashMap.remove(HELLO);
    verify(delegate).remove(HELLO);
  }

  @Test
  public void testRemoveAt() {
    tHashMap.removeAt(0);
    verify(delegate).removeAt(0);
  }

  @Test
  public void testValues() {
    tHashMap.values();
    verify(delegate).values();
  }

  @Test
  public void keySet() {
    tHashMap.keySet();
    verify(delegate).keySet();
  }

  @Test
  public void testEntrySet() {
    tHashMap.entrySet();
    verify(delegate).entrySet();
  }

  @Test
  public void testContainsValue() {
    tHashMap.containsValue(WORLD);
    verify(delegate).containsValue(WORLD);
  }

  @Test
  public void testPutAll() {
    Map<String, String> map = new HashMap<>();
    tHashMap.putAll(map);
    verify(delegate).putAll(map);
  }

  @Test
  public void testReadExternal() throws IOException, ClassNotFoundException {
    tHashMap.readExternal(objectInput);
    verify(delegate).readExternal(objectInput);
  }

  @Test
  public void testWriteExternal() throws IOException {
    tHashMap.writeExternal(objectOutput);
    verify(delegate).writeExternal(objectOutput);
  }

  @Test
  public void testGetOrDefault() {
    tHashMap.getOrDefault(HELLO, WORLD);
    verify(delegate).getOrDefault(HELLO, WORLD);
  }

  @Test
  public void testForEach() {
    BiConsumer<String, String> biConsumer = (s, s2) -> {};
    tHashMap.forEach(biConsumer);
    verify(delegate).forEach(biConsumer);
  }

  @Test
  public void testReplaceAll() {
    BiFunction<String, String, String> biFunction = (s1, s2) -> s1;
    tHashMap.replaceAll(biFunction);
    verify(delegate).replaceAll(biFunction);
  }

  @Test
  public void testReplace() {
    tHashMap.replace(HELLO, WORLD, "other");
    verify(delegate).replace(HELLO, WORLD, "other");
  }

  @Test
  public void testComputeIfAbsent() {
    Function<String, String> function = s1 -> s1;
    tHashMap.computeIfAbsent(HELLO, function);
    verify(delegate).computeIfAbsent(HELLO, function);
  }

  @Test
  public void testComputeIfPresent() {
    BiFunction<String, String, String> biFunction = (s1, s2) -> s1;
    tHashMap.computeIfPresent(HELLO, biFunction);
    verify(delegate).computeIfPresent(HELLO, biFunction);
  }

  @Test
  public void testCompute() {
    BiFunction<String, String, String> biFunction = (s1, s2) -> s1;
    tHashMap.compute(HELLO, biFunction);
    verify(delegate).compute(HELLO, biFunction);
  }

  @Test
  public void testMerge() {
    BiFunction<String, String, String> biFunction = (s1, s2) -> s1;
    tHashMap.merge(HELLO, WORLD, biFunction);
    verify(delegate).merge(HELLO, WORLD, biFunction);
  }

  @Test
  public void testForEachKey() {
    THashMap<String, String> tHashMapWithRealDelegate = new THashMap<>();
    tHashMapWithRealDelegate.put(WORLD, HELLO);
    assertFalse(tHashMapWithRealDelegate.forEachKey(key -> key.equals(HELLO)));
    tHashMapWithRealDelegate.clear();
    tHashMapWithRealDelegate.put(HELLO, WORLD);
    assertTrue(tHashMapWithRealDelegate.forEachKey(key -> key.equals(HELLO)));
  }

  @Test
  public void testForEachValue() {
    THashMap<String, String> tHashMapWithRealDelegate = new THashMap<>();
    tHashMapWithRealDelegate.put(WORLD, HELLO);
    assertFalse(tHashMapWithRealDelegate.forEachValue(value -> value.equals(WORLD)));
    tHashMapWithRealDelegate.clear();
    tHashMapWithRealDelegate.put(HELLO, WORLD);
    assertTrue(tHashMapWithRealDelegate.forEachValue(value -> value.equals(WORLD)));
  }

  @Test
  public void testForEachEntry() {
    THashMap<String, String> tHashMapWithRealDelegate = new THashMap<>();
    tHashMapWithRealDelegate.put(WORLD, HELLO);
    assertFalse(
        tHashMapWithRealDelegate.forEachEntry(
            (key, value) -> key.equals(HELLO) && value.equals(WORLD)));
    tHashMapWithRealDelegate.clear();
    tHashMapWithRealDelegate.put(HELLO, WORLD);
    assertTrue(
        tHashMapWithRealDelegate.forEachEntry(
            (key, value) -> key.equals(HELLO) && value.equals(WORLD)));
  }

  @Test
  public void testRetainEntries() {
    THashMap<String, String> tHashMapWithRealDelegate = new THashMap<>();
    tHashMapWithRealDelegate.put(HELLO, WORLD);
    tHashMapWithRealDelegate.put(WORLD, HELLO);
    tHashMapWithRealDelegate.retainEntries(
        (key, value) -> key.equals(HELLO) && value.equals(WORLD));
    assertEquals(1, tHashMapWithRealDelegate.size());
  }

  @Test
  public void testTransformValues() {
    THashMap<String, String> tHashMapWithRealDelegate = new THashMap<>();
    tHashMapWithRealDelegate.put(HELLO, WORLD);
    tHashMapWithRealDelegate.transformValues(value -> "New");
    assertEquals("New", tHashMapWithRealDelegate.get(HELLO));
  }
}
