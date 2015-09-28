//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.logging;

import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.apache.uima.UIMAFramework;
import org.apache.uima.util.Level;
import org.junit.Test;

import uk.gov.dstl.baleen.testing.DummyAnnotator1;
import uk.gov.dstl.baleen.testing.logging.InMemoryAppender;
import uk.gov.dstl.baleen.testing.logging.InMemoryLoggingBuilder;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Test Uima's logging is integrated with the baleen logging output.
 *
 * 
 *
 */
public class UimaLoggingTest {

	@Test
	public void test() throws Exception {
		BaleenLogging logging = new BaleenLogging();
		InMemoryLoggingBuilder builder = new InMemoryLoggingBuilder();
		logging.configure(Collections.singletonList(builder));

		InMemoryAppender<ILoggingEvent> appender = builder.getAppender();
		appender.clear();

		UIMAFramework.getLogger(DummyAnnotator1.class).log(Level.INFO, "Logging from uima");

		assertTrue(appender.getAll().stream().filter(l -> l.getMessage().contains("Logging from uima")).count() > 0);

	}
}
