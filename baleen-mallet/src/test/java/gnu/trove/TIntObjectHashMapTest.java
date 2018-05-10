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

import gnu.trove.map.TIntObjectMap;

@RunWith(MockitoJUnitRunner.class)
public class TIntObjectHashMapTest {

  private static final String ZERO = "zero";

  private TIntObjectHashMap<String> tIntObjectHashMap;

  @Mock private gnu.trove.map.hash.TIntObjectHashMap<String> delegate;
  @Mock private ObjectOutput objectOutput;
  @Mock private ObjectInput objectInput;

  @Before
  public void setUp() throws Exception {
    tIntObjectHashMap = new TIntObjectHashMap<>(delegate);
  }

  @Test
  public void testMapConstructor() {
    gnu.trove.map.hash.TIntObjectHashMap<String> original =
        new gnu.trove.map.hash.TIntObjectHashMap<>();
    original.put(0, ZERO);
    TIntObjectHashMap<String> tIntObjectHashMapFromMap = new TIntObjectHashMap<>(original);
    assertEquals(ZERO, tIntObjectHashMapFromMap.get(0));
  }

  @Test
  public void testClone() throws CloneNotSupportedException {
    TIntObjectHashMap<String> original = new TIntObjectHashMap<>();
    assertEquals(original, original.clone());
  }

  @Test
  public void testCapacity() {
    tIntObjectHashMap.capacity();
    verify(delegate).capacity();
  }

  @Test
  public void testNoEntryValue() {
    tIntObjectHashMap.getNoEntryValue();
    verify(delegate).getNoEntryValue();
  }

  @Test
  public void testNoEntryKey() {
    tIntObjectHashMap.getNoEntryKey();
    verify(delegate).getNoEntryKey();
  }

  @Test
  public void testIsEmpty() {
    tIntObjectHashMap.isEmpty();
    verify(delegate).isEmpty();
  }

  @Test
  public void testContains() {
    tIntObjectHashMap.contains(0);
    verify(delegate).contains(0);
  }

  @Test
  public void testSize() {
    tIntObjectHashMap.size();
    verify(delegate).size();
  }

  @Test
  public void testForEach() {
    TIntObjectHashMap<String> tIntObjectHashMapWithRealDelegate = new TIntObjectHashMap<>();
    tIntObjectHashMapWithRealDelegate.put(0, ZERO);
    assertFalse(tIntObjectHashMapWithRealDelegate.forEach(i -> i == 1));
  }

  @Test
  public void testForEachEntry() {
    TIntObjectHashMap<String> tIntObjectHashMapWithRealDelegate = new TIntObjectHashMap<>();
    tIntObjectHashMapWithRealDelegate.put(0, ZERO);
    assertTrue(tIntObjectHashMapWithRealDelegate.forEachEntry((a, b) -> a == 0 && b.equals(ZERO)));
    assertFalse(tIntObjectHashMapWithRealDelegate.forEachEntry((a, b) -> a == 1 && b.equals(ZERO)));
  }

  @Test
  public void testForEachKey() {
    TIntObjectHashMap<String> tIntObjectHashMapWithRealDelegate = new TIntObjectHashMap<>();
    tIntObjectHashMapWithRealDelegate.put(0, ZERO);
    assertTrue(tIntObjectHashMapWithRealDelegate.forEachKey(k -> k == 0));
    assertFalse(tIntObjectHashMapWithRealDelegate.forEachKey(k -> k == 1));
  }

  @Test
  public void testForEachValue() {
    TIntObjectHashMap<String> tIntObjectHashMapWithRealDelegate = new TIntObjectHashMap<>();
    tIntObjectHashMapWithRealDelegate.put(0, ZERO);
    assertTrue(tIntObjectHashMapWithRealDelegate.forEachValue(v -> v.equals(ZERO)));
    assertFalse(tIntObjectHashMapWithRealDelegate.forEachValue(v -> v.equals("hello")));
  }

  @Test
  public void testEnsureCapacity() {
    tIntObjectHashMap.ensureCapacity(1);
    verify(delegate).ensureCapacity(1);
  }

  @Test
  public void testGetNoEntryKey() {
    tIntObjectHashMap.getNoEntryKey();
    verify(delegate).getNoEntryKey();
  }

  @Test
  public void testContainsKey() {
    tIntObjectHashMap.containsKey(0);
    verify(delegate).containsKey(0);
  }

  @Test
  public void testContainsValue() {
    tIntObjectHashMap.containsValue(ZERO);
    verify(delegate).containsValue(ZERO);
  }

  @Test
  public void testCompact() {
    tIntObjectHashMap.compact();
    verify(delegate).compact();
  }

  @Test
  public void testGet() {
    tIntObjectHashMap.get(0);
    verify(delegate).get(0);
  }

  @Test
  public void testPut() {
    tIntObjectHashMap.put(0, ZERO);
    verify(delegate).put(0, ZERO);
  }

