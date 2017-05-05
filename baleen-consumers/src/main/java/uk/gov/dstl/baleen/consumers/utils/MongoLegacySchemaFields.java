//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.utils;

/** Entity converter fields for the legacy Mongo consumer.
 * 
 *
 */
public class MongoLegacySchemaFields implements IEntityConverterFields {

	@Override
	public String getExternalId() {
		return "uniqueID";
	}

	@Override
	public String getGeoJSON() {
		return "geoJson";
	}

	@Override
	public String getHistory() {
		return null;
	}

	@Override
	public String getHistoryRecordable() {
		return null;
	}

	@Override
	public String getHistoryAction() {
		return null;
	}

	@Override
	public String getHistoryType() {
		return null;
	}

	@Override
	public String getHistoryParameters() {
		return null;
	}

	@Override
	public String getHistoryReferrer() {
		return null;
	}

	@Override
	public String getHistoryTimestamp() {
		return null;
	}
}
