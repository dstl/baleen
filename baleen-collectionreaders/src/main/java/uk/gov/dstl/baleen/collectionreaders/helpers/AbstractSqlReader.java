//NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders.helpers;

import com.google.common.base.Strings;
import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dstl.baleen.core.utils.BaleenDefaults;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;
import uk.gov.dstl.baleen.uima.IContentExtractor;

import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class for building collection readers using an SQL backend.
 * Class handles connection to database.
 *
 * @baleen.javadoc
 */
public abstract class AbstractSqlReader extends BaleenCollectionReader {
    /**
     * The content extractor to use to extract content from files
     *
     * @baleen.config Value of BaleenDefaults.DEFAULT_CONTENT_EXTRACTOR
     */
    public static final String PARAM_CONTENT_EXTRACTOR = "contentExtractor";
    @ConfigurationParameter(name = PARAM_CONTENT_EXTRACTOR, defaultValue= BaleenDefaults.DEFAULT_CONTENT_EXTRACTOR)
    protected String contentExtractor;

    /**
     * The JDBC connection string, including database.
     *
     * For example: jdbc:mysql://localhost:3106/mydatabase
     *
     * @baleen.config
     */
    public static final String PARAM_SQL_CONNECTION = "connection";
    @ConfigurationParameter(name = PARAM_SQL_CONNECTION)
    protected String sqlConn;

    /**
     * The username for the database, or empty if there is no username.
     * Both user and password must be set if you wish to use authentication.
     *
     * @baleen.config
     */
    public static final String PARAM_SQL_USER = "user";
    @ConfigurationParameter(name = PARAM_SQL_USER, defaultValue = "", mandatory = false)
    private String user;

    /**
     * The password for the database, or empty if there is no password.
     * Both user and password must be set if you wish to use authentication.
     *
     * @baleen.config
     */
    public static final String PARAM_SQL_PASS = "password";
    @ConfigurationParameter(name = PARAM_SQL_PASS, defaultValue = "", mandatory = false)
    private String pass;

    /**
     * The name of the table
     *
     * @baleen.config
     */
    public static final String PARAM_SQL_TABLE = "table";
    @ConfigurationParameter(name = PARAM_SQL_TABLE)
    protected String table;

    /**
     * ID column in table - this column will be used to look for new data.
     * It is assumed that this field is numeric and increases as new values are added.
     *
     * @baleen.config id
     */
    public static final String PARAM_SQL_ID = "id";
    @ConfigurationParameter(name = PARAM_SQL_ID, defaultValue = "id")
    protected String idColumn;

    /**
     * Ignore these columns in the table
     *
     * @baleen.config
     */
    public static final String PARAM_SQL_IGNORE = "ignore";
    @ConfigurationParameter(name = PARAM_SQL_IGNORE, defaultValue = {})
    protected String[] ignore;

    /**
     * The query to list the columns in the database. Column names should be returned
     * in a column called COLUMN_NAME, and any instances of ? will be replaced by the
     * table name.
     *
     * This may need changing depending on the database you are connecting to.
     *
     * @baleen.config SHOW COLUMNS FROM `?`
     */
    public static final String PARAM_SQL_COLUMNS = "columnsSql";
    @ConfigurationParameter(name = PARAM_SQL_COLUMNS, defaultValue = "SHOW COLUMNS FROM `?`")
    protected String columnsSql;

    protected IContentExtractor extractor;
    protected Connection conn;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSqlReader.class);

    private PreparedStatement psListColumns;

    @Override
    protected void doInitialize(UimaContext context) throws ResourceInitializationException {
        try{
            extractor = getContentExtractor(contentExtractor);
        }catch(InvalidParameterException ipe){
            throw new ResourceInitializationException(ipe);
        }
        extractor.initialize(context, getConfigParameters(context));

        try {
            if (Strings.isNullOrEmpty(user) || Strings.isNullOrEmpty(pass)) {
                conn = DriverManager.getConnection(sqlConn);
            } else {
                conn = DriverManager.getConnection(sqlConn, user, pass);
            }

            psListColumns = conn.prepareStatement(columnsSql.replaceAll("\\?", table));
        }catch(SQLException e){
            throw new ResourceInitializationException(e);
        }

    }

    @Override
    protected void doClose() throws IOException {
        if(conn != null){
            try{
                conn.close();
            }catch (SQLException e){
                LOGGER.warn("Error closing connection to database", e);
            }
        }
    }

    /**
     * Return a list of column names for a table, ignoring any columns listed in ignore.
     */
    protected Set<String> getColumnNames(){
        Set<String> cols = new HashSet<>();

        try {
            ResultSet rsColumns = psListColumns.executeQuery();

            while (rsColumns.next()) {
                String columnName = rsColumns.getString("COLUMN_NAME");
                if (inArray(columnName, ignore))
                    continue;

                cols.add(columnName);
            }
        }catch (SQLException e){
            LOGGER.error("Unable to get column names from table {}, empty set will be returned", table, e);
        }
        return cols;
    }

    /**
     * Get IDs from a table, for a given idColumn.
     *
     * If a startId is provided, then this ID is not included in the dataset
     * (i.e. this should be the last ID you processed).
     * If a startId is not provided (null), then all IDs are returned.
     */
    protected Set<Long> getIds(Long startId){
        Set<Long> ids = new HashSet<>();

        try{
            ResultSet rs;
            if(startId == null){
                rs = conn.prepareStatement("SELECT `"+idColumn+"` FROM `"+table+"`").executeQuery();
            }else{
                rs = conn.prepareStatement("SELECT `"+idColumn+"` FROM `"+table+"` WHERE `"+idColumn+"` > "+startId).executeQuery();
            }

            while(rs.next()){
                ids.add(rs.getLong(idColumn));
            }
        }catch (SQLException e){
            LOGGER.error("Unable to get IDs from table {}, empty set will be returned", table, e);
        }

        return ids;
    }

    /**
     * Check whether the needle appears in the haystack (case insensitive)
     */
    protected static boolean inArray(String needle, String[] haystack){
        for(String h : haystack){
            if(needle.equalsIgnoreCase(h))
                return true;
        }

        return false;
    }
}
