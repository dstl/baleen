// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.common.geo.osgb;

/**
 * <b>Convert between LatLon and Easting/Northings for Transverse Mercator projection</b>
 *
 * <p>Equations taken from
 * http://www.ordnancesurvey.co.uk/docs/support/guide-coordinate-systems-great-britain.pdf
 */
public class EastingNorthingConversion {
  private EastingNorthingConversion() {
    // Utility class - private constructor
  }

  /**
   * Convert from Lat lon
   *
   * @param inputCoordinates Array of coordinates [lat, lon] to convert
   * @param a Semi-major axis of ellipsoid
   * @param b Semi-minor axis of the ellipsoid
   * @param n0 Northing of true origin
   * @param e0 Easting of true origin
   * @param f0 Scale factor on central meridian
   * @param lat0 Latitude of true origin
   * @param lon0 Longitude of true origin
   * @return Array of easting/northings [easting, northing]
   */
  public static double[] fromLatLon(
      double[] inputCoordinates,
      double a,
      double b,
      double n0,
      double e0,
      double f0,
      double lat0,
      double lon0) {
    double lat = Math.toRadians(inputCoordinates[0]);
    double lon = Math.toRadians(inputCoordinates[1]);

    double lat0Rad = Math.toRadians(lat0);
    double lon0Rad = Math.toRadians(lon0);

    double e2 = (Math.pow(a, 2) - Math.pow(b, 2)) / Math.pow(a, 2);

    double n = (a - b) / (a + b);
    double n2 = Math.pow(n, 2);
    double n3 = Math.pow(n, 3);

    double eSinPhi = 1 - e2 * Math.pow(Math.sin(lat), 2); // eSinPhi = 1 - e^2 * sin^2 phi
    double nu = a * f0 * Math.pow(eSinPhi, -0.5);
    double rho = a * f0 * (1 - e2) * Math.pow(eSinPhi, -1.5);
    double eta2 = (nu / rho) - 1;

    double m =
        b
            * f0
            * ((1 + n + (5.0 / 4.0) * n2 + (5.0 / 4.0) * n3) * (lat - lat0Rad)
                - (3.0 * n + 3.0 * n2 + (21.0 / 8.0) * n3)
                    * Math.sin(lat - lat0Rad)
                    * Math.cos(lat + lat0Rad)
                + ((15.0 / 8.0) * n2 + (15.0 / 8.0) * n3)
                    * Math.sin(2.0 * (lat - lat0Rad))
                    * Math.cos(2.0 * (lat + lat0Rad))
                - (35.0 / 24.0)
                    * n3
                    * Math.sin(3.0 * (lat - lat0Rad))
                    * Math.cos(3.0 * (lat + lat0Rad)));

    double i = m + n0;
    double ii = (nu / 2) * Math.sin(lat) * Math.cos(lat);
    double iii =
        (nu / 24)
            * Math.sin(lat)
            * Math.pow(Math.cos(lat), 3)
            * (5 - Math.pow(Math.tan(lat), 2) + 9 * eta2);
    double iiiA =
        (nu / 720)
            * Math.sin(lat)
            * Math.pow(Math.cos(lat), 5)
            * (61 - 58 * Math.pow(Math.tan(lat), 2) + Math.pow(Math.tan(lat), 4));
    double iv = nu * Math.cos(lat);
    double v = (nu / 6) * Math.pow(Math.cos(lat), 3) * ((nu / rho) - Math.pow(Math.tan(lat), 2));
    double vi =
        (nu / 120)
            * Math.pow(Math.cos(lat), 5)
            * (5
                - 18 * Math.pow(Math.tan(lat), 2)
                + Math.pow(Math.tan(lat), 4)
                + 14 * eta2
                - 58 * (Math.pow(Math.tan(lat), 2)) * eta2);

    double retN =
        i
            + ii * Math.pow(lon - lon0Rad, 2)
            + iii * Math.pow(lon - lon0Rad, 4)
            + iiiA * Math.pow(lon - lon0Rad, 6);
    double retE =
        e0
            + iv * (lon - lon0Rad)
            + v * Math.pow(lon - lon0Rad, 3)
            + vi * Math.pow(lon - lon0Rad, 5);

    return new double[] {retE, retN};
  }

