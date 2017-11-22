//Dstl (c) Crown Copyright 2017
//Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import uk.gov.dstl.baleen.consumers.utils.AbstractElasticsearchConsumer;
import uk.gov.dstl.baleen.resources.SharedElasticsearchRestResource;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
* Use the Elasticsearch REST API to save processed documents, using the schema defined in AbstractElasticsearchConsumer.
* 
* Because we use the REST API, this should be compatible with all versions of Elasticsearch.
* 
* @baleen.javadoc
*/
public class ElasticsearchRest extends AbstractElasticsearchConsumer {
	/**
	 * Connection to Elasticsearch
	 *
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedElasticsearchRestResource
	 */
	public static final String KEY_ELASTICSEARCH_REST = "elasticsearchRest";
	@ExternalResource(key = KEY_ELASTICSEARCH_REST)
	private SharedElasticsearchRestResource esrResource;

	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
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

	@Override
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

	@Override
	public void addDocument(String id, Map<String, Object> json) {
		try{
			HttpEntity entity = new StringEntity(objectMapper.writeValueAsString(json));

			esrResource.getClient().performRequest("PUT", "/"+index+"/"+type+"/"+id,
					Collections.emptyMap(),
					entity);
		}catch(IOException ioe){
			getMonitor().error("Couldn't persist document to Elasticsearch", ioe);
		}
	}

}