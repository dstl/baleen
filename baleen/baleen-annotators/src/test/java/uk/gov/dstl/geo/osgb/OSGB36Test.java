//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.geo.osgb;

import static org.junit.Assert.*;

import org.junit.Test;


public class OSGB36Test {
	@Test
	public void testFromWGS84(){
		double[] output = uk.gov.dstl.common.geo.osgb.OSGB36.fromWGS84(51.5085300, -0.1257400);
		
		assertEquals(51.508019, output[0], 0.0001);
		assertEquals(-0.1241133, output[1], 0.0001);
	}
	
	@Test
	public void testToWGS84(){
		double[] output = uk.gov.dstl.common.geo.osgb.OSGB36.toWGS84(51.5, 0.116667);
		
		assertEquals(51.500514, output[0], 0.01);
		assertEquals(0.115033, output[1], 0.00001);
	}
}