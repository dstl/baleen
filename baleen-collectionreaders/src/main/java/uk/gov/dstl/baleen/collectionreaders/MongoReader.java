//Dstl (c) Crown Copyright 2017
//Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders;

import com.mongodb.client.MongoCollection;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;
import org.bson.types.ObjectId;
import uk.gov.dstl.baleen.core.utils.BaleenDefaults;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;
import uk.gov.dstl.baleen.uima.IContentExtractor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

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
	 * @baleen.config Value of BaleenDefaults.DEFAULT_CONTENT_EXTRACTOR
	 */
	public static final String PARAM_CONTENT_EXTRACTOR = "contentExtractor";
	@ConfigurationParameter(name = PARAM_CONTENT_EXTRACTOR, defaultValue=BaleenDefaults.DEFAULT_CONTENT_EXTRACTOR)
	private String contentExtractor;
	
	/**
	 * Should the source document be deleted from Mongo after reading
	 * 
	 * @baleen.config false
	 */
	public static final String PARAM_DELETE_SOURCE = "deleteSource";
	@ConfigurationParameter(name = PARAM_DELETE_SOURCE, defaultValue = "false")
	private boolean deleteSource = false;
	
	private MongoCollection<Document> coll;
	
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
		
		Document docIdField = new Document(idField, id);
		Document document = coll.find(docIdField).first();
		
		if(document == null){
			getMonitor().error("No document returned from Mongo");
			throw new CollectionException();
		}
		
		String content = (String) document.get(contentField);
		extractor.processStream(new ByteArrayInputStream(content.getBytes(Charset.defaultCharset())), mongo.getMongoURI() + "." + collection + "#" + id, jCas);
		
		for(String key : document.keySet()){
			if(contentField.equals(key) || idField.equals(key)){
				continue;
			}else{
				Object obj = document.get(key);
				
				processMongoMetadataField(jCas, key, obj);
			}
		}
		
		if(deleteSource){
			coll.deleteOne(docIdField);
		}
	}

	private void processMongoMetadataField(JCas jCas, String key, Object obj){
		if(obj == null)
			return;

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
		Document query;
		if(lastId != null){
			query = new Document(idField, new Document("$gt", lastId));
		}else{
			query = new Document();
		}
		
		Document docIdField = new Document(idField, 1);
		for(Document doc : coll.find(query).projection(docIdField).sort(docIdField)){
			ObjectId id = (ObjectId) doc.get(idField);
			
			queue.add(id);
			lastId = id;
		}
	}
}
