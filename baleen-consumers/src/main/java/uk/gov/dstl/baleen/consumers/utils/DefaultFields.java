// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.utils;

/** Default implementation of IEntityConverterFields with sensible defaults for all of the names */
public class DefaultFields implements IEntityConverterFields {
  @Override
  public String getExternalId() {
    return "externalId";
  }

  @Override
  public String getHistory() {
    return "history";
  }

  @Override
  public String getGeoJSON() {
    return "geoJson";
  }

  @Override
  public String getHistoryRecordable() {
    return "id";
  }

  @Override
  public String getHistoryAction() {
    return "action";
  }

  @Override
  public String getHistoryType() {
    return "type";
  }

  @Override
  public String getHistoryParameters() {
    return "params";
  }

  @Override
  public String getHistoryReferrer() {
    return "ref";
  }

  @Override
  public String getHistoryTimestamp() {
    return "timestamp";
  }

  @Override
  public String getType() {
    return "type";
  }
}
