// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import ch.qos.logback.classic.spi.ILoggingEvent;

import uk.gov.dstl.baleen.core.logging.builders.EvictingQueueAppender;
import uk.gov.dstl.baleen.core.logging.builders.EvictingQueueBuilder;

/** Tests for {@link EvictingQueueAppender}. */
public class EvictingQueueAppenderTest {

  @Test
  public void testBuilder() throws Exception {
    EvictingQueueBuilder builder = new EvictingQueueBuilder("test", Collections.emptyList(), 10);
    assertSame(builder.build(null, null), builder.getAppender());
  }

  @Test
  public void testAppend() {
    EvictingQueueAppender<ILoggingEvent> appender = new EvictingQueueAppender<ILoggingEvent>(10);

    for (int i = 1; i <= appender.getMaxSize(); i++) {
      String s = Integer.toString(i);
      ILoggingEvent n = createLog(s);
      appender.append(n);
      List<RecentLog> list = appender.getAll();
      assertEquals(list.get(list.size() - 1).getMessage(), s);
      assertEquals(i, appender.size());
    }
    RecentLog f = appender.getAll().get(0);

    // Check that pushing one more will check f out!
    assertEquals(appender.getMaxSize(), appender.getAll().size());
    ILoggingEvent next = createLog("next");
    appender.append(next);
    assertEquals(appender.getMaxSize(), appender.getAll().size());
    assertTrue(
        appender
                .getAll()
                .stream()
                .filter(e -> e.getMessage().equals(next.getMessage()))
                .toArray()
                .length
            == 1);
    assertTrue(
        appender
                .getAll()
                .stream()
                .filter(e -> e.getMessage().equals(f.getMessage()))
                .toArray()
                .length
            == 0);
  }

  @Test
  public void testNegativeSize() {
    EvictingQueueAppender<ILoggingEvent> appender = new EvictingQueueAppender<ILoggingEvent>(-1);
    assertEquals(0, appender.getMaxSize());
  }

  @Test
  public void testGetMaxSize() {
    EvictingQueueAppender<ILoggingEvent> appender = new EvictingQueueAppender<ILoggingEvent>(100);
    assertEquals(100, appender.getMaxSize());
  }

  @Test
  public void testClear() {
    EvictingQueueAppender<ILoggingEvent> appender = new EvictingQueueAppender<ILoggingEvent>(10);
    appender.append(createLog("test"));
    appender.clear();
    assertTrue(appender.getAll().isEmpty());
  }

  public ILoggingEvent createLog(String msg) {
    ILoggingEvent m = mock(ILoggingEvent.class);
    doReturn(msg).when(m).getMessage();
    doReturn(msg).when(m).getFormattedMessage();
    return m;
  }
}
