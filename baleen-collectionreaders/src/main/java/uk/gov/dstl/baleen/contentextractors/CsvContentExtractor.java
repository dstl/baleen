// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentextractors;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import uk.gov.dstl.baleen.contentextractors.helpers.AbstractContentExtractor;
import uk.gov.dstl.baleen.types.metadata.Metadata;

/**
 * Takes a single line of CSV data, and splits it into 'columns' based on the specified separator
 * character. The column designated as the main content is set as the JCas body, and other columns
 * are added as Metadata annotations.
 *
 * @baleen.javadoc
 */
public class CsvContentExtractor extends AbstractContentExtractor {

  /**
   * Separator to split columns by. Can only be a single character, and if more than one character
   * is provided only the first will be used.
   *
   * @baleen.config ,
   */
  public static final String PARAM_SEPARATOR = "separator";

  @ConfigurationParameter(name = PARAM_SEPARATOR, defaultValue = ",")
  String separator;

  /**
   * The column number containing the content
   *
   * @baleen.config 1
   */
  public static final String PARAM_CONTENT_COLUMN = "contentColumn";

  @ConfigurationParameter(name = PARAM_CONTENT_COLUMN, defaultValue = "1")
  Integer contentColumn;

  /**
   * A list of column headings (in order, skipping the content column) to use as metadata keys. If
   * not provided, each column will be called column# where the # represents the column number.
   *
   * @baleen.config
   */
  public static final String PARAM_COLUMNS = "columns";

  @ConfigurationParameter(
      name = PARAM_COLUMNS,
      defaultValue = {})
  List<String> columns;

  @Override
  public void doProcessStream(InputStream stream, String source, JCas jCas) throws IOException {
    super.doProcessStream(stream, source, jCas);
    CSVParser parser = new CSVParserBuilder().withSeparator(separator.charAt(0)).build();
    try (CSVReader reader =
        new CSVReaderBuilder(new InputStreamReader(stream, StandardCharsets.UTF_8))
            .withCSVParser(parser)
            .build()) {
      String[] cols = reader.readNext();
      if (cols == null || cols.length < contentColumn) {
        throw new IOException("Not enough columns");
      }

      for (int i = 0; i < cols.length; i++) {
        if (i == (contentColumn - 1)) {
          jCas.setDocumentText(cols[i]);
        } else {
          addMetadata(jCas, i, cols[i]);
        }
      }
    }
  }

  private void addMetadata(JCas jCas, Integer index, String value) {
    Metadata md = new Metadata(jCas);

    Integer colNameIndex = index;
    if (index >= contentColumn) colNameIndex--;

    if (colNameIndex >= columns.size() || columns.get(colNameIndex).trim().isEmpty()) {
      md.setKey("column" + (index + 1));
    } else {
      md.setKey(columns.get(colNameIndex).trim());
    }
    md.setValue(value);
    getSupport().add(md);
  }
}
