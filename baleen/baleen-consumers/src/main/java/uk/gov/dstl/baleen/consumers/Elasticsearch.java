//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import uk.gov.dstl.baleen.consumers.utils.EntityRelationConverter;
import uk.gov.dstl.baleen.consumers.utils.IEntityConverterFields;
import uk.gov.dstl.baleen.consumers.utils.elasticsearch.ElasticsearchFields;
import uk.gov.dstl.baleen.core.utils.IdentityUtils;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.SharedElasticsearchResource;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.metadata.PublishedId;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.uima.BaleenConsumer;

import com.google.common.base.Strings;

/**
 * Output processed CAS object into Elasticsearch, using either a legacy schema (compatible with the outputs from Baleen 1) or an updated schema
 *
 * <p>
 * <p><b>Legacy Schema</b></p>
 * <p>If more than one PublishedId is found, then only the first instance is used.
 * The protective marking set on the DocumentAnnotation is used as the classification of the document, and ProtectiveMarking annotations are ignored.
 * Media, relationships and events aren't supported.</p>
 * <p>The output document format is as follows:</p>
<pre>
{
	doc,
	language,
	uniqueID,
	dateAccessed,
	link,
	title,
	summary,
	countries: [],
	source_reliability,
	source_validity,
	docType: {
		value,
		confidence,
		annotator
	},
	publishedId,
	classification,
	caveats: [],
	releasability: [],
	documentInfo: [
		{
			key: value
		}
	],
	entities: [
		{
			uniqueId,
			value,
			confidence,
			annotator,
			type,
			begin,
			end,
			references: [
				{
					begin,
					end,
					value
				}
			],
			...
		}
	]
}</pre>
 *
 * <p><b>Updated Schema</b></p>
 * <p>As with the legacy schema, the protective marking set on the DocumentAnnotation is used as the classification of the document,
 * and ProtectiveMarking annotations are ignored.
 * Multiple PublishedIds are supported however. Media, relationships and events aren't supported.</p>
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
 * 
 * @baleen.javadoc
 */
public class Elasticsearch extends BaleenConsumer {
	/**
	 * Connection to Elasticsearch
	 *
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedElasticsearchResource
	 */
	public static final String KEY_ELASTICSEARCH = "elasticsearch";
	@ExternalResource(key = KEY_ELASTICSEARCH)
	private SharedElasticsearchResource esResource;

	/**
	 * The Elasticsearch index to use
	 *
	 * @baleen.config baleen_index
	 */
	public static final String PARAM_INDEX = "index";
	@ConfigurationParameter(name = PARAM_INDEX, defaultValue = "baleen_index")
	String index;

	/**
	 * The Elasticsearch type to use for documents inserted into the index
	 *
	 * @baleen.config baleen_output
	 */
	public static final String PARAM_TYPE = "type";
	@ConfigurationParameter(name = PARAM_TYPE, defaultValue = "baleen_output")
	String type;

	/**
	 * Should the legacy schema be used?
	 *
	 * @baleen.config false
	 */
	public static final String PARAM_LEGACY = "legacy";
	@ConfigurationParameter(name = PARAM_LEGACY, defaultValue = "false")
	boolean legacy = false;

	/**
	 * Should a hash of the content be used to generate the ID?
	 * If false, then a hash of the Source URI is used instead.
	 *
	 * @baleen.config true
	 */
	public static final String PARAM_CONTENT_HASH_AS_ID = "contentHashAsId";
	@ConfigurationParameter(name = PARAM_CONTENT_HASH_AS_ID, defaultValue = "true")
	boolean contentHashAsId = true;

