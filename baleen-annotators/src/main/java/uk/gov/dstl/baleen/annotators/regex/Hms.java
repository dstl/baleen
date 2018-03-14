// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexNPAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.military.MilitaryPlatform;

/**
 * Extract military platforms (i.e. naval ships) that begin with HMS, or one of the variants used by
 * other commonwealth states (e.g. HMCS for Canadian ships).
 *
 * <p>This annotator is uses Noun Phrases to detect the end of the phrase, and these must be present
 * (e.g. language.OpenNLP should have been run prior).
 *
 * @baleen.javadoc
 */
public class Hms extends AbstractRegexNPAnnotator<MilitaryPlatform> {
  private static final Pattern HMS_PATTERN =
      Pattern.compile(
          "\\bH(\\.)?M(\\.)?((A|B|C|N(\\.)?Z|P(\\.)?N(\\.)?G|J|T(\\.)?S)(\\.)?)?S(\\.)? .*\\b");
  /** Constructor method */
  public Hms() {
    super(HMS_PATTERN, 0, 1.0);
  }

  @Override
  protected MilitaryPlatform create(JCas jCas, Matcher matcher) {
    MilitaryPlatform mp = new MilitaryPlatform(jCas);
    mp.setSubType("NAVAL");

    return mp;
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        Collections.emptySet(), ImmutableSet.of(MilitaryPlatform.class));
  }
}
