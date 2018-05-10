// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.triage;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Assign yyyy/mm/dd string in file path to the document date
 *
 * @baleen.javadoc
 */
public class YYYYMMDDAssigner extends BaleenAnnotator {
  /**
   * The regex to use (must have year, month, day in and these should be the bits of the path)
   *
   * @baleen.config (?<year>X)\\d{4})\\/($<month>\\d{1,2})\\/(?<day>\\d{1,2})
   */
  public static final String PARAM_METADATA_KEY = "regex";

  @ConfigurationParameter(
    name = PARAM_METADATA_KEY,
    defaultValue = ".*(?<year>\\d{4})\\/(?<month>\\d{1,2})\\/(?<day>\\d{1,2}).*"
  ) // ")
  private String regex;

  private Pattern pattern;

  @Override
  public void doInitialize(final UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);

    pattern = Pattern.compile(regex);
  }

  @Override
  protected void doProcess(final JCas jCas) throws AnalysisEngineProcessException {
    final DocumentAnnotation da = getDocumentAnnotation(jCas);
    final String source = da.getSourceUri();

    final Matcher matcher = pattern.matcher(source);
    if (matcher.matches()) {
      try {
        final int y = Integer.parseInt(matcher.group("year"));
        final int m = Integer.parseInt(matcher.group("month"));
        final int d = Integer.parseInt(matcher.group("day"));

        if (m >= 1 && m <= 12 && d >= 1 && d <= 31) {
          // This will check if its' actually valid (31 Feb) it's actualy valid date...

          final LocalDate date = LocalDate.of(y, m, d);
          final long ts = date.atStartOfDay().atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();

          da.setTimestamp(ts);
        }

      } catch (final Exception e) {
        // Do nothing.. not a valid source path...
        getMonitor().warn("Cant parse date from source uri {} ", source, e);
      }
    }
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(Collections.emptySet(), Collections.emptySet());
  }
}
