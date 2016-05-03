//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import uk.gov.dstl.baleen.consumers.utils.IEntityConverterFields;
import uk.gov.dstl.baleen.consumers.utils.mongo.MongoFields;
import uk.gov.dstl.baleen.core.history.BaleenHistory;
import uk.gov.dstl.baleen.core.history.DocumentHistory;
import uk.gov.dstl.baleen.core.history.HistoryEvent;
import uk.gov.dstl.baleen.core.history.HistoryEvents;
import uk.gov.dstl.baleen.core.history.memory.InMemoryBaleenHistory;
import uk.gov.dstl.baleen.cpe.CpeBuilder;
import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.types.common.Buzzword;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.metadata.PublishedId;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.types.temporal.DateType;
import uk.gov.dstl.baleen.uima.utils.UimaTypesUtils;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class MongoTest extends ConsumerTestBase {

	private static final String LONDON = "London";

	private static final String NAME_2 = "William";

	private static final String TYPE = "type";

	private static final String VALUE = "value";

	private static final String CONFIDENCE = "confidence";

	private static final String END = "end";

	private static final String BEGIN = "begin";

	private static final String EMAIL = "james@example.com";

	private static final String DATE = "19th February 2015";

	private static final String PERSON = "James";

	private static final String TEXT = "Hello World";

	private static final String MONGO = "mongo";
	
	private static final String WENT = "went";

	private AnalysisEngine ae;

	private DBCollection documents;
	private DBCollection entities;
	private DBCollection relations;

	private BaleenHistory history;
	
	private final IEntityConverterFields fields = new MongoFields();

	@Before
	public void setUp() throws ResourceInitializationException, ResourceAccessException {
		// Create a description of an external resource - a fongo instance, in the same way we would have created a shared mongo resource
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription(MONGO, SharedFongoResource.class, "fongo.collection", "test", "fongo.data", "[]");
		ExternalResourceDescription historyErd = ExternalResourceFactory.createExternalResourceDescription(CpeBuilder.BALEEN_HISTORY, InMemoryBaleenHistory.class);

		history = Mockito.mock(BaleenHistory.class);

		// Create the analysis engine
		AnalysisEngineDescription aed = AnalysisEngineFactory.createEngineDescription(Mongo.class, MONGO, erd, "collection", "test", CpeBuilder.BALEEN_HISTORY, historyErd, "outputHistory", Boolean.TRUE);
		ae = AnalysisEngineFactory.createEngine(aed);
		ae.initialize(new CustomResourceSpecifier_impl(), Collections.emptyMap());
		SharedFongoResource sfr = (SharedFongoResource) ae.getUimaContext().getResourceObject(MONGO);
		history = (BaleenHistory) ae.getUimaContext().getResourceObject(CpeBuilder.BALEEN_HISTORY);

		entities = sfr.getDB().getCollection("entities");
		documents = sfr.getDB().getCollection("documents");
		relations = sfr.getDB().getCollection("relations");

		// Ensure we start with no data!
		assertEquals(0L, documents.count());
		assertEquals(0L, entities.count());
		assertEquals(0L, relations.count());
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

		assertEquals(1L, documents.count());
		assertEquals(0L, entities.count());
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

		assertEquals(1, documents.count());
		DBObject result = documents.findOne();

		assertEquals(TEXT, result.get(Mongo.FIELD_CONTENT));
		assertEquals("en", ((DBObject)result.get(Mongo.FIELD_DOCUMENT)).get(Mongo.FIELD_DOCUMENT_LANGUAGE));

		assertEquals(new Date(timestamp), ((DBObject)result.get(Mongo.FIELD_DOCUMENT)).get(Mongo.FIELD_DOCUMENT_TIMESTAMP));
		assertEquals("test/no_entities", ((DBObject)result.get(Mongo.FIELD_DOCUMENT)).get(Mongo.FIELD_DOCUMENT_SOURCE));

		assertEquals("test", ((DBObject)result.get(Mongo.FIELD_DOCUMENT)).get(Mongo.FIELD_DOCUMENT_TYPE));

		assertEquals("OFFICIAL", ((DBObject)result.get(Mongo.FIELD_DOCUMENT)).get(Mongo.FIELD_DOCUMENT_CLASSIFICATION));
		assertArrayEquals(new String[] { "TEST_A", "TEST_B" }, ((Collection<String>)((DBObject)result.get(Mongo.FIELD_DOCUMENT)).get(Mongo.FIELD_DOCUMENT_CAVEATS)).toArray());
		assertArrayEquals(new String[] { "ENG", "SCO", "WAL" }, ((Collection<String>)((DBObject)result.get(Mongo.FIELD_DOCUMENT)).get(Mongo.FIELD_DOCUMENT_RELEASABILITY)).toArray());

		assertEquals(getDocumentAnnotation(jCas).getHash(), result.get(fields.getExternalId()));
	}

	@Test
	public void testMetadata() throws Exception {
		jCas.setDocumentText(TEXT);

		PublishedId pid1 = new PublishedId(jCas);
		pid1.setValue("id_1");
		pid1.addToIndexes();

		PublishedId pid2 = new PublishedId(jCas);
		pid2.setValue("id_2");
		pid2.addToIndexes();

		Metadata mdSourceAndInformation = new Metadata(jCas);
		mdSourceAndInformation.setKey("sourceAndInformationGrading");
		mdSourceAndInformation.setValue("D3");
		mdSourceAndInformation.addToIndexes();

		Metadata mdCountries = new Metadata(jCas);
		mdCountries.setKey("countryInfo");
		mdCountries.setValue("ENG|WAL|SCO");
		mdCountries.addToIndexes();

		Metadata mdTitle = new Metadata(jCas);
		mdTitle.setKey("documentTitle");
		mdTitle.setValue("Test Title");
		mdTitle.addToIndexes();

		Metadata mdMisc = new Metadata(jCas);
		mdMisc.setKey("test.key");
		mdMisc.setValue("test.value");
		mdMisc.addToIndexes();

		ae.process(jCas);

		assertEquals(1, documents.count());
		DBObject result = documents.findOne();

		assertEquals("id_1", ((DBObject)((BasicDBList)result.get(Mongo.FIELD_PUBLISHEDIDS)).get(0)).get(Mongo.FIELD_PUBLISHEDIDS_ID));

		DBObject meta = (DBObject) result.get(Mongo.FIELD_METADATA);

		assertMeta(meta, "sourceAndInformationGrading", "D3");
		assertMeta(meta, "test_key", "test.value");
		assertMeta(meta, "documentTitle", "Test Title");
		assertMeta(meta, "countryInfo", "ENG|WAL|SCO");
	}

	private void assertMeta(DBObject meta, String key, Object value) {
		assertEquals(value, ((BasicDBList)meta.get(key)).get(0));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testEntities() throws Exception {
		jCas.setDocumentText("James went to London on 19th February 2015. His e-mail address is james@example.com");

		Person p = new Person(jCas);
		p.setBegin(0);
		p.setEnd(5);
		p.setValue(PERSON);
		p.addToIndexes();

		Location l = new Location(jCas);
		l.setBegin(14);
		l.setEnd(20);
		l.setValue(LONDON);
		l.setGeoJson("{\"type\": \"Point\", \"coordinates\": [-0.1, 51.5]}");
		l.addToIndexes();

		DateType dt = new DateType(jCas);
		dt.setBegin(24);
		dt.setEnd(42);
		dt.setConfidence(1.0);
		dt.setValue(DATE);
		dt.addToIndexes();

		CommsIdentifier ci = new CommsIdentifier(jCas);
		ci.setBegin(66);
		ci.setEnd(83);
		ci.setSubType("email");
		ci.setValue(EMAIL);
		ci.addToIndexes();
		
		Buzzword bw = new Buzzword(jCas);
		bw.setBegin(6);
		bw.setEnd(10);
		bw.setValue(WENT);
		
		StringArray tags = new StringArray(jCas, 2);
		tags.set(0, "verb");
		tags.set(1, "past");
		
		bw.setTags(tags);
		bw.addToIndexes();

		ae.process(jCas);

		assertEquals(1, documents.count());
		assertEquals(5, entities.count());


		Map<String, Object> a = (Map<String, Object>)entities.findOne(new BasicDBObject(Mongo.FIELD_ENTITIES + "." + VALUE, PERSON));
		Map<String, Object> person = ((List<Map<String, Object>>)a.get(Mongo.FIELD_ENTITIES)).get(0);
		assertEquals(10, person.size());
		assertEquals(0, person.get(BEGIN));
		assertEquals(5, person.get(END));
		assertEquals(0.0, person.get(CONFIDENCE));
		assertEquals("Person", person.get(TYPE));
		assertEquals(PERSON, person.get(VALUE));

		Map<String, Object> b = (Map<String, Object>)entities.findOne(new BasicDBObject(Mongo.FIELD_ENTITIES + "." + VALUE, LONDON));
		Map<String, Object> location = ((List<Map<String, Object>>)b.get(Mongo.FIELD_ENTITIES)).get(0);
		assertEquals(10, location.size());
		assertEquals(14, location.get(BEGIN));
		assertEquals(20, location.get(END));
		assertEquals(0.0, location.get(CONFIDENCE));
		assertEquals("Location", location.get(TYPE));
		assertEquals(LONDON, location.get(VALUE));

		assertEquals("Point", ((DBObject)location.get("geoJson")).get(TYPE));
		assertArrayEquals(new Double[] { -0.1, 51.5 }, ((BasicDBList)((DBObject)location.get("geoJson")).get("coordinates")).toArray());

		Map<String, Object> c = (Map<String, Object>)entities.findOne(new BasicDBObject(Mongo.FIELD_ENTITIES + "." + VALUE, DATE));
		Map<String, Object> date = ((List<Map<String, Object>>)c.get(Mongo.FIELD_ENTITIES)).get(0);
		assertEquals(9, date.size());
		assertEquals(24, date.get(BEGIN));
		assertEquals(42, date.get(END));
		assertEquals(1.0, date.get(CONFIDENCE));
		assertEquals("DateType", date.get(TYPE));
		assertEquals(DATE, date.get(VALUE));

		Map<String, Object> d = (Map<String, Object>)entities.findOne(new BasicDBObject(Mongo.FIELD_ENTITIES + "." + VALUE, EMAIL));
		Map<String, Object> email = ((List<Map<String, Object>>)d.get(Mongo.FIELD_ENTITIES)).get(0);
		assertEquals(9, email.size());
		assertEquals(66, email.get(BEGIN));
		assertEquals(83, email.get(END));
		assertEquals(0.0, email.get(CONFIDENCE));
		assertEquals("CommsIdentifier", email.get(TYPE));
		assertEquals("email", email.get("subType"));
		assertEquals(EMAIL, email.get(VALUE));
		
		Map<String, Object> e = (Map<String, Object>)entities.findOne(new BasicDBObject(Mongo.FIELD_ENTITIES + "." + VALUE, WENT));
		Map<String, Object> went = ((List<Map<String, Object>>)e.get(Mongo.FIELD_ENTITIES)).get(0);
		assertEquals(10, went.size());
		assertEquals(6, went.get(BEGIN));
		assertEquals(10, went.get(END));
		assertEquals(0.0, went.get(CONFIDENCE));
		List<String> wentTags = (List<String>) went.get("tags");
		assertEquals("verb", wentTags.get(0));
		assertEquals("past", wentTags.get(1));
		assertEquals(WENT, went.get(VALUE));
	}


	@SuppressWarnings("unchecked")
	@Test
	public void testReferenceTargets() throws AnalysisEngineProcessException {
		jCas.setDocumentText("Bill went to London. William came back.");

		ReferenceTarget rt = new ReferenceTarget(jCas);
		rt.addToIndexes();

		Person p = new Person(jCas);
		p.setBegin(0);
		p.setEnd(4);
		p.setValue("Bill");
		p.addToIndexes();
		p.setReferent(rt);

		Person q = new Person(jCas);
		q.setBegin(21);
		q.setEnd(28);
		q.setValue(NAME_2);
		q.addToIndexes();
		q.setReferent(rt);

		ae.process(jCas);
		assertEquals(1, documents.count());
		assertEquals(1, entities.count());

		Map<String, Object> a = (Map<String, Object>)entities.findOne();
		assertEquals(2, ((List<Object>)a.get(Mongo.FIELD_ENTITIES)).size());


	}

	@SuppressWarnings("unchecked")
	@Test
	public void testHistory() throws AnalysisEngineProcessException {
		jCas.setDocumentText("Bill went to London. William came back.");

		Person p = new Person(jCas);
		p.setBegin(0);
		p.setEnd(4);
		p.setValue("Bill");
		p.addToIndexes();

		Person q = new Person(jCas);
		q.setBegin(21);
		q.setEnd(28);
		q.setValue(NAME_2);
		q.addToIndexes();

		DocumentHistory documentHistory = history.getHistory("unknown:" + getDocumentAnnotation(jCas).getHash());
		documentHistory.add(HistoryEvents.createAdded(p, "test"));
		documentHistory.add(HistoryEvents.createAdded(q, "test"));
		documentHistory.add(HistoryEvents.createMerged(p, "test", q.getInternalId()));
		documentHistory.add(HistoryEvents.createMerged(p, "fakeId merge", 500));
		documentHistory.add(HistoryEvents.createRemoved(q, "test"));

		ae.process(jCas);

		Collection<HistoryEvent> pHistory = documentHistory.getHistory(p.getInternalId());
		Collection<HistoryEvent> qHistory = documentHistory.getHistory(q.getInternalId());


		assertEquals(1, documents.count());
		assertEquals(2, entities.count());
		Map<String, Object> a = (Map<String, Object>)entities.findOne(new BasicDBObject(Mongo.FIELD_ENTITIES+"." + VALUE, "Bill"));
		List<Map<String,Object>> pH = (List<Map<String, Object>>) ((List<Map<String,Object>>)a.get(Mongo.FIELD_ENTITIES)).get(0).get(fields.getHistory());
		assertEquals(pHistory.size() + qHistory.size(), pH.size());

		Map<String, Object> b = (Map<String, Object>)entities.findOne(new BasicDBObject(Mongo.FIELD_ENTITIES+"." + VALUE, NAME_2));
		List<Map<String,Object>> qH = (List<Map<String, Object>>) ((List<Map<String,Object>>)b.get(Mongo.FIELD_ENTITIES)).get(0).get(fields.getHistory());
		assertEquals(qHistory.size(), qH.size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRelations() throws Exception {
		jCas.setDocumentText("James went to London on 19th February 2015.");

		Person p = new Person(jCas);
		p.setBegin(0);
		p.setEnd(5);
		p.setValue(PERSON);
		p.addToIndexes();

		Location l = new Location(jCas);
		l.setBegin(14);
		l.setEnd(20);
		l.setValue(LONDON);
		l.setGeoJson("{\"type\": \"Point\", \"coordinates\": [-0.1, 51.5]}");
		l.addToIndexes();

		DateType dt = new DateType(jCas);
		dt.setBegin(24);
		dt.setEnd(42);
		dt.setConfidence(1.0);
		dt.setValue(DATE);
		dt.addToIndexes();

		Relation r = new Relation(jCas);
		r.setBegin(0);
		r.setEnd(20);
		r.setValue("James went to London");
		r.setSource(p);
		r.setTarget(l);
		r.setRelationshipType("AT");
		r.setConfidence(0.7);
		r.addToIndexes();

		ae.process(jCas);

		assertEquals(1, documents.count());
		assertEquals(3, entities.count());
		assertEquals(1, relations.count());

		Map<String, Object> a = (Map<String, Object>)entities.findOne(new BasicDBObject(Mongo.FIELD_ENTITIES + "." + VALUE, PERSON));
		Map<String, Object> person = ((List<Map<String, Object>>)a.get(Mongo.FIELD_ENTITIES)).get(0);
		assertEquals(10, person.size());
		assertEquals(0, person.get(BEGIN));
		assertEquals(5, person.get(END));
		assertEquals(0.0, person.get(CONFIDENCE));
		assertEquals("Person", person.get(TYPE));
		assertEquals(PERSON, person.get(VALUE));

		Map<String, Object> b = (Map<String, Object>)entities.findOne(new BasicDBObject(Mongo.FIELD_ENTITIES + "." + VALUE, LONDON));
		Map<String, Object> location = ((List<Map<String, Object>>)b.get(Mongo.FIELD_ENTITIES)).get(0);
		assertEquals(10, location.size());
		assertEquals(14, location.get(BEGIN));
		assertEquals(20, location.get(END));
		assertEquals(0.0, location.get(CONFIDENCE));
		assertEquals("Location", location.get(TYPE));
		assertEquals(LONDON, location.get(VALUE));

		assertEquals("Point", ((DBObject)location.get("geoJson")).get(TYPE));
		assertArrayEquals(new Double[] { -0.1, 51.5 }, ((BasicDBList)((DBObject)location.get("geoJson")).get("coordinates")).toArray());

		Map<String, Object> c = (Map<String, Object>)entities.findOne(new BasicDBObject(Mongo.FIELD_ENTITIES + "." + VALUE, DATE));
		Map<String, Object> date = ((List<Map<String, Object>>)c.get(Mongo.FIELD_ENTITIES)).get(0);
		assertEquals(9, date.size());
		assertEquals(24, date.get(BEGIN));
		assertEquals(42, date.get(END));
		assertEquals(1.0, date.get(CONFIDENCE));
		assertEquals("DateType", date.get(TYPE));
		assertEquals(DATE, date.get(VALUE));
		
		Map<String, Object> relation = (Map<String, Object>)relations.findOne();
		assertEquals(13, relation.size());
		assertEquals(0, relation.get(BEGIN));
		assertEquals(20, relation.get(END));
		assertEquals(0.7, relation.get(CONFIDENCE));
		assertEquals(person.get("externalId"), relation.get("source"));
		assertEquals(location.get("externalId"), relation.get("target"));
		assertEquals("AT", relation.get("relationshipType"));
		assertEquals("James went to London", relation.get(VALUE));
	}

}
