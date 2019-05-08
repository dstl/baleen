// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.opensextant.geodesy.MGRS;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.baleen.uima.BaleenTextAwareAnnotator;
import uk.gov.dstl.baleen.uima.data.TextBlock;

/**
 * Annotate MGRS coordinates within a document using regular expressions
 *
 * <p>Military Grid Reference System (MGRS) coordinates are extracted from the document content
 * using the following regular expression:
 *
 * <p>Military Grid Reference System (MGRS) coordinates are extracted from the document content
 * using the following regular expression:
 *
 * <pre>
 * \b(GR\\h*)?([0-6]?[0-9]\\h*([C-HJ-NP-X])\\h*[A-HJ-NP-Z][A-HJ-NP-V]\\h*(([0-9]{5}\\h*[0-9]{5})|([0-9]{4}\\h*[0-9]{4})|([0-9]{3}\\h*[0-9]{3})|([0-9]{2}\\h*[0-9]{2})))\\b
 * </pre>
 *
 * <p>Some date strings, e.g. 19MAR1968, are also valid MGRS coordinates. These can be ignored by
 * setting the ignoreDates parameter. If ignoreDates is true, then the following MGRS is used to
 * exclude dates:
 *
 * <pre>([0-2]?[0-9]|3[01])\\h*(JAN|FEB|MAR|JUN|JUL|SEP|DEC)\\h*([0-9]{2}|[0-9]{4})</pre>
 *
 * @baleen.javadoc
 */
public class Mgrs extends BaleenTextAwareAnnotator {
  private final Pattern mgrsPattern =
      Pattern.compile(
          "\\b(GR\\h*)?([0-6]?[0-9]\\h*([C-HJ-NP-X])\\h*[A-HJ-NP-Z][A-HJ-NP-V]\\h*(([0-9]{5}\\h*[0-9]{5})|([0-9]{4}\\h*[0-9]{4})|([0-9]{3}\\h*[0-9]{3})|([0-9]{2}\\h*[0-9]{2})))\\b");
  private final Pattern datesPattern =
      Pattern.compile(
          "([0-2]?[0-9]|3[01])\\h*(JAN|FEB|MAR|JUN|JUL|SEP|DEC)\\h*([0-9]{2}|[0-9]{4})");

  /**
   * Should MGRS coordinates that may refer to dates be ignored?
   *
   * @baleen.config false
   */
  public static final String PARAM_IGNORE_DATES = "ignoreDates";

  @ConfigurationParameter(name = PARAM_IGNORE_DATES, defaultValue = "false")
  private boolean ignoreDates;

  @Override
  public void doProcessTextBlock(TextBlock block) throws AnalysisEngineProcessException {
    String text = block.getCoveredText();

    Matcher matcher = mgrsPattern.matcher(text);

    while (matcher.find()) {

      if (ignoreDates) {
        Matcher dateMatcher = datesPattern.matcher(matcher.group(2));
        if (dateMatcher.matches()) {
          getMonitor()
              .info(
                  "Discarding possible MGRS coordinate '{}' as it resembles a date",
                  matcher.group(2));
          continue;
        }
      }

      Coordinate loc = new Coordinate(block.getJCas());

      loc.setConfidence(1.0f);

      block.setBeginAndEnd(loc, matcher.start(), matcher.end());
      loc.setValue(matcher.group(2));

      loc.setSubType("mgrs");

      enhanceCoordinate(matcher, loc);

      addToJCasIndex(loc);
    }
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(Coordinate.class));
  }

  /**
   * Enhances the coordinate by parsing the MGRS coordinate and adding additional information (i.e.
   * the GeoJson polygon representing the MGRS square)
   */
  private void enhanceCoordinate(Matcher matcher, Coordinate loc) {
    try {
      MGRS mgrs = new MGRS(matcher.group(2));
      loc.setGeoJson(
          "{\"type\":\"Polygon\",\"coordinates\":[["
              + "["
              + mgrs.getBoundingBox().getEastLon().inDegrees()
              + ","
              + mgrs.getBoundingBox().getNorthLat().inDegrees()
              + "],"
              + "["
              + mgrs.getBoundingBox().getWestLon().inDegrees()
              + ","
              + mgrs.getBoundingBox().getNorthLat().inDegrees()
              + "],"
              + "["
              + mgrs.getBoundingBox().getWestLon().inDegrees()
              + ","
              + mgrs.getBoundingBox().getSouthLat().inDegrees()
              + "],"
              + "["
              + mgrs.getBoundingBox().getEastLon().inDegrees()
              + ","
              + mgrs.getBoundingBox().getSouthLat().inDegrees()
              + "],"
              + "["
              + mgrs.getBoundingBox().getEastLon().inDegrees()
              + ","
              + mgrs.getBoundingBox().getNorthLat().inDegrees()
              + "]]]}");
    } catch (IllegalArgumentException e) {
      getMonitor().warn("Couldn't parse MGRS co-ordinate", e);
    }
  }
}
