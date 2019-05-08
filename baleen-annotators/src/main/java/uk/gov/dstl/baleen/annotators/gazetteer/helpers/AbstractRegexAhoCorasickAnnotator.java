// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.gazetteer.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.gazetteer.IGazetteer;
import uk.gov.dstl.baleen.types.BaleenAnnotation;
import uk.gov.dstl.baleen.uima.data.TextBlock;

/**
 * Abstract class that builds on AbstractAhoCorasickAnnotator, but rather than searching the entire
 * document for gazetteer matches, it uses a regular expression to find potential matches and then
 * checks to see whether they appear in the Gazetteer.
 *
 * @baleen.javadoc
 */
public abstract class AbstractRegexAhoCorasickAnnotator extends AbstractAhoCorasickAnnotator {

  /**
   * The regular expression to check against
   *
   * @baleen.config \\b\\w*\\b
   */
  public static final String PARAM_REGEX = "regex";

  @ConfigurationParameter(name = PARAM_REGEX, defaultValue = "\\b\\w*\\b")
  protected String regex;

  Pattern regexPattern;

  @Override
  public abstract IGazetteer configureGazetteer() throws BaleenException;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);

    if (caseSensitive) {
      regexPattern = Pattern.compile(regex);
    } else {
      regexPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }
  }

  @Override
  public void doProcessTextBlock(TextBlock block) throws AnalysisEngineProcessException {
    Map<String, List<BaleenAnnotation>> entities = new HashMap<>();

    Matcher m = regexPattern.matcher(block.getCoveredText());
    while (m.find()) {
      String csValue = caseSensitive ? m.group() : m.group().toLowerCase();
      if (gazetteer.hasValue(csValue)) {
        try {
          BaleenAnnotation ent = createEntity(block, m.start(), m.end(), m.group(), csValue);

          List<String> aliases = new ArrayList<>(Arrays.asList(gazetteer.getAliases(csValue)));
          aliases.add(csValue);

          String key = generateKey(aliases);

          List<BaleenAnnotation> groupEntities =
              entities.containsKey(key) ? entities.get(key) : new ArrayList<>();
          groupEntities.add(ent);
          entities.put(key, groupEntities);
        } catch (Exception e) {
          getMonitor()
              .error(
                  "Unable to create entity of type '{}' for value '{}'",
                  entityType.getName(),
                  m.group(),
                  e);
          continue;
        }
      }
    }

    createReferenceTargets(block, entities.values());
  }
}
