// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.uima.UIMAException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.gov.dstl.baleen.collectionreaders.testing.AbstractReaderTest;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;

public class ReutersReaderTest extends AbstractReaderTest {

  public ReutersReaderTest() {
    super(ReutersReader.class);
  }

  private static final String SGML =
      "<!DOCTYPE lewis SYSTEM \"lewis.dtd\">\n"
          + "<REUTERS TOPICS=\"YES\" LEWISSPLIT=\"TRAIN\" CGISPLIT=\"TRAINING-SET\" OLDID=\"5544\" NEWID=\"1\">\n"
          + "<DATE>1-JAN-1990 10:11:12.13</DATE>\n"
          + "<TOPICS><D>topics</D></TOPICS>\n"
          + "<PLACES><D>uk</D><D>usa</D></PLACES>\n"
          + "<PEOPLE></PEOPLE>\n"
          + "<ORGS></ORGS>\n"
          + "<EXCHANGES></EXCHANGES>\n"
          + "<COMPANIES></COMPANIES>\n"
          + "<UNKNOWN> \n"
          + "&#5;&#5;&#5;C T\n"
          + "&#22;&#22;&#1;f0704&#31;reute\n"
          + "</UNKNOWN>\n"
          + "<TEXT>&#2;\n"
          + "<TITLE>TITLE</TITLE>\n"
          + "<DATELINE>   DATELINE </DATELINE><BODY> Some example\n"
          + "text. \n"
          + "Reuter\n"
          + "&#3;</BODY></TEXT>\n"
          + "</REUTERS>\n"
          + "<REUTERS TOPICS=\"NO\" LEWISSPLIT=\"TRAIN\" CGISPLIT=\"TRAINING-SET\" OLDID=\"2\" NEWID=\"2\">\n"
          + "<DATE>2-FEB-2002 20:21:22.00</DATE>\n"
          + "<TOPICS></TOPICS>\n"
          + "<PLACES><D>usa</D></PLACES>\n"
          + "<PEOPLE></PEOPLE>\n"
          + "<ORGS></ORGS>\n"
          + "<EXCHANGES></EXCHANGES>\n"
          + "<COMPANIES></COMPANIES>\n"
          + "<UNKNOWN>blah</UNKNOWN>\n"
          + "<TEXT>&#2;\n"
          + "<TITLE>TITLE 2</TITLE>\n"
          + "<DATELINE>    LOCATION, Date - </DATELINE><BODY>Another example\n"
          + " Reute\n"
          + "&#3;</BODY></TEXT>\n"
          + "</REUTERS>\n";

  private static Path tmpDir;

  @BeforeClass
  public static void beforeClass() throws IOException {
    tmpDir = Files.createTempDirectory("reuterstest");
    Files.write(tmpDir.resolve("file.sgm"), SGML.getBytes(StandardCharsets.UTF_8));
  }

  @AfterClass
  public static void afterClass() {
    tmpDir.toFile().delete();
  }

  @Test
  public void test() throws IOException, UIMAException {
    BaleenCollectionReader bcr =
        getCollectionReader(ReutersReader.KEY_PATH, tmpDir.toAbsolutePath().toString());
    bcr.initialize();

    assertTrue(bcr.doHasNext());

    bcr.getNext(jCas.getCas());

    // assertEquals("DEV-MUC3-0001 (NOSC)", getSource(jCas));
    assertEquals("Some example\ntext.", jCas.getDocumentText());

    jCas.reset();

    assertTrue(bcr.doHasNext());
    bcr.getNext(jCas.getCas());

    // assertEquals("DEV-MUC3-0001 (NOSC)", getSource(jCas));
    assertEquals("Another example", jCas.getDocumentText());

    assertFalse(bcr.doHasNext());

    bcr.close();
  }
}