  /**
   * Convert to Lat Lon.
   *
   * @param inputCoordinates Array of easting/northings [easting, northing] to convert
   * @param a Semi-major axis of ellipsoid
   * @param b Semi-minor axis of the ellipsoid
   * @param n0 Northing of true origin
   * @param e0 Easting of true origin
   * @param f0 Scale factor on central meridian
   * @param lat0 Latitude of true origin
   * @param lon0 Longitude of true origin
   * @return Array of coordinates [lat, lon]
   */
  public static double[] toLatLon(
      double[] inputCoordinates,
      double a,
      double b,
      double n0,
      double e0,
      double f0,
      double lat0Degrees,
      double lon0Degrees) {
    double coordE = inputCoordinates[0];
    double coordN = inputCoordinates[1];

    double e2 = (Math.pow(a, 2) - Math.pow(b, 2)) / Math.pow(a, 2);

    double n = (a - b) / (a + b);
    double n2 = Math.pow(n, 2);
    double n3 = Math.pow(n, 3);

    double lat0 = Math.toRadians(lat0Degrees);
    double lon0 = Math.toRadians(lon0Degrees);

    double m = 0;
    double latPrime = lat0;
    double delta = 1;

    while (delta > 0.00001) {
      latPrime = ((coordN - n0 - m) / (a * f0)) + latPrime;

      m =
          b
              * f0
              * ((1 + n + (5.0 / 4.0) * n2 + (5.0 / 4.0) * n3) * (latPrime - lat0)
                  - (3.0 * n + 3.0 * n2 + (21.0 / 8.0) * n3)
                      * Math.sin(latPrime - lat0)
                      * Math.cos(latPrime + lat0)
                  + ((15.0 / 8.0) * n2 + (15.0 / 8.0) * n3)
                      * Math.sin(2.0 * (latPrime - lat0))
                      * Math.cos(2.0 * (latPrime + lat0))
                  - (35.0 / 24.0)
                      * n3
                      * Math.sin(3.0 * (latPrime - lat0))
                      * Math.cos(3.0 * (latPrime + lat0)));

      delta = Math.abs(coordN - n0 - m);
    }

    double eSinPhi = 1 - e2 * Math.pow(Math.sin(latPrime), 2); // eSinPhi = 1 - e^2 * sin^2 phi
    double nu = a * f0 * Math.pow(eSinPhi, -0.5);
    double rho = a * f0 * (1 - e2) * Math.pow(eSinPhi, -1.5);
    double eta2 = (nu / rho) - 1;

    double vii = Math.tan(latPrime) / (2.0 * rho * nu);
    double viii =
        (Math.tan(latPrime) / (24.0 * rho * Math.pow(nu, 3)))
            * (5.0
                + 3.0 * Math.pow(Math.tan(latPrime), 2)
                + eta2
                - 9.0 * Math.pow(Math.tan(latPrime), 2) * eta2);
    double ix =
        (Math.tan(latPrime) / (720.0 * rho * Math.pow(nu, 5)))
            * (61.0
                + 90.0 * Math.pow(Math.tan(latPrime), 2)
                + 45.0 * Math.pow(Math.tan(latPrime), 4));
    double x = 1.0 / (Math.cos(latPrime) * nu);
    double xi =
        (1.0 / (6.0 * Math.cos(latPrime) * Math.pow(nu, 3)))
            * (nu / rho + 2.0 * Math.pow(Math.tan(latPrime), 2));
    double xii =
        (1.0 / (120.0 * Math.cos(latPrime) * Math.pow(nu, 5)))
            * (5.0
                + 28.0 * Math.pow(Math.tan(latPrime), 2)
                + 24.0 * Math.pow(Math.tan(latPrime), 4));
    double xiiA =
        (1.0 / (5040.0 * Math.cos(latPrime) * Math.pow(nu, 7)))
            * (61.0
                + 662.0 * Math.pow(Math.tan(latPrime), 2)
                + 1320.0 * Math.pow(Math.tan(latPrime), 4)
                + 720.0 * Math.pow(Math.tan(latPrime), 6));

    double dE = coordE - e0;

    double lat = latPrime - vii * Math.pow(dE, 2) + viii * Math.pow(dE, 4) - ix * Math.pow(dE, 6);
    double lon =
        lon0 + x * dE - xi * Math.pow(dE, 3) + xii * Math.pow(dE, 5) - xiiA * Math.pow(dE, 7);

    lat = Math.toDegrees(lat);
    lon = Math.toDegrees(lon);

    return new double[] {lat, lon};
  }
}
