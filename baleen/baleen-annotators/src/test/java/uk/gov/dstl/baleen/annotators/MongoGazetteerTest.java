//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ExternalResourceDescription;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.gazetteer.Mongo;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.types.common.Buzzword;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class MongoGazetteerTest extends AnnotatorTestBase{
	private static final String WORLD = "world";
	private static final String LOCATION = "Location";
	private static final String TYPE = "type";
	private static final String COLLECTION = "collection";
	private static final String FONGO_DATA = "fongo.data";
	private static final String FONGO_COLLECTION = "fongo.collection";
	private static final String MONGO = "mongo";
	private static final String VALUE = "value";
	private static final String MONGO_COLL = "baleen_testing_MongoGazetteerTest";
	private static final DBObject LONDON_GEOJSON = new BasicDBObject(TYPE, "Point").append("coordinates", new Double[]{-0.1275, 51.5072});
	
	private static final List<DBObject> GAZ_DATA = Lists.newArrayList(
			new BasicDBObject(VALUE, new String[]{WORLD, "earth", "planet"}),
			new BasicDBObject(VALUE, new String[]{"london", "londres"}).append("geoJson", LONDON_GEOJSON),
			new BasicDBObject(VALUE, new String[]{"madrid"}).append("geoJson", "Property Test"),
			new BasicDBObject(VALUE, new String[]{"sydney (australia"}).append("tags", Arrays.asList("broken_regex")));

	@Test
	public void test() throws Exception{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(MONGO, SharedFongoResource.class, FONGO_COLLECTION, MONGO_COLL, FONGO_DATA, JSON.serialize(GAZ_DATA));
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(Mongo.class, MONGO, erd, COLLECTION, MONGO_COLL, TYPE, LOCATION);
		
		
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);
		
		jCas.setDocumentText("Hello world, this is a test");
		
		ae.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Location.class).size());
		Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
		assertEquals(WORLD, l.getValue());
		assertEquals(WORLD, l.getCoveredText());
		
		ae.destroy();
	}
	
	@Test
	public void testRegex() throws Exception{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(MONGO, SharedFongoResource.class, FONGO_COLLECTION, MONGO_COLL, FONGO_DATA, JSON.serialize(GAZ_DATA));
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(Mongo.class, MONGO, erd, COLLECTION, MONGO_COLL, TYPE, LOCATION);
		
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);
		
		jCas.setDocumentText("Hello Sydney (Australia), this is a test");
		
		ae.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Location.class).size());
		Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
		assertEquals("Sydney (Australia", l.getValue());
		assertEquals("Sydney (Australia", l.getCoveredText());
		
		ae.destroy();
	}
	
	@Test
	public void testMidword() throws Exception{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(MONGO, SharedFongoResource.class, FONGO_COLLECTION, MONGO_COLL, FONGO_DATA, JSON.serialize(GAZ_DATA));
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(Mongo.class, MONGO, erd, COLLECTION, MONGO_COLL, TYPE, LOCATION);
		
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);
		
		jCas.setDocumentText("HelloWorld");
		
		ae.process(jCas);
		
		assertEquals(0, JCasUtil.select(jCas, Location.class).size());
		
		ae.destroy();
	}
	
	@Test
	public void testProperty() throws Exception{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(MONGO, SharedFongoResource.class, FONGO_COLLECTION, MONGO_COLL, FONGO_DATA, JSON.serialize(GAZ_DATA));
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(Mongo.class, MONGO, erd, COLLECTION, MONGO_COLL, TYPE, LOCATION);
		
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);
		
		jCas.setDocumentText("Hello London, this is a test");
		
		ae.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Location.class).size());
		Location lLon = JCasUtil.selectByIndex(jCas, Location.class, 0);
		assertEquals("London", lLon.getValue());
		assertEquals("London", lLon.getCoveredText());
		assertEquals(LONDON_GEOJSON.toString(), lLon.getGeoJson());
		
		jCas.reset();
		
		jCas.setDocumentText("Hello Madrid, this is a test");
		
		ae.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Location.class).size());
		Location lMad = JCasUtil.selectByIndex(jCas, Location.class, 0);
		assertEquals("Madrid", lMad.getValue());
		assertEquals("Madrid", lMad.getCoveredText());
		assertEquals("Property Test", lMad.getGeoJson());
		
		ae.destroy();
	}
	
	@Test
	public void testBuzzwordProperty() throws Exception{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(MONGO, SharedFongoResource.class, FONGO_COLLECTION, MONGO_COLL, FONGO_DATA, JSON.serialize(GAZ_DATA));
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(Mongo.class, MONGO, erd, COLLECTION, MONGO_COLL, TYPE, "Buzzword");
		
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);
		
		jCas.setDocumentText("Hello Sydney (Australia), this is a test");
		
		ae.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Buzzword.class).size());
		Buzzword b = JCasUtil.selectByIndex(jCas, Buzzword.class, 0);
		assertEquals("Sydney (Australia", b.getValue());
		assertEquals("Sydney (Australia", b.getCoveredText());
		
		StringArray tags = b.getTags();
		assertEquals(1, tags.size());
		assertEquals("broken_regex", tags.get(0));
		
		ae.destroy();
	}
	
	@Test
	public void testCoref() throws Exception{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(MONGO, SharedFongoResource.class, FONGO_COLLECTION, MONGO_COLL, FONGO_DATA, JSON.serialize(GAZ_DATA));
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(Mongo.class, MONGO, erd, COLLECTION, MONGO_COLL, TYPE, LOCATION);
		
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
