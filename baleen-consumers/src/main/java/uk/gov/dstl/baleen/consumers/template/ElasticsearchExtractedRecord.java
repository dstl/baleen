//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.template;

/**
 * A representation of an ExtractedRecord for Elasticsearch.
 * <p>
 * In ElasticSearch we store a document per record, so we need to also store the
 * sourceUri / externalId inside each extracted record document.
 * </p>
 */
public class ElasticsearchExtractedRecord extends ExtractedRecord {

	/** The external id. */
	private String externalId;

	/** The source uri. */
	private String sourceUri;

	/**
	 * Instantiates a new Elasticsearch extracted record.
	 */
	public ElasticsearchExtractedRecord() {
		// for reflective construction in Jackson
	}

	/**
	 * Instantiates a new Elasticsearch extracted record.
	 *
	 * @param externalId
	 *            the external id
	 * @param sourceUri
	 *            the source uri
	 * @param extractedRecord
	 *            the extracted record
	 */
	public ElasticsearchExtractedRecord(String externalId, String sourceUri, ExtractedRecord extractedRecord) {
		super(extractedRecord);
		this.externalId = externalId;
		this.sourceUri = sourceUri;
	}

	/**
	 * Gets the external id.
	 *
	 * @return the external id
	 */
	public String getExternalId() {
		return externalId;
	}

	/**
	 * Sets the external id.
	 *
	 * @param externalId
	 *            the new external id
	 */
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	/**
	 * Gets the source uri.
	 *
	 * @return the source uri
	 */
	public String getSourceUri() {
		return sourceUri;
	}

	/**
	 * Sets the source uri.
	 *
	 * @param sourceUri
	 *            the new source uri
	 */
	public void setSourceUri(String sourceUri) {
		this.sourceUri = sourceUri;
	}

}