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
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import gnu.trove.function.TDoubleFunction;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.hash.TDoubleHashSet;

@RunWith(MockitoJUnitRunner.class)
public class TDoubleArrayListTest {

  private gnu.trove.TDoubleArrayList tDoubleArrayList;

  @Spy private gnu.trove.list.array.TDoubleArrayList delegate;
  @Mock private ObjectInput objectInput;
  @Mock private ObjectOutput objectOutput;

  @Before
  public void setUp() {
    tDoubleArrayList = new TDoubleArrayList(delegate);
  }

  @After
  public void tearDown() throws Exception {
    tDoubleArrayList.clear();
    delegate.clear();
  }

  @Test
  public void testClone() throws CloneNotSupportedException {
    TDoubleArrayList t2 = tDoubleArrayList.clone();
    assertEquals(t2, tDoubleArrayList);
  }

  @Test
  public void testToNativeArray() {
    tDoubleArrayList.toNativeArray();
    verify(delegate).toArray();
  }

  @Test
  public void testAdd() {
    tDoubleArrayList.add(1.0);
    verify(delegate).add(1.0);
  }

  @Test
  public void testAddArrayWithOffset() {
    double[] vals = new double[3];
    tDoubleArrayList.add(vals, 0, 1);
    verify(delegate).add(vals, 0, 1);
  }

  @Test
  public void testAddArray() {
    double[] vals = new double[3];
    tDoubleArrayList.add(vals);
    verify(delegate).add(vals);
  }

  @Test
  public void testAddAllCollection() {
    Collection<Double> doubles = new HashSet<>();
    tDoubleArrayList.addAll(doubles);
    verify(delegate).addAll(doubles);
  }

  @Test
  public void addAllArray() {
    double[] doubles = new double[3];
    tDoubleArrayList.addAll(doubles);
    verify(delegate).addAll(doubles);
  }

  @Test
  public void testAddAllTDoubleCollection() {
    TDoubleCollection doubleCollection = new TDoubleHashSet();
    tDoubleArrayList.addAll(doubleCollection);
    verify(delegate).addAll(doubleCollection);
  }

  @Test
  public void testBinarySearchArgs() {
    tDoubleArrayList.binarySearch(3.0, 1, 0);
    verify(delegate).binarySearch(3.0, 1, 0);
  }

  @Test
  public void testBinarySearch() {
    tDoubleArrayList.binarySearch(1.0);
    verify(delegate).binarySearch(1.0);
  }

  @Test
  public void testClear() {
    tDoubleArrayList.clear();
    verify(delegate).clear();
  }

  @Test
  public void testClearCapacity() {
    tDoubleArrayList.clear(1);
    verify(delegate).clear(1);
  }

  @Test
  public void testContainsAll() {
    double[] doubles = new double[2];
    tDoubleArrayList.containsAll(doubles);
    verify(delegate).containsAll(doubles);
  }

  @Test
  public void testContainsAllCollection() {
    Collection<Double> doubles = new HashSet<>();
    tDoubleArrayList.containsAll(doubles);
    verify(delegate).containsAll(doubles);
  }

  @Test
  public void testContainsAllDoubleCollection() {
    TDoubleCollection doubleCollection = new TDoubleHashSet();
    tDoubleArrayList.containsAll(doubleCollection);
    verify(delegate).containsAll(doubleCollection);
  }

  @Test
  public void testEnsureCapacity() {
    tDoubleArrayList.ensureCapacity(1);
    verify(delegate).ensureCapacity(1);
  }

  @Test
  @SuppressWarnings("unlikely-arg-type")
  public void testEquals() {
    assertTrue(new TDoubleArrayList().equals(new TDoubleArrayList()));
    assertFalse(new TDoubleArrayList().equals(new TDoubleArrayList().toString()));
  }

  @Test
  public void testFill() {
    tDoubleArrayList.fill(1.0);
    verify(delegate).fill(1.0);
  }

  @Test
  public void testFillFrom() {
    tDoubleArrayList.fill(0, 1, 1.0);
    verify(delegate).fill(0, 1, 1.0);
  }

  @Test
  public void testForEach() {
    TDoubleProcedure procedure = value -> false;

    tDoubleArrayList.forEach(procedure);
    verify(delegate).forEach(procedure);
  }

  @Test
  public void testForEachDescending() {
    TDoubleProcedure procedure = v -> false;
    tDoubleArrayList.forEachDescending(procedure);
    verify(delegate).forEachDescending(procedure);
  }

