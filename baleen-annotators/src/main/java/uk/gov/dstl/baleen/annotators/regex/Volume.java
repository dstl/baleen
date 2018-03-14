// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractQuantityRegexAnnotator;
import uk.gov.dstl.baleen.uima.data.TextBlock;

/**
 * Annotate volumes within a document using regular expressions
 *
 * <p>The document content is searched for things that might represent volumes using regular
 * expressions. Any extracted volumes are normalized to m^3.
 */
public class Volume extends AbstractQuantityRegexAnnotator {
  public static final double PINT_TO_M3 = 0.000568;
  public static final double GALLON_TO_M3 = 0.00454609;

  private final Pattern m3Pattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(m\\^3|cubic metre|cubic meter)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern cm3Pattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(cm\\^3|cubic centimetre|cubic centimeter)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern lPattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(l|litre|liter)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern mlPattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(ml|millilitre|milliliter)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern pintPattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(pt|pint)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern gallonPattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(gal|gallon)(s)?\\b",
          Pattern.CASE_INSENSITIVE);

  /** Constructor */
  public Volume() {
    super("m^3", "volume");
  }

  @Override
  public void doProcessTextBlock(TextBlock block) throws AnalysisEngineProcessException {
    String text = block.getCoveredText();

    process(block, text, m3Pattern, "m^3", 1.0);
    process(block, text, cm3Pattern, "cm^3", 1 / 1000000.0);
    process(block, text, lPattern, "l", 1 / 1000.0);
    process(block, text, mlPattern, "ml", 1 / 1000000.0);
    process(block, text, pintPattern, "pt", PINT_TO_M3);
    process(block, text, gallonPattern, "gal", GALLON_TO_M3);
  }
}
