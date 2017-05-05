//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.utils;

import java.io.IOException;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import uk.gov.dstl.baleen.uima.BaleenConsumer;

/**
 * Abstract class for producing the objects required by the Elasticsearch consumers
 *
 * The schema used is as defined in {@link SingleDocumentConsumerFormat}}.
 * A default mapping is created to avoid issues where ElasticSearch might guess the wrong type for a field
 *
 * Be aware that this schema is not compatible with that of Baleen 1, which is no longer supported.
 * 
 * @baleen.javadoc
 */
public abstract class AbstractElasticsearchConsumer extends BaleenConsumer {

	/**
	 * The Elasticsearch index to use
	 *
	 * @baleen.config baleen_index
	 */
	public static final String PARAM_INDEX = "index";
	@ConfigurationParameter(name = PARAM_INDEX, defaultValue = "baleen_index")
	protected String index;

	/**
	 * The Elasticsearch type to use for documents inserted into the index
	 *
	 * @baleen.config baleen_output
	 */
	public static final String PARAM_TYPE = "type";
	@ConfigurationParameter(name = PARAM_TYPE, defaultValue = "baleen_output")
	protected String type;

	/**
	 * Should a hash of the content be used to generate the ID?
	 * If false, then a hash of the Source URI is used instead.
	 *
	 * @baleen.config true
	 */
	public static final String PARAM_CONTENT_HASH_AS_ID = "contentHashAsId";
	@ConfigurationParameter(name = PARAM_CONTENT_HASH_AS_ID, defaultValue = "true")
	boolean contentHashAsId = true;

	private static final String ES_PROPERTIES = "properties";
	private static final String ES_TYPE = "type";
	private static final String ES_TYPE_STRING = "string";
	private static final String ES_TYPE_INTEGER = "integer";
	private static final String ES_TYPE_LONG = "long";
	private static final String ES_TYPE_DOUBLE = "double";
	private static final String ES_TYPE_GEOSHAPE = "geo_shape";
	private static final String ES_TYPE_DATE = "date";
	private static final String ES_TYPE_NESTED = "nested";

	private IEntityConverterFields fields = new DefaultFields();

	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		boolean indexCreated = createIndex();
		
		if(indexCreated){
			try{
				addMapping(createMappingObject());
			}catch(IOException ioe){
				getMonitor().error("Unable to create mapping, you may get unexpected results in your Elasticsearch index", ioe);
			}
		}
	}
	
	/**
	 * Create an index in Elasticsearch.
	 * If necessary, this function should check whether a new index is required.
	 * 
	 * @return true if a new index has been created, false otherwise
	 */
	public abstract boolean createIndex();
	
	/**
	 * Add a mapping to Elasticsearch.
	 * This will only be called if a new index has been created
	 */
	public abstract void addMapping(XContentBuilder mapping);

	/**
	 * Create a mapping for the new index
	 */
	private XContentBuilder createMappingObject() throws IOException{
		// Just specify known non-String types and potential problem cases
		return XContentFactory.jsonBuilder().startObject()
			.startObject(type)
				.startObject(ES_PROPERTIES)
					.startObject("dateAccessed")
						.field(ES_TYPE, ES_TYPE_LONG)
						.endObject()
					.startObject("metadata")
						.field(ES_TYPE, ES_TYPE_NESTED)
						.startObject(ES_PROPERTIES)
							.startObject("value")
								.field(ES_TYPE, ES_TYPE_STRING)
								.endObject()
							.startObject("key")
								.field(ES_TYPE, ES_TYPE_STRING)
								.endObject()
							.endObject()
						.endObject()
					.startObject("entities")
						.field(ES_TYPE, ES_TYPE_NESTED)
						.startObject(ES_PROPERTIES)
							.startObject("value")
								.field(ES_TYPE, ES_TYPE_STRING)
								.endObject()
							.startObject("begin")
								.field(ES_TYPE, ES_TYPE_INTEGER)
								.endObject()
							.startObject("end")
								.field(ES_TYPE, ES_TYPE_INTEGER)
								.endObject()
							.startObject("confidence")
								.field(ES_TYPE, ES_TYPE_DOUBLE)
								.endObject()
							.startObject("geoJson")
								.field(ES_TYPE, ES_TYPE_GEOSHAPE)
								.endObject()
							.startObject("timestampStart")
								.field(ES_TYPE, ES_TYPE_DATE)
								.field("format", "epoch_second")
								.endObject()
							.startObject("timestampStop")
								.field(ES_TYPE, ES_TYPE_DATE)
								.field("format", "epoch_second")
								.endObject()
							.endObject()
						.endObject()
					.endObject()
				.endObject()
			.endObject();
	}

	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		Map<String, Object> json = SingleDocumentConsumerFormat.formatCas(jCas, fields, contentHashAsId, getMonitor(), getSupport());
		String id = (String) json.getOrDefault("externalId", "");

		//Persist to ElasticSearch
		addDocument(id, json);
	}
	
	/**
	 * Add the document (provided as JSON) to Elasticsearch, using the id provided.
	 */
	public abstract void addDocument(String id, Map <String, Object> json);
}