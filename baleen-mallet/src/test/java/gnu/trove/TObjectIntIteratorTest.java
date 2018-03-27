// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TObjectIntIteratorTest {

  private TObjectIntIterator<Integer> iterator;

  @Mock private gnu.trove.iterator.TObjectIntIterator<Integer> delegate;

  @Before
  public void setUp() throws Exception {
    iterator = new TObjectIntIterator<>(delegate);
  }

  @Test
  public void testHasNext() {
    iterator.hasNext();
    verify(delegate).hasNext();
  }

  @Test
  public void testAdvance() {
    iterator.advance();
    verify(delegate).advance();
  }

  @Test
  public void testRemove() {
    iterator.remove();
    verify(delegate).remove();
  }

  @Test
  public void testKey() {
    iterator.key();
    verify(delegate).key();
  }

  @Test
  public void testValue() {
    iterator.value();
    verify(delegate).value();
  }

  @Test
  public void testSetValue() {
    iterator.setValue(0);
    verify(delegate).setValue(0);
  }
}
