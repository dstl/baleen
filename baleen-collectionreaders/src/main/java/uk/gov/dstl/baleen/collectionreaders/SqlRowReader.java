//NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import uk.gov.dstl.baleen.collectionreaders.helpers.AbstractSqlReader;
import uk.gov.dstl.baleen.types.metadata.Metadata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Read each cell in an SQL table as a separate document.
 * The appropriate JDBC driver for the database you wish to connect to must
 * be on the classpath.
 *
 * Users can specify which field(s) contain text content to be processed,
 * and all other fields will be treated as metadata. If more than one text
 * field is specified, they will be joined together in the order they were
 * specified, with content separated by two newlines.
 *
 * @baleen.javadoc
 */
public class SqlRowReader extends AbstractSqlReader {

    /**
     * Text column(s) in table - these columns will be used as the main content to process.
     * If more than one column is specified, then the content of the cells will be joined
     * with two newlines.
     *
     * @baleen.config
     */
    public static final String PARAM_TEXT_COLUMNS = "text";
    @ConfigurationParameter(name = PARAM_TEXT_COLUMNS)
    private String[] textColumn;


    private List<Long> idsToProcess = new ArrayList<>();
    private Set<String> textCols = new HashSet<>();
    private Set<String> metaCols = new HashSet<>();
    private long currId;

    @Override
    protected void doInitialize(UimaContext context) throws ResourceInitializationException {
        super.doInitialize(context);

        for(String columnName : getColumnNames()){
            if(inArray(columnName, textColumn)){
                textCols.add(columnName);
            }else {
                metaCols.add(columnName);
            }
        }

        idsToProcess.addAll(getIds(null));
    }

    @Override
    public boolean doHasNext() throws IOException, CollectionException {
        if(!idsToProcess.isEmpty())
            return true;

        idsToProcess.addAll(getIds(currId));
        return !idsToProcess.isEmpty();
    }

    @Override
    protected void doGetNext(JCas jCas) throws IOException, CollectionException {
        currId = idsToProcess.remove(0);

        String content;
        Map<String, String> metadata = new HashMap<>();
        try {
            ResultSet rs = conn.prepareStatement("SELECT * FROM `" + table + "` WHERE `"+idColumn+"` = "+currId).executeQuery();

            if(rs.next()){
                StringJoiner sj = new StringJoiner("\n\n");
                for(String col : textCols){
                    sj.add(rs.getObject(col).toString());
                }
                content = sj.toString();

                for(String col : metaCols){
                    metadata.put(col, rs.getObject(col).toString());
                }
            }else{
                throw new IOException("Unable to get row content - query returned no results");
            }

        }catch (SQLException e) {
            throw new IOException("Unable to get row content", e);
        }

        String sourceUrl = sqlConn.substring(5) + "." + table + "#" + currId;

        extractor.processStream(new ByteArrayInputStream(content.getBytes(Charset.defaultCharset())), sourceUrl, jCas);

        //Need to do after we've set the JCas content
        for(Map.Entry<String, String> e : metadata.entrySet()){
            Metadata md = new Metadata(jCas);
            md.setKey(e.getKey());
            md.setValue(e.getValue());
            md.addToIndexes();
        }
    }
}
