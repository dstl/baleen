//Dstl (c) Crown Copyright 2017
//Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.template;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.resources.SharedElasticsearchRestResource;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A RecordConsumer that writes extracted records documents to Elasticsearch.
 * <p>
 * Unlike the MongoDB consumer that writes all of the records for a source
 * document as a single database document (with a records array), each record is
 * written as a separate document.
 * </p>
 * <p>
 * This stores the extracted records in an Elasticsearch index, specified using
 * the index parameter, as the Elasticsearch type specified in the type
 * parameter, into a shared Elasticsearch resource as supplied through the
 * elasticsearchRest parameter. Document IDs are, by default, a hash of the
 * document content but can be optionally configured to use the document source
 * URI by setting the contentHashAsId parameter to false.
 * </p>
 */
public class ElasticsearchTemplateRecordConsumer extends AbstractTemplateRecordConsumer {

	/**
	 * Connection to Elasticsearch
	 *
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedElasticsearchRestResource
	 */
	public static final String KEY_ELASTICSEARCH_REST = "elasticsearchRest";
	@ExternalResource(key = KEY_ELASTICSEARCH_REST)
	private SharedElasticsearchRestResource esrResource;

	/**
	 * The Elasticsearch index to use
	 *
	 * @baleen.config baleen_index
	 */
	public static final String PARAM_INDEX = "index";
	@ConfigurationParameter(name = PARAM_INDEX, defaultValue = "baleen_record_index")
	protected String index;

	/**
	 * The Elasticsearch type to use for documents inserted into the index
	 *
	 * @baleen.config baleen_record
	 */
	public static final String PARAM_TYPE = "type";
	@ConfigurationParameter(name = PARAM_TYPE, defaultValue = "baleen_record")
	protected String type;

	/**
	 * Should a hash of the content be used to generate the ID? If false, then a
	 * hash of the Source URI is used instead.
	 * 
	 * @baleen.config true
	 */
	public static final String PARAM_CONTENT_HASH_AS_ID = "contentHashAsId";
	@ConfigurationParameter(name = PARAM_CONTENT_HASH_AS_ID, defaultValue = "true")
	private boolean contentHashAsId;

	/**
	 * The object mapper.
	 */
	private ObjectMapper mapper;

	private static final String ES_PROPERTIES = "properties";
	private static final String ES_TYPE = "type";
	private static final String ES_TYPE_STRING = "string";
	private static final String ES_TYPE_NESTED = "nested";

	@Override
	protected void writeRecords(JCas jCas, String documentSourceName,
			Map<String, Collection<ExtractedRecord>> extractedRecords) throws AnalysisEngineProcessException {
		String externalId = getUniqueId(jCas);
		List<ElasticsearchExtractedRecord> elasticSearchRecords = extractedRecords.entrySet().stream()
				.flatMap(entry -> {
					String sourceUri = entry.getKey();
					return entry.getValue().stream()
							.map(r -> new ElasticsearchExtractedRecord(externalId, sourceUri, r));
				}).collect(Collectors.toList());

		for (ElasticsearchExtractedRecord elasticsearchExtractedRecord : elasticSearchRecords) {
			String json;
			try {
				json = mapper.writeValueAsString(elasticsearchExtractedRecord);
			} catch (JsonProcessingException e) {
				getMonitor().warn("Failed to serialise record for Elasticsearch - skipping", e);
				continue;
			}

			try{
				HttpEntity entity = new StringEntity(json);

				esrResource.getClient().performRequest("PUT", "/"+index+"/"+type+"/"+
								String.format("%s-%s", externalId, elasticsearchExtractedRecord.getName()),
						Collections.emptyMap(),
						entity);
			}catch(IOException ioe){
				getMonitor().error("Failed to index document in Elasticsearch for index " + index, ioe);
			}
		}
	}

	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		super.doInitialize(aContext);
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);

		boolean indexCreated = createIndex();
		if (indexCreated) {
			try {
				XContentBuilder createMappingObject = createMappingObject();
				addMapping(createMappingObject);
			} catch (IOException ioe) {
				getMonitor().error(
						"Unable to create mapping, you may get unexpected results in your Elasticsearch index", ioe);
			}
		}
	}

	/**
	 * Create an index in Elasticsearch. If necessary, this function should
	 * check whether a new index is required.
	 * 
	 * @return true if a new index has been created, false otherwise
	 */
	public boolean createIndex() {
		RestClient client = esrResource.getClient();

		try {
			Response r = client.performRequest("HEAD", "/" + index);

			if(r.getStatusLine().getStatusCode() != 200) {
				client.performRequest("PUT", "/" + index);

				return true;
			}
		}catch(IOException ioe){
			getMonitor().error("Unable to create index", ioe);
		}

		return false;
	}

	/**
	 * Add a mapping to Elasticsearch. This will only be called if a new index
	 * has been created
	 */
	public void addMapping(XContentBuilder mapping) {
		try{
			HttpEntity entity = new StringEntity(mapping.string());

			esrResource.getClient().performRequest("PUT", "/"+index+"/_mapping/"+type,
					Collections.emptyMap(),
					entity);
		}catch(IOException ioe){
			getMonitor().error("Unable to add mapping to index", ioe);
		}
	}

	/**
	 * Create a mapping for the new index
	 */
	private XContentBuilder createMappingObject() throws IOException {
		return XContentFactory.jsonBuilder()
				.startObject()
					.startObject(type)
						.startObject(ES_PROPERTIES)
							.startObject("externalId")
								.field(ES_TYPE, ES_TYPE_STRING)
								.endObject()
							.startObject("sourceUri")
								.field(ES_TYPE, ES_TYPE_STRING)
								.endObject()
							.startObject("kind")
								.field(ES_TYPE, ES_TYPE_STRING)
								.endObject()
							.startObject("name")
								.field(ES_TYPE, ES_TYPE_STRING)
								.endObject()
							.startObject("fields")
								.field(ES_TYPE, ES_TYPE_NESTED)
								.startObject(ES_PROPERTIES)
									.startObject("name")
										.field(ES_TYPE, ES_TYPE_STRING)
										.endObject()
									.startObject("value")
										.field(ES_TYPE, ES_TYPE_STRING)
										.endObject()
								.endObject()
							.endObject()
						.endObject()
					.endObject()
				.endObject();
	}

	/**
	 * Gets the unique id for a document (if contentHashAsId is true then as
	 * hash of the content is used, otherwise a hash of the source URI is used).
	 *
	 * @param jCas
	 *            the JCas
	 * @return the unique id
	 */
	private String getUniqueId(JCas jCas) {
		return ConsumerUtils.getExternalId(getDocumentAnnotation(jCas), contentHashAsId);
	}

}