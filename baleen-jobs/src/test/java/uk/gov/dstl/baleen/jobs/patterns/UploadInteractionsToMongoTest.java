//Dstl (c) Crown Copyright 2017
//Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.jobs.patterns;

import com.google.common.io.Files;
import com.mongodb.client.MongoCollection;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dstl.baleen.jobs.interactions.UploadInteractionsToMongo;
import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.uima.AbstractBaleenTaskTest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertTrue;

public class UploadInteractionsToMongoTest extends AbstractBaleenTaskTest {

	private ExternalResourceDescription fongoErd;

	@Before
	public void before() {
		fongoErd = ExternalResourceFactory.createExternalResourceDescription("mongo", SharedFongoResource.class,
				"fongo.collection", "na", "fongo.data",
				"[  ]");

	}

	@Test
	public void test() throws ResourceInitializationException, AnalysisEngineProcessException, IOException,
			ResourceAccessException {
		final File file = File.createTempFile("test", "uimt");
		Files.asCharSink(file, StandardCharsets.UTF_8).write("Type,SubType\nMOVEMENT,went,source,target,went,VERB,gone");

		final AnalysisEngine ae = create(UploadInteractionsToMongo.class, "mongo", fongoErd, "input", file);
		execute(ae);

		final SharedFongoResource sfr = (SharedFongoResource) ae.getUimaContext().getResourceObject("mongo");
		final MongoCollection<Document> relationTypes = sfr.getDB().getCollection("relationTypes");
		assertTrue(relationTypes.count() > 0);
		final MongoCollection<Document> interactions = sfr.getDB().getCollection("interactions");
		assertTrue(interactions.count() > 0);

	}

}