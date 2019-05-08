// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.gazetteer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.gazetteer.helpers.AbstractAhoCorasickAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.resources.SharedFileResource;
import uk.gov.dstl.baleen.resources.gazetteer.FileGazetteer;
import uk.gov.dstl.baleen.resources.gazetteer.IGazetteer;

/**
 * Generic file-backed RadixTree Gazetteer annotator, that will use a file based gazetteer to find
 * and annotate entities.
 *
 * @baleen.javadoc
 */
public class File extends AbstractAhoCorasickAnnotator {

  /**
   * Connection to File Gazetteer
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedFileResource
   */
  public static final String KEY_FILE = "fileGazetteer";

  @ExternalResource(key = KEY_FILE)
  private SharedFileResource fileResource;

  /**
   * The file, which is expected to be a line separated gazetteer with aliases comma-separated (by
   * default) on the same line, to use as the gazetteer
   *
   * @baleen.config gazetteer.txt
   */
  public static final String PARAM_FILE_NAME = "fileName";

  @ConfigurationParameter(name = PARAM_FILE_NAME, defaultValue = "gazetteer.txt")
  private String fileName;

  /**
   * An alias term separator string that will override the "," default value
   *
   * @baleen.config ,
   */
  public static final String PARAM_TERM_SEPARATOR = "termSeparator";

  @ConfigurationParameter(name = PARAM_TERM_SEPARATOR, defaultValue = ",")
  private String termSeparator;

  @Override
  public IGazetteer configureGazetteer() throws BaleenException {
    Map<String, Object> config = new HashMap<>();
    config.put(FileGazetteer.CONFIG_CASE_SENSITIVE, caseSensitive);
    config.put(FileGazetteer.CONFIG_FILE, fileName);
    config.put(FileGazetteer.CONFIG_TERM_SEPARATOR, termSeparator);

    IGazetteer gaz = new FileGazetteer();
    gaz.init(fileResource, config);

    return gaz;
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(entityType));
  }
}
