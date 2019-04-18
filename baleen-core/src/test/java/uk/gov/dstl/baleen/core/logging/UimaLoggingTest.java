// Dstl (c) Crown Copyright 2017-2019
package uk.gov.dstl.baleen.core.logging;

import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.apache.uima.UIMAFramework;
import org.apache.uima.util.Level;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;

import uk.gov.dstl.baleen.testing.DummyAnnotator1;
import uk.gov.dstl.baleen.testing.logging.InMemoryAppender;
import uk.gov.dstl.baleen.testing.logging.InMemoryLoggingBuilder;

/** Test Uima's logging is integrated with the baleen logging output. */
public class UimaLoggingTest {

  @Test
  public void test() throws Exception {
    BaleenLogging logging = new BaleenLogging();
    InMemoryLoggingBuilder builder = new InMemoryLoggingBuilder();
    logging.configure(Collections.singletonList(builder));

    InMemoryAppender<ILoggingEvent> appender = builder.getAppender();
    appender.clear();

    LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
    Logger rootLogger = context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

    if (rootLogger.isInfoEnabled()) {
      UIMAFramework.getLogger(DummyAnnotator1.class).log(Level.INFO, "Logging from uima");

      assertTrue(
          appender.getAll().stream()
                  .filter(l -> l.getMessage().contains("Logging from uima"))
                  .count()
              > 0);
    }
  }
}
