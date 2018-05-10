// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import gnu.trove.function.TIntFunction;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.hash.TIntHashSet;

@RunWith(MockitoJUnitRunner.class)
public class TIntArrayListTest {

  private TIntArrayList tIntArrayList;

  @Mock private gnu.trove.list.array.TIntArrayList delegate;
  @Mock private ObjectOutput objectOutput;
  @Mock private ObjectInput objectInput;

  @Before
  public void setUp() throws Exception {
    tIntArrayList = new TIntArrayList(delegate);
  }

  @Test
  public void testToNativeArray() {
    tIntArrayList.toNativeArray();
    verify(delegate).toArray();
  }

  @Test
  public void testClone() throws CloneNotSupportedException {
    TIntArrayList tIntArrayListToClone = new TIntArrayList();
    TIntArrayList clone = tIntArrayListToClone.clone();
    assertEquals(clone, tIntArrayListToClone);
  }

  @Test
  public void testGetNoEntryValue() {
    tIntArrayList.getNoEntryValue();
    verify(delegate).getNoEntryValue();
  }

  @Test
  public void testEnsureCapacity() {
    tIntArrayList.ensureCapacity(1);
    verify(delegate).ensureCapacity(1);
  }

  @Test
  public void testSize() {
    tIntArrayList.size();
    verify(delegate).size();
  }

  @Test
  public void testIsEmpty() {
    tIntArrayList.isEmpty();
    verify(delegate).isEmpty();
  }

  @Test
  public void testTrimToSize() {
    tIntArrayList.trimToSize();
    verify(delegate).trimToSize();
  }

  @Test
  public void testAdd() {
    tIntArrayList.add(1);
    verify(delegate).add(1);
  }

  @Test
  public void testAddArray() {
    int[] ints = new int[2];
    tIntArrayList.add(ints);
    verify(delegate).add(ints);
  }

  @Test
  public void testAddArrayOffset() {
    int[] ints = new int[2];
    tIntArrayList.add(ints, 0, 2);
    verify(delegate).add(ints, 0, 2);
  }

  @Test
  public void testInsert() {
    tIntArrayList.insert(0, 1);
    verify(delegate).insert(0, 1);
  }

  @Test
  public void testInsertArray() {
    int[] ints = new int[3];
    tIntArrayList.insert(0, ints);
    verify(delegate).insert(0, ints);
  }

  @Test
  public void testInsertArrayValueOffset() {
    int[] ints = new int[3];
    tIntArrayList.insert(0, ints, 0, 3);
    verify(delegate).insert(0, ints, 0, 3);
  }

  @Test
  public void testGet() {
    tIntArrayList.get(0);
    verify(delegate).get(0);
  }

  @Test
  public void testGetQuick() {
    tIntArrayList.getQuick(0);
    verify(delegate).getQuick(0);
  }

  @Test
  public void testSet() {
    tIntArrayList.set(0, 0);
    verify(delegate).set(0, 0);
  }

  @Test
  public void testReplace() {
    tIntArrayList.replace(0, 0);
    verify(delegate).replace(0, 0);
  }

  @Test
  public void testSetArray() {
    int[] ints = new int[2];
    tIntArrayList.set(0, ints);
    verify(delegate).set(0, ints);
  }

  @Test
  public void testSetQuick() {
    tIntArrayList.setQuick(0, 0);
    verify(delegate).setQuick(0, 0);
  }

  @Test
  public void testClear() {
    tIntArrayList.clear();
    verify(delegate).clear();
  }

  @Test
  public void testClearCapacity() {
    tIntArrayList.clear(0);
    verify(delegate).clear(0);
  }

  @Test
  public void testReset() {
    tIntArrayList.reset();
    verify(delegate).reset();
  }

  @Test
  public void testResetQuick() {
    tIntArrayList.resetQuick();
    verify(delegate).resetQuick();
  }

