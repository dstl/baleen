// NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Optional;

import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.collectionreaders.testing.AbstractReaderTest;
import uk.gov.dstl.baleen.contentextractors.StructureContentExtractor;
import uk.gov.dstl.baleen.core.pipelines.content.ContentExtractor;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;
import uk.gov.dstl.baleen.uima.utils.JCasMetadata;

public class SqlDbCellReaderTest extends AbstractReaderTest {
  public SqlDbCellReaderTest() {
    super(SqlDbCellReader.class);
  }

  @Override
  protected Class<? extends ContentExtractor> getContentExtractorClass() {
    return StructureContentExtractor.class;
  }

  private Connection conn;

  @Test
  public void test() throws Exception {
    setup();

    BaleenCollectionReader bcr =
        getCollectionReader(
            SqlDbCellReader.PARAM_SQL_CONNECTION,
            "jdbc:h2:mem:test",
            SqlDbCellReader.PARAM_SQL_IGNORE_COLUMNS,
            new String[] {"MY_TABLE.ignore_me", "MY_TABLE.id", "MY_TABLE2.id"});

    // MY_TABLE

    assertTrue(bcr.hasNext());
    bcr.getNext(jCas);
    testJcasTable1(jCas, 1);
    jCas.reset();

    assertTrue(bcr.hasNext());
    bcr.getNext(jCas);
    testJcasTable1(jCas, 1);
    jCas.reset();

    assertTrue(bcr.hasNext());
    bcr.getNext(jCas);
    testJcasTable1(jCas, 2);
    jCas.reset();

    assertTrue(bcr.hasNext());
    bcr.getNext(jCas);
    testJcasTable1(jCas, 2);
    jCas.reset();

    // MY_TABLE2

    assertTrue(bcr.hasNext());
    bcr.getNext(jCas);
    testJcasTable2(jCas, 1);
    jCas.reset();

    assertTrue(bcr.hasNext());
    bcr.getNext(jCas);
    testJcasTable2(jCas, 1);
    jCas.reset();

    assertTrue(bcr.hasNext());
    bcr.getNext(jCas);
    testJcasTable2(jCas, 1);
    jCas.reset();

    assertFalse(bcr.hasNext());

    teardown();
  }

  private void setup() throws Exception {
    conn = DriverManager.getConnection("jdbc:h2:mem:test");
    conn.prepareStatement(
            "CREATE TABLE my_table (id int primary key, ignore_me varchar(255), text_column1 varchar(255), text_column2 varchar(255))")
        .execute();

    conn.prepareStatement(
            "INSERT INTO my_table "
                + "(id, ignore_me, text_column1, text_column2) VALUES "
                + "(1, 'IGNORE', 'TEXT BLOCK 1', 'TEXT BLOCK 2')")
        .execute();
    conn.prepareStatement(
            "INSERT INTO my_table "
                + "(id, ignore_me, text_column1, text_column2) VALUES "
                + "(2, 'IGNORE', 'TEXT BLOCK 1', 'TEXT BLOCK 2')")
        .execute();

    conn.prepareStatement(
            "CREATE TABLE my_table2 (id int primary key, ignore_me varchar(255), text_column1 varchar(255), text_column2 varchar(255))")
        .execute();

    conn.prepareStatement(
            "INSERT INTO my_table2 "
                + "(id, ignore_me, text_column1, text_column2) VALUES "
                + "(3, 'IGNORE', 'TEXT BLOCK 1', 'TEXT BLOCK 2')")
        .execute();
  }

  private void testJcasTable1(JCas jCas, int row) {
    assertTrue(jCas.getDocumentText().startsWith("TEXT BLOCK"));
    JCasMetadata metadata = new JCasMetadata(jCas);
    Optional<String> find = metadata.find("resourceName");
    assertTrue(find.get().toLowerCase().startsWith("h2:mem:test.my_table#" + row));
  }

  private void testJcasTable2(JCas jCas, int row) {
    JCasMetadata metadata = new JCasMetadata(jCas);
    Optional<String> find = metadata.find("resourceName").map(String::toLowerCase);

    assertTrue(find.get().startsWith("h2:mem:test.my_table2#" + row));

    if (find.get().endsWith("ignore_me")) {
      assertEquals("IGNORE", jCas.getDocumentText());
    } else {
      assertTrue(jCas.getDocumentText().startsWith("TEXT BLOCK"));
    }
  }

  private void teardown() throws Exception {
    conn.prepareStatement("DROP TABLE my_table").execute();
    conn.close();
  }
}
