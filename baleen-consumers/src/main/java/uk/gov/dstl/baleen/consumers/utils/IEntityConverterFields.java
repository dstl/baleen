//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.utils;

/**
 * An interface to hold the field names for the Entity Converter
 * 
 * 
 */
public interface IEntityConverterFields {
	/**
	 * @return Name of the external ID field of the entity
	 */
	public String getExternalId();
	/**
	 * @return Name of the GeoJSON field of the entity
	 */
	public String getGeoJSON();
	/**
	 * @return Name of the history field of the entity
	 */
	public String getHistory();
	/**
	 * @return Name of the recordable field in the history object
	 */
	public String getHistoryRecordable();
	/**
	 * @return Name of the action field in the history object
	 */
	public String getHistoryAction();
	/**
	 * @return Name of the type field in the history object
	 */
	public String getHistoryType();
	/**
	 * @return Name of the parameters field in the history object
	 */
	public String getHistoryParameters();
	/**
	 * @return Name of the referrer field in the history object
	 */
	public String getHistoryReferrer();
	/**
	 * @return Name of the timestamp field in the history object
	 */
	public String getHistoryTimestamp();
}