  @Test
  public void testGet() {
    tDoubleArrayList.add(1.0);
    tDoubleArrayList.get(0);
    verify(delegate).get(0);
  }

  @Test
  public void testGetNoEntryValue() {
    tDoubleArrayList.getNoEntryValue();
    verify(delegate).getNoEntryValue();
  }

  @Test
  public void testGetQuick() {
    tDoubleArrayList.getQuick(0);
    verify(delegate).getQuick(0);
  }

  @Test
  public void testGrep() {
    TDoubleProcedure procedure = value -> false;
    tDoubleArrayList.grep(procedure);
    verify(delegate).grep(procedure);
  }

  @Test
  public void testHashCode() {
    assertEquals(new TDoubleArrayList().hashCode(), new TDoubleArrayList().hashCode());
    assertNotEquals(new TDoubleArrayList().hashCode(), "hello".hashCode());
  }

  @Test
  public void testIndexOf() {
    tDoubleArrayList.indexOf(1.0);
    verify(delegate).indexOf(1.0);
  }

  @Test
  public void testIndexOfWithArg() {
    tDoubleArrayList.indexOf(1, 1.0);
    verify(delegate).indexOf(1, 1.0);
  }

  @Test
  public void testInsert() {
    tDoubleArrayList.insert(0, 1.0);
    verify(delegate).insert(0, 1.0);
  }

  @Test
  public void testInsertArray() {
    double[] doubles = new double[2];
    tDoubleArrayList.insert(0, doubles);
    verify(delegate).insert(0, doubles);
  }

  @Test
  public void testInsertArrayOffset() {
    double[] doubles = new double[2];
    tDoubleArrayList.insert(0, doubles, 0, 2);
    verify(delegate).insert(0, doubles, 0, 2);
  }

  @Test
  public void testInverseGrep() {
    TDoubleProcedure procedure = v -> false;
    tDoubleArrayList.inverseGrep(procedure);
    verify(delegate).inverseGrep(procedure);
  }

  @Test
  public void testIsEmpty() {
    tDoubleArrayList.isEmpty();
    verify(delegate).isEmpty();
  }

  @Test
  public void testIterator() {
    tDoubleArrayList.iterator();
    verify(delegate).iterator();
  }

  @Test
  public void testLastIndexOf() {
    tDoubleArrayList.lastIndexOf(1.0);
    verify(delegate).lastIndexOf(1.0);
  }

  @Test
  public void testLastIndexOfArg() {
    tDoubleArrayList.lastIndexOf(1, 1.0);
    verify(delegate).lastIndexOf(1, 1.0);
  }

  @Test
  public void testMax() {
    tDoubleArrayList.add(1.0);
    tDoubleArrayList.max();
    verify(delegate).max();
  }

  @Test
  public void testMin() {
    tDoubleArrayList.add(1.0);
    tDoubleArrayList.min();
    verify(delegate).min();
  }

  @Test
  public void testReadExternal() throws IOException, ClassNotFoundException {
    tDoubleArrayList.readExternal(objectInput);
    verify(delegate).readExternal(objectInput);
  }

  @Test
  public void testRemove() {
    tDoubleArrayList.remove(1.0);
    verify(delegate).remove(1.0);
  }

  @Test
  public void testRemoveOffset() {
    tDoubleArrayList.remove(0, 0);
    verify(delegate).remove(0, 0);
  }

  @Test
  public void testRemoveCollection() {
    Collection<Double> doubleCollection = new HashSet<>();
    tDoubleArrayList.removeAll(doubleCollection);
    verify(delegate).removeAll(doubleCollection);
  }

  @Test
  public void testRemoveArray() {
    double[] doubles = new double[2];
    tDoubleArrayList.removeAll(doubles);
    verify(delegate).removeAll(doubles);
  }

  @Test
  public void testRemoveTDoubleCollection() {
    TDoubleCollection tDoubleCollection = new TDoubleHashSet();
    tDoubleArrayList.removeAll(tDoubleCollection);
    verify(delegate).removeAll(tDoubleCollection);
  }

  @Test
  public void testRemoveAt() {
    tDoubleArrayList.add(1.0);
    tDoubleArrayList.removeAt(0);
    verify(delegate).removeAt(0);
  }

