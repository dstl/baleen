//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.helper;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.helpers.QuantityUtils;

public class QuantityUtilsTest {


	// Tests the common function that handles the text-number distance unit conversion
	@Test
	public void testConvertQuantityMagnitude(){
		
		Double amount = 10d;
		
		//Check null, empty and unknown units
		assertEquals(10.0, QuantityUtils.scaleByMultipler(amount, null), 0.001);
		assertEquals(10.0, QuantityUtils.scaleByMultipler(amount, ""), 0.001);
		assertEquals(10.0, QuantityUtils.scaleByMultipler(amount, "zillion"), 0.001);
		
		//Check 'hundred' converts (expected 10 x 100 = 1000)
		assertEquals(1000.0, QuantityUtils.scaleByMultipler(amount, "hundred"), 0.001);
		
		//Check 'thousand' converts
		assertEquals(10000.0, QuantityUtils.scaleByMultipler(amount, "k"), 0.001);
		assertEquals(10000.0, QuantityUtils.scaleByMultipler(amount, "thousand"), 0.001);
				
		//Check 'million' converts 
		assertEquals(10000000.0, QuantityUtils.scaleByMultipler(amount, "m"), 0.001);
		assertEquals(10000000.0, QuantityUtils.scaleByMultipler(amount, "million"), 0.001);
				
		//Check 'billion' converts
		assertEquals(10000000000.0, QuantityUtils.scaleByMultipler(amount, "b"), 0.001);
		assertEquals(10000000000.0, QuantityUtils.scaleByMultipler(amount, "billion"), 0.001);
		
		//Check 'trillion' converts
		assertEquals(10000000000000.0, QuantityUtils.scaleByMultipler(amount, "t"), 0.001);
		assertEquals(10000000000000.0, QuantityUtils.scaleByMultipler(amount, "trillion"), 0.001);
		
		
	}
	
	@Test
	public void testHaystack() {
		assertEquals(1, QuantityUtils.countPeriods("1.2"));
		assertEquals(2, QuantityUtils.countPeriods("1..2"));
		assertEquals(2, QuantityUtils.countPeriods("1.2.3"));
	}
}
