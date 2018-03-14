// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import java.util.Collections;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.common.Chemical;

/** Identify chemicals by looking for CAS numbers and checking the check digit */
public class CasRegistryNumber extends AbstractRegexAnnotator<Chemical> {
  private static final String CAS_REGEX = "\\b(\\d{2,7})-(\\d{2})-(\\d{1})\\b";

  /** New instance. */
  public CasRegistryNumber() {
    super(CAS_REGEX, false, 1.0f);
  }

  @Override
  protected Chemical create(JCas jCas, Matcher matcher) {
    // Check checksum
    Integer checkDigit = Integer.valueOf(matcher.group(3));

    String part1 = matcher.group(1);
    String part2 = matcher.group(2);

    part1 = StringUtils.reverse(part1);

    Integer sum =
        Integer.valueOf(part2.substring(1, 2)) + (2 * Integer.valueOf(part2.substring(0, 1)));
    Integer pos = 0;
    while (pos < part1.length()) {
      Integer x = Integer.valueOf(part1.substring(pos, pos + 1));
      sum += (pos + 3) * x;

      pos++;
    }

    if (sum % 10 != checkDigit) {
      getMonitor().debug("Pattern matching CAS format found, but check digit is incorrect");
      return null;
    }

    Chemical c = new Chemical(jCas);
    c.setSubType("CAS");
    return c;
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(Chemical.class));
  }
}
