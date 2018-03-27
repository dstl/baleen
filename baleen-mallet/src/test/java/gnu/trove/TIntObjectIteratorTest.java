// Copyright (c) Committed Software 2018, opensource@committed.io
package gnu.trove;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TIntObjectIteratorTest {

  private TIntObjectIterator<String> tIntObjectIterator;

  @Mock private gnu.trove.iterator.TIntObjectIterator<String> delegate;

  @Before
  public void setUp() throws Exception {
    tIntObjectIterator = new TIntObjectIterator<>(delegate);
  }

  @Test
  public void testHasNext() {
    tIntObjectIterator.hasNext();
    verify(delegate).hasNext();
  }

  @Test
  public void testAdvance() {
    tIntObjectIterator.advance();
    verify(delegate).advance();
  }

  @Test
  public void testRemove() {
    tIntObjectIterator.remove();
    verify(delegate).remove();
  }

  @Test
  public void testKey() {
    tIntObjectIterator.key();
    verify(delegate).key();
  }

  @Test
  public void testValue() {
    tIntObjectIterator.value();
    verify(delegate).value();
  }

  @Test
  public void testSetValue() {
    tIntObjectIterator.setValue("value");
    verify(delegate).setValue("value");
  }
}
