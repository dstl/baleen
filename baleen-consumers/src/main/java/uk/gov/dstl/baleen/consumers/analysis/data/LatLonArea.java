// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.analysis.data;

/**
 * Unit of area, with center-point Lat lon coordinate
 *
 * <p>Note: this class has a natural ordering that is inconsistent with equals
 */
public class LatLonArea extends LatLon implements Comparable<LatLonArea> {

  private final double area;

  /**
   * @param lat Center-Point Latitude
   * @param lon Center-Point Longitude
   * @param area The Area
   */
  public LatLonArea(final double lat, final double lon, final double area) {
    super(lat, lon);
    this.area = area;
  }

  /** @return The Area */
  public double getArea() {
    return area;
  }

  /**
   * Overrides the compareTo operator to compare objects based on area only in order largest to
   * smallest.
   *
   * <p>Note that {@link #equals(Object o) equals} has not been overridden so it is not the case
   * that (x.compareTo(y)==p)==(x.equals(y)) if x and y have the same Area but different locations.
   *
   * @param o the LatLonArea object to be compared.
   * @returns a negative integer, zero, or a positive integer as the the specified object is less
   *     than, equal to, or greater than the area of this object.
   */
  @SuppressWarnings("squid:S1210" /* Equals is deliberately not overridden (see Javadoc) */)
  @Override
  public int compareTo(final LatLonArea o) {
    // Largest area to smallest
    return Double.compare(o.getArea(), getArea());
  }
}
