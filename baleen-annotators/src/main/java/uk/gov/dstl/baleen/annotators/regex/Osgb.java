// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import com.google.common.collect.ImmutableSet;
import org.apache.uima.jcas.JCas;
import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.geo.osgb.Constants;
import uk.gov.dstl.geo.osgb.EastingNorthingConversion;
import uk.gov.dstl.geo.osgb.NationalGrid;
import uk.gov.dstl.geo.osgb.OSGB36;

import java.util.Collections;
import java.util.regex.Matcher;

/**
 * Annotate Ordnance Survey coordinates
 *
 * <p>This annotator will match 6, 8 or 10 figure OS coordinates within the document using a regular
 * expression. LatLon accurate to better than 1.11km.
 */
public class Osgb extends AbstractRegexAnnotator<Coordinate> {
  private static final String OSGB_REGEX =
      "\\b([HJNOST][A-HJ-Z])( )?([0-9]{6}|[0-9]{3} [0-9]{3}|[0-9]{8}|[0-9]{4} [0-9]{4}|[0-9]{10}|[0-9]{5} [0-9]{5})\\b";

  /** New instance. */
  public Osgb() {
    super(OSGB_REGEX, false, 1.0);
  }

  @Override
  protected Coordinate create(JCas jCas, Matcher matcher) {
    Coordinate loc = new Coordinate(jCas);
    loc.setSubType("osgb");
    try {
      // Attempt to conver to a lat lon
      double[] en = NationalGrid.fromNationalGrid(matcher.group());
      double[] latlonOSGB38 =
          EastingNorthingConversion.toLatLon(
              en,
              Constants.ELLIPSOID_AIRY1830_MAJORAXIS,
              Constants.ELLIPSOID_AIRY1830_MINORAXIS,
              Constants.NATIONALGRID_N0,
              Constants.NATIONALGRID_E0,
              Constants.NATIONALGRID_F0,
              Constants.NATIONALGRID_LAT0,
              Constants.NATIONALGRID_LON0);
      double[] latlonWGS84 = OSGB36.toWGS84(latlonOSGB38[0], latlonOSGB38[1]);
      loc.setGeoJson(
          String.format(
              "{\"type\": \"Point\", \"coordinates\": [%f,%f]}", latlonWGS84[1], latlonWGS84[0]));
    } catch (Exception e) {
      // Made this debug as it's a common occurance and not serious error, eg to 120911 (as in the
      // date)
      getMonitor().debug("Unable to convert OSGB {}", matcher.group(), e);
    }
    return loc;
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(Coordinate.class));
  }
}
