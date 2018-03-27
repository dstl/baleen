// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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

import gnu.trove.function.TIntFunction;
import gnu.trove.procedure.TIntIntProcedure;
import gnu.trove.procedure.TIntProcedure;

@RunWith(MockitoJUnitRunner.class)
public class TIntIntHashMapTest {

  private TIntIntHashMap tIntIntHashMap;
  @Mock private gnu.trove.map.hash.TIntIntHashMap delegate;
  @Mock private ObjectInput objectInput;
  @Mock private ObjectOutput objectOutput;

  @Before
  public void setUp() throws Exception {
    tIntIntHashMap = new TIntIntHashMap(delegate);
  }

  @Test
  public void testClone() throws CloneNotSupportedException {
    TIntIntHashMap tIntIntHashMapToClone = new TIntIntHashMap();
    tIntIntHashMapToClone.clone();
    assertEquals(tIntIntHashMapToClone, new TIntIntHashMap());
  }

  @Test
  public void testCapacity() {
    tIntIntHashMap.capacity();
    verify(delegate).capacity();
  }

  @Test
  public void testGetNoEntryKey() {
    tIntIntHashMap.getNoEntryKey();
    verify(delegate).getNoEntryKey();
  }

  @Test
  public void testGetNoEntryValue() {
    tIntIntHashMap.getNoEntryValue();
    verify(delegate).getNoEntryValue();
  }

  @Test
  public void testSize() {
    tIntIntHashMap.size();
    verify(delegate).size();
  }

  @Test
  public void testContains() {
    tIntIntHashMap.contains(0);
    verify(delegate).contains(0);
  }

  @Test
  public void testEnsureCapacity() {
    tIntIntHashMap.ensureCapacity(1);
    verify(delegate).ensureCapacity(1);
  }

  @Test
  public void testForEach() {
    TIntProcedure procedure = i -> true;
    tIntIntHashMap.forEach(procedure);
    verify(delegate).forEach(procedure);
  }

  @Test
  public void testCompact() {
    tIntIntHashMap.compact();
    verify(delegate).compact();
  }

  @Test
  public void testPut() {
    tIntIntHashMap.put(0, 1);
    verify(delegate).put(0, 1);
  }

  @Test
  public void testPutIfAbsent() {
    tIntIntHashMap.putIfAbsent(0, 1);
    verify(delegate).putIfAbsent(0, 1);
  }

  @Test
  public void testSetAutoCompactionFactor() {
    tIntIntHashMap.setAutoCompactionFactor(1.0F);
    verify(delegate).setAutoCompactionFactor(1.0F);
  }

  @Test
  public void testPutAllMap() {
    Map<Integer, Integer> map = new HashMap<>();
    tIntIntHashMap.putAll(map);
    verify(delegate).putAll(map);
  }

  @Test
  public void testPutAllTIntIntMap() {
    gnu.trove.map.hash.TIntIntHashMap map = new gnu.trove.map.hash.TIntIntHashMap();
    tIntIntHashMap.putAll(map);
    verify(delegate).putAll(new gnu.trove.map.hash.TIntIntHashMap());
  }

  @Test
  public void testGet() {
    tIntIntHashMap.get(0);
    verify(delegate).get(0);
  }

  @Test
  public void testGetAutoCompletionFactor() {
    tIntIntHashMap.getAutoCompactionFactor();
    verify(delegate).getAutoCompactionFactor();
  }

  @Test
  public void testClear() {
    tIntIntHashMap.clear();
    verify(delegate).clear();
  }

  @Test
  public void testTrimToSize() {
    tIntIntHashMap.trimToSize();
    verify(delegate).trimToSize();
  }

  @Test
  public void testIsEmpty() {
    tIntIntHashMap.isEmpty();
    verify(delegate).isEmpty();
  }

  @Test
  public void testRemove() {
    tIntIntHashMap.remove(0);
    verify(delegate).remove(0);
  }

