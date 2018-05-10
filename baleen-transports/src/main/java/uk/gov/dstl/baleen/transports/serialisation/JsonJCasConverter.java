// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.transports.serialisation;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.uima.jcas.JCas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import uk.gov.dstl.baleen.types.BaleenAnnotation;
import uk.gov.dstl.baleen.uima.UimaMonitor;
import uk.gov.dstl.baleen.uima.UimaSupport;

/** Class to serialise and deserialise the JCas to a JSON string. */
public class JsonJCasConverter {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private final JCasSerialiser serialiser;
  private final JCasDeserialiser deserialiser;

  private final MapLikeType mapLikeType =
      TypeFactory.defaultInstance().constructMapLikeType(Map.class, String.class, Object.class);

  /**
   * Construct a JsonJCasConverter using the given {@link UimaSupport}, {@link UimaMonitor} and,
   * optional, white and black lists to filter by.
   *
   * <p>NB: a null or empty filter list implies no filtering.
   *
   * @param monitor the {@link UimaMonitor} to use
   * @param whiteList given annotation classes (optional)
   * @param blackList given annotation classes (optional)
   */
  public JsonJCasConverter(
      final UimaMonitor monitor,
      final Collection<Class<? extends BaleenAnnotation>> whiteList,
      final Collection<Class<? extends BaleenAnnotation>> blackList) {
    serialiser = new JCasSerialiser(monitor, whiteList, blackList);
    deserialiser = new JCasDeserialiser(monitor, whiteList, blackList);
  }

  /**
   * Serialise the JCas to a JSON string
   *
   * @param jCas to serialise
   * @return a JSON string representation
   * @throws IOException if the serialisation cannot be performed
   */
  public String serialise(final JCas jCas) throws IOException {
    final Map<String, Object> map = serialiser.serialise(jCas);
    return OBJECT_MAPPER.writeValueAsString(map);
  }

  /**
   * Deserialise the given JSON string by populating the given JCas.
   *
   * @param jCas to populate
   * @param jsonString to deserialise
   * @return the populated JCas (for convenient method chaining)
   * @throws IOException if there is an error while deserialising.
   */
  public JCas deserialise(final JCas jCas, final String jsonString) throws IOException {
    final Map<String, Object> map = OBJECT_MAPPER.readValue(jsonString, mapLikeType);
    deserialiser.deseralize(jCas, map);
    return jCas;
  }
}
