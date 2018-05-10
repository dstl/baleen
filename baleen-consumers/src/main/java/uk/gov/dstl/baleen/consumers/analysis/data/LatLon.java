// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.analysis.data;

/** Lat lon coordinate */
public class LatLon {

  /** The lat. */
  private double lat;

  /** The lon. */
  private double lon;

  /**
   * Instantiates a new lat lon.
   *
   * @param lat the lat
   * @param lon the lon
   */
  public LatLon(final double lat, final double lon) {
    this.lat = lat;
    this.lon = lon;
  }

  /**
   * Gets the lat.
   *
   * @return the lat
   */
  public double getLat() {
    return lat;
  }

  /**
   * Sets the lat.
   *
   * @param lat the new lat
   */
  public void setLat(final double lat) {
    this.lat = lat;
  }

  /**
   * Gets the lon.
   *
   * @return the lon
   */
  public double getLon() {
    return lon;
  }

  /**
   * Sets the lon.
   *
   * @param lon the new lon
   */
  public void setLon(final double lon) {
    this.lon = lon;
  }

  /**
   * Converts to a 2 element array, longitude first, latitude second
   *
   * @return The longLat array
   */
  public double[] asLonLat() {
    return new double[] {this.lon, this.lat};
  }
}
