//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.core.utils.IdentityUtils;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.metadata.PublishedId;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.BaleenConsumer;

/**
 * Abstract class for producing the objects required by the Elasticsearch consumers
 * 
 * <p><b>Schema</b></p>
 * <p>The protective marking set on the DocumentAnnotation is used as the classification of the document, and ProtectiveMarking annotations are ignored.
 * Media, relationships and events aren't currently supported.</p>
 * <p>A default mapping is created to avoid issues where ElasticSearch might guess the wrong type for a field</p>
<pre>
{
	content,
	language,
	externalId,
	dateAccessed,
	sourceUri,
	docType,
	classification,
	caveats: [],
	releasability: [],
	publishedId: [],
	metadata: [
		{
			key: value
		}
	],
	entities: [
		{
			externalId,
			value,
			confidence,
			type,
			begin,
			end,
			...
		}
	],
}</pre>
 *
 * <p>Be aware that this schema is not compatible with that of Baleen 1, which is no longer supported.</p>
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
	
	private Set<String> stopFeatures;
	
	private String[] parsePatterns = {"ddHHmm'Z' MMM yy", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ss", "dd MMM yyyy HHmm'Z'", "dd MMM yy"};
	private List<DateFormat> dateFormats;

	private static final String ES_PROPERTIES = "properties";
	private static final String ES_TYPE = "type";
	private static final String ES_TYPE_STRING = "string";
	private static final String ES_TYPE_INTEGER = "integer";
	private static final String ES_TYPE_LONG = "long";
	private static final String ES_TYPE_DOUBLE = "double";
	private static final String ES_TYPE_GEOSHAPE = "geo_shape";

	private IEntityConverterFields fields;

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
		
		dateFormats = new ArrayList<>();
		for(String s : parsePatterns){
			dateFormats.add(new SimpleDateFormat(s));
		}

		stopFeatures = new HashSet<>();
		stopFeatures.add("uima.cas.AnnotationBase:sofa");
		stopFeatures.add("uk.gov.dstl.baleen.types.BaleenAnnotation:internalId");

		fields = new DefaultFields();
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
							.endObject()
						.endObject()
					.endObject()
				.endObject()
			.endObject();
	}

	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		EntityRelationConverter entityConverter = new EntityRelationConverter(getMonitor(), false, getSupport().getDocumentHistory(jCas), stopFeatures, fields);
		Map<String, Object> json = new HashMap<>();

		//Content and language
		json.put("content", jCas.getDocumentText());
		if(!Strings.isNullOrEmpty(jCas.getDocumentLanguage())){
			json.put("language", jCas.getDocumentLanguage());
		}

		//Document Annotations
		DocumentAnnotation da = getSupport().getDocumentAnnotation(jCas);
		addDocumentAnnotationsToJson(da, json);

		String id = getExternalIdContent(da, contentHashAsId);
		json.put("externalId", id);

		//Metadata Annotations
		Collection<PublishedId> publishedIds = JCasUtil.select(jCas, PublishedId.class);
		if(!publishedIds.isEmpty()){
			json.put("publishedId", getPublishedIdObject(publishedIds));
		}

		Collection<Metadata> metadata = JCasUtil.select(jCas, Metadata.class);
		if(!metadata.isEmpty()){
			addMetadataToJson(metadata, json);
		}

		//Entities
		List<Map<String, Object>> entitiesList = new ArrayList<>();

		Collection<Entity> entities = JCasUtil.select(jCas, Entity.class);

		for(Entity ent : entities){
			entitiesList.add(entityConverter.convertEntity(ent));
		}
		json.put("entities", entitiesList);

		//Persist to ElasticSearch
		addDocument(id, json);
	}
	
	/**
	 * Add the document (provided as JSON) to Elasticsearch, using the id provided.
	 */
	public abstract void addDocument(String id, Map <String, Object> json);

	/**
	 * Returns the external ID, which might be a hash of the content or might be a hash of the source URI
	 *
	 * @param da The document annotation
	 * @param contentHashAsId Should we hash the document content (true), or the source URI (false)
	 */
	public String getExternalIdContent(DocumentAnnotation da, boolean contentHashAsId){
		if(contentHashAsId){
			return da.getHash();
		}else{
			try {
				return IdentityUtils.hashStrings(da.getSourceUri());
			} catch (BaleenException e) {
				getMonitor().error("Couldn't hash Source URI - External ID will be empty", e);
				return "";
			}
		}
	}

	/**
	 * Returns an object representing the published ID(s)
	 *
	 * @param publishedIds
	 */
	public Object getPublishedIdObject(Collection<PublishedId> publishedIds){
		List<String> pids = new ArrayList<>();

		publishedIds.forEach(x -> pids.add(x.getValue()));

		return pids;
	}

	/**
	 * Add document annotations to the JSON object, dependent on whether we're producing a legacy schema or a new one
	 *
	 * @param da The document annotation
	 * @param json The JSON object to add it to
	 */
	public void addDocumentAnnotationsToJson(DocumentAnnotation da, Map<String, Object> json){
		if(!Strings.isNullOrEmpty(da.getSourceUri())){
			json.put("sourceUri", da.getSourceUri());
		}
		json.put("dateAccessed", da.getTimestamp());
		if(!Strings.isNullOrEmpty(da.getDocType())){
			json.put("docType", da.getDocType());
		}
		if(!Strings.isNullOrEmpty(da.getDocumentClassification())){
			json.put("classification", da.getDocumentClassification().toUpperCase());
		}
		if(da.getDocumentCaveats() != null){
			String[] caveats = da.getDocumentCaveats().toArray();
			if(caveats.length > 0){
				json.put("caveats", caveats);
			}
		}
		if(da.getDocumentReleasability() != null){
			String[] rels = da.getDocumentReleasability().toArray();
			if(rels.length > 0){
				json.put("releasability", rels);
			}
		}
	}
	
	/**
	 * Add metadata annotations to the JSON object
	 *
	 * @param md The metadata annotations
	 * @param json The JSON object to add it to
	 */
	public void addMetadataToJson(Collection<Metadata> md, Map<String, Object> json){
		Map<String, Object> metadata = new HashMap<>();

		for(Metadata m : md){
			metadata.put(m.getKey().replaceAll("\\.", "_"), m.getValue());
		}

		json.put("metadata", metadata);
	}
}
