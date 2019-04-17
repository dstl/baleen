// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.csv;

import static com.opencsv.CSVWriter.NO_ESCAPE_CHARACTER;
import static com.opencsv.CSVWriter.NO_QUOTE_CHARACTER;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.opencsv.CSVWriter;

import uk.gov.dstl.baleen.uima.BaleenConsumer;

/** Base class for outputting CSV files. */
public abstract class AbstractCsvConsumer extends BaleenConsumer {
  private static final Pattern NORMALIZE_PATTERN = Pattern.compile("\\s+");

  /**
   * The filename to use
   *
   * @baleen.config output.csv
   */
  public static final String KEY_FILENAME = "filename";

  @ConfigurationParameter(name = KEY_FILENAME, defaultValue = "output.csv")
  private String filename;

  /**
   * The separator char to use
   *
   * @baleen.config ,
   */
  public static final String SEPARATOR_CHAR = "separator";

  @ConfigurationParameter(name = SEPARATOR_CHAR, defaultValue = ",")
  private String separator;

  /**
   * The quote char to use
   *
   * @baleen.config "
   */
  public static final String QUOTE_CHAR = "quote";

  @ConfigurationParameter(name = QUOTE_CHAR, defaultValue = "\"")
  private String quoteCharacter;

  /**
   * The escape char to use
   *
   * @baleen.config "
   */
  public static final String ESCAPE_CHAR = "escape";

  @ConfigurationParameter(name = ESCAPE_CHAR, defaultValue = "\"")
  private String escapeCharacter;

  /**
   * The line ending to use
   *
   * @baleen.config ,
   */
  public static final String LINE_ENDING = "lineEnding";

  @ConfigurationParameter(name = LINE_ENDING, defaultValue = "\n")
  private String lineEnding;

  private CSVWriter writer;

  /** Instantiates a new abstract csv consumer. */
  protected AbstractCsvConsumer() {
    super();
  }

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);

    try {
      // Attempt to create the path if it doesn't exist
      new File(filename).getParentFile().mkdirs();

      writer =
          new CSVWriter(
              new OutputStreamWriter(new FileOutputStream(filename, false), StandardCharsets.UTF_8),
              separator.charAt(0),
              getQuote(),
              getEscape(),
              lineEnding);
    } catch (final IOException e) {
      throw new ResourceInitializationException(e);
    }
  }

  private char getQuote() {
    if (StringUtils.isEmpty(quoteCharacter)) {
      return NO_QUOTE_CHARACTER;
    } else {
      return quoteCharacter.charAt(0);
    }
  }

  private char getEscape() {
    if (StringUtils.isEmpty(escapeCharacter)) {
      return NO_ESCAPE_CHARACTER;
    } else {
      return escapeCharacter.charAt(0);
    }
  }

  @Override
  protected final void doProcess(JCas jCas) throws AnalysisEngineProcessException {
    write(jCas);

    try {
      writer.flush();
    } catch (final IOException e) {
      getMonitor().warn("Unable to flush file", e);
    }
  }

  /**
   * Write the JCas to CSV.
   *
   * @param jCas the j cas
   */
  protected abstract void write(JCas jCas);

  @Override
  protected void doDestroy() {

    try {
      if (writer != null) {
        try {
          writer.flush();
          writer.close();
        } catch (final IOException e) {
          getMonitor().warn("Failed to close csv writer", e);
        }
      }
    } finally {
      writer = null;
    }

    super.doDestroy();
  }

  /**
   * Called by implementors to write a row.
   *
   * @param row the row
   */
  protected void write(String... row) {
    writer.writeNext(row);
  }

  /**
   * Normalize the text (called by implementors).
   *
   * @param text the text
   * @return the string
   */
  protected String normalize(String text) {
    return text == null ? "" : NORMALIZE_PATTERN.matcher(text).replaceAll(" ").trim();
  }
}
