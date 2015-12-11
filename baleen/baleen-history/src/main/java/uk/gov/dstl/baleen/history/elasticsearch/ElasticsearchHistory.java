//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.history.elasticsearch;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.history.HistoryEvent;
import uk.gov.dstl.baleen.core.history.memory.AbstractCachingBaleenHistory;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.history.utils.HistoryModule;
import uk.gov.dstl.baleen.resources.SharedElasticsearchResource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A history implementation which backs off to Elasticsearch.
 *
 * The implementation uses Elasticsearch as a data store. Events are collected
 * in memory and then ON CLOSE the data is persisted to ES. If the document is
 * requested again (and is not in the local cache) then the history is queried
 * from Elasticsearch.
 *
 * Thus the in memory and the ES history are not necessarily in sync (though this
 * should make no difference in Baleen's use case).
 *
 * You must ensure that close is called to persist the data to ES.
 *
 * Use the history.esIndex and history.esType to configure where the data is
 * stored in Elasticsearch.
 *
 * 
 * @baleen.javadoc
 */
public class ElasticsearchHistory extends AbstractCachingBaleenHistory<ElasticsearchDocumentHistory> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchHistory.class);

	/**
	 * Connection to Elasticsearch
	 * 
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedElasticsearchResource
	 */
	public static final String KEY_ELASTICSEARCH = "elasticsearch";
	@ExternalResource(key = KEY_ELASTICSEARCH, mandatory=true)
	private SharedElasticsearchResource elasticsearch;

	/**
	 * The Elasticsearch index to write history to
	 * 
	 * @baleen.config history
	 */
	public static final String PARAM_INDEX = "history.esIndex";
	@ConfigurationParameter(name = PARAM_INDEX,  defaultValue = "history")
	private String esIndex;

	/**
	 * The type to use when writing history to Elasticsearch
	 * 
	 * @baleen.config event
	 */
	public static final String PARAM_TYPE = "history.esType";
	@ConfigurationParameter(name = PARAM_TYPE, defaultValue = "event")
	private String esType;

	private ObjectMapper mapper;

	/**
	 * New instance, used by UimaFit.
	 *
	 */
	public ElasticsearchHistory() {
		// Do nothing
	}

	/**
	 * New instance for use with UimaFit DI, eg for testing.
	 *
	 * @param elasticsearch
	 *            the Elasticsearch resource
	 */
	public ElasticsearchHistory(SharedElasticsearchResource elasticsearch) {
		this.elasticsearch = elasticsearch;
	}

	@Override
	protected void initialize() throws BaleenException {
		super.initialize();

		mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.registerModule(new HistoryModule());

		// It might be worth setting the mapping explicitly here, but that will
		// depend on the specific events (which might be extended outside core).
		// So currently we trust ES to do the right thing.
	}

	@Override
	public void closeHistory(String documentId) {
		ElasticsearchDocumentHistory dh = getCachedHistoryIfPresent(documentId);

		if (dh == null) {
			LOGGER.warn("Attempt to close a document {} which is not in cache, thus can't be persisted", documentId);
			return;
		}

		try {
			byte[] source = mapper.writeValueAsBytes(new ESHistory(documentId, dh.getAllHistory()));

			new IndexRequestBuilder(elasticsearch.getClient()).setIndex(documentId).setIndex(esIndex).setType(esType)
					.setId(documentId).setSource(source).get();

		} catch (JsonProcessingException e) {
			LOGGER.warn("Unable to convert history to source, so can't be persisted {}", documentId, e);
		}

		super.closeHistory(documentId);

	}

	@Override
	protected ElasticsearchDocumentHistory createNewDocumentHistory(String documentId) {
		return new ElasticsearchDocumentHistory(this, documentId);
	}

	@Override
	protected ElasticsearchDocumentHistory loadExistingDocumentHistory(String documentId) throws BaleenException {
		try {
			GetResponse response = new GetRequestBuilder(elasticsearch.getClient()).setId(documentId).setIndex(esIndex)
					.setType(esType).get();

			if (!response.isExists() || response.isSourceEmpty()) {
				// If we don't have any data, then let parent implementation create a new history
				return null;
			} else {
				ESHistory esh = mapper.readValue(response.getSourceAsBytes(), ESHistory.class);
				if(esh == null){
					return new ElasticsearchDocumentHistory(this, documentId, new LinkedBlockingDeque<HistoryEvent>(
						Collections.emptyList()));
				}else{
					return new ElasticsearchDocumentHistory(this, documentId, new LinkedBlockingDeque<HistoryEvent>(
						esh.getEvents()));
				}
			}
		} catch (IOException e) {
			throw new BaleenException(e);
		}
	}

}
