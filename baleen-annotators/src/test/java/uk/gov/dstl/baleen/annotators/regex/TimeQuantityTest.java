//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.TimeQuantity;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.types.TestQuantity;
import uk.gov.dstl.baleen.types.common.Quantity;

/**
 * Tests for {@link TimeQuantity}.
 * 
 * 
 */
public class TimeQuantityTest extends AbstractAnnotatorTest {

	public TimeQuantityTest() {
		super(TimeQuantity.class);
	}

	@Test
	public void testYear() throws Exception {		
		jCas.setDocumentText("6 years later");
		processJCas();
		
		assertAnnotations(1, Quantity.class, 
				new TestQuantity(0, "6 years", 6, "year", 189216000, "s", "time"));
	}

	@Test
	public void testMonth() throws Exception {

		jCas.setDocumentText("In 18 months");
		processJCas();

		assertAnnotations(1, Quantity.class, 
				new TestQuantity(0, "18 months", 18, "month", 0, null, "time"));

	}
	

	@Test
	public void testWeek() throws Exception {

		jCas.setDocumentText("In the next 2 weeks");
		processJCas();

		assertAnnotations(1, Quantity.class, 
				new TestQuantity(0, "2 weeks", 2, "week", 1209600, "s", "time"));

	}

	@Test
	public void testDays() throws Exception {
		
		jCas.setDocumentText("460 days after the event");
		processJCas();

		assertAnnotations(1, Quantity.class, 
				new TestQuantity(0, "460 days", 460, "day", 39744000, "s", "time"));
	}

	@Test
	public void testHours() throws Exception {
		
		jCas.setDocumentText("Only 2 hours to go...");
		processJCas();

		assertAnnotations(1, Quantity.class, 
				new TestQuantity(0, "2 hours", 2, "hour", 7200, "s", "time"));

	}
	
	@Test
	public void testHoursTime() throws Exception {
		
		jCas.setDocumentText("At 2200hrs, things will happen... But they'll be over by 0200hrs");
		processJCas();

		assertAnnotations(0, Quantity.class);

	}

	@Test
	public void testMinutes() throws Exception {
		

		jCas.setDocumentText("27 minutes until it happens");
		processJCas();

		assertAnnotations(1, Quantity.class, 
				new TestQuantity(0, "27 minutes", 27, "minute", 1620, "s", "time"));
	}

	@Test
	public void testSeconds() throws Exception {

		jCas.setDocumentText("In 30 seconds time");
		processJCas();
		
		assertAnnotations(1, Quantity.class, 
				new TestQuantity(0, "30 seconds", 30, "s", 30, "s", "time"));
	}

	@Test
	public void testPunctuation() throws Exception {
		jCas.setDocumentText("There are 86,400 seconds in a day.");
		processJCas();


		assertAnnotations(1, Quantity.class, 
				new TestQuantity(0, "86,400 seconds", 86400, "s", 86400, "s", "time"));

	}
}
