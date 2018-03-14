// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Marker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggerContextVO;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.dstl.baleen.core.logging.BaleenLogging;
import uk.gov.dstl.baleen.testing.servlets.ServletCaller;

/** Tests for {@link LoggingServlet}. */
@RunWith(MockitoJUnitRunner.Silent.class)
public class LoggingServletTest {

  @Mock BaleenLogging logging;

  List<ILoggingEvent> events = new ArrayList<ILoggingEvent>();

  @Before
  public void setUp() {
    doReturn(events).when(logging).getRecentLogs();
  }

  @Test
  public void testGet() throws Exception {
    ServletCaller caller = new ServletCaller();

    ILoggingEvent e = createEvent("test");

    events.add(e);
    caller.doGet(new LoggingServlet(logging));

    ObjectMapper mapper = new ObjectMapper();
    @SuppressWarnings("rawtypes")
    List list = mapper.readValue(caller.getResponseBody(), List.class);
    assertEquals(1, list.size());
  }

  private ILoggingEvent createEvent(String message) {
    return new ILoggingEvent() {

      @Override
      public void prepareForDeferredProcessing() {
        // Do nothing
      }

      @Override
      public boolean hasCallerData() {
        return false;
      }

      @Override
      public long getTimeStamp() {
        return 0;
      }

      @Override
      public IThrowableProxy getThrowableProxy() {
        return null;
      }

      @Override
      public String getThreadName() {
        return "test";
      }

      @Override
      public String getMessage() {
        return message;
      }

      @Override
      public Map<String, String> getMdc() {
        return Collections.emptyMap();
      }

      @Override
      public Marker getMarker() {
        return null;
      }

      @Override
      public Map<String, String> getMDCPropertyMap() {
        return Collections.emptyMap();
      }

      @Override
      public String getLoggerName() {
        return "fake";
      }

      @Override
      public LoggerContextVO getLoggerContextVO() {
        return null;
      }

      @Override
      public Level getLevel() {
        return Level.INFO;
      }

      @Override
      public String getFormattedMessage() {
        return message;
      }

      @Override
      public StackTraceElement[] getCallerData() {
        return new StackTraceElement[0];
      }

      @Override
      public Object[] getArgumentArray() {
        return new Object[0];
      }
    };
  }
}