  @Test
  public void testRemove() {
    tIntArrayList.remove(0);
    verify(delegate).remove(0);
  }

  @Test
  public void testRemoveAt() {
    tIntArrayList.removeAt(0);
    verify(delegate).removeAt(0);
  }

  @Test
  public void testRemoveLength() {
    tIntArrayList.remove(0, 1);
    verify(delegate).remove(0, 1);
  }

  @Test
  public void testIterator() {
    tIntArrayList.iterator();
    verify(delegate).iterator();
  }

  @Test
  public void testContainsAllCollection() {
    Collection<Integer> collection = new HashSet<>();
    tIntArrayList.containsAll(collection);
    verify(delegate).containsAll(collection);
  }

  @Test
  public void testContainsAllTIntCollection() {
    TIntHashSet collection = new TIntHashSet();
    tIntArrayList.containsAll(collection);
    verify(delegate).containsAll(collection);
  }

  @Test
  public void testContainsAllArray() {
    int[] ints = new int[2];
    tIntArrayList.containsAll(ints);
    verify(delegate).containsAll(ints);
  }

  @Test
  public void testAddAllCollection() {
    Collection<Integer> collection = new HashSet<>();
    tIntArrayList.addAll(collection);
    verify(delegate).addAll(collection);
  }

  @Test
  public void testAddAllTIntCollection() {
    TIntHashSet collection = new TIntHashSet();
    tIntArrayList.addAll(collection);
    verify(delegate).addAll(collection);
  }

  @Test
  public void testAddAllArray() {
    int[] ints = new int[2];
    tIntArrayList.addAll(ints);
    verify(delegate).addAll(ints);
  }

  @Test
  public void testRetainAllCollection() {
    Collection<Integer> collection = new HashSet<>();
    tIntArrayList.retainAll(collection);
    verify(delegate).retainAll(collection);
  }

  @Test
  public void testRetainAllTIntCollection() {
    TIntHashSet collection = new TIntHashSet();
    tIntArrayList.retainAll(collection);
    verify(delegate).retainAll(collection);
  }

  @Test
  public void testRetainAllArray() {
    int[] ints = new int[2];
    tIntArrayList.retainAll(ints);
    verify(delegate).retainAll(ints);
  }

  @Test
  public void testRemoveAllCollection() {
    Collection<Integer> collection = new HashSet<>();
    tIntArrayList.removeAll(collection);
    verify(delegate).removeAll(collection);
  }

  @Test
  public void testRemoveAllTIntCollection() {
    TIntHashSet collection = new TIntHashSet();
    tIntArrayList.removeAll(collection);
    verify(delegate).removeAll(collection);
  }

  @Test
  public void testRemoveAllArray() {
    int[] ints = new int[2];
    tIntArrayList.removeAll(ints);
    verify(delegate).removeAll(ints);
  }

  @Test
  public void testTransformValues() {
    TIntFunction function = value -> value;
    tIntArrayList.transformValues(function);
    verify(delegate).transformValues(function);
  }

  @Test
  public void testReverse() {
    tIntArrayList.reverse();
    verify(delegate).reverse();
  }

  @Test
  public void testReverseRange() {
    tIntArrayList.reverse(0, 1);
    verify(delegate).reverse(0, 1);
  }

  @Test
  public void testShuffle() {
    Random random = new Random();
    tIntArrayList.shuffle(random);
    verify(delegate).shuffle(random);
  }

  @Test
  public void testSubList() {
    tIntArrayList.subList(0, 1);
    verify(delegate).subList(0, 1);
  }

  @Test
  public void testToArray() {
    tIntArrayList.toArray();
    verify(delegate).toArray();
  }

  @Test
  public void testToArrayOffset() {
    tIntArrayList.toArray(0, 1);
    verify(delegate).toArray(0, 1);
  }

  @Test
  public void testToDestinationArray() {
    int[] ints = new int[2];
    tIntArrayList.toArray(ints);
    verify(delegate).toArray(ints);
  }

