// NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.collectionreaders.helpers.AbstractSqlReader;

/**
 * Read each cell in an SQL table as a separate document. The appropriate JDBC driver for the
 * database you wish to connect to must be on the classpath.
 *
 * @baleen.javadoc
 */
public class SqlCellReader extends AbstractSqlReader {

  private List<Long> idsToProcess = new ArrayList<>();
  private List<String> colsToProcess = new ArrayList<>();
  private Set<String> allCols = new HashSet<>();
  private long currId;

  @Override
  protected void doInitialize(UimaContext context) throws ResourceInitializationException {
    super.doInitialize(context);

    getColumnNames().stream().filter(s -> !idColumn.equalsIgnoreCase(s)).forEach(allCols::add);
    idsToProcess.addAll(getIds(null));
  }

  @Override
  public boolean doHasNext() throws IOException, CollectionException {
    if (!colsToProcess.isEmpty()) return true;

    if (!idsToProcess.isEmpty()) return true;

    idsToProcess.addAll(getIds(currId));
    return !idsToProcess.isEmpty();
  }

  @Override
  @SuppressWarnings("squid:S2077" /* The value of col is read from the database column names and so should be safe to use in this context */)
    
  protected void doGetNext(JCas jCas) throws IOException, CollectionException {
    if (colsToProcess.isEmpty()) {
      // Get next row
      currId = idsToProcess.remove(0);

      colsToProcess.addAll(allCols);
    }

    String col = colsToProcess.remove(0);

    String content;
    
    try (ResultSet rs =
        conn.prepareStatement(
                "SELECT `" + col + "` FROM `" + table + "` WHERE `" + idColumn + "` = " + currId)
            .executeQuery()) {
      if (rs.next()) {
        content = rs.getObject(col).toString();
      } else {
        throw new IOException("Unable to get cell content - query returned no results");
      }

    } catch (SQLException e) {
      throw new IOException("Unable to get cell content", e);
    }

    String sourceUrl = sqlConn.substring(5) + "." + table + "#" + currId + "." + col;

    extractor.processStream(
        new ByteArrayInputStream(content.getBytes(Charset.defaultCharset())), sourceUrl, jCas);
  }
}
