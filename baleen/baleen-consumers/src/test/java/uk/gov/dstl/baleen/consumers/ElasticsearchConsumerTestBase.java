//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers;

import java.util.Arrays;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.indices.IndexMissingException;
import org.junit.AfterClass;
import org.junit.Before;

import uk.gov.dstl.baleen.types.common.CommsIdentifier;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.metadata.PublishedId;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.temporal.DateType;
import uk.gov.dstl.baleen.uima.utils.UimaTypesUtils;

public class ElasticsearchConsumerTestBase {
	protected static JCas jCas;
	protected static Client client;

	protected static AnalysisEngine ae;
	
	protected static final long SLEEP_DELAY = 1500;
	
	@AfterClass
	public static void destroyClass(){
		if (client != null) {
			client.close();
		}

		if (ae != null) {
			ae.destroy();
		}
	}

	@Before
	public void beforeTest() throws Exception{
		jCas.reset();

		try{
			client.admin().indices().delete(new DeleteIndexRequest("baleen_index")).actionGet();
		}catch(IndexMissingException ime){
			//Index doesn't exist - ignore
		}
	}
	
	protected DocumentAnnotation getDocumentAnnotation(JCas jCas){
		return (DocumentAnnotation) jCas.getDocumentAnnotationFs();
	}
	
	protected long createNoEntitiesDocument(){
		jCas.reset();
		jCas.setDocumentText("Hello World");
		jCas.setDocumentLanguage("en");

		long timestamp = System.currentTimeMillis();

		DocumentAnnotation da = getDocumentAnnotation(jCas);
		da.setTimestamp(timestamp);
		da.setSourceUri("test/no_entities");
		da.setDocType("test");
		da.setDocumentClassification("OFFICIAL");
		da.setDocumentCaveats(UimaTypesUtils.toArray(jCas, Arrays.asList(new String[] { "TEST_A", "TEST_B" })));
		da.setDocumentReleasability(UimaTypesUtils.toArray(jCas, Arrays.asList(new String[] { "ENG", "SCO", "WAL" })));
		
		return timestamp;
	}
	
	protected void createMetadataDocument(){
		jCas.reset();
		jCas.setDocumentText("Hello World");

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
		mdMisc.setKey("test_key");
		mdMisc.setValue("test_value");
		mdMisc.addToIndexes();
	}
	
	protected void createEntitiesDocument(){
		jCas.reset();
		jCas.setDocumentText("James went to London on 19th February 2015. His e-mail address is james@example.com");

		Person p = new Person(jCas);
		p.setBegin(0);
		p.setEnd(5);
		p.setValue("James");
		p.addToIndexes();

		Location l = new Location(jCas);
		l.setBegin(14);
		l.setEnd(20);
		l.setValue("London");
		l.setGeoJson("{\"type\": \"Point\", \"coordinates\": [-0.1, 51.5]}");
		l.addToIndexes();

		DateType d = new DateType(jCas);
		d.setBegin(24);
		d.setEnd(42);
		d.setConfidence(1.0);
		d.addToIndexes();

		CommsIdentifier ci = new CommsIdentifier(jCas);
		ci.setBegin(66);
		ci.setEnd(83);
		ci.setSubType("email");
		ci.addToIndexes();
	}
}
