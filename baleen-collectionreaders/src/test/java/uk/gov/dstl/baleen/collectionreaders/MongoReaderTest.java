// Dstl (c) Crown Copyright 2017
// Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.bson.Document;
import org.junit.Test;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import uk.gov.dstl.baleen.collectionreaders.testing.AbstractReaderTest;
import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;

public class MongoReaderTest extends AbstractReaderTest {

  private static final String MONGO = "mongo";
  private static final String TEXT = "Hello Metadata";
  private static final String CONTENT = "content";
  private static final String COLLECTION = "documents";

  private ExternalResourceDescription erd =
      ExternalResourceFactory.createExternalResourceDescription(MONGO, SharedFongoResource.class);

  public MongoReaderTest() {
    super(MongoReader.class);
  }

  @Test
  public void test() throws Exception {
    BaleenCollectionReader bcr =
        getCollectionReader(
            MONGO,
            erd,
            "collection",
            COLLECTION,
            "idField",
            "_id",
            "contentField",
            CONTENT,
            "contentExtractor",
            "PlainTextContentExtractor");
    bcr.initialize();

    SharedFongoResource sfr = (SharedFongoResource) bcr.getUimaContext().getResourceObject(MONGO);
    createContent(sfr);

    assertTrue(bcr.doHasNext());
    bcr.getNext(jCas);
    assertEquals("Hello World", jCas.getDocumentText().trim());
    assertEquals(1, JCasUtil.select(jCas, Metadata.class).size());
    jCas.reset();

    assertTrue(bcr.doHasNext());
    bcr.getNext(jCas);
    assertEquals("Hello Test", jCas.getDocumentText().trim());
    assertEquals(1, JCasUtil.select(jCas, Metadata.class).size());
    jCas.reset();

    assertTrue(bcr.doHasNext());
    bcr.getNext(jCas);
    assertEquals(TEXT, jCas.getDocumentText().trim());
    assertEquals(5, JCasUtil.select(jCas, Metadata.class).size());
    List<Metadata> metadata =
        JCasUtil.select(jCas, Metadata.class)
            .stream()
            .filter(m -> !m.getKey().equalsIgnoreCase("baleen:content-extractor"))
            .sorted((a, b) -> a.getKey().compareTo(b.getKey()))
            .collect(Collectors.toList());
    assertEquals("key1", metadata.get(0).getKey());
    assertEquals("key2", metadata.get(1).getKey());
    assertEquals("key3", metadata.get(2).getKey());
    assertEquals("key3", metadata.get(3).getKey());
    assertEquals("foo", metadata.get(0).getValue());
    assertEquals("bar", metadata.get(1).getValue());
    assertEquals("howdy", metadata.get(2).getValue());
    assertEquals("hey", metadata.get(3).getValue());
    jCas.reset();

    assertFalse(bcr.doHasNext());

    bcr.close();
  }

  private void createContent(SharedFongoResource sfr) {
    MongoDatabase db = sfr.getDB();

    MongoCollection<Document> coll = db.getCollection(COLLECTION);
    coll.insertMany(
        Arrays.asList(
            new Document(CONTENT, "Hello World"),
            new Document(CONTENT, "Hello Test"),
            new Document(CONTENT, TEXT)
                .append("key1", "foo")
                .append("key2", "bar")
                .append("key3", Arrays.asList("howdy", "hey"))));
  }
}
