// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractQuantityRegexAnnotator;
import uk.gov.dstl.baleen.uima.data.TextBlock;

/**
 * Annotate areas within a document using regular expressions
 *
 * <p>The document content is searched for things that might represent areas using regular
 * expressions. Any extracted areas are normalized to m^2.
 */
public class Area extends AbstractQuantityRegexAnnotator {
  public static final double MM2_TO_M2 = 0.000001;
  public static final double CM2_TO_M2 = 0.0001;
  public static final double KM2_TO_M2 = 1000000.0;

  public static final double MI2_TO_M2 = 2589988.1;
  public static final double YD2_TO_M2 = 0.83612739;
  public static final double FT2_TO_M2 = 0.092903044;
  public static final double IN2_TO_M2 = 0.000064516;

  public static final double ACRE_TO_M2 = 4046.8564;
  public static final double HECTARE_TO_M2 = 10000.0;

  private final Pattern m2Pattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(m\\^2|square metre|square meter|square m)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern mm2Pattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(mm\\^2|square millimetre|square millimeter|square mm)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern cm2Pattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(cm\\^2|square centimetre|square centimeter|square cm)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern km2Pattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(km\\^2|square kilometre|square kilometers|square km)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern mi2Pattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(mi\\^2|square miles|square mi)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern yd2Pattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(yd\\^2|square yard|square yd)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern ft2Pattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(ft\\^2|square foot|square feet|square ft)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern in2Pattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(in\\^2|square inch|square in|square inche)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern ha2Pattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(hectare|ha)(s)?\\b",
          Pattern.CASE_INSENSITIVE);
  private final Pattern acre2Pattern =
      Pattern.compile(
          "\\b([0-9]+([0-9\\.,]+[0-9])?)[ ]?(thousand|million|billion|trillion)?[ ]?(acre)(s)?\\b",
          Pattern.CASE_INSENSITIVE);

  /** Constructor */
  public Area() {
    super("m^2", "area");
  }

  @Override
  public void doProcessTextBlock(TextBlock block) throws AnalysisEngineProcessException {
    String text = block.getCoveredText();

    process(block, text, m2Pattern, "m^2", 1);
    process(block, text, cm2Pattern, "cm^2", CM2_TO_M2);
    process(block, text, mm2Pattern, "mm^2", MM2_TO_M2);
    process(block, text, km2Pattern, "km^2", KM2_TO_M2);
    process(block, text, mi2Pattern, "mi^2", MI2_TO_M2);
    process(block, text, yd2Pattern, "yd^2", YD2_TO_M2);
    process(block, text, ft2Pattern, "ft^2", FT2_TO_M2);
    process(block, text, in2Pattern, "in^2", IN2_TO_M2);
    process(block, text, acre2Pattern, "acre", ACRE_TO_M2);
    process(block, text, ha2Pattern, "ha", HECTARE_TO_M2);
  }
}
