//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers.utils.mongo;


import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.core.utils.IdentityUtils;
import uk.gov.dstl.baleen.resources.SharedDocumentCheckerResource;
import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.resources.documentchecker.DocumentExistanceStatus;
import uk.gov.dstl.baleen.uima.BaleenConsumer;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

/**
 * Base for implementing Mongo consumers which provides configuration parameters and save functions
 * when saving a CAS to a single collection as a single document.
 * 
 * 
 * @baleen.javadoc
 */
public abstract class AbstractSingleDocumentMongoConsumer extends BaleenConsumer implements DocumentExistanceStatus {
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
	 * Document checker resource
	 * 
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedDocumentStatusResource
	 */
	public static final String KEY_DOC_CHECKER = "documentchecker";
	@ExternalResource(key = KEY_DOC_CHECKER)
	private SharedDocumentCheckerResource docChecker;

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
	 * Should referecnes to deleted documents be removed from DB.
	 *
	 * @baleen.config false
	 */
	public static final String REMOVE_DELETED_DOCS = "removeDeletedDocs";
	@ConfigurationParameter(name = REMOVE_DELETED_DOCS, defaultValue = "false")
	boolean removeDeletedDocs = false;

	/**
	 * Get the MongoDB, collection and create some indexes
	 */
	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		DB db = mongoResource.getDB();
		collection = db.getCollection(collectionName);

		collection.createIndex(new BasicDBObject(FIELD_UNIQUE_ID, 1));

		if (removeDeletedDocs) {
			if (!contentHashAsId) {
				docChecker.register(this);
				checkExistingDocuments();
			} else {
				removeDeletedDocs=false;
			}
		}
	}

	protected DBCollection getCollection() {
		return collection;
	}

	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		DBObject doc = convert(jCas);

		String uniqueId = getUniqueId(jCas);

		saveToMongo(uniqueId, doc);
		if (removeDeletedDocs) {
			DocumentAnnotation da = getSupport().getDocumentAnnotation(jCas);
			docChecker.check(da.getSourceUri());
		}
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
		if (removeDeletedDocs) {
			docChecker.unregister(this);
		}
	}

	@Override
	public void documentRemoved(String uri) {
		try {
			String id=IdentityUtils.hashStrings(uri);
            BasicDBObject obj=new BasicDBObject();
            obj.put(FIELD_UNIQUE_ID, id);
            collection.remove(obj);
            getMonitor().debug("Removed {} / {} from DB", uri, id);
		} catch (Exception e) {
			getMonitor().error("Failed to remove {} from DB", uri);
		}
	}
	
	/**
	 * Retrieve source.location of all documents in DB, and then check
	 * that these are still valid.
	 */
	private void checkExistingDocuments() {
		BasicDBObject fields = new BasicDBObject();
		fields.put("source", 1); // Filter response to only contain 'source'
		DBCursor cursor=collection.find(new BasicDBObject(), fields);
		try {
			while (cursor.hasNext()) {
				DBObject doc=cursor.next();
				if (doc.containsField("source")) {
					DBObject source=(DBObject)doc.get("source");
					if (source.containsField("location")) {
						String location=(String)source.get("location");
						docChecker.check(location);
					}
				}
			}
		} finally {
			cursor.close();
		}
	}
}
