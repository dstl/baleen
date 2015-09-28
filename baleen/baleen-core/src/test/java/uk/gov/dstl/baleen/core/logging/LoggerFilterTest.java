//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.logging;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.spi.FilterReply;

public class LoggerFilterTest {
	private static final String LOGGER_NAME = "uk.gov.dstl.baleen.testing.DummyAnnotator1";
	private static final String TRUE_LOGGER_PACKAGE = "uk.gov.dstl.baleen.testing";
	private static final String FALSE_LOGGER_PACKAGE = "uk.gov.dstl.baleen.testing.fail";
	private static final List<String> TRUE_LOGGER_PACKAGE_LIST = Lists.newArrayList("uk.gov.dstl.baleen.testing", FALSE_LOGGER_PACKAGE);
	private static final List<String> FALSE_LOGGER_PACKAGE_LIST = Lists.newArrayList(FALSE_LOGGER_PACKAGE, "uk.gov.dstl.baleen.testing.anotherfail");
	
	@Test
	public void testInclude(){
		assertNeutral(LOGGER_NAME, LOGGER_NAME, false);
		assertNeutral(LOGGER_NAME, TRUE_LOGGER_PACKAGE, false);
		assertDeny(LOGGER_NAME, FALSE_LOGGER_PACKAGE, false);
	}
	
	@Test
	public void testExclude(){
		assertDeny(LOGGER_NAME, LOGGER_NAME, true);
		assertDeny(LOGGER_NAME, TRUE_LOGGER_PACKAGE, true);
		assertNeutral(LOGGER_NAME, FALSE_LOGGER_PACKAGE, true);
	}
	
	@Test
	public void testNullTerminated(){
		assertNeutral(LOGGER_NAME, LOGGER_NAME+"/0", false);
		assertDeny(LOGGER_NAME+"Test", LOGGER_NAME+"/0", false);
	}
	
	@Test
	public void testIncludeList(){
		assertNeutral(LOGGER_NAME, TRUE_LOGGER_PACKAGE_LIST, false);
		assertDeny(LOGGER_NAME, FALSE_LOGGER_PACKAGE_LIST, false);
	}
	
	@Test
	public void testExcludeList(){
		assertDeny(LOGGER_NAME, TRUE_LOGGER_PACKAGE_LIST, true);
		assertNeutral(LOGGER_NAME, FALSE_LOGGER_PACKAGE_LIST, true);
	}
	
	private LoggerFilter create(String loggerName, Boolean exclude) {
		return new LoggerFilter(loggerName, exclude);
	}
	
	private LoggerFilter create(List<String> loggerName, Boolean exclude) {
		return new LoggerFilter(loggerName, exclude);
	}
	
	private void assertNeutral(String logger, String loggerName, Boolean exclude) {
		LoggerFilter filter = create(loggerName, exclude);
		LoggingEvent event = new LoggingEvent();
		
		event.setLevel(Level.INFO);
		event.setLoggerName(logger);
		
		assertEquals(FilterReply.NEUTRAL, filter.decide(event));
	}
	
	private void assertDeny(String logger, String loggerName, Boolean exclude) {
		LoggerFilter filter = create(loggerName, exclude);
		LoggingEvent event = new LoggingEvent();
		
		event.setLevel(Level.INFO);
		event.setLoggerName(logger);
		
		assertEquals(FilterReply.DENY, filter.decide(event));
	}
	
	private void assertNeutral(String logger, List<String> loggerName, Boolean exclude) {
		LoggerFilter filter = create(loggerName, exclude);
		LoggingEvent event = new LoggingEvent();
		
		event.setLevel(Level.INFO);
		event.setLoggerName(logger);
		
		assertEquals(FilterReply.NEUTRAL, filter.decide(event));
	}
	
	private void assertDeny(String logger, List<String> loggerName, Boolean exclude) {
		LoggerFilter filter = create(loggerName, exclude);
		LoggingEvent event = new LoggingEvent();
		
		event.setLevel(Level.INFO);
		event.setLoggerName(logger);
		
		assertEquals(FilterReply.DENY, filter.decide(event));
	}
}
