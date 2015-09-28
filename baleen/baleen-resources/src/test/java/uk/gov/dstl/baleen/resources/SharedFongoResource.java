//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.resources;

import java.util.List;
import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;
import org.elasticsearch.common.base.Strings;

import com.github.fakemongo.Fongo;
import com.google.common.base.Optional;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.util.JSON;

public class SharedFongoResource extends SharedMongoResource {
	private static final String BALEEN = "baleen";

	private Fongo fongo = new Fongo("baleen_test_server");
	
	@ConfigurationParameter(name = "fongo.collection", defaultValue = "baleen_test_collection")
	String fongoCollection;
	
	@ConfigurationParameter(name = "fongo.data", defaultValue = "{}")
	String fongoData;
	
	@SuppressWarnings("unchecked")
	@Override
	protected boolean doInitialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams) throws ResourceInitializationException{
		// Work whether it's a list of DB Objects or a single
		if("{}".equals(fongoData) || Strings.isNullOrEmpty(fongoData)){
			return true;
		}
		
		Object obj = JSON.parse(fongoData);
		
		if(obj instanceof List<?>){
			List<DBObject> data = (List<DBObject>) JSON.parse(fongoData);
			fongo.getDB(BALEEN).getCollection(fongoCollection).insert(data);
			
		}else if(obj instanceof DBObject){
			DBObject data = (DBObject) JSON.parse(fongoData);
			fongo.getDB(BALEEN).getCollection(fongoCollection).insert(data);
			
		}else{
			getMonitor().error("Unsupported type");
			throw new ResourceInitializationException();
		}
		
		return true;
	}
	
	@Override
	protected MongoClient createMongoClient(ServerAddress sa, Optional<MongoCredential> credentials){
		// Doesn't test credentials
		return fongo.getMongo();
	}
	
	@Override
	protected void doDestroy(){
		// Do nothing
	}
	
	@Override
	public DB getDB(){
		return fongo.getDB(BALEEN);
	}
}
