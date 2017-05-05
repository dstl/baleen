//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.common.geo.osgb;

import static uk.gov.dstl.common.geo.osgb.CartesianConversion.*;

/**
 * <b>Convert between OSGB36 and WGS84 coordinate systems</b>
 * <p>
 * Values taken from http://www.ordnancesurvey.co.uk/docs/support/guide-coordinate-systems-great-britain.pdf
 *
 * 
 */
public class OSGB36{
	private static double tX = -446.448;
	private static double tY = 125.157;
	private static double tZ = -542.060;
	private static double s = 20.4894/1000000;					//Value given by OS in ppm, so convert to a unitless quantity
	private static double rX = -0.1502*(Math.PI/648000);		//Value given by OS in arcseconds, so convert to radians
	private static double rY = -0.2470*(Math.PI/648000);		//Value given by OS in arcseconds, so convert to radians
	private static double rZ = -0.8421*(Math.PI/648000);		//Value given by OS in arcseconds, so convert to radians
	
	private OSGB36(){
		//Utility class - private constructor
	}
	
	/** Convert to WGS86 Lat Lon to OSGBG.
	 * @param lat Latitude in OSGB36 coordinates
	 * @param lon Longitude in OSGB36 coordinates
	 * @return Array of coordinates [lat, long] in WGS84
	 */
	public static double[] toWGS84(double lat, double lon){
		double[] cartesian = fromLatLon(new double[]{lat, lon, 0}, Constants.ELLIPSOID_GRS80_MAJORAXIS, Constants.ELLIPSOID_GRS80_MINORAXIS);
		double[] transformed = helmertTransformation(cartesian, -tX, -tY, -tZ, -s, -rX, -rY, -rZ);
		double[] ret = toLatLon(transformed, Constants.ELLIPSOID_AIRY1830_MAJORAXIS, Constants.ELLIPSOID_AIRY1830_MINORAXIS, 0.00000001);
		
		return new double[]{ret[0], ret[1]};
	}
	
	/** Convert from WGS86 Lat Lon to OSBG.
	 * @param lat Latitude in WGS84 coordinates
	 * @param lon Longitude in WGS84 coordinates
	 * @return Array of coordinates [lat, lon] in OSGB36
	 */
	public static double[] fromWGS84(double lat, double lon){
		double[] cartesian = fromLatLon(new double[]{lat, lon, 0}, Constants.ELLIPSOID_GRS80_MAJORAXIS, Constants.ELLIPSOID_GRS80_MINORAXIS);
		double[] transformed = helmertTransformation(cartesian, tX, tY, tZ, s, rX, rY, rZ);
		double[] ret = toLatLon(transformed, Constants.ELLIPSOID_AIRY1830_MAJORAXIS, Constants.ELLIPSOID_AIRY1830_MINORAXIS, 0.00000001);
		
		return new double[]{ret[0], ret[1]};
	}
}
