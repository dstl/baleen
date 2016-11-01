//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.geo.osgb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import uk.gov.dstl.common.geo.osgb.NationalGrid;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

public class NationalGridTest {

	private static final String NG_COMPACT = "NW1623434223";
	private static final String NG_EXPANDED = "NW 16234 34223";
	// Invalid as different lengths of thing
	private static final String NG_INVALID = "NW134534223";
	
	private static final double E = 116234;
	private static final double N = 534223;	
	
	private static final Splitter WHITESPACE_SPLITTER = Splitter.on(CharMatcher.whitespace()).omitEmptyStrings().trimResults();

	@Test
	public void testFrom() {
		double[] ne = NationalGrid.fromNationalGrid(NG_COMPACT);
		assertEquals(E, ne[0], 0.001);
		assertEquals(N, ne[1], 0.001);

		double[] neExpanded = NationalGrid.fromNationalGrid(NG_EXPANDED);
		assertEquals(E, neExpanded[0], 0.001);
		assertEquals(N, neExpanded[1], 0.001);
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testFromInvalid() {
		NationalGrid.fromNationalGrid(NG_INVALID);
	}
	
	@Test
	public void testTo() {
		Optional<String> ng = NationalGrid.toNationalGrid(new double[] { E , N });
			
		assertEquals(NG_EXPANDED, ng.get());
	}


	@Test
	public void testExample() {
		assertConversion("TG 51409 13177", 651409.0, 313177.0);
		assertConversion("NN 166 712", 216600.0, 771200.0);
		assertConversion("NN 126 729", 212600.0, 772900.0);
		assertConversion("SU 21940 45374", 421940.0, 145374.0);
		assertConversion("SU 02194 45374", 402194.0, 145374.0);
	}

	private void assertConversion(String example, double exE, double exN) {
		double[] out = NationalGrid.fromNationalGrid(example);
		assertEquals(exE, out[0], 0.001);
		assertEquals(exN, out[1], 0.001);
		
		Optional<String> ng = NationalGrid.toNationalGrid(new double[] { exE , exN });
		assertCoordinateEquals(example, ng.get());
	}
	
	private void assertCoordinateEquals(String expected, String actual){
		String coordExpected = expected.substring(2);
		String coordActual = actual.substring(2);
		
		List<String> listExpected = WHITESPACE_SPLITTER.splitToList(coordExpected.substring(2));
		List<String> listActual = WHITESPACE_SPLITTER.splitToList(coordActual.substring(2));
		
		String normalizedExpected = null;
		String normalizedActual = null;
		
		if(listActual.size() >= 2){
			normalizedExpected = expected.substring(0, 2) + addZeroes(listExpected.get(0)) + addZeroes(listExpected.get(1));
			normalizedActual = expected.substring(0, 2) + addZeroes(listActual.get(0)) + addZeroes(listActual.get(1));
		}else if(listActual.size() == 1){
			int index = listExpected.get(0).length() / 2;
			normalizedExpected = expected.substring(0, 2) + addZeroes(listExpected.get(0).substring(0, index)) + addZeroes(listExpected.get(0).substring(index));
			
			index = listActual.get(0).length() / 2;
			normalizedActual = actual.substring(0, 2) + addZeroes(listActual.get(0).substring(0, index)) + addZeroes(listActual.get(0).substring(index));
		}else{
			fail("Unable to parse coordinate");
		}
		
		assertEquals(normalizedExpected.toUpperCase(), normalizedActual.toUpperCase());
	}
	
	private String addZeroes(String s){
		String t = s;
		while(t.length() < 5){
			t += "0";
		}
		
		return t;
	}
}
