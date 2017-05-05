//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources.gazetteer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.resource.Resource;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.resources.SharedMongoResource;

/**
 * Connect to MongoDB and use as the backend for a Gazetteer
 * 
 * 
 */
public class MongoGazetteer extends AbstractMultiMapGazetteer<ObjectId> {
	public static final String CONFIG_COLLECTION = "collection";
	public static final String CONFIG_VALUE_FIELD = "valueField";
	
	public static final String DEFAULT_COLLECTION = "gazetteer";
	public static final String DEFAULT_VALUE_FIELD = "value";
	
	private MongoCollection<Document> coll;
	
	private String valueField;
	
	/** 
	 * Configure a new instance of MongoGazetteer. The following config parameters are expected/allowed:
	 * <ul>
	 * <li><b>collection</b> - The collection containing the gazetteer; defaults to gazetteer</li>
	 * <li><b>valueField</b> - The field containing the gazetteer values; defaults to value</li>
	 * </ul>
	 * 
	 * @param connection A SharedMongoResource object to use to connect to Mongo
	 * @param config A map of additional configuration options
	 * 
	 * @throws InvalidParameterException if the passed connection parameter is not a SharedMongoResource
	 */
	@Override
	public void init(Resource connection, Map<String, Object> config) throws BaleenException{
		SharedMongoResource mongo;
		try{
			mongo = (SharedMongoResource) connection;
		}catch(ClassCastException cce){
			throw new InvalidParameterException("Unable to cast connection parameter to SharedMongoResource", cce);
		}
		
		valueField = DEFAULT_VALUE_FIELD;
		if(config.containsKey(CONFIG_VALUE_FIELD)){
			valueField = config.get(CONFIG_VALUE_FIELD).toString();
		}
		
		String collection = DEFAULT_COLLECTION;
		if(config.containsKey(CONFIG_COLLECTION)){
			collection = config.get(CONFIG_COLLECTION).toString();
		}
		
		coll = mongo.getDB().getCollection(collection);

		super.init(connection, config);
	}
	
	@Override
	public Map<String, Object> getAdditionalData(String key) {
		ObjectId id = getId(key);
		if(id == null){
			return Collections.emptyMap();
		}
		
		Document doc = coll.find(new Document("_id", id)).projection(new Document(valueField, 0).append("_id", 0)).first();
		if(doc == null){
			return Collections.emptyMap();
		}
		
		Map<String, Object> ret = new HashMap<>();
		
		for(String mongoKey : doc.keySet()){
			Object val = doc.get(mongoKey);
			ret.put(mongoKey, val);
		}
		
		return ret;
	}

	@Override
	public void destroy() {
		coll = null;
		super.destroy();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void reloadValues(){
		reset();
		
		for(Document doc : coll.find()){
			ObjectId id = (ObjectId) doc.get("_id");

			Object val = doc.get(valueField);
			if(val instanceof String){
				String s = (String) val;
				
				addTerm(id, s);
			}else if(val instanceof List){
				for(String s : (List<String>)val){
					addTerm(id, s);
				}
			}
		}
	}
}
