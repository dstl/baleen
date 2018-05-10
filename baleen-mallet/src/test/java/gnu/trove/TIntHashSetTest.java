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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TIntHashSetTest {

  private TIntHashSet tIntHashSet;

  @Mock private gnu.trove.set.hash.TIntHashSet delegate;
  @Mock private ObjectOutput objectOutput;
  @Mock private ObjectInput objectInput;

  @Before
  public void setUp() throws Exception {
    tIntHashSet = new TIntHashSet(delegate);
  }

  @Test
  public void testCapacity() {
    tIntHashSet.capacity();
    verify(delegate).capacity();
  }

  @Test
  public void testGetNoEntryValue() {
    tIntHashSet.getNoEntryValue();
    verify(delegate).getNoEntryValue();
  }

  @Test
  public void testIsEmpty() {
    tIntHashSet.isEmpty();
    verify(delegate).isEmpty();
  }

  @Test
  public void testIterator() {
    tIntHashSet.iterator();
    verify(delegate).iterator();
  }

  @Test
  public void testContains() {
    tIntHashSet.contains(1);
    verify(delegate).contains(1);
  }

  @Test
  public void testSize() {
    tIntHashSet.size();
    verify(delegate).size();
  }

  @Test
  public void testToArray() {
    tIntHashSet.toArray();
    delegate.toArray();
  }

  @Test
  public void testEnsureCapacity() {
    tIntHashSet.ensureCapacity(1);
    verify(delegate).ensureCapacity(1);
  }

  @Test
  public void testToArrayInts() {
    int[] ints = new int[3];
    tIntHashSet.toArray(ints);
    verify(delegate).toArray(ints);
  }

  @Test
  public void testAdd() {
    tIntHashSet.add(1);
    verify(delegate).add(1);
  }

  @Test
  public void testCompact() {
    tIntHashSet.compact();
    verify(delegate).compact();
  }

  @Test
  public void testRemove() {
    tIntHashSet.remove(1);
    verify(delegate).remove(1);
  }

  @Test
  public void testContainsAllArray() {
    int[] ints = new int[3];
    tIntHashSet.containsAll(ints);
    verify(delegate).containsAll(ints);
  }

  @Test
  public void testContainsAllCollection() {
    Collection<Integer> collection = new HashSet<>();
    tIntHashSet.containsAll(collection);
    verify(delegate).containsAll(collection);
  }

  @Test
  public void testContainsAllTIntCollection() {
    gnu.trove.set.hash.TIntHashSet tIntCollection = new gnu.trove.set.hash.TIntHashSet();
    tIntHashSet.containsAll(tIntCollection);
    verify(delegate).containsAll(tIntCollection);
  }

  @Test
  public void testSetAutoCompactionFactor() {
    tIntHashSet.setAutoCompactionFactor(1.0F);
    verify(delegate).setAutoCompactionFactor(1.0F);
  }

  @Test
  public void testAddAllCollection() {
    Collection<Integer> collection = new HashSet<>();
    tIntHashSet.addAll(collection);
    verify(delegate).addAll(collection);
  }

  @Test
  public void testAddAllTIntCollection() {
    TIntCollection tIntCollection = new gnu.trove.set.hash.TIntHashSet();
    tIntHashSet.addAll(tIntCollection);
    verify(delegate).addAll(tIntCollection);
  }

  @Test
  public void testAddAllArray() {
    int[] ints = new int[3];
    tIntHashSet.addAll(ints);
    verify(delegate).addAll(ints);
  }

  @Test
  public void testGetAutoCompactionFactor() {
    tIntHashSet.getAutoCompactionFactor();
    verify(delegate).getAutoCompactionFactor();
  }

  @Test
  public void testRetainAllCollection() {
    Collection<Integer> collection = new HashSet<>();
    tIntHashSet.retainAll(collection);
    verify(delegate).retainAll(collection);
  }

  @Test
  public void testRetainAllTIntCollection() {
    TIntCollection collection = new gnu.trove.set.hash.TIntHashSet();
    tIntHashSet.retainAll(collection);
    verify(delegate).retainAll(collection);
  }

  @Test
  public void testRetainAllArray() {
    int[] ints = new int[3];
    tIntHashSet.retainAll(ints);
    verify(delegate).retainAll(ints);
  }

  @Test
  public void testRemoveAllCollection() {
    Collection<Integer> collection = new HashSet<>();
    tIntHashSet.removeAll(collection);
    verify(delegate).removeAll(collection);
  }

  @Test
  public void testRemoveAllTIntCollection() {
    TIntCollection collection = new gnu.trove.set.hash.TIntHashSet();
    tIntHashSet.removeAll(collection);
    verify(delegate).removeAll(collection);
  }

  @Test
  public void testRemoveAllArray() {
    int[] ints = new int[3];
    tIntHashSet.removeAll(ints);
    verify(delegate).removeAll(ints);
  }

  @Test
  public void testTempDisableAutoCompaction() {
    tIntHashSet.tempDisableAutoCompaction();
    verify(delegate).tempDisableAutoCompaction();
  }

  @Test
  public void testReEnableAutoCompaction() {
    tIntHashSet.reenableAutoCompaction(true);
    verify(delegate).reenableAutoCompaction(true);
  }

  @Test
  public void testClear() {
    tIntHashSet.clear();
    verify(delegate).clear();
  }

  @Test
  public void testEquals() {
    assertEquals(tIntHashSet, new TIntHashSet(delegate));
    assertNotEquals(tIntHashSet, tIntHashSet.toString());
  }

  @Test
  public void testHashCode() {
    assertEquals(new TIntHashSet().hashCode(), new TIntHashSet().hashCode());
    assertNotEquals(new TIntHashSetTest().hashCode(), new Integer(5).hashCode());
  }

  @Test
  public void tetWriteExternal() throws IOException {
    tIntHashSet.writeExternal(objectOutput);
    verify(delegate).writeExternal(objectOutput);
  }

  @Test
  public void testReadExternal() throws IOException, ClassNotFoundException {
    tIntHashSet.readExternal(objectInput);
    verify(delegate).readExternal(objectInput);
  }

  @Test
  public void testForEach() {
    TIntHashSet tIntHashSetWithRealDelegate = new TIntHashSet(new gnu.trove.set.hash.TIntHashSet());
    tIntHashSetWithRealDelegate.add(1);
    assertTrue(tIntHashSetWithRealDelegate.forEach(value -> value == 1));
    tIntHashSetWithRealDelegate.add(2);
    assertFalse(tIntHashSetWithRealDelegate.forEach(value -> value == 1));
  }
}
