// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.geo.osgb;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.gov.dstl.common.geo.osgb.CartesianConversion;
import uk.gov.dstl.common.geo.osgb.Constants;

public class CartesianConversionTest {
  private static final double LAT_AIRY1830 = 52.657570305555552;
  private static final double LON_AIRY1830 = 1.7179215833333332;
  private static final double ELL_AIRY1830 = 24.7;

  private static final double X_CART = 3874938.850;
  private static final double Y_CART = 116218.624;
  private static final double Z_CART = 5047168.207;

  @Test
  public void testConvertToCartesian() {
    double[] output =
        uk.gov.dstl.common.geo.osgb.CartesianConversion.fromLatLon(
            new double[] {LAT_AIRY1830, LON_AIRY1830, ELL_AIRY1830},
            Constants.ELLIPSOID_AIRY1830_MAJORAXIS,
            Constants.ELLIPSOID_AIRY1830_MINORAXIS);

    assertEquals(X_CART, output[0], 0.0005);
    assertEquals(Y_CART, output[1], 0.0005);
    assertEquals(Z_CART, output[2], 0.0005);
  }

  @Test
  public void testConvertToLatLon() {
    double[] output =
        uk.gov.dstl.common.geo.osgb.CartesianConversion.toLatLon(
            new double[] {X_CART, Y_CART, Z_CART},
            Constants.ELLIPSOID_AIRY1830_MAJORAXIS,
            Constants.ELLIPSOID_AIRY1830_MINORAXIS,
            0.00000001);

    assertEquals(LAT_AIRY1830, output[0], 0.00001);
    assertEquals(LON_AIRY1830, output[1], 0.00001);
    assertEquals(ELL_AIRY1830, output[2], 0.0005);
  }

  @Test
  public void testConvertToAndFro() {
    double[] cartesian =
        uk.gov.dstl.common.geo.osgb.CartesianConversion.fromLatLon(
            new double[] {LAT_AIRY1830, LON_AIRY1830, ELL_AIRY1830},
            Constants.ELLIPSOID_AIRY1830_MAJORAXIS,
            Constants.ELLIPSOID_AIRY1830_MINORAXIS);
    double[] latlon =
        uk.gov.dstl.common.geo.osgb.CartesianConversion.toLatLon(
            cartesian,
            Constants.ELLIPSOID_AIRY1830_MAJORAXIS,
            Constants.ELLIPSOID_AIRY1830_MINORAXIS,
            0.000001);

    assertEquals(LAT_AIRY1830, latlon[0], 0.00001);
    assertEquals(LON_AIRY1830, latlon[1], 0.00001);
    assertEquals(ELL_AIRY1830, latlon[2], 0.00001);
  }

  @Test
  public void testHelmertConversion() {
    double[] ht =
        CartesianConversion.helmertTransformation(
            new double[] {1, 2, 3}, 3.0, 2.0, 1.0, 0.5, 1.0, 2.0, 3.0);
    assertEquals(ht[0], 4.5, 0.0001);
    assertEquals(ht[1], 5.0, 0.0001);
    assertEquals(ht[2], 5.5, 0.0001);
  }
}
