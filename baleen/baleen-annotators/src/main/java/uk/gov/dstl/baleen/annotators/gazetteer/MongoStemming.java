//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.gazetteer;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;

import uk.gov.dstl.baleen.annotators.gazetteer.helpers.AbstractStemmingAhoCorasickAnnotator;
import uk.gov.dstl.baleen.annotators.gazetteer.helpers.GazetteerUtils;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.resources.gazetteer.IGazetteer;
import uk.gov.dstl.baleen.resources.gazetteer.MongoGazetteer;

/**
 * Generic Mongo-backed Stemming RadixTree Gazetteer annotator, that will use a Mongo gazetteer to find and annotate entities.
 * 
 * @baleen.javadoc
 */
public class MongoStemming extends AbstractStemmingAhoCorasickAnnotator {
	/**
	 * Connection to Mongo
	 * 
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedMongoResource
	 */
	public static final String KEY_MONGO = "mongo";
	@ExternalResource(key = KEY_MONGO)
	private SharedMongoResource mongo;
	
	/**
	 * The name of the Mongo collection containing the gazetteer
	 * 
	 * @baleen.config gazetteer
	 */
	public static final String PARAM_COLLECTION = "collection";
	@ConfigurationParameter(name = PARAM_COLLECTION, defaultValue = "gazetteer")
	private String collection;
	
	/**
	 * The name of the field in Mongo that contains the gazetteer values
	 * 
	 * @baleen.config value
	 */
	public static final String PARAM_VALUE_FIELD = "valueField";
	@ConfigurationParameter(name = PARAM_VALUE_FIELD, defaultValue = "value")
	private String valueField;
	
	/**
	 * Constructor
	 */
	public MongoStemming() {
		// Do nothing
	}
	
	@Override
	public IGazetteer configureGazetteer() throws BaleenException {
		IGazetteer gaz = new MongoGazetteer();
		gaz.init(mongo, GazetteerUtils.configureMongo(caseSensitive, collection, valueField));
		
		return gaz;
	}
}
