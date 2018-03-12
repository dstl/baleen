//NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import uk.gov.dstl.baleen.collectionreaders.testing.AbstractReaderTest;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class SqlRowReaderTest extends AbstractReaderTest {
    public SqlRowReaderTest(){
        super(SqlRowReader.class);
    }

    private Connection conn;
    private int curr = 1;

    @Test
    public void test() throws Exception{
        setup();

        BaleenCollectionReader bcr = getCollectionReader(SqlRowReader.PARAM_SQL_CONNECTION, "jdbc:h2:mem:test",
                SqlRowReader.PARAM_SQL_TABLE, "my_table",
                SqlRowReader.PARAM_SQL_IGNORE, "ignore_me",
                SqlRowReader.PARAM_TEXT_COLUMNS, new String[]{"text_column1", "text_column2"});

        assertTrue(bcr.hasNext());
        bcr.getNext(jCas);

        testJcas(jCas, 1);


        jCas.reset();
        assertFalse(bcr.hasNext());

        insert();

        assertTrue(bcr.hasNext());
        bcr.getNext(jCas);

        testJcas(jCas, 2);

        assertFalse(bcr.hasNext());

        teardown();
    }

    private void setup() throws Exception{
        conn = DriverManager.getConnection("jdbc:h2:mem:test");
        conn.prepareStatement("CREATE TABLE my_table (id int primary key, ignore_me varchar(255), text_column1 varchar(255), text_column2 varchar(255), meta1 int, meta2 varchar(255))").execute();
        insert();
    }

    private void insert() throws Exception{
        conn.prepareStatement("INSERT INTO my_table " +
                "(id, ignore_me, text_column1, text_column2, meta1, meta2) VALUES " +
                "("+curr+", 'IGNORE', 'TEXT BLOCK 1', 'TEXT BLOCK 2', "+curr+", 'META')").execute();

        curr++;
    }

    private void testJcas(JCas jCas, int id){
        assertEquals("TEXT BLOCK 1\n\nTEXT BLOCK 2", jCas.getDocumentText());

        Map<String, String> meta = new HashMap<>();
        for(Metadata md : JCasUtil.select(jCas, Metadata.class)){
            meta.put(md.getKey().toLowerCase(), md.getValue());
        }

        assertEquals(String.valueOf(id), meta.get("id"));
        assertEquals(String.valueOf(id), meta.get("meta1"));
        assertEquals("META", meta.get("meta2"));
        assertFalse(meta.containsKey("ignore_me"));
    }

    private void teardown() throws Exception{
        conn.prepareStatement("DROP TABLE my_table").execute();
        conn.close();
    }
}
