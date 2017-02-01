//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.collectionreaders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

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

public class MongoReaderTest extends AbstractReaderTest{
	
	private static final String MONGO = "mongo";
	private static final String TEXT = "Hello Metadata";
	private static final String CONTENT = "content";
	private static final String COLLECTION = "documents";
	
	private final ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(MONGO, SharedFongoResource.class);
	
	public MongoReaderTest(){
		super(MongoReader.class);
	}
	
	@Test
	public void test() throws Exception{
		BaleenCollectionReader bcr = getCollectionReader(MONGO, erd, "collection", COLLECTION, "idField", "_id", "contentField", CONTENT, "contentExtractor", "UimaContentExtractor");
		bcr.initialize();
		
		SharedFongoResource sfr = (SharedFongoResource) bcr.getUimaContext().getResourceObject(MONGO);
		createContent(sfr);
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		assertEquals("Hello World", jCas.getDocumentText().trim());
		assertEquals(0, JCasUtil.select(jCas, Metadata.class).size());
		jCas.reset();
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		assertEquals("Hello Test", jCas.getDocumentText().trim());
		assertEquals(0, JCasUtil.select(jCas, Metadata.class).size());
		jCas.reset();

		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		assertEquals(TEXT, jCas.getDocumentText().trim());
		assertEquals(4, JCasUtil.select(jCas, Metadata.class).size());
		assertEquals("key1", JCasUtil.selectByIndex(jCas, Metadata.class, 0).getKey());
		assertEquals("key2", JCasUtil.selectByIndex(jCas, Metadata.class, 3).getKey());
		assertEquals("key3", JCasUtil.selectByIndex(jCas, Metadata.class, 1).getKey());
		assertEquals("key3", JCasUtil.selectByIndex(jCas, Metadata.class, 2).getKey());
		assertEquals("foo", JCasUtil.selectByIndex(jCas, Metadata.class, 0).getValue());
		assertEquals("bar", JCasUtil.selectByIndex(jCas, Metadata.class, 3).getValue());
		assertEquals("howdy", JCasUtil.selectByIndex(jCas, Metadata.class, 1).getValue());
		assertEquals("hey", JCasUtil.selectByIndex(jCas, Metadata.class, 2).getValue());
		jCas.reset();
		
		assertFalse(bcr.doHasNext());
		
		bcr.close();
	}
	
	private void createContent(SharedFongoResource sfr){
		MongoDatabase db = sfr.getDB();
		
		MongoCollection<Document> coll = db.getCollection(COLLECTION);
		coll.insertMany(Arrays.asList(
				new Document(CONTENT, "Hello World"),
				new Document(CONTENT, "Hello Test"),
				new Document(CONTENT, TEXT).append("key1", "foo").append("key2", "bar").append("key3", Arrays.asList("howdy", "hey"))
		));
	}
}
