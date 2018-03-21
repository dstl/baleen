// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources.gazetteer;

import java.util.List;
import java.util.Map;

import org.apache.uima.resource.Resource;

import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.exceptions.BaleenException;

/** Read list from configuration to create a simple gazetteer */
public class ListGazetteer extends AbstractMultiMapGazetteer<Integer> {
  public static final String CONFIG_TERMS = "terms";
  public static final String CONFIG_TERM_SEPARATOR = "termSeparator";

  private List<String> terms;
  private String termSeparator = ",";

  /**
   * Configure a new instance of ListGazetteer. The following config parameters are
   * expected/allowed:
   *
   * <ul>
   *   <li><b>terms</b> - List of the terms to use for the gazetteer
   *   <li><b>termSeparator</b> - The string that separates aliases of the same entity on a single
   *       line in the gazetteer. Defaults to ","
   * </ul>
   *
   * @param connection - not required
   * @param config A map of additional configuration options
   */
  @Override
  @SuppressWarnings("unchecked")
  public void init(Resource connection, Map<String, Object> config) throws BaleenException {
    if (config.containsKey(CONFIG_TERM_SEPARATOR)) {
      termSeparator = config.get(CONFIG_TERM_SEPARATOR).toString();
    }
    terms = ImmutableList.copyOf((Iterable<String>) config.get(CONFIG_TERMS));
    super.init(connection, config);
  }

  @Override
  public void reloadValues() throws BaleenException {
    reset();

    int lineNumber = 0;
    for (String line : terms) {
      lineNumber++;
      if (line.trim().isEmpty()) {
        continue;
      }

      if (!caseSensitive) {
        line = line.toLowerCase();
      }

      String[] termsArray = line.split(termSeparator);
      for (String t : termsArray) {
        if (t.trim().isEmpty()) {
          continue;
        }

        addTerm(lineNumber, t.trim());
      }
    }
  }
}