	/**
	 * Should only normalised entities be persisted in the elasticsearch database?
	 * This is useful when another persistent store, such as mongodb, is used to
	 * store the entities and elasticsearch only needs the entity content for
	 * searching purposes. In which case, to reduce duplication of data and storage
	 * overhead it is only necessary to store the unmodified full text and any modified,
	 * i.e. normalised, entities.
	 *
	 * @baleen.config false
	 */
	public static final String ONLY_STORE_NORMALISED_ENTITIES = "onlyStoreNormalisedEntities";
	@ConfigurationParameter(name = ONLY_STORE_NORMALISED_ENTITIES, defaultValue = "false")
	boolean onlyStoreNormalisedEntities = false;
	
	/**
	 * A list of keys to look in for dates that could used for sorting.
	 * Keys are listed in order of preference, and are expected to be in one of the following formats:
	 * <ul>
	 * <li>ddHHmm'Z' MMM yy</li>
	 * <li>yyyy-MM-dd HH:mm:ss</li>
	 * <li>yyyy-MM-dd'T'HH:mm:ss'Z'</li>
	 * <li>yyyy-MM-dd'T'HH:mm:ss</li>
	 * <li>dd MMM yyyy HHmm'Z'</li>
	 * <li>dd MMM yy</li>
	 * </ul>
	 * This is only used in legacy mode, as the updated schema doesn't explicitly provide a sort date
	 * 
	 * @baleen.config documentDtg
	 * @baleen.config dateOfReport
	 * @baleen.config dateOfInformation
	 * @baleen.config report_date
	 * @baleen.config Creation-Date
	 * @baleen.config dcterms:created
	 * @baleen.config Last-Modified
	 * @baleen.config Last-Save_Date
	 */
	public static final String PARAM_SORT_DATE = "sortDate";
	@ConfigurationParameter(name = PARAM_SORT_DATE, defaultValue = {"documentDtg", "dateOfReport", "dateOfInformation", "report_date", "Creation-Date", "dcterms:created", "Last-Modified", "Last-Save_Date"})
	private String[] sortDateKeys;
	
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
	private static final String LEGACY_SOURCE_RELIABILITY = "source_reliability";
	private static final String LEGACY_SOURCE_VALIDITY = "source_validity";

	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		if(!esResource.getClient().admin().indices().exists(Requests.indicesExistsRequest(index)).actionGet().isExists()){
			esResource.getClient().admin().indices()
				.create(Requests.createIndexRequest(index))
				.actionGet();
			if(!legacy){
				createMapping();
			}
		}
		
		dateFormats = new ArrayList<>();
		for(String s : parsePatterns){
			dateFormats.add(new SimpleDateFormat(s));
		}

		stopFeatures = new HashSet<>();
		stopFeatures.add("uima.cas.AnnotationBase:sofa");
		stopFeatures.add("uk.gov.dstl.baleen.types.BaleenAnnotation:internalId");

