//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.template;

import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Hold a collection of records for a given document
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MongoExtractedRecords {

	private String externalId;

	private String sourceUri;

	private Map<String, Collection<ExtractedRecord>> records;

	/**
	 * Default constructor
	 */
	public MongoExtractedRecords() {
		// for reflective construction in Jackson
	}

	/**
	 * Explicit constructor
	 */
	public MongoExtractedRecords(String id, String sourceUri, Map<String, Collection<ExtractedRecord>> records) {
		this.externalId = id;
		this.sourceUri = sourceUri;
		this.records = records;
	}

	/**
	 * Get external ID
	 */
	public String getExternalId() {
		return externalId;
	}

	/**
	 * Set external ID
	 */
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	/**
	 * Get source URI
	 */
	public String getSourceUri() {
		return sourceUri;
	}

	/**
	 * Set source URI
	 */
	public void setSourceUri(String sourceUri) {
		this.sourceUri = sourceUri;
	}

	/**
	 * Get records
	 */
	public Map<String, Collection<ExtractedRecord>> getRecords() {
		return records;
	}

	/**
	 * Set records
	 */
	public void setRecords(Map<String, Collection<ExtractedRecord>> records) {
		this.records = records;
	}

}