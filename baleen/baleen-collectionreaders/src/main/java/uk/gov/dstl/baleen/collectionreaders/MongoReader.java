//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.collectionreaders;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.types.ObjectId;

import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;
import uk.gov.dstl.baleen.uima.IContentExtractor;

import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * This collection reader will process an entire Mongo collection, and then watch for new documents.
 * 
 * <p>The ObjectId (_id) is used to sort documents and to identify new documents.
 * This may miss documents if there is a high write rate, or if documents are being inserted by multiple clients.
 * For more information, see http://docs.mongodb.org/manual/reference/object-id/</p>
 * 
 * 
 * @baleen.javadoc
 */
public class MongoReader extends BaleenCollectionReader {
	/**
	 * Connection to Mongo
	 * 
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedMongoResource
	 */
	public static final String KEY_MONGO = "mongo";
	@ExternalResource(key = KEY_MONGO)
	SharedMongoResource mongo;
	
	/**
	 * The Mongo collection to read data from
	 * 
	 * @baleen.config input
	 */
	public static final String PARAM_COLLECTION = "collection";
	@ConfigurationParameter(name = PARAM_COLLECTION, defaultValue = "input")
	private String collection;
	
	/**
	 * The field containing the Mongo ID (ObjectId)
	 * 
	 * @baleen.config _id
	 */
	public static final String PARAM_ID_FIELD = "idField";
	@ConfigurationParameter(name = PARAM_ID_FIELD, defaultValue = "_id")
	private String idField;
	
	/**
	 * The field containing the document content
	 * 
	 * @baleen.config content
	 */
	public static final String PARAM_CONTENT_FIELD = "contentField";
	@ConfigurationParameter(name = PARAM_CONTENT_FIELD, defaultValue = "content")
	private String contentField;
	
	/**
	 * The content extractor to use to extract content from files
	 * 
	 * @baleen.config TikaContentExtractor
	 */
	public static final String PARAM_CONTENT_EXTRACTOR = "contentExtractor";
	@ConfigurationParameter(name = PARAM_CONTENT_EXTRACTOR, defaultValue="TikaContentExtractor")
	private String contentExtractor = "TikaContentExtractor";
	
	/**
	 * Should the source document be deleted from Mongo after reading
	 * 
	 * @baleen.config false
	 */
	public static final String PARAM_DELETE_SOURCE = "deleteSource";
	@ConfigurationParameter(name = PARAM_DELETE_SOURCE, defaultValue = "false")
	private boolean deleteSource = false;
	
	private DBCollection coll;
	
	List<ObjectId> queue = new LinkedList<>();
	ObjectId lastId = null;
	
	private IContentExtractor extractor;
	
	@Override
	protected void doInitialize(UimaContext context) throws ResourceInitializationException {
		try{
			extractor = getContentExtractor(contentExtractor);
		}catch(InvalidParameterException ipe){
			throw new ResourceInitializationException(ipe);
		}
		extractor.initialize(context, getConfigParameters(context));
		
		coll = mongo.getDB().getCollection(collection);
		getNewIds();
	}

	@Override
	protected void doGetNext(JCas jCas) throws IOException, CollectionException {
		ObjectId id = queue.remove(0);
		
		DBObject docIdField = new BasicDBObject(idField, id);
		DBObject document = coll.findOne(docIdField);
		
		String content = (String) document.get(contentField);
		InputStream is = IOUtils.toInputStream(content);
		
		extractor.processStream(is, mongo.getMongoURI() + "." + collection + "#" + id, jCas);
		
		for(String key : document.keySet()){
			if(contentField.equals(key) || idField.equals(key)){
				continue;
			}else{
				Object obj = document.get(key);
				
				processMongoMetadataField(jCas, key, obj);
			}
		}
		
		if(deleteSource){
			coll.findAndRemove(docIdField);
		}
	}

	private void processMongoMetadataField(JCas jCas, String key, Object obj){
		if(obj instanceof List){
			List<?> list = (List<?>) obj;
			for(Object o : list){
				addMetadata(jCas, key, o.toString());
			}
		}else{
			addMetadata(jCas, key, obj.toString());
		}
	}
	
	private void addMetadata(JCas jCas, String key, String value){
		Metadata md = new Metadata(jCas);
		md.setKey(key);
		md.setValue(value);
		getSupport().add(md);
	}
	
	@Override
	protected void doClose() throws IOException {
		coll = null;
		
		if(extractor != null) {
			extractor.destroy();
			extractor = null;
		}
	}

	@Override
	public boolean doHasNext() throws IOException, CollectionException {
		getNewIds();
		return !queue.isEmpty();
	}
	
	private void getNewIds(){
		DBObject query;
		if(lastId != null){
			query = new BasicDBObject(idField, new BasicDBObject("$gt", lastId));
		}else{
			query = new BasicDBObject();
		}
		
		DBObject docIdField = new BasicDBObject(idField, 1);
		Cursor cur = coll.find(query, docIdField).sort(docIdField);
		while(cur.hasNext()){
			DBObject doc = cur.next();
			ObjectId id = (ObjectId) doc.get(idField);
			
			queue.add(id);
			lastId = id;
		}
	}
}
