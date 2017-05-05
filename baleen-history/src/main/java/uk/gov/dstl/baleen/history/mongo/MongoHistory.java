//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.history.mongo;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;

import com.mongodb.client.MongoCollection;

import uk.gov.dstl.baleen.core.history.AbstractBaleenHistory;
import uk.gov.dstl.baleen.core.history.DocumentHistory;
import uk.gov.dstl.baleen.resources.SharedMongoResource;

/** A history implementation which is backed by Mongo.
 * 
 * Use history.mongoCollection to set the collection (defaults to history).
 * The Mongo database used if as per the global configuration.
 *  
 * The specifics of implementation are discussed in @link MongoDocumentHistory.
 * 
 * For implementors wishing for a different db structure they should override
 * MongoDocumentHistory, and then add configuration options here. (Example
 * would be to store an entity per Mongo document, rather than a document
 * per Mongo document).
 * 
 * 
 * @baleen.javadoc
 */
public class MongoHistory extends AbstractBaleenHistory {
	
	/**
	 * Optional connection to Mongo
	 * 
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedMongoResource
	 */
	public static final String KEY_MONGO = "mongo";
	@ExternalResource(key = KEY_MONGO)
	private SharedMongoResource mongo;
	
	/**
	 * The Mongo collection to write history to
	 * 
	 * @baleen.config history
	 */
	public static final String PARAM_COLLECTION = "history.mongoCollection";
	@ConfigurationParameter(name = PARAM_COLLECTION, defaultValue="history")
	private String collectionName;

	private MongoCollection<Document> collection;
	
	/** New instance, used for UIMA fit dependency injection.
	 * 
	 */
	public MongoHistory() {
		//Empty contructor, do nothing
	}
	
	/** New instance, used for testing without DI.
	 * 
	 */
	public MongoHistory(SharedMongoResource mongo) {
		this.mongo = mongo;
	}
	
	@Override
	public void afterResourcesInitialized()
			throws ResourceInitializationException {
		// Our initialisation needs to wait for Mongo to be initialised and then injected
		
		collection = mongo.getDB().getCollection(collectionName);
		collection.createIndex(new Document("docId", 1));
	}
	

	@Override
	public DocumentHistory getHistory(String documentId) {
		return new MongoDocumentHistory(this, collection, documentId);
	}

	@Override
	public void closeHistory(String documentId) {
		// Do nothing
	}
	
}
