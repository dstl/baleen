//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers;

import java.util.Map;

import org.apache.uima.fit.descriptor.ExternalResource;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;

import uk.gov.dstl.baleen.consumers.utils.AbstractElasticsearchConsumer;
import uk.gov.dstl.baleen.resources.SharedElasticsearchResource;

/**
 * Output processed CAS object into Elasticsearch 2, using the schema defined in AbstractElasticsearchConsumer
 *
 * @baleen.javadoc
 */
public class Elasticsearch extends AbstractElasticsearchConsumer {
	/**
	 * Connection to Elasticsearch
	 *
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedElasticsearchResource
	 */
	public static final String KEY_ELASTICSEARCH = "elasticsearch";
	@ExternalResource(key = KEY_ELASTICSEARCH)
	private SharedElasticsearchResource esResource;


	@Override
	public boolean createIndex() {
		if(!esResource.getClient().admin().indices().exists(Requests.indicesExistsRequest(index)).actionGet().isExists()){
			esResource.getClient().admin().indices()
				.create(Requests.createIndexRequest(index))
				.actionGet();
			
			return true;
		}
		
		return false;
	}

	@Override
	public void addMapping(XContentBuilder mapping) {
		esResource.getClient().admin().indices()
			.preparePutMapping(index)
			.setType(type)
			.setSource(mapping)
			.execute().actionGet();
	}

	@Override
	public void addDocument(String id, Map<String, Object> json) {
		try{
			esResource.getClient().prepareIndex(index, type, id).setSource(json).execute().actionGet();
		}catch(ElasticsearchException ee){
			getMonitor().error("Couldn't persist document to Elasticsearch", ee);
		}
	}
}
