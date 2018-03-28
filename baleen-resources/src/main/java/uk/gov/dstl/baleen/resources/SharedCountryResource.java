// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import uk.gov.dstl.baleen.uima.BaleenResource;
import uk.gov.dstl.baleen.uima.UimaMonitor;

/**
 * A shared resource that provides access to JSON and GeoJSON files pertaining to country
 * information. Presently only supports some basic functions, but can be extended in the future to
 * allow for retrieval of more information from the JSON files.
 */
public class SharedCountryResource extends BaleenResource {

  private static Map<String, String> demonyms = null;
  private static Map<String, String> geoJson = null;
  private static Map<String, String> countryNames = null;

  private static final String PROPERTIES = "properties";

  @Override
  protected boolean doInitialize(ResourceSpecifier specifier, Map<String, Object> additionalParams)
      throws ResourceInitializationException {
    ObjectMapper mapper = new ObjectMapper();
    synchronized (this) {
      UimaMonitor monitor = getMonitor();
      if (demonyms == null) {
        monitor.info("Shared country resource initialising");
        JsonNode countriesJson = loadCountriesJson(mapper, monitor);
        countryNames = ImmutableMap.copyOf(loadCountryNames(countriesJson, monitor));
        demonyms = ImmutableMap.copyOf(loadDemonyms(countriesJson, monitor));
        geoJson = ImmutableMap.copyOf(loadCountriesGeoJson(mapper, monitor));
      } else {
        monitor.info("Shared country resource already initialised");
      }
    }
    return true;
  }

  private static JsonNode loadCountriesJson(ObjectMapper mapper, UimaMonitor uimaMonitor)
      throws ResourceInitializationException {
    try (InputStream is =
        SharedCountryResource.class.getResourceAsStream("countries/countries.json"); ) {
      return mapper.readTree(is);

    } catch (IOException ioe) {
      uimaMonitor.error("Unable to read nationalities from countries.json", ioe);
      throw new ResourceInitializationException(ioe);
    }
  }

  private static Map<String, String> loadCountryNames(JsonNode rootNode, UimaMonitor uimaMonitor) {

    Iterator<JsonNode> iter = rootNode.elements();

    Map<String, String> countryNames = new HashMap<>();

    while (iter.hasNext()) {
      JsonNode node = iter.next();
      String cca3 = getProperty(node, "cca3").toUpperCase();

      if (node == null || cca3.isEmpty()) {
        uimaMonitor.warn("Empty country code found - entry will be skipped");
        continue;
      }

      for (String name : getNames(node.path("name"))) {
        countryNames.put(name, cca3);
      }
    }
    uimaMonitor.info("{} country names read", countryNames.size());
    return countryNames;
  }

  private static Map<String, String> loadDemonyms(JsonNode rootNode, UimaMonitor uimaMonitor) {
    Iterator<JsonNode> iter = rootNode.elements();

    demonyms = new HashMap<>();

    while (iter.hasNext()) {
      JsonNode node = iter.next();

      String demonym = getProperty(node, "demonym").toLowerCase();
      String cca3 = getProperty(node, "cca3").toUpperCase();

      if (demonym.isEmpty() || cca3.isEmpty()) {
        uimaMonitor.warn("Empty demonym or country code found - entry will be skipped");
        continue;
      }

      demonyms.put(demonym, cca3);
    }
    uimaMonitor.info("{} nationalities read", demonyms.size());
    return demonyms;
  }

  private static Map<String, String> loadCountriesGeoJson(
      ObjectMapper mapper, UimaMonitor uimaMonitor) throws ResourceInitializationException {
    try (InputStream is =
        SharedCountryResource.class.getResourceAsStream("countries/countries.geojson"); ) {
      JsonNode rootNode = mapper.readTree(is);

      Iterator<JsonNode> iter = rootNode.path("features").elements();

      Map<String, String> geoJson = new HashMap<>();

      while (iter.hasNext()) {
        JsonNode node = iter.next();

        if (!node.has(PROPERTIES)) {
          uimaMonitor.warn("No properties found for entry - entry will be skipped");
          continue;
        }

        String cca3 = getProperty(node.path(PROPERTIES), "ISO_A3").toUpperCase();
        String geojson = getProperty(node, "geometry");

        if (geojson.isEmpty() || cca3.isEmpty()) {
          uimaMonitor.warn(
              "Empty country code or GeoJSON found - entry will not have GeoJSON information");
        } else if ("-99".equals(cca3)) {
          uimaMonitor.warn(
              "Generic country code -99 found - entry {} will not have GeoJSON information",
              getProperty(node.path(PROPERTIES), "ADMIN"));
        } else {
          geoJson.put(cca3, geojson);
        }
      }
      uimaMonitor.info("{} countries read", geoJson.size());
      return geoJson;
    } catch (IOException ioe) {
      uimaMonitor.error("Unable to read countries from countries.geojson", ioe);
      throw new ResourceInitializationException(ioe);
    }
  }

  private static String getProperty(JsonNode node, String propertyName) {
    if (node != null && node.has(propertyName)) {
      JsonNode property = node.get(propertyName);
      if (property == null) {
        return "";
      } else if (property.isValueNode()) {
        return node.get(propertyName).asText().trim();
      } else {
        return node.get(propertyName).toString().trim();
      }

    } else {
      return "";
    }
  }

  private static List<String> getNames(JsonNode node) {
    List<String> names = new ArrayList<>();

    names.addAll(node.findValuesAsText("common"));
    names.addAll(node.findValuesAsText("official"));

    return names.stream().filter(s -> !Strings.isNullOrEmpty(s)).collect(Collectors.toList());
  }

  /**
   * Return a map of all demonyms to country codes
   *
   * @return
   */
  public Map<String, String> getDemonyms() {
    return demonyms;
  }

  /**
   * Return the GeoJSON for a given country code
   *
   * @param countryCode
   * @return
   */
  public String getGeoJson(String countryCode) {
    return geoJson.get(countryCode.toUpperCase().trim());
  }

  /**
   * Returns a map of all the country names to country codes
   *
   * @return
   */
  public Map<String, String> getCountryNames() {
    return countryNames;
  }
}
