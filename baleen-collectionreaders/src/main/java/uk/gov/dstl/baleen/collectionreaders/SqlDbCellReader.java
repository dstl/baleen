// NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;

/**
 * Processes all tables in an SQL database, treating each cell in the table as a separate document.
 *
 * <p>This collection reader does not keep track of new records being inserted into the database,
 * and should therefore only be used on static databases. If this collection reader is run on a
 * non-static database, no guarantees are made as to what data will be processed.
 *
 * @baleen.javadoc
 */
public class SqlDbCellReader extends BaleenCollectionReader {

  /**
   * The JDBC connection string, including database.
   *
   * <p>For example: jdbc:mysql://localhost:3106/mydatabase
   *
   * @baleen.config
   */
  public static final String PARAM_SQL_CONNECTION = "connection";

  @ConfigurationParameter(name = PARAM_SQL_CONNECTION)
  protected String sqlConn;

  /**
   * The username for the database, or empty if there is no username. Both user and password must be
   * set if you wish to use authentication.
   *
   * @baleen.config
   */
  public static final String PARAM_SQL_USER = "user";

  @ConfigurationParameter(name = PARAM_SQL_USER, defaultValue = "", mandatory = false)
  private String user;

  /**
   * The password for the database, or empty if there is no password. Both user and password must be
   * set if you wish to use authentication.
   *
   * @baleen.config
   */
  public static final String PARAM_SQL_PASS = "password";

  @ConfigurationParameter(name = PARAM_SQL_PASS, defaultValue = "", mandatory = false)
  private String pass;

  /**
   * Ignore these tables
   *
   * @baleen.config
   */
  public static final String PARAM_SQL_IGNORE_TABLES = "ignoreTables";

  @ConfigurationParameter(
      name = PARAM_SQL_IGNORE_TABLES,
      defaultValue = {})
  protected String[] ignoreTables;

  /**
   * Ignore these columns. Columns should be fully qualified (e.g. table.column_name)
   *
   * @baleen.config
   */
  public static final String PARAM_SQL_IGNORE_COLUMNS = "ignoreColumns";

  @ConfigurationParameter(
      name = PARAM_SQL_IGNORE_COLUMNS,
      defaultValue = {})
  protected String[] ignoreColumns;

  /**
   * The query to list the tables in the database. Table names should be returned in the first
   * column.
   *
   * <p>This may need changing depending on the database you are connecting to.
   *
   * @baleen.config SHOW TABLES
   */
  public static final String PARAM_SQL_TABLES = "tablesSql";

  @ConfigurationParameter(name = PARAM_SQL_TABLES, defaultValue = "SHOW TABLES")
  protected String tablesSql;

  /**
   * The query to list the columns in the database. Column names should be returned in the first
   * column, and any instances of ? will be replaced by the table name.
   *
   * <p>This may need changing depending on the database you are connecting to.
   *
   * @baleen.config SHOW COLUMNS FROM `?`
   */
  public static final String PARAM_SQL_COLUMNS = "columnsSql";

  @ConfigurationParameter(name = PARAM_SQL_COLUMNS, defaultValue = "SHOW COLUMNS FROM `?`")
  protected String columnsSql;

  private static final Logger LOGGER = LoggerFactory.getLogger(SqlDbCellReader.class);

  private Connection conn;

  private String currTable = "";
  private long rowId = 0;
  private List<String> tables = new ArrayList<>();
  private List<String> columns = new ArrayList<>();
  private ResultSet rsCurrTable = null;
  private Map<String, Object> currRow = new HashMap<>();

  @Override
  protected void doInitialize(UimaContext context) throws ResourceInitializationException {
    try {
      if (Strings.isNullOrEmpty(user) || Strings.isNullOrEmpty(pass)) {
        conn = DriverManager.getConnection(sqlConn);
      } else {
        conn = DriverManager.getConnection(sqlConn, user, pass);
      }
    } catch (SQLException e) {
      throw new ResourceInitializationException(e);
    }

    try (ResultSet rsTables = conn.prepareStatement(tablesSql).executeQuery()) {
      while (rsTables.next()) {
        String tableName = rsTables.getString(1);
        if (inArray(tableName, ignoreTables)) {
          continue;
        }

        tables.add(tableName);
      }

      LOGGER.info("{} tables found for processing", tables.size());
      LOGGER.debug("Table names: {}", tables);

      if (tables.isEmpty()) {
        throw new ResourceInitializationException(
            new BaleenException("No tables found in database"));
      }

      getNextTable();
    } catch (SQLException e) {
      throw new ResourceInitializationException(e);
    }
  }

  @Override
  public boolean doHasNext() throws IOException, CollectionException {
    if (currRow.isEmpty()) {
      try {
        if (!rsCurrTable.next()) {
          if (!getNextTable()) {
            return false;
          }
          rowId = 0;
          rsCurrTable.next();
        }

        rowId++;
        for (String col : columns) {
          currRow.put(col, rsCurrTable.getObject(col));
        }
      } catch (SQLException se) {
        throw new IOException(se);
      }
    }
    return !currRow.isEmpty();
  }

  @SuppressWarnings("squid:S2095")
  private boolean getNextTable() throws SQLException {
    if (tables.isEmpty()) {
      return false;
    }

    columns.clear();

    currTable = tables.remove(0);
    LOGGER.info("Now processing table {}", currTable);

    try (ResultSet rsColumns =
        conn.prepareStatement(columnsSql.replaceAll("\\?", currTable)).executeQuery()) {
      while (rsColumns.next()) {
        String columnName = rsColumns.getString(1);
        if (inArray(currTable + "." + columnName, ignoreColumns)) {
          continue;
        }

        columns.add(columnName);
      }

      LOGGER.info("{} columns found for processing", columns.size());
      LOGGER.debug("Column names: {}", columns);
    }

    StringJoiner sjCols = new StringJoiner("`,`", "`", "`");
    columns.forEach(sjCols::add);

    // We can't wrap this line in a try-with-resources, as it is needed by doHasNext()
    // We close this in doClose()
    rsCurrTable =
        conn.prepareStatement("SELECT " + sjCols.toString() + " FROM " + currTable).executeQuery();
    return true;
  }

  @Override
  protected void doGetNext(JCas jCas) throws IOException, CollectionException {
    String key = currRow.keySet().iterator().next();

    Object o = currRow.remove(key);

    String sourceUrl = sqlConn.substring(5) + "." + currTable + "#" + rowId + "." + key;
    extractContent(
        new ByteArrayInputStream(o.toString().getBytes(Charset.defaultCharset())), sourceUrl, jCas);
  }

  @Override
  protected void doClose() throws IOException {
    if (rsCurrTable != null) {
      try {
        rsCurrTable.close();
      } catch (SQLException e) {
        LOGGER.warn("Error closing ResultSet", e);
      }
    }

    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException e) {
        LOGGER.warn("Error closing connection to database", e);
      }
    }
  }

  private static boolean inArray(String needle, String[] haystack) {
    for (String h : haystack) {
      if (needle.equalsIgnoreCase(h)) {
        return true;
      }
    }

    return false;
  }
}
