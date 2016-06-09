package uk.gov.dstl.baleen.jobs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DBCollection;

import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.uima.BaleenTask;
import uk.gov.dstl.baleen.uima.JobSettings;

/**
 * A task which outputs statistics on the Mongo database.
 *
 * Statistics are saved to a CSV file (specified through the configuration parameter 'file').
 *
 * Typically this task will be used with a FixedRate scheduler for say hourly information:
 *
 * <pre>
 * mongo:
 *   host: localhost
 *   port: 27017
 *
 * job:
 *   schedule:
 *     class: FixedRate
 *     period: 3600
 *   tasks:
 *   - MongoStats
 * </pre>
 *
 * The format of the csv file has columns: timestamp, num documents, num entities, num relations.
 *
 * @baleen.javadoc
 */
public class MongoStats extends BaleenTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(MongoStats.class);

	/**
	 * Connection to Mongo
	 *
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedMongoResource
	 */
	public static final String KEY_MONGO = "mongo";
	@ExternalResource(key = KEY_MONGO)
	private SharedMongoResource mongoResource;

	/**
	 * The collection to entities are output to
	 *
	 * @baleen.config entities
	 */
	public static final String PARAM_ENTITIES_COLLECTION = "entities";
	@ConfigurationParameter(name = PARAM_ENTITIES_COLLECTION, defaultValue = "entities")
	private String entitiesCollectionName;

	/**
	 * The collection to relationships are output to
	 *
	 * @baleen.config relations
	 */
	public static final String PARAM_RELATIONS_COLLECTION = "relations";
	@ConfigurationParameter(name = PARAM_RELATIONS_COLLECTION, defaultValue = "relations")
	private String relationsCollectionName;

	/**
	 * The collection to documents are output to
	 *
	 * @baleen.config documents
	 */
	public static final String PARAM_DOCUMENTS_COLLECTION = "documents";
	@ConfigurationParameter(name = PARAM_DOCUMENTS_COLLECTION, defaultValue = "documents")
	private String documentsCollectionName;

	/**
	 * The collection to output documents to
	 *
	 * @baleen.config documents
	 */
	public static final String PARAM_FILE = "file";
	@ConfigurationParameter(name = PARAM_FILE, defaultValue = "mongo_stats.csv")
	private String filename;

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

	@Override
	protected void execute(JobSettings settings) throws AnalysisEngineProcessException {

		DBCollection entityCollection = mongoResource.getDB().getCollection(entitiesCollectionName);
		DBCollection documentCollection = mongoResource.getDB().getCollection(documentsCollectionName);
		DBCollection relationCollection = mongoResource.getDB().getCollection(relationsCollectionName);

		File file = new File(filename);
		boolean newFile = !file.exists() || file.length() == 0;

		try (Writer writer = new OutputStreamWriter(new FileOutputStream(filename, true), StandardCharsets.UTF_8)) {
			// We have a new file, so write a header line
			if (newFile) {
				writer.write("timestamp,documents,entities,relations\n");
			}

			writer.write(String.format("%s,%d,%d,%d%n", FORMATTER.format(LocalDateTime.now()),
					documentCollection.count(),
					entityCollection.count(),
					relationCollection.count()));

		} catch (IOException e) {
			LOGGER.warn("Unable to write stats to file {} ", filename, e);
		}
	}

}
