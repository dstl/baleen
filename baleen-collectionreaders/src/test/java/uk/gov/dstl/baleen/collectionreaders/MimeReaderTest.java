// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.collectionreaders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.uima.cas.text.AnnotationIndex;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Streams;

import uk.gov.dstl.baleen.collectionreaders.testing.AbstractReaderTest;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;

public class MimeReaderTest extends AbstractReaderTest {

  public MimeReaderTest() {
    super(MimeReader.class);
  }

  private static Path tmpDir;

  private static final String FROM = "whalem@baleen.org";
  private static final String X_FROM = "whalem@mailman.baleen.org";
  private static final String TO = "krill@baleen.org";
  private static final String MESSAGE_ID = "<1234567.123456789876.JavaMail.evans@thyme>";
  private static final String SUBJECT = "Your time is up";

  private static final String MIME =
      "Message-ID: "
          + MESSAGE_ID
          + "\n"
          + "Date: Wed, 13 Dec 2015 11:02:00 -0800 (PST)\n"
          + "From: "
          + FROM
          + "\n"
          + "To: "
          + TO
          + "\n"
          + "Subject: "
          + SUBJECT
          + "\n"
          + "Mime-Version: 1.0\n"
          + "Content-Type: text/plain; charset=us-ascii\n"
          + "Content-Transfer-Encoding: 7bit\n"
          + "X-From: "
          + X_FROM
          + "\n"
          + "X-To: krill@baleen.org\n"
          + "X-cc: \n"
          + "X-bcc: \n"
          + "X-Folder: \\Whale_Dec2015\\Notes Folders\\All documents\n"
          + "X-Origin: whale\n"
          + "X-FileName: whale.nsf\n\n"
          + "I'm going to eat you!\n"
          + "\n\n\n\n\n\n"
          + "Request ID          : 000000000009659\n"
          + "Request Create Date : 12/8/15 9:23:47 AM\n"
          + "Requested For       : krill@baleen.org\n"
          + "Resource Name       : VPN\n"
          + "Resource Type       : Applications\n\n\n\n";

  @BeforeClass
  public static void beforeClass() throws IOException {
    tmpDir = Files.createTempDirectory("mimetest");
    Files.write(tmpDir.resolve("file"), MIME.getBytes(StandardCharsets.UTF_8));
  }

  @AfterClass
  public static void afterClass() {
    tmpDir.toFile().delete();
  }

  @Test
  public void testMetadata() throws Exception {
    BaleenCollectionReader bcr =
        getCollectionReader(MimeReader.PARAM_FOLDER, tmpDir.toAbsolutePath().toString());
    bcr.initialize();

    assertTrue(bcr.doHasNext());
    bcr.getNext(jCas.getCas());

    AnnotationIndex<Metadata> annotationIndex = jCas.getAnnotationIndex(Metadata.class);

    // returns the X-From and not the From, these can differ
    assertEquals(X_FROM, get(annotationIndex, "email.X-From"));
    assertEquals(TO, get(annotationIndex, "email.X-To"));
    assertEquals(MESSAGE_ID, get(annotationIndex, "email.Message-ID"));

    bcr.close();
  }

  private String get(AnnotationIndex<Metadata> annotationIndex, String key) {
    return Streams.stream(annotationIndex)
        .filter(m -> m.getKey().equals(key))
        .map(Metadata::getValue)
        .findAny()
        .orElse(null);
  }

  @Test
  public void testDocumentText() throws Exception {
    BaleenCollectionReader bcr =
        getCollectionReader(MimeReader.PARAM_FOLDER, tmpDir.toAbsolutePath().toString());
    bcr.initialize();

    assertTrue(bcr.doHasNext());
    bcr.getNext(jCas.getCas());

    assertTrue(jCas.getDocumentText().startsWith("I'm going to eat you!"));

    bcr.close();
  }
}
