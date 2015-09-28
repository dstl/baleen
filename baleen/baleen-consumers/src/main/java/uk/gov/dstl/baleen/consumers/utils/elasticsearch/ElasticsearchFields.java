//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers.utils.elasticsearch;

import uk.gov.dstl.baleen.consumers.utils.DefaultFields;

/** Entity converter fields for the Elasticsearch consumer.
 * 
 */
public class ElasticsearchFields extends DefaultFields{
	private boolean legacy = false;
	
	/**
	 * Default constructor, which will use the new schema
	 */
	public ElasticsearchFields(){
		this(false);
	}
	
	/**
	 * Constructor allowing choice of new or legacy schema
	 * 
	 * @param legacy
	 */
	public ElasticsearchFields(boolean legacy){
		this.legacy = legacy;
	}
	
	@Override
	public String getExternalId() {
		return legacy ? "uniqueID" : "externalId";
	}

	@Override
	public String getHistory() {
		return legacy ? null : "history";
	}
}
