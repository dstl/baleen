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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import gnu.trove.map.TObjectIntMap;

@RunWith(MockitoJUnitRunner.class)
public class TObjectIntHashMapTest {

  private static final int ZERO = 0;
  private static final String KEY = "key";

  private TObjectIntHashMap<String> tObjectIntHashMap;

  @Mock private gnu.trove.map.hash.TObjectIntHashMap<String> delegate;
  @Mock private ObjectOutput objectOutput;
  @Mock private ObjectInput objectInput;

  @Before
  public void setUp() throws Exception {
    tObjectIntHashMap = new TObjectIntHashMap<>(delegate);
  }

  @Test
  public void testMapConstructor() {
    gnu.trove.map.hash.TIntObjectHashMap<String> original =
        new gnu.trove.map.hash.TIntObjectHashMap<>();
    original.put(0, "zero");
    TIntObjectHashMap<String> tIntObjectHashMapFromMap = new TIntObjectHashMap<>(original);
    assertEquals("zero", tIntObjectHashMapFromMap.get(0));
  }

  @Test
  public void testClone() throws CloneNotSupportedException {
    TIntObjectHashMap<String> original = new TIntObjectHashMap<>();
    assertEquals(original, original.clone());
  }

  @Test
  public void testCapacity() {
    tObjectIntHashMap.capacity();
    verify(delegate).capacity();
  }

  @Test
  public void testNoEntryValue() {
    tObjectIntHashMap.getNoEntryValue();
    verify(delegate).getNoEntryValue();
  }

  @Test
  public void testIsEmpty() {
    tObjectIntHashMap.isEmpty();
    verify(delegate).isEmpty();
  }

  @Test
  public void testContains() {
    tObjectIntHashMap.contains(0);
    verify(delegate).contains(0);
  }

  @Test
  public void testSize() {
    tObjectIntHashMap.size();
    verify(delegate).size();
  }

  @Test
  public void testForEach() {
    TObjectIntHashMap<String> tIntObjectHashMapWithRealDelegate = new TObjectIntHashMap<>();
    tIntObjectHashMapWithRealDelegate.put("key", ZERO);
    assertFalse(tIntObjectHashMapWithRealDelegate.forEach(i -> i.equals("other")));
  }

  @Test
  public void testForEachEntry() {
    TObjectIntHashMap<String> tIntObjectHashMapWithRealDelegate = new TObjectIntHashMap<>();
    tIntObjectHashMapWithRealDelegate.put("key", ZERO);
    assertTrue(
        tIntObjectHashMapWithRealDelegate.forEachEntry((a, b) -> a.equals("key") && b == ZERO));
    assertFalse(
        tIntObjectHashMapWithRealDelegate.forEachEntry(
            (a, b) -> a.equals("notequal") && b == ZERO));
  }

  @Test
  public void testForEachKey() {
    TObjectIntHashMap<String> tIntObjectHashMapWithRealDelegate = new TObjectIntHashMap<>();
    tIntObjectHashMapWithRealDelegate.put(KEY, ZERO);
    assertTrue(tIntObjectHashMapWithRealDelegate.forEachKey(k -> k.equals(KEY)));
    assertFalse(tIntObjectHashMapWithRealDelegate.forEachKey(k -> k.equals("otherValue")));
  }

  @Test
  public void testForEachValue() {
    TObjectIntHashMap<String> tIntObjectHashMapWithRealDelegate = new TObjectIntHashMap<>();
    tIntObjectHashMapWithRealDelegate.put(KEY, ZERO);
    assertTrue(tIntObjectHashMapWithRealDelegate.forEachValue(v -> v == ZERO));
    assertFalse(tIntObjectHashMapWithRealDelegate.forEachValue(v -> v == 100));
  }

  @Test
  public void testEnsureCapacity() {
    tObjectIntHashMap.ensureCapacity(1);
    verify(delegate).ensureCapacity(1);
  }

  @Test
  public void testGetNoEntryValue() {
    tObjectIntHashMap.getNoEntryValue();
    verify(delegate).getNoEntryValue();
  }

  @Test
  public void testContainsKey() {
    tObjectIntHashMap.containsKey(0);
    verify(delegate).containsKey(0);
  }

  @Test
  public void testContainsValue() {
    tObjectIntHashMap.containsValue(ZERO);
    verify(delegate).containsValue(ZERO);
  }

  @Test
  public void testCompact() {
    tObjectIntHashMap.compact();
    verify(delegate).compact();
  }

  @Test
  public void testGet() {
    tObjectIntHashMap.get(0);
    verify(delegate).get(0);
  }

  @Test
  public void testPut() {
    tObjectIntHashMap.put(KEY, ZERO);
    verify(delegate).put(KEY, ZERO);
  }

