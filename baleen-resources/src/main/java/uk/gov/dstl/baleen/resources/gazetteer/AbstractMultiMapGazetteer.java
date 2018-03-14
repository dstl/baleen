// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources.gazetteer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.resource.Resource;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * An abstract class to implement a gazetteer using a MultiMap as the backend.
 *
 * <p>Developers should provide a reloadValues() method, and override any existing methods they wish
 * to extend.
 *
 * <p>The following configuration parameters are expected/allowed:
 *
 * <ul>
 *   <li><b>caseSensitive</b> - If false, then all values are lower cased; otherwise the original
 *       casing is kept and comparisons are done case sensitively. Defaults to false
 * </ul>
 *
 * @param <T> The type used for the ID of terms
 */
public abstract class AbstractMultiMapGazetteer<T> implements IGazetteer {
  private Multimap<T, String> idToValues = ArrayListMultimap.create();
  private Map<String, T> valueToId = new HashMap<>();

  public static final String CONFIG_CASE_SENSITIVE = "caseSensitive";
  public static final Boolean DEFAULT_CASE_SENSITIVE = false;

  protected boolean caseSensitive = DEFAULT_CASE_SENSITIVE;

  @Override
  public void init(Resource connection, Map<String, Object> config) throws BaleenException {
    caseSensitive = false;
    if (config.containsKey(CONFIG_CASE_SENSITIVE)
        && "true".equalsIgnoreCase(config.get(CONFIG_CASE_SENSITIVE).toString())) {
      caseSensitive = true;
    }

    reloadValues();
  }

  @Override
  public String[] getValues() {
    return valueToId.keySet().toArray(new String[0]);
  }

  @Override
  public boolean hasValue(String key) {
    return valueToId.containsKey(caseSensitive ? key : key.toLowerCase());
  }

  @Override
  public String[] getAliases(String key) {
    String val = caseSensitive ? key : key.toLowerCase();
    T id = valueToId.get(val);

    return idToValues
        .get(id)
        .stream()
        .filter(s -> !s.equals(val))
        .toArray(size -> new String[size]);
  }

  @Override
  public Map<String, Object> getAdditionalData(String key) {
    return Collections.emptyMap();
  }

  @Override
  public void destroy() {
    idToValues = null;
    valueToId = null;
  }

  protected void reset() {
    valueToId.clear();
    idToValues.clear();
  }

  protected void addTerm(T id, String value) {
    String val = value;
    if (!caseSensitive) {
      val = value.toLowerCase();
    }

    valueToId.put(val, id);
    idToValues.put(id, val);
  }

  protected T getId(String value) {
    return valueToId.get(caseSensitive ? value : value.toLowerCase());
  }
}
