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
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class THashSetTest {

  private static final String HELLO = "hello";

  private THashSet<String> tHashSet;

  @Mock private gnu.trove.set.hash.THashSet<String> delegate;
  @Mock private ObjectOutput objectOutput;
  @Mock private ObjectInput objectInput;

  @Before
  public void setUp() throws Exception {
    tHashSet = new THashSet<>(delegate);
  }

  @Test
  public void testForEachwithConsumer() {
    Consumer<String> consumer = s -> {};
    tHashSet.forEach(consumer);
    verify(delegate).forEach(consumer);
  }

  @Test
  public void testCapacity() {
    tHashSet.capacity();
    verify(delegate).capacity();
  }

  @Test
  public void testAdd() {
    tHashSet.add(HELLO);
    verify(delegate).add(HELLO);
  }

  @Test
  public void testSetupTHashSet() {
    tHashSet.setUp(0);
    verify(delegate).setUp(0);
  }

  @Test
  public void testEquals() {
    assertEquals(tHashSet, new THashSet<>(delegate));
    assertNotEquals(tHashSet, tHashSet.toString());
  }

  @Test
  public void testHashCode() {
    assertEquals(new THashSet<>().hashCode(), new THashSet<>().hashCode());
    assertNotEquals(tHashSet.hashCode(), HELLO.hashCode());
  }

  @Test
  public void testForEach() {
    THashSet<String> tHashSetWithRealDelegate = new THashSet<>(new gnu.trove.set.hash.THashSet<>());
    tHashSetWithRealDelegate.add(HELLO);
    assertTrue(
        tHashSetWithRealDelegate.forEach(
            object -> {
              return object.equals(HELLO);
            }));

    assertFalse(
        tHashSetWithRealDelegate.forEach(
            object -> {
              return object.equals("world");
            }));
  }

  @Test
  public void testContains() {
    tHashSet.contains(HELLO);
    verify(delegate).contains(HELLO);
  }

  @Test
  public void testIsEmpty() {
    tHashSet.isEmpty();
    verify(delegate).isEmpty();
  }

  @Test
  public void testSize() {
    tHashSet.size();
    verify(delegate).size();
  }

  @Test
  public void testEnsureCapacity() {
    tHashSet.ensureCapacity(0);
    verify(delegate).ensureCapacity(0);
  }

  @Test
  public void testToArray() {
    tHashSet.toArray();
    verify(delegate).toArray();
  }

  @Test
  public void testToArrayWithArg() {
    String[] strings = new String[2];
    tHashSet.toArray(strings);
    verify(delegate).toArray(strings);
  }

  @Test
  public void testCompact() {
    tHashSet.compact();
    verify(delegate).compact();
  }

  @Test
  public void testClear() {
    tHashSet.clear();
    verify(delegate).clear();
  }

  @Test
  public void testRemove() {
    tHashSet.remove(HELLO);
    verify(delegate).remove(HELLO);
  }

  @Test
  public void testSetAutoCompactionFactor() {
    tHashSet.setAutoCompactionFactor(1.0F);
    verify(delegate).setAutoCompactionFactor(1.0F);
  }

  @Test
  public void testIterator() {
    tHashSet.iterator();
    verify(delegate).iterator();
  }

  @Test
  public void testContainsAll() {
    Collection<String> strings = new HashSet<>();
    tHashSet.containsAll(strings);
    verify(delegate).containsAll(strings);
  }

  @Test
  public void testGetAutoCompactionFactor() {
    tHashSet.getAutoCompactionFactor();
    verify(delegate).getAutoCompactionFactor();
  }

  @Test
  public void testTrimToSize() {
    tHashSet.trimToSize();
    verify(delegate).trimToSize();
  }

  @Test
  public void testAddAll() {
    Collection<String> strings = new HashSet<>();
    tHashSet.addAll(strings);
    verify(delegate).addAll(strings);
  }

  @Test
  public void testRemoveAll() {
    Collection<String> strings = new HashSet<>();
    tHashSet.removeAll(strings);
    verify(delegate).removeAll(strings);
  }

  @Test
  public void testRetainAll() {
    Collection<String> strings = new HashSet<>();
    tHashSet.retainAll(strings);
    verify(delegate).retainAll(strings);
  }

  @Test
  public void testTempDisableAutoCompaction() {
    tHashSet.tempDisableAutoCompaction();
    verify(delegate).tempDisableAutoCompaction();
  }

  @Test
  public void testReEnableAutoCompaction() {
    tHashSet.reenableAutoCompaction(true);
    verify(delegate).reenableAutoCompaction(true);
  }

  @Test
  public void testWriteExternal() throws IOException {
    tHashSet.writeExternal(objectOutput);
    verify(delegate).writeExternal(objectOutput);
  }

  @Test
  public void testReadExternal() throws IOException, ClassNotFoundException {
    tHashSet.readExternal(objectInput);
    verify(delegate).readExternal(objectInput);
  }

  @Test
  public void testSpliterator() {
    tHashSet.spliterator();
    verify(delegate).spliterator();
  }

  @Test
  public void testRemoveIf() {
    Predicate<String> filter = s -> s.equals(s);
    tHashSet.removeIf(filter);
    verify(delegate).removeIf(filter);
  }

  @Test
  public void testStream() {
    tHashSet.stream();
    verify(delegate).stream();
  }

  @Test
  public void testParallelStream() {
    tHashSet.parallelStream();
    verify(delegate).parallelStream();
  }
}
