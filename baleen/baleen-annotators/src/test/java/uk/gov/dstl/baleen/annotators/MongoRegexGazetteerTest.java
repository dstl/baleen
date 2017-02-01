//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ExternalResourceDescription;
import org.bson.Document;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.mongodb.util.JSON;

import uk.gov.dstl.baleen.annotators.gazetteer.MongoRegex;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;

public class MongoRegexGazetteerTest extends AnnotatorTestBase{
	private static final String COLLECTION = "collection";
	private static final String LONDON_REGEX = "\\blon\\w*\\b";
	private static final String REGEX = "regex";
	private static final String LOCATION = "Location";
	private static final String TYPE = "type";
	private static final String FONGO_COLLECTION = "fongo.collection";
	private static final String FONGO_DATA = "fongo.data";
	private static final String MONGO = "mongo";
	private static final String TEXT = "Hello world, this is a test. Hello London, this is a test.";
	private static final String VALUE = "value";
	private static final String MONGO_COLL = "baleen_testing_MongoRadixTreeGazetteerTest";
	private static final List<Document> GAZ_DATA = Lists.newArrayList(
			new Document(VALUE, new String[]{"world", "earth", "planet"}),
			new Document(VALUE, new String[]{"london", "londres"}).append("geoJson","Property_Test"),
			new Document(VALUE, new String[]{"madrid"}));

	@Test
	public void test() throws Exception{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(MONGO, SharedFongoResource.class, FONGO_COLLECTION, MONGO_COLL, FONGO_DATA, JSON.serialize(GAZ_DATA));
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(MongoRegex.class, MONGO, erd, COLLECTION, MONGO_COLL, TYPE, LOCATION, "caseSensitive", true, REGEX, LONDON_REGEX);
		
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);
		
		jCas.setDocumentText(TEXT);
		
		ae.process(jCas);
		
		assertEquals(0, JCasUtil.select(jCas, Location.class).size());
		
		ae.destroy();
	}
	
	@Test
	public void testProperty() throws Exception{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(MONGO, SharedFongoResource.class, FONGO_COLLECTION, MONGO_COLL, FONGO_DATA, JSON.serialize(GAZ_DATA));
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(MongoRegex.class, MONGO, erd, COLLECTION, MONGO_COLL, TYPE, LOCATION, REGEX, LONDON_REGEX);
		
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);
		
		jCas.setDocumentText(TEXT);
		
		ae.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Location.class).size());
		Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
		assertEquals("London", l.getValue());
		assertEquals("London", l.getCoveredText());
		assertEquals("Property_Test", l.getGeoJson());
		
		ae.destroy();
	}
	
	@Test
	public void testCoref() throws Exception{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(MONGO, SharedFongoResource.class, FONGO_COLLECTION, MONGO_COLL, FONGO_DATA, JSON.serialize(GAZ_DATA));
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(MongoRegex.class, MONGO, erd, COLLECTION, MONGO_COLL, TYPE, LOCATION, REGEX, "\\b[A-Z][a-z]*\\b");
		
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);
		
		jCas.setDocumentText("Hello World, Hello Earth");
		
		ae.process(jCas);
		
		assertEquals(2, JCasUtil.select(jCas, Location.class).size());
		assertEquals(1, JCasUtil.select(jCas, ReferenceTarget.class).size());
		
		ReferenceTarget rt = JCasUtil.selectByIndex(jCas, ReferenceTarget.class, 0);
		
		Location l1 = JCasUtil.selectByIndex(jCas, Location.class, 0);
		assertEquals("World", l1.getValue());
		assertEquals("World", l1.getCoveredText());
		assertEquals(rt, l1.getReferent());
		
		Location l2 = JCasUtil.selectByIndex(jCas, Location.class, 1);
		assertEquals("Earth", l2.getValue());
		assertEquals("Earth", l2.getCoveredText());
		assertEquals(rt, l2.getReferent());
		
		ae.destroy();
	}
}
