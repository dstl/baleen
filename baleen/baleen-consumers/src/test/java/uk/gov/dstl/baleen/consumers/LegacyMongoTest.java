//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBList;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.metadata.PublishedId;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.temporal.DateType;
import uk.gov.dstl.baleen.uima.utils.UimaTypesUtils;

public class LegacyMongoTest extends ConsumerTestBase {

	private static final String GEO_JSON = "geoJson";
	private static final String TYPE = "type";
	private static final String END = "end";
	private static final String BEGIN = "begin";
	private static final String PUBLISHED_ID = "publishedId";
	private static final String COUNTRY_INFO = "countryInfo";
	private static final String DOCUMENT_TITLE = "documentTitle";
	private static final String SOURCE_AND_INFORMATION_GRADING = "sourceAndInformationGrading";
	private static final String CLASSIFICATION = "classification";
	private static final String PROTECTIVE_MARKING = "protectiveMarking";
	private static final String DOC_TYPE = "docType";
	private static final String SOURCE = "source";
	private static final String CONFIDENCE = "confidence";
	private static final String VALUE = "value";
	private static final String TEXT = "Hello World";
	private static final String ENTITIES = "entities";
	private static final String MONGO = "mongo";
	private static final List<DBObject> GAZ_DATA = Lists.newArrayList();
	private DBCollection outputColl;
	private AnalysisEngine ae;

	@Before
	public void setUp() throws ResourceInitializationException, ResourceAccessException {
		// Create a description of an external resource - a fongo instance, in the same way we would have created a shared mongo resource
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(MONGO, SharedFongoResource.class, "fongo.collection", "test", "fongo.data", JSON.serialize(GAZ_DATA));

		// Create the analysis engine
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(LegacyMongo.class, MONGO, erd, "collection", "test");
		ae = AnalysisEngineFactory.createEngine(aed);
		ae.initialize(new CustomResourceSpecifier_impl(), Collections.emptyMap());
		SharedFongoResource sfr = (SharedFongoResource) ae.getUimaContext().getResourceObject(MONGO);

		outputColl = sfr.getDB().getCollection("test");

		// Ensure we start with no data!
		assertEquals(0L, outputColl.count());

	}

	@After
	public void tearDown() {
		if(ae != null) {
			ae.destroy();
		}
	}

	@Test
	public void testSave() throws Exception{
		// Set the document content
		jCas.setDocumentText("Hello world, this is a test");

		// Put some other stuff in that should end up in Mongo

		// Process ae.process(jCas)
		ae.process(jCas);

		// Try and get separate connection to fongo instance

		assertEquals(1L, outputColl.count());
	}	
	
