package uk.gov.dstl.baleen.resources;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import uk.gov.dstl.baleen.core.utils.ConfigUtils;

/**
 * A shared resource that can be used to persist document status (last modified, and pipeline version).
 * This can the be used to determine if documents should be re-ingested or not.
 *
 */
public class SharedDocumentStatusResource extends SharedMongoResource {
	public static final String LOCATION_KEY = "location";
	public static final String MODIFIED_DATE_KEY = "lastModified";
	public static final String VERSION_KEY = "version";
	
	/**
	 * The Mongo host to connect to
	 * 
	 * @baleen.config localhost
	 */
	public static final String DB_COLLECTION = "documentstatus.collection";
	@ConfigurationParameter(name = DB_COLLECTION, defaultValue="ingest")
	private String collectionName;
	
	/**
	 * Version of pipeline
	 * 
	 * @baleen.config 0
	 */
	public static final String PARAM_PIPELINE_VERSION = "documentstatus.pipelineVersion";
	@ConfigurationParameter(name = PARAM_PIPELINE_VERSION, defaultValue="0")
	private String pipelineVersionString = "0";
	
	//Parse the pipelineVersion config parameter into this variable to avoid issues with parameter types
	private int  pipelineVersion = 0;
	
	private DBCollection collection;

	@Override
	protected boolean doInitialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams) throws ResourceInitializationException {
		pipelineVersion = ConfigUtils.stringToInteger(pipelineVersionString, pipelineVersion);
		if (super.doInitialize(aSpecifier, aAdditionalParams)) {
			collection = getDB().getCollection(collectionName);
			collection.createIndex(new BasicDBObject(LOCATION_KEY, 1));
			return true;
		}
		return false;
	}
	
	/**
	 * Store lastModified and pipelineVersion for uri
	 * @param uri
	 */
	public void persistDocumentDetails(String uri) {
		try {
			BasicDBObject doc = new BasicDBObject();
			doc.put(LOCATION_KEY, uri);
			doc.put(MODIFIED_DATE_KEY, new File(uri).lastModified());
			doc.put(VERSION_KEY, pipelineVersion);
			collection.update(new BasicDBObject().append(LOCATION_KEY, uri), doc, true, false);
		} catch (Exception e) {
			getMonitor().warn("Failed to persist {}", uri);
		}
	}
	
	/**
	 * Remove details of document
	 * @param uri
	 */
	public void removeDocumentDetails(String uri) {
		try {
            BasicDBObject obj=new BasicDBObject();
            obj.put(LOCATION_KEY, uri);
            collection.remove(obj);
            getMonitor().debug("Removed {} from DB", uri);
		} catch (Exception e) {
			getMonitor().error("Failed to remove {} from DB", uri);
		}
	}

	/**
	 * Return lastModified of all documents that match pipelineVersion
	 * @return
	 */
	public Map<String, Long> getExistingDocumentDetails() {
		Map<String, Long> documents = new HashMap<>();
		BasicDBObject fields = new BasicDBObject();
		DBCursor cursor=collection.find(new BasicDBObject(), fields);
		try {
			while (cursor.hasNext()) {
				DBObject doc=cursor.next();
				if (doc.containsField(LOCATION_KEY)) {
					try {
						Integer version=(Integer)doc.get(VERSION_KEY);
						if (version==pipelineVersion) {
							String location=(String)doc.get(LOCATION_KEY);
							Long lastModified=(Long)doc.get(MODIFIED_DATE_KEY);
							documents.put(location, lastModified);
						}
					} catch (Exception e) {
					}
				}
			}
		} finally {
			cursor.close();
		}
		return documents;
	}
}
