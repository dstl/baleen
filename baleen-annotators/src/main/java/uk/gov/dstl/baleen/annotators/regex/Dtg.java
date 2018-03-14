// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import java.time.DateTimeException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.helpers.DateTimeUtils;
import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.semantic.Temporal;

/**
 * Annotate DTG (Date Time Groups) within a document using regular expressions
 *
 * <p>The document content is run through a regular expression matcher looking for things that match
 * the following regular expression:
 *
 * <pre>
 * ([0-9]{2})\\s*([0-9]{2})([0-9]{2})([A-IK-Z]|D\\*)\\s*(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)\\s*([0-9]{2})
 * </pre>
 *
 * <p>Matched DTGs are parsed as a date and annotated as Temporal entities.
 *
 * @baleen.javadoc
 */
public class Dtg extends AbstractRegexAnnotator<Temporal> {
  private static final Map<String, ZoneOffset> zoneMap = createTimeCodeMap();
  private static final String DATETIME_REGEX =
      "([0-9]{2})\\s*([0-9]{2})([0-9]{2})([A-IK-Z]|D\\*)\\s*(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)\\s*([0-9]{2})";

  /** New instance. */
  public Dtg() {
    super(DATETIME_REGEX, false, 1.0);
  }

  @Override
  protected Temporal create(JCas jCas, Matcher matcher) {
    long timestamp = 0L;

    try {
      ZonedDateTime zdt =
          ZonedDateTime.of(
              2000 + Integer.parseInt(matcher.group(6)),
              DateTimeUtils.asMonth(matcher.group(5)).getValue(),
              Integer.parseInt(matcher.group(1)),
              Integer.parseInt(matcher.group(2)),
              Integer.parseInt(matcher.group(3)),
              0,
              0,
              militaryTimeCodeToOffset(matcher.group(4)));

      timestamp = zdt.toEpochSecond();
    } catch (DateTimeException dte) {
      getMonitor().warn("Unable to parse DTG", dte);
      return null;
    }

    Temporal dtg = new Temporal(jCas);

    dtg.setPrecision("EXACT");
    dtg.setScope("SINGLE");
    dtg.setTemporalType("DATETIME");

    dtg.setTimestampStart(timestamp);
    dtg.setTimestampStop(timestamp + 60);

    return dtg;
  }

  private static Map<String, ZoneOffset> createTimeCodeMap() {
    Map<String, ZoneOffset> map = new HashMap<>();
    map.put("A", ZoneOffset.ofHours(1));
    map.put("B", ZoneOffset.ofHours(2));
    map.put("C", ZoneOffset.ofHours(3));
    map.put("D", ZoneOffset.ofHours(4));
    map.put("D*", ZoneOffset.ofHoursMinutes(4, 30));
    map.put("E", ZoneOffset.ofHours(5));
    map.put("F", ZoneOffset.ofHours(6));
    map.put("G", ZoneOffset.ofHours(7));
    map.put("H", ZoneOffset.ofHours(8));
    map.put("I", ZoneOffset.ofHours(9));
    map.put("K", ZoneOffset.ofHours(10));
    map.put("L", ZoneOffset.ofHours(11));
    map.put("M", ZoneOffset.ofHours(12));
    map.put("N", ZoneOffset.ofHours(-1));
    map.put("O", ZoneOffset.ofHours(-2));
    map.put("P", ZoneOffset.ofHours(-3));
    map.put("Q", ZoneOffset.ofHours(-4));
    map.put("R", ZoneOffset.ofHours(-5));
    map.put("S", ZoneOffset.ofHours(-6));
    map.put("T", ZoneOffset.ofHours(-7));
    map.put("U", ZoneOffset.ofHours(-8));
    map.put("V", ZoneOffset.ofHours(-9));
    map.put("W", ZoneOffset.ofHours(-10));
    map.put("X", ZoneOffset.ofHours(-11));
    map.put("Y", ZoneOffset.ofHours(-12));

    return map;
  }

  private static ZoneOffset militaryTimeCodeToOffset(String timeCode) {
    return zoneMap.getOrDefault(timeCode.toUpperCase(), ZoneOffset.UTC);
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(Temporal.class));
  }
}