  @Test
  public void testPutIfAbsent() {
    tObjectIntHashMap.putIfAbsent(KEY, ZERO);
    verify(delegate).putIfAbsent(KEY, ZERO);
  }

  @Test
  public void testSetAutoCompactionFactor() {
    tObjectIntHashMap.setAutoCompactionFactor(1.0F);
    verify(delegate).setAutoCompactionFactor(1.0F);
  }

  @Test
  public void testRemove() {
    tObjectIntHashMap.remove(0);
    verify(delegate).remove(0);
  }

  @Test
  public void testPutAll() {
    Map<String, Integer> map = new HashMap<>();
    tObjectIntHashMap.putAll(map);
    verify(delegate).putAll(map);
  }

  @Test
  public void testPutAllTObjectIntMap() {
    TObjectIntMap<String> map = new gnu.trove.map.hash.TObjectIntHashMap<>();
    tObjectIntHashMap.putAll(map);
    verify(delegate).putAll(map);
  }

  @Test
  public void testGetAutoCompactionFactor() {
    tObjectIntHashMap.getAutoCompactionFactor();
    verify(delegate).getAutoCompactionFactor();
  }

  @Test
  public void testTrimToSize() {
    tObjectIntHashMap.trimToSize();
    verify(delegate).trimToSize();
  }

  @Test
  public void testClear() {
    tObjectIntHashMap.clear();
    verify(delegate).clear();
  }

  @Test
  public void testKeySet() {
    tObjectIntHashMap.keySet();
    verify(delegate).keySet();
  }

  @Test
  public void testKeys() {
    tObjectIntHashMap.keys();
    verify(delegate).keys();
  }

  @Test
  public void testKeysDestinationArray() {
    String[] strings = new String[3];
    tObjectIntHashMap.keys(strings);
    verify(delegate).keys(strings);
  }

  @Test
  public void testValueCollection() {
    tObjectIntHashMap.valueCollection();
    verify(delegate).valueCollection();
  }

  @Test
  public void testValues() {
    tObjectIntHashMap.values();
    verify(delegate).values();
  }

  @Test
  public void testValuesDestinationArray() {
    int[] ints = new int[3];
    tObjectIntHashMap.values(ints);
    verify(delegate).values(ints);
  }

  @Test
  public void testTempDisableAutoCompaction() {
    tObjectIntHashMap.tempDisableAutoCompaction();
    verify(delegate).tempDisableAutoCompaction();
  }

  @Test
  public void testReEnableAutoCompaction() {
    tObjectIntHashMap.reenableAutoCompaction(true);
    verify(delegate).reenableAutoCompaction(true);
  }

  @Test
  public void testIterator() {
    tObjectIntHashMap.iterator();
    verify(delegate).iterator();
  }

  @Test
  public void testRetainEntries() {
    TObjectIntHashMap<String> tIntObjectHashMapWithRealDelegate = new TObjectIntHashMap<>();
    tIntObjectHashMapWithRealDelegate.put(KEY, ZERO);
    tIntObjectHashMapWithRealDelegate.put("otherKey", 1);
    assertEquals(2, tIntObjectHashMapWithRealDelegate.size());
    tIntObjectHashMapWithRealDelegate.retainEntries((k, v) -> k.equals(KEY) && v == ZERO);
    assertEquals(1, tIntObjectHashMapWithRealDelegate.size());
    assertEquals(ZERO, tIntObjectHashMapWithRealDelegate.get(0));
  }

  @Test
  public void testTransformValues() {
    TObjectIntHashMap<String> tObjectIntHashMapWithRealDelegate = new TObjectIntHashMap<>();
    tObjectIntHashMapWithRealDelegate.put(KEY, ZERO);
    tObjectIntHashMapWithRealDelegate.transformValues(v -> 1);
    assertEquals("New value should be 1", 1, tObjectIntHashMapWithRealDelegate.get(KEY));
    assertEquals("Size should be 1", 1, tObjectIntHashMapWithRealDelegate.size());
  }

  @Test
  public void testEquals() {
    assertEquals(tObjectIntHashMap, new TObjectIntHashMap<>(delegate));
    assertNotEquals(tObjectIntHashMap, tObjectIntHashMap.toString());
  }

  @Test
  public void testHashCode() {
    assertEquals(new TIntIntHashMap().hashCode(), new TObjectIntHashMap<Object>().hashCode());
    assertNotEquals(new TIntIntHashMap().hashCode(), "hello".hashCode());
  }

  @Test
  public void testReadExternal() throws IOException, ClassNotFoundException {
    tObjectIntHashMap.readExternal(objectInput);
    verify(delegate).readExternal(objectInput);
  }

  @Test
  public void testWriteExternal() throws IOException {
    tObjectIntHashMap.writeExternal(objectOutput);
    verify(delegate).writeExternal(objectOutput);
  }
}
