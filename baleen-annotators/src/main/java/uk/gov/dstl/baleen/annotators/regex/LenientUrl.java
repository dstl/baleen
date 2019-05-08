// NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import java.util.Collections;
import java.util.regex.Matcher;

import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.common.Url;

/**
 * Extract URLs in a more lenient fashion than the standard URL extractor, by making the http or
 * https optional.
 *
 * <p>The regex used to perform this extraction is: <code>
 * \b(?&lt;!@)(http:\/\/www\.|https:\/\/www\.|http:\/\/|https:\/\/)?[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?([?\/]\S*)?\b
 * </code>
 *
 * @baleen.javadoc
 */
public class LenientUrl extends AbstractRegexAnnotator<Url> {

  private static final String URL =
      "\\b(?<!@)(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?([?\\/]\\S*)?\\b";

  /** New instance. */
  public LenientUrl() {
    super(URL, false, 1.0);
  }

  @Override
  protected Url create(JCas jCas, Matcher matcher) {
    return new Url(jCas);
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(Collections.emptySet(), ImmutableSet.of(Url.class));
  }
}