		fields = new ElasticsearchFields(legacy);
	}

	/**
	 * Create a mapping for the new index
	 */
	private void createMapping(){
		try{
			// Just specify known non-String types and potential problem cases
			XContentBuilder mappingBuilder = XContentFactory.jsonBuilder().startObject()
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
									.field(ES_TYPE, legacy ? ES_TYPE_STRING : ES_TYPE_GEOSHAPE)
									.endObject()
								.endObject()
							.endObject()
						.endObject()
					.endObject()
				.endObject();

			esResource.getClient().admin().indices()
				.preparePutMapping(index)
				.setType(type)
				.setSource(mappingBuilder)
				.execute().actionGet();
		}catch(IOException ioe){
			getMonitor().error("Unable to create Elasticsearch mapping", ioe);
		}
	}

	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		EntityRelationConverter entityConverter = new EntityRelationConverter(getMonitor(), false, getSupport().getDocumentHistory(jCas), stopFeatures, fields);
		Map<String, Object> json = new HashMap<>();

		//Content and language
		json.put(getContentName(legacy), jCas.getDocumentText());
		if(!Strings.isNullOrEmpty(jCas.getDocumentLanguage())){
			json.put("language", jCas.getDocumentLanguage());
		}

		//Document Annotations
		DocumentAnnotation da = getSupport().getDocumentAnnotation(jCas);
		addDocumentAnnotationsToJson(da, json, legacy);

		String id = getExternalIdContent(da, contentHashAsId);
		json.put(getExternalIdName(legacy), id);

		//Metadata Annotations
		Collection<PublishedId> publishedIds = JCasUtil.select(jCas, PublishedId.class);
		if(!publishedIds.isEmpty()){
			json.put("publishedId", getPublishedIdObject(publishedIds, legacy));
		}

		Collection<Metadata> metadata = JCasUtil.select(jCas, Metadata.class);
		if(!metadata.isEmpty()){
			if(legacy){
				addLegacyMetadataToJson(metadata, json);
			}else{
				addMetadataToJson(metadata, json);
			}
		}

		//Entities
		List<Map<String, Object>> entitiesList = new ArrayList<>();

		Collection<Entity> entities = JCasUtil.select(jCas, Entity.class);

		for(Entity ent : entities){
			if (onlyStoreNormalisedEntities && !ent.getIsNormalised()) {
				continue;
			}

			Map<String, Object> entity = entityConverter.convertEntity(ent);
			if(legacy && ent instanceof Location){
				Map<String, Object> geoFeature = new HashMap<>();
				geoFeature.put("type","Feature");
				geoFeature.put("geometry", entity.remove(fields.getGeoJSON()));
				entity.put(fields.getGeoJSON(), geoFeature);

			}
			entitiesList.add(entity);
		}
		json.put("entities", entitiesList);

		//Persist to ElasticSearch
		try{
			esResource.getClient().prepareIndex(index, type, id).setSource(json).execute().actionGet();
		}catch(ElasticsearchException ee){
			getMonitor().error("Couldn't persist document to Elasticsearch", ee);
		}
	}

	/**
	 * Returns the name for the external ID, dependent on whether we're producing a legacy schema or a new one
	 *
	 * @param legacy
	 */
	public String getExternalIdName(boolean legacy){
		return legacy ? "uniqueID" : "externalId";
	}

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
	 * Returns the name for the document content, dependent on whether we're producing a legacy schema or a new one
	 *
	 * @param legacy
	 */
	public String getContentName(boolean legacy){
		return legacy ? "doc" : "content";
	}

	/**
	 * Returns the name for the source, dependent on whether we're producing a legacy schema or a new one
	 *
	 * @param legacy
	 */
	public String getSourceName(boolean legacy){
		return legacy ? "link" : "sourceUri";
	}

	/**
	 * Returns an object representing the document type, dependent on whether we're producing a legacy schema or a new one
	 *
	 * @param docAnnot The document annotation
	 * @param legacy
	 */
	public Object getDocTypeObject(DocumentAnnotation docAnnot, boolean legacy){
		if(legacy){
			Map<String, Object> docType = new HashMap<>();

			docType.put("value", docAnnot.getDocType());
			docType.put("confidence", 1.0);
			docType.put("annotator", Elasticsearch.class.getName());

			return docType;
		}else{
			return docAnnot.getDocType();
		}
	}

	/**
	 * Returns an object representing the published ID, dependent on whether we're producing a legacy schema or a new one
	 *
	 * @param publishedIds
	 * @param legacy
	 */
	public Object getPublishedIdObject(Collection<PublishedId> publishedIds, boolean legacy){
		if(legacy){
			PublishedId pid = publishedIds.iterator().next();

			if(publishedIds.size() > 1){
				getMonitor().warn("{} additional PublishedID(s) found, which will not be persisted in ElasticSearch", publishedIds.size() - 1);
			}

			return pid.getValue();
		}else{
			List<String> pids = new ArrayList<>();

			publishedIds.forEach(x -> pids.add(x.getValue()));

			return pids;
		}
	}

	/**
	 * Add document annotations to the JSON object, dependent on whether we're producing a legacy schema or a new one
	 *
	 * @param da The document annotation
	 * @param json The JSON object to add it to
	 * @param legacy
	 */
	public void addDocumentAnnotationsToJson(DocumentAnnotation da, Map<String, Object> json, boolean legacy){
		if(!Strings.isNullOrEmpty(da.getSourceUri())){
			json.put(getSourceName(legacy), da.getSourceUri());
		}
		json.put("dateAccessed", da.getTimestamp());
		if(!Strings.isNullOrEmpty(da.getDocType())){
			json.put("docType", getDocTypeObject(da, legacy));
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
	 * Returns the name for the metadata container, dependent on whether we're producing a legacy schema or a new one
	 *
	 * @param legacy
	 */
	public String getMetadataName(boolean legacy){
		return legacy ? "documentInfo" : "metadata";
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
			metadata.put(m.getKey(), m.getValue());
		}

		json.put(getMetadataName(false), metadata);
	}

	/**
	 * Add metadata annotations to the JSON object using a legacy schema
	 *
	 * @param md The metadata annotations
	 * @param json The JSON object to add it to
	 */
	public void addLegacyMetadataToJson(Collection<Metadata> md, Map<String, Object> json){
		Map<String, Object> metadata = new HashMap<>();

		for(Metadata m : md){
			switch(m.getKey()){
			case "documentTitle":
				json.put("title", m.getValue());
				break;
			case "documentSummary":
				json.put("summary", m.getValue());
				break;
			case "countryInfo":
				String[] countries = m.getValue().split("\\|");
				json.put("countries", countries);
				break;
			case "sourceGrading":
				json.put("sourceGrading", m.getValue());
				json.put(LEGACY_SOURCE_RELIABILITY, sourceGradingToInteger(m.getValue()));
				break;
			case "informationGrading":
				json.put("informationGrading", m.getValue());
				json.put(LEGACY_SOURCE_VALIDITY, informationGradingToInteger(m.getValue()));
				break;
			case "sourceAndInformationGrading":
				metadata.put("sourceAndInformationGrading", m.getValue());
				json.put(LEGACY_SOURCE_RELIABILITY, sourceGradingToInteger(m.getValue().substring(0, 1)));
				json.put(LEGACY_SOURCE_VALIDITY, informationGradingToInteger(m.getValue().substring(1)));
				break;
			default:
				metadata.put(m.getKey(), m.getValue());
			}
		}
		
		Instant sortDateInstant = getSortDate(md);
		if(sortDateInstant != null){
			metadata.put("report_sort_date", sortDateInstant);
		}

		json.put(getMetadataName(true), metadata);
	}

	private int informationGradingToInteger(String grading){
		try{
			return 6 - Integer.parseInt(grading);
		}catch(NumberFormatException nfe){
			getMonitor().warn("Unable to parse information grading", nfe);
			return 0;
		}
	}

	private int sourceGradingToInteger(String grading){
		List<String> gradings = Arrays.asList("f", "e", "d", "c", "b", "a");

		int i = gradings.indexOf(grading.toLowerCase());
		if(i == -1){
			i = 0;
		}

		return i;
	}
	
	private Instant getSortDate(Collection<Metadata> md){
		Map<String, String> mdValues = new HashMap<>();
		for(Metadata m : md){
			mdValues.put(m.getKey(), m.getValue());
		}
		
		for(String key : sortDateKeys){
			if(mdValues.containsKey(key)){
				Instant i = parseString(mdValues.get(key));
				if(i != null){
					return i;
				}
			}
		}
		return null;
	}
	
	private Instant parseString(String s){
		if(Strings.isNullOrEmpty(s)){
			return null;
		}
		
		for (DateFormat df : dateFormats) {
			Date d = null;

			df.setTimeZone(TimeZone.getTimeZone("UTC"));

			try {
				d = df.parse(s.replaceAll("(?i)sept([ \\.0-9])", "sep$1")); // Sept isn't correctly parsed by parser, but is commonly used in place of Sep
			} catch (ParseException pe) {
				continue;
			}
			
			return d.toInstant();
		}
		
		return null;
	}
}