	@SuppressWarnings("unchecked")
	@Test
	public void testUpsert() throws Exception {
		jCas.setDocumentText("Hello Bob");
		ae.process(jCas);
		
		assertEquals(1L, outputColl.count());
		DBObject result = outputColl.findOne();
		assertEquals(0, ((List<DBObject>)result.get(ENTITIES)).size());
		
		Person p = new Person(jCas);
		p.setBegin(6);
		p.setEnd(9);
		p.setValue("Bob");
		p.addToIndexes();
		
		ae.process(jCas);
		
		assertEquals(1L, outputColl.count());
		result = outputColl.findOne();
		assertEquals(1, ((List<DBObject>)result.get(ENTITIES)).size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testNoEntities() throws Exception {
		jCas.setDocumentText(TEXT);
		jCas.setDocumentLanguage("en");

		long timestamp = System.currentTimeMillis();

		DocumentAnnotation da = getDocumentAnnotation(jCas);
		da.setTimestamp(timestamp);
		da.setSourceUri("test/no_entities");
		da.setDocType("test");
		da.setDocumentClassification("OFFICIAL");
		da.setDocumentCaveats(UimaTypesUtils.toArray(jCas, Arrays.asList(new String[] { "TEST_A", "TEST_B" })));
		da.setDocumentReleasability(UimaTypesUtils.toArray(jCas, Arrays.asList(new String[] { "ENG", "SCO", "WAL" })));

		ae.process(jCas);

		assertEquals(1, outputColl.count());
		DBObject result = outputColl.findOne();

		assertEquals(TEXT, result.get("content"));
		assertEquals("en", result.get("language"));

		assertEquals(new Date(timestamp), ((DBObject)result.get(SOURCE)).get("dateAccessed"));
		assertEquals("test/no_entities", ((DBObject)result.get(SOURCE)).get("location"));

		assertEquals("test", ((DBObject)result.get(DOC_TYPE)).get(VALUE));
		assertEquals(1.0, (double)((DBObject)result.get(DOC_TYPE)).get(CONFIDENCE), 0.001);
		assertNotNull(((DBObject)result.get(DOC_TYPE)).get("annotator"));

		assertEquals("OFFICIAL", ((DBObject)result.get(PROTECTIVE_MARKING)).get(CLASSIFICATION));
		assertArrayEquals(new String[] { "TEST_A", "TEST_B" }, ((Collection<String>)((DBObject)result.get(PROTECTIVE_MARKING)).get("caveats")).toArray());
		assertArrayEquals(new String[] { "ENG", "SCO", "WAL" }, ((Collection<String>)((DBObject)result.get(PROTECTIVE_MARKING)).get("releasability")).toArray());

		assertEquals(getDocumentAnnotation(jCas).getHash(), result.get("uniqueID"));
	}

	@Test
	public void testMetadata() throws Exception {
		jCas.setDocumentText(TEXT);
		jCas.setDocumentLanguage("");

		PublishedId pid1 = new PublishedId(jCas);
		pid1.setValue("id_1");
		pid1.addToIndexes();

		PublishedId pid2 = new PublishedId(jCas);
		pid2.setValue("id_2");
		pid2.addToIndexes();

		Metadata mdSourceAndInformation = new Metadata(jCas);
		mdSourceAndInformation.setKey(SOURCE_AND_INFORMATION_GRADING);
		mdSourceAndInformation.setValue("D3");
		mdSourceAndInformation.addToIndexes();

		Metadata mdCountries = new Metadata(jCas);
		mdCountries.setKey(COUNTRY_INFO);
		mdCountries.setValue("ENG|WAL|SCO");
		mdCountries.addToIndexes();

		Metadata mdTitle = new Metadata(jCas);
		mdTitle.setKey(DOCUMENT_TITLE);
		mdTitle.setValue("Test Title");
		mdTitle.addToIndexes();

		Metadata mdMisc = new Metadata(jCas);
		mdMisc.setKey("test_key");
		mdMisc.setValue("test_value");
		mdMisc.addToIndexes();

		ae.process(jCas);

		assertEquals(1, outputColl.count());
		DBObject result = outputColl.findOne();

		assertEquals("id_1", result.get(PUBLISHED_ID));

		BasicDBList di = (BasicDBList) result.get("documentInfo");

		assertDocumentInfo(di, SOURCE_AND_INFORMATION_GRADING, "D3");
		assertDocumentInfo(di, "test_key", "test_value");
		assertDocumentInfo(di, DOCUMENT_TITLE, "Test Title");
		assertDocumentInfo(di, COUNTRY_INFO, "ENG|WAL|SCO");
	}

	private void assertDocumentInfo(BasicDBList di, String key, Object value) {
		assertEquals(1,
				di.stream().filter( x -> ((DBObject)x).get("key").equals(key) && ((DBObject)x).get(VALUE).equals(value))
				.count());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEntities() throws Exception {
		jCas.setDocumentText("James went to London on 19th February 2015. His e-mail address is james@example.com");

		Person p = new Person(jCas);
		p.setBegin(0);
		p.setEnd(5);
		p.setValue("James");
		p.addToIndexes();
		// Obtain the number of features in the object. These should all
		// be stored in the database so the count retrieved from the
		// database should match.
		int expectedPersonSize =  p.getType().getFeatures().size();

		Location l = new Location(jCas);
		l.setBegin(14);
		l.setEnd(20);
		l.setValue("London");
		l.setGeoJson("{\"type\": \"Point\", \"coordinates\": [-0.1, 51.5]}");
		l.addToIndexes();
		// Obtain the number of features in the object. These should all
		// be stored in the database so the count retrieved from the
		// database should match.
		int expectedLocationSize =  l.getType().getFeatures().size();

		DateType d = new DateType(jCas);
		d.setBegin(24);
		d.setEnd(42);
		d.setConfidence(1.0);
		d.setValue("19th February 2015");
		d.addToIndexes();
		// Obtain the number of features in the object. These should all
		// be stored in the database so the count retrieved from the
		// database should match.
		int expectedDateSize =  d.getType().getFeatures().size();

		CommsIdentifier ci = new CommsIdentifier(jCas);
		ci.setBegin(66);
		ci.setEnd(83);
		ci.setSubType("email");
		ci.setValue("james@example.com");
		ci.addToIndexes();
		// Obtain the number of features in the object. These should all
		// be stored in the database so the count retrieved from the
		// database should match.
		int expectedEmailSize =  ci.getType().getFeatures().size();

		ae.process(jCas);

		assertEquals(1, outputColl.count());
		DBObject result = outputColl.findOne();

		BasicDBList entities= (BasicDBList) result.get(ENTITIES);
		assertEquals(4, entities.size());

		Map<String, Object> person = (Map<String, Object>)entities.get(0);
		assertEquals(expectedPersonSize, person.size());
		assertEquals(0, person.get(BEGIN));
		assertEquals(5, person.get(END));
		assertEquals(0.0, person.get(CONFIDENCE));
		assertEquals("Person", person.get(TYPE));
		assertEquals("James", person.get(VALUE));

		Map<String, Object> location =(Map<String, Object>) entities.get(1);
		assertEquals(expectedLocationSize, location.size());
		assertEquals(14, location.get(BEGIN));
		assertEquals(20, location.get(END));
		assertEquals(0.0, location.get(CONFIDENCE));
		assertEquals("Location", location.get(TYPE));
		assertEquals("London", location.get(VALUE));

		assertEquals("Feature", ((DBObject)location.get(GEO_JSON)).get(TYPE));
		assertEquals("Point", ((DBObject)((DBObject)location.get(GEO_JSON)).get("geometry")).get(TYPE));
		assertArrayEquals(new Double[] { -0.1, 51.5 }, ((BasicDBList)((DBObject)((DBObject)location.get(GEO_JSON)).get("geometry")).get("coordinates")).toArray());

		Map<String, Object> date = (Map<String, Object>)entities.get(2);
		assertEquals(expectedDateSize, date.size());
		assertEquals(24, date.get(BEGIN));
		assertEquals(42, date.get(END));
		assertEquals(1.0, date.get(CONFIDENCE));
		assertEquals("DateType", date.get(TYPE));
		assertEquals("19th February 2015", date.get(VALUE));

		Map<String, Object> email = (Map<String, Object>) entities.get(3);
		assertEquals(expectedEmailSize, email.size());
		assertEquals(66, email.get(BEGIN));
		assertEquals(83, email.get(END));
		assertEquals(0.0, email.get(CONFIDENCE));
		assertEquals("CommsIdentifier", email.get(TYPE));
		assertEquals("email", email.get("subType"));
		assertEquals("james@example.com", email.get(VALUE));
	}

	@Test
	public void testMaxContentLimit() throws Exception {
		// The  maxContentLength resource needs to be configured for this test.
		// This means supplying it to the createEngineDescription() method, so
		// means the default analysis engine ae created in the setup method can't be used.
		// So the following code was stolen from that setup() method with the addition
		// of the "maxContentLength", "21" parameters.
		DBCollection myOutputColl;
		AnalysisEngine myAe;

		// Create a description of an external resource - a fongo instance, in the same way we would have created a shared mongo resource
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(MONGO, SharedFongoResource.class, "fongo.collection", "test", "fongo.data", JSON.serialize(GAZ_DATA));

		// Create the analysis engine
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(LegacyMongo.class, MONGO, erd, "collection", "test", "maxContentLength", "21");
		myAe = AnalysisEngineFactory.createEngine(aed);
		myAe.initialize(new CustomResourceSpecifier_impl(), Collections.emptyMap());
		SharedFongoResource sfr = (SharedFongoResource) myAe.getUimaContext().getResourceObject(MONGO);

		myOutputColl = sfr.getDB().getCollection("test");

		// Ensure we start with no data!
		assertEquals(0L, outputColl.count());


		jCas.setDocumentText("James went to London on 19th February 2015. His e-mail address is james@example.com");
		jCas.setDocumentLanguage("en");

		myAe.process(jCas);

		assertEquals(1, myOutputColl.count());
		DBObject result = myOutputColl.findOne();

		// Expected, truncated text
		String expected = "James went to London" + "\u2026";

		assertEquals(expected, result.get("content"));
		assertEquals("en", result.get("language"));

		if(myAe != null) {
			myAe.destroy();
		}
	}

}
