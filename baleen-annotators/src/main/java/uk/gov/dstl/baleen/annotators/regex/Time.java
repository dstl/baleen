// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import java.util.Arrays;
import java.util.Collections;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.semantic.Temporal;
import uk.gov.dstl.baleen.uima.utils.TemporalUtils;

/**
 * Annotate times within a document using regular expressions
 *
 * <p>The document content is run through a regular expression matcher looking for things that match
 * the following time regular expression, where UTC is being used to represent all time zone
 * acronyms defined in Java:
 *
 * <pre>
 * \\b(((0?[0-9])|([0-9]{2}))[:][0-9]{2}\\h*((UTC)([ ]?[+-][ ]?((0?[0-9])|(1[0-2])))?)?\\h*(pm|am)?)\\b|\\b(((1[0-2])|([1-9]))(pm|am))\\b|\\b(midnight)\\b|\\b(midday)\\b|\\b((12\\h)?noon)\\b|\\b([0-2][0-9][0-5][0-9][ ]?(hr(s)?)?[ ]?((UTC)([ ]?[+-][ ]?((0?[0-9])|(1[0-2])))?)?)\\b
 * </pre>
 *
 * <p>This will only capture times that match the regular expression, and will miss times expressed
 * in a different format. By default, only times that contain alphabetical characters or colons will
 * be accepted to minimise false positives.
 */
public class Time extends AbstractRegexAnnotator<Temporal> {

  /**
   * Do we require that there are alphabetical characters in the time? This helps avoid picking out
   * things like 2015 as a time when it should be a year, as it forces the time to be written like
   * 2015hrs or 8:15pm.
   *
   * <p>For the purposes of the TimeRegex annotator, colons are treated as alphabetical characters,
   * such that times such as 20:15 are captured. Other punctuation isn't, as 20.15 is more like to
   * be an amount than a time.
   *
   * @baleen.config true
   */
  public static final String PARAM_REQUIRE_ALPHA = "requireAlpha";

  @ConfigurationParameter(name = PARAM_REQUIRE_ALPHA, defaultValue = "true")
  private Boolean requireAlpha;

  private static final String TIME_ZONES =
      StringUtils.join(
          Arrays.asList(TimeZone.getAvailableIDs())
              .stream()
              .filter(s -> StringUtils.isAllUpperCase(s) && s.length() <= 3)
              .collect(Collectors.toList()),
          "|");

  private static final String TIME_REGEX =
      "\\b(([0-1]?[0-9]|2[0-4])[:\\.][0-5][0-9]\\h*(("
          + TIME_ZONES
          + ")([ ]?[+-][ ]?((0?[0-9])|(1[0-2])))?)?\\h*(pm|am)?)\\b|\\b(((1[0-2])|([1-9]))(pm|am))\\b|\\b(midnight)\\b|\\b(midday)\\b|\\b((12\\h)?noon)\\b|\\b([0-1][0-9]|2[0-4])[0-5][0-9][ ]?(((hr(s)?)?[ ]?(("
          + TIME_ZONES
          + ")([ ]?[+-][ ]?((0?[0-9])|(1[0-2])))?)?)|hours|h)\\b";

  /** New instance. */
  public Time() {
    super(TIME_REGEX, false, 1.0);
  }

  @Override
  protected Temporal create(JCas jCas, Matcher matcher) {
    if (requireAlpha) {
      String time = matcher.group();
      if (!time.matches(".*[a-zA-Z:].*")) {
        return null;
      }
    }

    Temporal dtg = new Temporal(jCas);

    dtg.setPrecision(TemporalUtils.PRESISION_UNQUALIFIED);
    dtg.setScope(TemporalUtils.SCOPE_SINGLE);
    dtg.setTemporalType(TemporalUtils.TYPE_TIME);

    return dtg;
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(Temporal.class));
  }
}