  @Test
  public void testKeySet() {
    tIntIntHashMap.keySet();
    verify(delegate).keySet();
  }

  @Test
  public void testKeys() {
    tIntIntHashMap.keys();
    verify(delegate).keys();
  }

  @Test
  public void testKeysArray() {
    int[] ints = new int[3];
    tIntIntHashMap.keys(ints);
    verify(delegate).keys(ints);
  }

  @Test
  public void testValueCollection() {
    tIntIntHashMap.valueCollection();
    verify(delegate).valueCollection();
  }

  @Test
  public void testValues() {
    tIntIntHashMap.values();
    verify(delegate).values();
  }

  @Test
  public void testValuesArray() {
    int[] ints = new int[3];
    tIntIntHashMap.values(ints);
    verify(delegate).values(ints);
  }

  @Test
  public void testTempDisableAutoCompaction() {
    tIntIntHashMap.tempDisableAutoCompaction();
    verify(delegate).tempDisableAutoCompaction();
  }

  @Test
  public void testReEnableAutoCompaction() {
    tIntIntHashMap.reenableAutoCompaction(true);
    verify(delegate).reenableAutoCompaction(true);
  }

  @Test
  public void testContainsValue() {
    tIntIntHashMap.containsValue(0);
    verify(delegate).containsValue(0);
  }

  @Test
  public void testContainsKey() {
    tIntIntHashMap.containsKey(0);
    verify(delegate).containsKey(0);
  }

  @Test
  public void testIterator() {
    tIntIntHashMap.iterator();
    verify(delegate).iterator();
  }

  @Test
  public void testForEachKey() {
    TIntProcedure procedure = i -> true;
    tIntIntHashMap.forEachKey(procedure);
    verify(delegate).forEachKey(procedure);
  }

  @Test
  public void testForEachValue() {
    TIntProcedure procedure = i -> true;
    tIntIntHashMap.forEachValue(procedure);
    verify(delegate).forEachValue(procedure);
  }

  @Test
  public void testForEachEntry() {
    TIntIntProcedure procedure = (k, v) -> true;
    tIntIntHashMap.forEachEntry(procedure);
    verify(delegate).forEachEntry(procedure);
  }

  @Test
  public void testTransformValues() {
    TIntFunction function = i -> i;
    tIntIntHashMap.transformValues(function);
    verify(delegate).transformValues(function);
  }

  @Test
  public void testRetainEntries() {
    TIntIntProcedure procedure = (k, v) -> false;
    tIntIntHashMap.retainEntries(procedure);
    verify(delegate).retainEntries(procedure);
  }

  @Test
  public void testIncrement() {
    tIntIntHashMap.increment(0);
    verify(delegate).increment(0);
  }

  @Test
  public void testAdjustValue() {
    tIntIntHashMap.adjustValue(0, 1);
    verify(delegate).adjustValue(0, 1);
  }

  @Test
  public void testAdjustOrPutValue() {
    tIntIntHashMap.adjustOrPutValue(0, 1, 1);
    verify(delegate).adjustOrPutValue(0, 1, 1);
  }

  @Test
  public void testEquals() {
    assertEquals(new TIntIntHashMap(), new TIntIntHashMap());
    assertNotEquals(new TIntIntHashMap(), new TIntIntHashMap().toString());
  }

  @Test
  public void testHashCode() {
    assertEquals(new TIntIntHashMap().hashCode(), new TIntIntHashMap().hashCode());
    assertNotEquals(new TIntIntHashMap().hashCode(), new TIntIntHashMap().toString().hashCode());
  }

  @Test
  public void testReadExternal() throws IOException, ClassNotFoundException {
    tIntIntHashMap.readExternal(objectInput);
    verify(delegate).readExternal(objectInput);
  }

  @Test
  public void testWriteExternal() throws IOException {
    tIntIntHashMap.writeExternal(objectOutput);
    verify(delegate).writeExternal(objectOutput);
  }
}