  @Test
  public void testPutIfAbsent() {
    tIntObjectHashMap.putIfAbsent(0, ZERO);
    verify(delegate).putIfAbsent(0, ZERO);
  }

  @Test
  public void testSetAutoCompactionFactor() {
    tIntObjectHashMap.setAutoCompactionFactor(1.0F);
    verify(delegate).setAutoCompactionFactor(1.0F);
  }

  @Test
  public void testRemove() {
    tIntObjectHashMap.remove(0);
    verify(delegate).remove(0);
  }

  @Test
  public void testPutAll() {
    Map<Integer, String> map = new HashMap<>();
    tIntObjectHashMap.putAll(map);
    verify(delegate).putAll(map);
  }

  @Test
  public void testPutAllTIntObjectMap() {
    TIntObjectMap<String> map = new gnu.trove.map.hash.TIntObjectHashMap<>();
    tIntObjectHashMap.putAll(map);
    verify(delegate).putAll(map);
  }

  @Test
  public void testGetAutoCompactionFactor() {
    tIntObjectHashMap.getAutoCompactionFactor();
    verify(delegate).getAutoCompactionFactor();
  }

  @Test
  public void testTrimToSize() {
    tIntObjectHashMap.trimToSize();
    verify(delegate).trimToSize();
  }

  @Test
  public void testClear() {
    tIntObjectHashMap.clear();
    verify(delegate).clear();
  }

  @Test
  public void testKeySet() {
    tIntObjectHashMap.keySet();
    verify(delegate).keySet();
  }

  @Test
  public void testKeys() {
    tIntObjectHashMap.keys();
    verify(delegate).keys();
  }

  @Test
  public void testKeysDestinationArray() {
    int[] ints = new int[3];
    tIntObjectHashMap.keys(ints);
    verify(delegate).keys(ints);
  }

  @Test
  public void testValueCollection() {
    tIntObjectHashMap.valueCollection();
    verify(delegate).valueCollection();
  }

  @Test
  public void testValues() {
    tIntObjectHashMap.values();
    verify(delegate).values();
  }

  @Test
  public void testValuesDestinationArray() {
    String[] strings = new String[3];
    tIntObjectHashMap.values(strings);
    verify(delegate).values(strings);
  }

  @Test
  public void testTempDisableAutoCompaction() {
    tIntObjectHashMap.tempDisableAutoCompaction();
    verify(delegate).tempDisableAutoCompaction();
  }

  @Test
  public void testReEnableAutoCompaction() {
    tIntObjectHashMap.reenableAutoCompaction(true);
    verify(delegate).reenableAutoCompaction(true);
  }

  @Test
  public void testIterator() {
    tIntObjectHashMap.iterator();
    verify(delegate).iterator();
  }

  @Test
  public void testRetainEntries() {
    TIntObjectHashMap<String> tIntObjectHashMapWithRealDelegate = new TIntObjectHashMap<>();
    tIntObjectHashMapWithRealDelegate.put(0, ZERO);
    tIntObjectHashMapWithRealDelegate.put(1, "one");
    assertEquals(2, tIntObjectHashMapWithRealDelegate.size());
    tIntObjectHashMapWithRealDelegate.retainEntries((k, v) -> k == 0 && v.equals(ZERO));
    assertEquals(1, tIntObjectHashMapWithRealDelegate.size());
    assertEquals(ZERO, tIntObjectHashMapWithRealDelegate.get(0));
  }

  @Test
  public void testTransformValues() {
    TIntObjectHashMap<String> tIntObjectHashMapWithRealDelegate = new TIntObjectHashMap<>();
    tIntObjectHashMapWithRealDelegate.put(0, ZERO);
    tIntObjectHashMapWithRealDelegate.transformValues(s -> "newValue");
    assertEquals("newValue", tIntObjectHashMapWithRealDelegate.get(0));
    assertEquals(1, tIntObjectHashMapWithRealDelegate.size());
  }

  @Test
  public void testEquals() {
    assertEquals(tIntObjectHashMap, new TIntObjectHashMap<>(delegate));
    assertNotEquals(tIntObjectHashMap, tIntObjectHashMap.toString());
  }

  @Test
  public void testHashCode() {
    assertEquals(new TIntIntHashMap().hashCode(), new TIntObjectHashMap<Object>().hashCode());
    assertNotEquals(new TIntIntHashMap().hashCode(), ZERO.hashCode());
  }

  @Test
  public void testReadExternal() throws IOException, ClassNotFoundException {
    tIntObjectHashMap.readExternal(objectInput);
    verify(delegate).readExternal(objectInput);
  }

  @Test
  public void testWriteExternal() throws IOException {
    tIntObjectHashMap.writeExternal(objectOutput);
    verify(delegate).writeExternal(objectOutput);
  }

  @Test
  public void testMapConstructorCopiesData() {
    TIntObjectHashMap<String> first = new TIntObjectHashMap<>();
    first.put(0, ZERO);
    TIntObjectHashMap<String> copy = new TIntObjectHashMap<>(first);
    assertEquals(ZERO, copy.get(0));
  }
}
