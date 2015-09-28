//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers.utils.mongo;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.uima.BaleenConsumer;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

/**
 * Base for implementing Mongo consumers which provides configuration parameters and save functions
 * when saving a CAS to a single collection as a single document.
 * 
 * 
 * @baleen.javadoc
 */
public abstract class AbstractSingleDocumentMongoConsumer extends BaleenConsumer  {
	protected static final String FIELD_UNIQUE_ID = "uniqueID";

	/**
	 * Connection to Mongo
	 * 
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedMongoResource
	 */
	public static final String KEY_MONGO = "mongo";
	@ExternalResource(key = KEY_MONGO)
	private SharedMongoResource mongoResource;


	/**
	 * The Mongo collection name
	 * 
	 * @baleen.config output
	 */
	public static final String PARAM_COLLECTION = "collection";
	@ConfigurationParameter(name = PARAM_COLLECTION, defaultValue = "output")
	private String collectionName;

	private DBCollection collection;

	/**
	 * Should a hash of the content be used to generate the ID?
	 * If false, then a hash of the Source URI is used instead.
	 * 
	 * @baleen.config true
	 */
	public static final String PARAM_CONTENT_HASH_AS_ID = "contentHashAsId";
	@ConfigurationParameter(name = PARAM_CONTENT_HASH_AS_ID, defaultValue = "true")
	private boolean contentHashAsId = true;

	/**
	 * Get the MongoDB, collection and create some indexes
	 */
	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		DB db = mongoResource.getDB();
		collection = db.getCollection(collectionName);

		collection.createIndex(new BasicDBObject(FIELD_UNIQUE_ID, 1));
	}

	protected DBCollection getCollection() {
		return collection;
	}

	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		DBObject doc = convert(jCas);

		String uniqueId = getUniqueId(jCas);

		saveToMongo(uniqueId, doc);
	}

	protected String getUniqueId(JCas jCas) {
		return ConsumerUtils.getExternalId(getDocumentAnnotation(jCas), contentHashAsId);
	}

	/** Convert the CAS to a Mongo document.
	 * @param jCas the jCas to processs
	 * @return the document
	 */
	protected abstract DBObject convert(JCas jCas) throws AnalysisEngineProcessException;

	private void saveToMongo(String docUniqueId, DBObject doc) {
		getMonitor().debug("Saving document to MongoDB");
		try {
			getMonitor().trace("Upserting document with unique ID " + docUniqueId);
			collection.update(new BasicDBObject().append(FIELD_UNIQUE_ID, docUniqueId), doc, true, false);
		} catch (MongoException me) {
			getMonitor().error("Failed to write document to Mongo", me);
		}

		getMonitor().info("Document {} persisted by MongoConsumer", docUniqueId);
	}

	@Override
	public void doDestroy() {
		collection = null;
	}

}