  @Test
  public void testToDestinationArrayOffset() {
    int[] ints = new int[2];
    tIntArrayList.toArray(ints, 0, 2);
    verify(delegate).toArray(ints, 0, 2);
  }

  @Test
  public void testToDestinationArrayWithPositions() {
    int[] ints = new int[2];
    tIntArrayList.toArray(ints, 0, 0, 2);
    verify(delegate).toArray(ints, 0, 0, 2);
  }

  @Test
  public void testEquals() {
    assertEquals(new TIntArrayList(), new TIntArrayList());
    assertNotEquals(new TIntArrayList(), new TIntArrayList().toString());
  }

  @Test
  public void testHashCode() {
    assertEquals(new TIntArrayList().hashCode(), new TIntArrayList().hashCode());
    assertNotEquals(new TIntArrayList().hashCode(), "hello".hashCode());
  }

  @Test
  public void testForEach() {
    TIntProcedure procedure = i -> false;
    tIntArrayList.forEach(procedure);
    verify(delegate).forEach(procedure);
  }

  @Test
  public void testForEachDescending() {
    TIntProcedure procedure = i -> false;
    tIntArrayList.forEachDescending(procedure);
    verify(delegate).forEachDescending(procedure);
  }

  @Test
  public void testSort() {
    tIntArrayList.sort();
    verify(delegate).sort();
  }

  @Test
  public void testSortRange() {
    tIntArrayList.sort(0, 1);
    verify(delegate).sort(0, 1);
  }

  @Test
  public void testFill() {
    tIntArrayList.fill(0);
    verify(delegate).fill(0);
  }

  @Test
  public void testFillRange() {
    tIntArrayList.fill(0, 0, 2);
    verify(delegate).fill(0, 0, 2);
  }

  @Test
  public void testBinarySearch() {
    tIntArrayList.binarySearch(0);
    verify(delegate).binarySearch(0);
  }

  @Test
  public void testBinarySearchRange() {
    tIntArrayList.binarySearch(0, 0, 2);
    verify(delegate).binarySearch(0, 0, 2);
  }

  @Test
  public void testIndexOf() {
    tIntArrayList.indexOf(0);
    verify(delegate).indexOf(0);
  }

  @Test
  public void testIndexOfOffset() {
    tIntArrayList.indexOf(0, 1);
    verify(delegate).indexOf(0, 1);
  }

  @Test
  public void testLastIndexOf() {
    tIntArrayList.lastIndexOf(0);
    verify(delegate).lastIndexOf(0);
  }

  @Test
  public void testLastIndexOfOffset() {
    tIntArrayList.lastIndexOf(0, 1);
    verify(delegate).lastIndexOf(0, 1);
  }

  @Test
  public void testContains() {
    tIntArrayList.contains(0);
    verify(delegate).contains(0);
  }

  @Test
  public void testGrep() {
    TIntProcedure procedure = i -> true;
    tIntArrayList.grep(procedure);
    verify(delegate).grep(procedure);
  }

  @Test
  public void testInverseGrep() {
    TIntProcedure procedure = i -> true;
    tIntArrayList.inverseGrep(procedure);
    verify(delegate).inverseGrep(procedure);
  }

  @Test
  public void testMax() {
    tIntArrayList.max();
    verify(delegate).max();
  }

  @Test
  public void testMin() {
    tIntArrayList.min();
    verify(delegate).min();
  }

  @Test
  public void testSum() {
    tIntArrayList.sum();
    verify(delegate).sum();
  }

  @Test
  public void testReadExternal() throws IOException, ClassNotFoundException {
    tIntArrayList.readExternal(objectInput);
    verify(delegate).readExternal(objectInput);
  }

  @Test
  public void testWriteExternal() throws IOException {
    tIntArrayList.writeExternal(objectOutput);
    verify(delegate).writeExternal(objectOutput);
  }
}
