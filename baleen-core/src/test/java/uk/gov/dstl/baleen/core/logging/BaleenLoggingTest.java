//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.logging.builders.BaleenConsoleLoggerBuilder;
import uk.gov.dstl.baleen.core.logging.builders.EvictingQueueAppender;
import uk.gov.dstl.baleen.core.utils.YamlConfiguration;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.testing.logging.InMemoryLoggingBuilder;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.rolling.RollingFileAppender;

import com.codahale.metrics.logback.InstrumentedAppender;

/**
 * Tests {@link BaleenLogging}.
 *
 * 
 *
 */
public class BaleenLoggingTest {
	private static final String PATTERN = "%date";

	// TODO: Test config for LoggerFilter
	@Test
	public void test() {
		BaleenLogging logging = new BaleenLogging();
		InMemoryLoggingBuilder builder = new InMemoryLoggingBuilder();
		logging.configure(Arrays.asList(builder, new BaleenConsoleLoggerBuilder(PATTERN, new MinMaxFilter(Level.INFO,
				Level.ERROR))));

		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		Logger rootLogger = context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

		int count = 0;

		Iterator<Appender<ILoggingEvent>> it = rootLogger.iteratorForAppenders();
		while (it.hasNext()) {
			Appender<ILoggingEvent> appender = it.next();

			if (appender instanceof OutputStreamAppender) {
				Encoder<ILoggingEvent> e = ((OutputStreamAppender<ILoggingEvent>) appender).getEncoder();
				assertTrue(e instanceof PatternLayoutEncoder);

				assertEquals(PATTERN, ((PatternLayoutEncoder) e).getPattern());
			}

			count++;
		}

		// 3 = 2 + instrumented appender
		assertEquals(3, count);

	}

	@Test
	public void config() throws Exception {
		YamlConfiguration configuration = YamlConfiguration.readFromResource(BaleenLoggingTest.class,
				"dummyConfig.yaml");

		BaleenLogging logging = new BaleenLogging();
		logging.configure(configuration);

		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
		Logger rootLogger = context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

		int count = 0;

		Iterator<Appender<ILoggingEvent>> it = rootLogger.iteratorForAppenders();
		while (it.hasNext()) {
			Appender<ILoggingEvent> appender = it.next();

			switch (count) {
			case 0:
				assertTrue(appender instanceof ConsoleAppender);
				break;
			case 1:
				assertTrue(appender instanceof RollingFileAppender);
				break;
			case 2:
				assertTrue(appender instanceof FileAppender);
				assertFalse(appender instanceof RollingFileAppender);
				break;
			case 3:
				if (appender instanceof OutputStreamAppender) {
					Encoder<ILoggingEvent> e = ((OutputStreamAppender<ILoggingEvent>) appender).getEncoder();
					assertTrue(e instanceof PatternLayoutEncoder);
					assertEquals(PATTERN, ((PatternLayoutEncoder) e).getPattern());
				}
				break;
			case 4:
				if (appender instanceof EvictingQueueAppender) {
					assertEquals(EvictingQueueAppender.DEFAULT_MAX_SIZE, ((EvictingQueueAppender<ILoggingEvent>) appender).getMaxSize());
				} else {
					fail("Unknown additional appender");
				}
				break;
			case 5:
				// Allow additional appenders for checking, otherwise throw an error
				if (!(appender instanceof InstrumentedAppender)) {
					fail("Unknown additional appender");
				}
				break;
			default:
				fail("Too many appenders" + appender.getName());
			}
			count++;
		}

		assertEquals(6, count);

		// TODO: test the instance parameters match the configuration
	}
	
	@Test
	public void testParseDouble() throws InvalidParameterException{
		assertEquals(new Double(5.2), BaleenLogging.parseToDouble(5.2));
		assertEquals(new Double(5.0), BaleenLogging.parseToDouble(5));
		assertEquals(new Double(5.0), BaleenLogging.parseToDouble(5L));
		assertEquals(new Double(5.0), BaleenLogging.parseToDouble("5"));
		
		try{
			BaleenLogging.parseToDouble("Hello");
			fail("Expected exception not thrown");
		}catch(InvalidParameterException ipe){
			// Do nothing
		}
	}
}
