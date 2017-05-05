//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.FlightNumber;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.types.TestVehicle;
import uk.gov.dstl.baleen.types.common.Vehicle;

/**
 * Tests for {@link FlightNumber}.
 * 
 * 
 */
public class FlightNumberTest extends AbstractAnnotatorTest {

	public FlightNumberTest() {
		super(FlightNumber.class);
	}

	@Test
	public void test() throws Exception {

		jCas.setDocumentText("James caught flight BA22 to Baltimore. BA23 was delayed (and doesn't have the word flight infront of it), and ZZ00 isn't a real flight! Flight number BA1 goes to New York via Shannon.");
		processJCas();

		assertAnnotations(2, Vehicle.class, new TestVehicle(0, "BA22", "flight"),
				new TestVehicle(1, "BA1", "flight"));

	}

}
