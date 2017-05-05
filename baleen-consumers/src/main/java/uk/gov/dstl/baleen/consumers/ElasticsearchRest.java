//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers;

import java.io.IOException;
import java.util.Map;

import org.apache.uima.fit.descriptor.ExternalResource;
import org.elasticsearch.common.xcontent.XContentBuilder;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.IndicesExists;
import io.searchbox.indices.mapping.PutMapping;
import uk.gov.dstl.baleen.consumers.utils.AbstractElasticsearchConsumer;
import uk.gov.dstl.baleen.resources.SharedElasticsearchRestResource;

/**
* Use the Elasticsearch REST API to save processed documents, using the schema defined in AbstractElasticsearchConsumer.
* 
* Because we use the REST API, this should be compatible with both Elasticsearch 1.x and Elasticsearch 2.x.
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
	
	@Override
	public boolean createIndex() {
		JestClient client = esrResource.getClient();
		
		try{
			JestResult result = client.execute(new IndicesExists.Builder(index).build());
			if(result.getResponseCode() != 200){
				client.execute(new CreateIndex.Builder(index).build());
				
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
			PutMapping putMapping = new PutMapping.Builder(index, type, mapping.string()).build();
			esrResource.getClient().execute(putMapping);
		}catch(IOException ioe){
			getMonitor().error("Unable to add mapping to index", ioe);
		}
	}

	@Override
	public void addDocument(String id, Map<String, Object> json) {
		try{
			Index doc = new Index.Builder(json).id(id).index(index).type(type).build();
			esrResource.getClient().execute(doc);
		}catch(IOException ioe){
			getMonitor().error("Couldn't persist document to Elasticsearch", ioe);
		}
	}

}