//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.common.geo.osgb;

/**
 * <b>Convert between LatLon and Cartesian coordinate systems</b>
 * <p>
 * This code uses an approximate Helmert transformation, with an error of up to 5 metres (both horizontally and vertically)
 * Equations taken from http://www.ordnancesurvey.co.uk/docs/support/guide-coordinate-systems-great-britain.pdf
 *
 * 
 */
public class CartesianConversion {
	private CartesianConversion(){
		//Utility class - private constructor
	}
	
	/**
	 * @param inputCoordinates Array of coordinates [lat, lon, ellipsoidHeight] to convert
	 * @param a Semi-major axis of ellipsoid
	 * @param b Semi-minor axis of the ellipsoid
	 * @return Array of cartesian coordinates [x, y, z]
	 */
	public static double[] fromLatLon(double[] inputCoordinates, double a, double b){
		double lat = Math.toRadians(inputCoordinates[0]);
		double lon = Math.toRadians(inputCoordinates[1]);
		double height = inputCoordinates[2];
		
		double e2 = (Math.pow(a,  2) - Math.pow(b, 2))/Math.pow(a, 2);
		double v = a / Math.sqrt(1 - e2*Math.pow(Math.sin(lat), 2));
		
		double x = (v + height)*Math.cos(lat)*Math.cos(lon);
		double y = (v + height)*Math.cos(lat)*Math.sin(lon);
		double z = ((1 - e2)*v + height)*Math.sin(lat);
		
		return new double[]{x, y, z};
	}
	
	/**
	 * @param inputCoordinates Array of cartesian coordinates [x, y, z] to convert
	 * @param a Semi-major axis of ellipsoid
	 * @param b Semi-minor axis of the ellipsoid
	 * @param precision Precision to calculate the latitude to
	 * @return Array of coordinates [lat, lon, ellipsoidHeight]
	 */
	public static double[] toLatLon(double[] inputCoordinates, double a, double b, double precision){
		double x = inputCoordinates[0];
		double y = inputCoordinates[1];
		double z = inputCoordinates[2];
		
		double e2 = (Math.pow(a,  2) - Math.pow(b, 2))/Math.pow(a, 2);
		
		double lon = Math.atan(y / x);
		
		double p = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
		double lat = Math.atan(z / p*(1 - e2));
		
		double v = 0;
		double delta = 2*precision;
		
		while(delta > precision){
			v = a / Math.sqrt(1 - e2*Math.pow(Math.sin(lat), 2));
			double newLat = Math.atan((z + e2*v*Math.sin(lat))/p);
			
			delta = Math.abs(Math.toDegrees(lat - newLat));
			lat = newLat;
		}
		
		double height = (p/Math.cos(lat)) - v;
		
		return new double[]{Math.toDegrees(lat), Math.toDegrees(lon), height};
	}
	
	/**
	 * @param inputCoordinates Array of coordinates [x, y, z] to transform using the specified transformation parameters
	 * @param tX Translation along the x axis (in metres)
	 * @param tY Translation along the y axis (in metres)
	 * @param tZ Translation along the z azies (in metres)
	 * @param s Scale factor
	 * @param rX Rotation about the x axis (in radians)
	 * @param rY Rotation about the y axis (in radians)
	 * @param rZ Rotation about the z axis (in radians)
	 * @return Array of coordinates [x, y, z] that have been transformed
	 */
	public static double[] helmertTransformation(double[] inputCoordinates, double tX, double tY, double tZ, double s, double rX, double rY, double rZ){
		double aX = inputCoordinates[0];
		double aY = inputCoordinates[1];
		double aZ = inputCoordinates[2];
		
		double bX = tX + ((1 + s)*aX) + (-rZ*aY) + (rY*aZ);
		double bY = tY + (rZ*aX) + ((1 + s)*aY) + (-rX*aZ);
		double bZ = tZ + (-rY*aX) + (rX*aY) + ((1 + s)*aZ);
		
		return new double[]{bX, bY, bZ};
	}
}