  @Test
  public void testReplace() {
    tDoubleArrayList.add(1.0);
    tDoubleArrayList.replace(0, 2.0);
    verify(delegate).replace(0, 2.0);
  }

  @Test
  public void testReset() {
    tDoubleArrayList.reset();
    verify(delegate).reset();
  }

  @Test
  public void testResetQuick() {
    tDoubleArrayList.resetQuick();
    verify(delegate).resetQuick();
  }

  @Test
  public void testRetainAll() {
    Collection<Double> doubles = new HashSet<>();
    tDoubleArrayList.retainAll(doubles);
    verify(delegate).retainAll(doubles);
  }

  @Test
  public void testRetainTDoubleCollection() {
    TDoubleCollection doubles = new TDoubleHashSet();
    tDoubleArrayList.retainAll(doubles);
    verify(delegate).retainAll(doubles);
  }

  @Test
  public void testRetainArray() {
    double[] doubles = new double[3];
    tDoubleArrayList.retainAll(doubles);
    verify(delegate).retainAll(doubles);
  }

  @Test
  public void testReverse() {
    tDoubleArrayList.reverse();
    verify(delegate).reverse();
  }

  @Test
  public void testReverseArgs() {
    tDoubleArrayList.reverse(0, 0);
    verify(delegate).reverse(0, 0);
  }

  @Test
  public void testSet() {
    tDoubleArrayList.add(0.0);
    tDoubleArrayList.set(0, 1.0);
    verify(delegate).set(0, 1.0);
  }

  @Test
  public void testSetArrayOffset() {
    tDoubleArrayList.add(0.0);
    double[] doubles = new double[1];
    tDoubleArrayList.set(0, doubles, 0, 1);
  }

  @Test
  public void testSetArray() {
    tDoubleArrayList.add(0.0);
    double[] doubles = new double[1];
    tDoubleArrayList.set(0, doubles);
    verify(delegate).set(0, doubles);
  }

  @Test
  public void testSetQuick() {
    tDoubleArrayList.add(0.0);
    tDoubleArrayList.setQuick(0, 1.0);
    verify(delegate).setQuick(0, 1.0);
  }

  @Test
  public void testShuffle() {
    Random random = new Random();
    tDoubleArrayList.shuffle(random);
    verify(delegate).shuffle(random);
  }

  @Test
  public void testSize() {
    tDoubleArrayList.size();
    verify(delegate).size();
  }

  @Test
  public void testSort() {
    tDoubleArrayList.sort();
    verify(delegate).sort();
  }

  @Test
  public void testSortIndexes() {
    tDoubleArrayList.sort(0, 1);
    verify(delegate).sort(0, 1);
  }

  @Test
  public void testSubList() {
    tDoubleArrayList.subList(0, 0);
    verify(delegate).subList(0, 0);
  }

  @Test
  public void testSum() {
    tDoubleArrayList.sum();
    verify(delegate).sum();
  }

  @Test
  public void testToArrayNoParameters() {
    tDoubleArrayList.toArray();
    verify(delegate).toArray();
  }

  @Test
  public void testToArray() {
    double[] doubleArray = new double[10];
    tDoubleArrayList.toArray(doubleArray);
    verify(delegate).toArray(doubleArray);
  }

  @Test
  public void testToArrayPositions() {
    tDoubleArrayList.add(0.0);
    double[] destination = new double[1];
    tDoubleArrayList.toArray(destination, 0, 0, 1);
    verify(delegate).toArray(destination, 0, 0, 1);
  }

  @Test
  public void testToDestinationArray() {
    tDoubleArrayList.add(0.0);
    double[] destination = new double[1];
    tDoubleArrayList.toArray(destination, 0, 1);
    verify(delegate).toArray(destination, 0, 1);
  }

  @Test
  public void testToArrayOffset() {
    tDoubleArrayList.add(1.0);
    tDoubleArrayList.toArray(0, 1);
    verify(delegate).toArray(0, 1);
  }

  @Test
  public void testTransformValues() {
    TDoubleFunction function = value -> 0;
    tDoubleArrayList.transformValues(function);
    verify(delegate).transformValues(function);
  }

  @Test
  public void testTrimToSize() {
    tDoubleArrayList.trimToSize();
    verify(delegate).trimToSize();
  }

  @Test
  public void testWriteExternal() throws IOException {
    tDoubleArrayList.writeExternal(objectOutput);
    verify(delegate).writeExternal(objectOutput);
  }
}
