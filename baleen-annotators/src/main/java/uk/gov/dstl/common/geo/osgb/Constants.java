// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.common.geo.osgb;

/**
 * <b>Constants that define different coordinate systems and ellipsoids</b>
 *
 * <p>Values taken from
 * http://www.ordnancesurvey.co.uk/docs/support/guide-coordinate-systems-great-britain.pdf
 */
public class Constants {
  public static final double NATIONALGRID_F0 = 0.9996012717;
  // In degrees
  public static final double NATIONALGRID_LAT0 = 49;
  // In degrees
  public static final double NATIONALGRID_LON0 = -2;
  public static final double NATIONALGRID_E0 = 400000;
  public static final double NATIONALGRID_N0 = -100000;

  public static final double ELLIPSOID_AIRY1830_MAJORAXIS = 6377563.396;
  public static final double ELLIPSOID_AIRY1830_MINORAXIS = 6356256.909;

  public static final double ELLIPSOID_GRS80_MAJORAXIS = 6378137.000;
  public static final double ELLIPSOID_GRS80_MINORAXIS = 6356752.3141;

  private Constants() {
    // Utility class - private constructor
  }
}
