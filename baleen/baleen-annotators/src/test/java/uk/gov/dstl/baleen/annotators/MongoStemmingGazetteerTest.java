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
import org.junit.Test;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import uk.gov.dstl.baleen.annotators.gazetteer.MongoStemming;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.types.common.Buzzword;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;

public class MongoStemmingGazetteerTest extends AnnotatorTestBase{
	private static final String LOCATION = "Location";
	private static final String BUZZWORD = "Buzzword";
	private static final String TYPE = "type";
	private static final String COLLECTION = "collection";
	private static final String FONGO_DATA = "fongo.data";
	private static final String FONGO_COLLECTION = "fongo.collection";
	private static final String MONGO = "mongo";
	private static final String VALUE = "value";
	private static final String MONGO_COLL = "baleen_testing_MongoStemmingRadixTreeGazetteerTest";
	private static final DBObject LONDON_GEOJSON = new BasicDBObject(TYPE, "Point").append("coordinates", new Double[]{-0.1275, 51.5072});
	
	private static final List<DBObject> GAZ_DATA = Lists.newArrayList(
			new BasicDBObject(VALUE, new String[]{"conspiracy", "conspire", "scheme", "plot"}),
			new BasicDBObject(VALUE, new String[]{"london", "londres"}).append("geoJson", LONDON_GEOJSON),
			new BasicDBObject(VALUE, new String[]{"knight", "sir", "dame", "lady"}),
			new BasicDBObject(VALUE, new String[]{"enter the room"}));

	@Test
	public void test() throws Exception{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(MONGO, SharedFongoResource.class, FONGO_COLLECTION, MONGO_COLL, FONGO_DATA, JSON.serialize(GAZ_DATA));
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(MongoStemming.class, MONGO, erd, COLLECTION, MONGO_COLL, TYPE, BUZZWORD);
		
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);
		
		jCas.setDocumentText("Forty seven knights conspired against the crown.");
		
		ae.process(jCas);
		
		assertEquals(2, JCasUtil.select(jCas, Buzzword.class).size());
		
		Buzzword b1 = JCasUtil.selectByIndex(jCas, Buzzword.class, 0);
		assertEquals("knights", b1.getValue());
		assertEquals("knights", b1.getCoveredText());
		
		Buzzword b2 = JCasUtil.selectByIndex(jCas, Buzzword.class, 1);
		assertEquals("conspired", b2.getValue());
		assertEquals("conspired", b2.getCoveredText());
		
		ae.destroy();
	}
	
	@Test
	public void testMultipleWords() throws Exception{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(MONGO, SharedFongoResource.class, FONGO_COLLECTION, MONGO_COLL, FONGO_DATA, JSON.serialize(GAZ_DATA));
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(MongoStemming.class, MONGO, erd, COLLECTION, MONGO_COLL, TYPE, BUZZWORD);
		
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);
		
		jCas.setDocumentText("Bill and Ben entered the room on a dark and windy night.");
		
		ae.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Buzzword.class).size());
		
		Buzzword b1 = JCasUtil.selectByIndex(jCas, Buzzword.class, 0);
		assertEquals("entered the room", b1.getValue());
		assertEquals("entered the room", b1.getCoveredText());
		
		ae.destroy();
	}
	
	@Test
	public void testMidword() throws Exception{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(MONGO, SharedFongoResource.class, FONGO_COLLECTION, MONGO_COLL, FONGO_DATA, JSON.serialize(GAZ_DATA));
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(MongoStemming.class, MONGO, erd, COLLECTION, MONGO_COLL, TYPE, BUZZWORD);
		
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);
		
		jCas.setDocumentText("Desiring chocolate is not a sin");
		
		ae.process(jCas);
		
		assertEquals(0, JCasUtil.select(jCas, Buzzword.class).size());
		
		ae.destroy();
	}
	
	@Test
	public void testProperty() throws Exception{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(MONGO, SharedFongoResource.class, FONGO_COLLECTION, MONGO_COLL, FONGO_DATA, JSON.serialize(GAZ_DATA));
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(MongoStemming.class, MONGO, erd, COLLECTION, MONGO_COLL, TYPE, LOCATION);
		
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);
		
		jCas.setDocumentText("Guy Fawkes was caught in London");
		
		ae.process(jCas);
		
		assertEquals(1, JCasUtil.select(jCas, Location.class).size());
		Location lLon = JCasUtil.selectByIndex(jCas, Location.class, 0);
		assertEquals("London", lLon.getValue());
		assertEquals("London", lLon.getCoveredText());
		assertEquals(LONDON_GEOJSON.toString(), lLon.getGeoJson());
	}
	
	@Test
	public void testCoref() throws Exception{
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(MONGO, SharedFongoResource.class, FONGO_COLLECTION, MONGO_COLL, FONGO_DATA, JSON.serialize(GAZ_DATA));
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(MongoStemming.class, MONGO, erd, COLLECTION, MONGO_COLL, TYPE, BUZZWORD);
		
		AnalysisEngine ae = AnalysisEngineFactory.createEngine(aed);
		
		jCas.setDocumentText("Lords, ladies, sirs, and madames...");
		
		ae.process(jCas);
		
		assertEquals(2, JCasUtil.select(jCas, Buzzword.class).size());
		assertEquals(1, JCasUtil.select(jCas, ReferenceTarget.class).size());
		
		ReferenceTarget rt = JCasUtil.selectByIndex(jCas, ReferenceTarget.class, 0);
		
		Buzzword b1 = JCasUtil.selectByIndex(jCas, Buzzword.class, 0);
		assertEquals("ladies", b1.getValue());
		assertEquals("ladies", b1.getCoveredText());
		assertEquals(rt, b1.getReferent());
		
		Buzzword b2 = JCasUtil.selectByIndex(jCas, Buzzword.class, 1);
		assertEquals("sirs", b2.getValue());
		assertEquals("sirs", b2.getCoveredText());
		assertEquals(rt, b2.getReferent());
		
		ae.destroy();
	}
}
