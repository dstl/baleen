//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.jobs;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.mongodb.util.JSON;

import uk.gov.dstl.baleen.resources.SharedFongoResource;
import uk.gov.dstl.baleen.uima.AbstractBaleenTaskTest;

public class MongoStatsTest extends AbstractBaleenTaskTest {

	private static final List<Document> DATA = Lists.newArrayList(
			new Document("fake", "doc1"),
			new Document("fake", "doc2"),
			new Document("fake", "doc3"));

	@Test
	public void testNewFile() throws ResourceInitializationException, AnalysisEngineProcessException, IOException {
		// Due to limitations in the shared fongo resource we only test document count here!
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription("mongo",
				SharedFongoResource.class, SharedFongoResource.PARAM_FONGO_COLLECTION, "documents",
				SharedFongoResource.PARAM_FONGO_DATA, JSON.serialize(DATA));

		File tempFile = File.createTempFile("test", "mongostats");
		tempFile.delete();
		try {

			AnalysisEngine task = create(MongoStats.class, "mongo", erd, "file", tempFile.getAbsolutePath());
			execute(task);
			task.destroy();

			List<String> lines = Files.readAllLines(tempFile.toPath());
			assertEquals(2, lines.size());
			assertEquals("timestamp,documents,entities,relations", lines.get(0));

			String[] split = lines.get(1).split(",");
			assertEquals("3", split[1]);
			assertEquals("0", split[2]);
			assertEquals("0", split[3]);
		} finally {
			tempFile.delete();
		}
	}
	
	@Test
	public void testEmptyFile() throws ResourceInitializationException, AnalysisEngineProcessException, IOException {
		// Due to limitations in the shared fongo resource we only test document count here!
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription("mongo",
				SharedFongoResource.class, SharedFongoResource.PARAM_FONGO_COLLECTION, "documents",
				SharedFongoResource.PARAM_FONGO_DATA, JSON.serialize(DATA));

		File tempFile = File.createTempFile("test", "mongostats");
		try {

			AnalysisEngine task = create(MongoStats.class, "mongo", erd, "file", tempFile.getAbsolutePath());
			execute(task);
			task.destroy();
			
			List<String> lines = Files.readAllLines(tempFile.toPath());
			assertEquals(2, lines.size());
			assertEquals("timestamp,documents,entities,relations", lines.get(0));

			String[] split = lines.get(1).split(",");
			assertEquals("3", split[1]);
			assertEquals("0", split[2]);
			assertEquals("0", split[3]);
		} finally {
			tempFile.delete();
		}
	}

	@Test
	public void testWritingToExistingFile()
			throws ResourceInitializationException, AnalysisEngineProcessException, IOException {
		// Due to limitations in the shared fongo resource we only test document count here!
		ExternalResourceDescription erd = ExternalResourceFactory.createExternalResourceDescription("mongo",
				SharedFongoResource.class, SharedFongoResource.PARAM_FONGO_COLLECTION, "documents",
				SharedFongoResource.PARAM_FONGO_DATA, JSON.serialize(DATA));

		File tempFile = File.createTempFile("test", "mongostats-existing");
		try (FileWriter fileWriter = new FileWriter(tempFile)) {
			fileWriter.write("hello\n");
		}

		try {

			AnalysisEngine task = create(MongoStats.class, "mongo", erd, "file", tempFile.getAbsolutePath());
			execute(task);
			task.destroy();
			
			List<String> lines = Files.readAllLines(tempFile.toPath());
			assertEquals(2, lines.size());
			assertEquals("hello", lines.get(0));

			String[] split = lines.get(1).split(",");
			assertEquals("3", split[1]);
			assertEquals("0", split[2]);
			assertEquals("0", split[3]);
		} finally {
			tempFile.delete();
		}
	}

}