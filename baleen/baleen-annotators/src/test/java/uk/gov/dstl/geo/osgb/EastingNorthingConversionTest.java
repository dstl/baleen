//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.geo.osgb;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.gov.dstl.common.geo.osgb.Constants;

public class EastingNorthingConversionTest {
	private static final double LAT_AIRY1830 = 52.657570305555552;
	private static final double LON_AIRY1830 = 1.7179215833333332;
	
	private static final double EASTING = 651409.903;
	private static final double NORTHING = 313177.270;
	
	@Test
	public void testFromLatLon(){
		double[] output = uk.gov.dstl.common.geo.osgb.EastingNorthingConversion.fromLatLon(new double[]{LAT_AIRY1830,LON_AIRY1830}, Constants.ELLIPSOID_AIRY1830_MAJORAXIS, Constants.ELLIPSOID_AIRY1830_MINORAXIS, Constants.NATIONALGRID_N0, Constants.NATIONALGRID_E0, Constants.NATIONALGRID_F0, Constants.NATIONALGRID_LAT0, Constants.NATIONALGRID_LON0);
		
		assertEquals(EASTING, output[0], 0.0005);
		assertEquals(NORTHING, output[1], 0.0005);
	}
	
	@Test
	public void testToLatLon(){
		double[] output = uk.gov.dstl.common.geo.osgb.EastingNorthingConversion.toLatLon(new double[]{EASTING, NORTHING}, Constants.ELLIPSOID_AIRY1830_MAJORAXIS, Constants.ELLIPSOID_AIRY1830_MINORAXIS, Constants.NATIONALGRID_N0, Constants.NATIONALGRID_E0, Constants.NATIONALGRID_F0, Constants.NATIONALGRID_LAT0, Constants.NATIONALGRID_LON0);
		
		assertEquals(LAT_AIRY1830, output[0], 0.0005);
		assertEquals(LON_AIRY1830, output[1], 0.0005);
	}
}