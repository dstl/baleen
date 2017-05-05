//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.logging;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.spi.FilterReply;

/** Tests for the{@link MinMaxFilter}.
 * 
 *
 */
public class MinMaxFilterTest {

	protected MinMaxFilter create(Level min, Level max) {
		return new MinMaxFilter(min, max);
	}
	
	/** Test when the level is within the bounds (including at the edges).
	 * 
	 */
	@Test
	public void within() {
		assertNeutral(Level.INFO, Level.DEBUG, Level.ERROR);
		assertNeutral(Level.DEBUG, Level.DEBUG, Level.ERROR);
		assertNeutral(Level.ERROR, Level.DEBUG, Level.ERROR);
		assertNeutral(Level.WARN, Level.DEBUG, Level.ERROR);

		assertNeutral(Level.WARN, Level.WARN, Level.WARN);

	}
	
	/** Test when the level is outside the bounds.
	 * 
	 */
	@Test
	public void outside() {
		assertNeutral(Level.INFO, Level.INFO, Level.WARN);
		assertNeutral(Level.WARN, Level.INFO, Level.WARN);
		assertDeny(Level.DEBUG, Level.INFO, Level.WARN);
		assertDeny(Level.ERROR, Level.INFO, Level.WARN);
		
	}
	
	/** Test that nulls are handled correctly.
	 * 
	 */
	@Test
	public void nullLevels() {
		assertNeutral(Level.INFO, null, Level.WARN);
		assertDeny(Level.ERROR, null, Level.WARN);		
		

		assertNeutral(Level.WARN, Level.INFO, null);
		assertDeny(Level.DEBUG, Level.INFO, null);
		
		assertDeny(null, null, null);		
	}

	private void assertNeutral(Level level, Level min, Level max) {
		MinMaxFilter filter = create(min, max);
		LoggingEvent event = new LoggingEvent();
		event.setLevel(level);
		assertEquals(FilterReply.NEUTRAL, filter.decide(event));
	}
	
	private void assertDeny(Level level, Level min, Level max) {
		MinMaxFilter filter = create(min, max);
		LoggingEvent event = new LoggingEvent();
		event.setLevel(level);
		assertEquals(FilterReply.DENY, filter.decide(event));
	}

}
