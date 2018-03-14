// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import java.io.File;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Use part of the filename as the document type, using a regular expression to extract the correct
 * part.
 *
 * <p>Any leading or trailing whitespace from the type name is trimmed.
 *
 * @baleen.javadoc
 */
public class DocumentTypeByFilename extends BaleenAnnotator {

  /**
   * The pattern to match filenames against. By default, set's the document type to the file
   * extension.
   *
   * @baleen.config .*\\.([a-z0-9]{2,4})
   */
  public static final String PARAM_PATTERN = "pattern";

  @ConfigurationParameter(name = PARAM_PATTERN, defaultValue = ".*\\.([a-z0-9]{2,4})")
  private String pattern;

  private Pattern typePattern;

  /**
   * The regex group to use as the type
   *
   * @baleen.config 1
   */
  public static final String PARAM_GROUP = "group";

  @ConfigurationParameter(name = PARAM_GROUP, defaultValue = "1")
  private Integer group;

  /**
   * Is the regular expression case sensitive?
   *
   * @baleen.config false
   */
  public static final String PARAM_CASE_SENSITIVE = "caseSensitive";

  @ConfigurationParameter(name = PARAM_CASE_SENSITIVE, defaultValue = "false")
  private boolean caseSensitive = false;

  /**
   * The default value to use if the filename doesn't match the regex
   *
   * @baleen.config
   */
  public static final String PARAM_DEFAULT = "default";

  @ConfigurationParameter(name = PARAM_DEFAULT, defaultValue = "")
  private String defaultType;

  /**
   * Should the extracted type be lower-cased? This will provide some level of normalisation across
   * types
   *
   * @baleen.config true
   */
  public static final String PARAM_LOWER_CASE = "lowerCase";

  @ConfigurationParameter(name = PARAM_LOWER_CASE, defaultValue = "true")
  private boolean lowerCase = true;

  /**
   * An optional prefix to add to the type
   *
   * @baleen.config
   */
  public static final String PARAM_PREFIX = "prefix";

  @ConfigurationParameter(name = PARAM_PREFIX, defaultValue = "")
  private String prefix;

  @Override
  public void doInitialize(UimaContext context) throws ResourceInitializationException {
    if (caseSensitive) {
      typePattern = Pattern.compile(pattern);
    } else {
      typePattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }
  }

  @Override
  public void doProcess(JCas aJCas) throws AnalysisEngineProcessException {
    DocumentAnnotation da = getDocumentAnnotation(aJCas);

    File f = new File(da.getSourceUri());

    String type = defaultType;

    Matcher m = typePattern.matcher(f.getName());
    if (m.matches()) {
      type = m.group(group);
    }

    if (lowerCase) type = type.toLowerCase();

    da.setDocType(prefix + type.trim());
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        Collections.emptySet(), ImmutableSet.of(DocumentAnnotation.class));
  }
}
