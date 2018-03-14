// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import java.util.Collections;
import java.util.regex.Matcher;

import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.common.DocumentReference;

/**
 * Annotate document references that start with document, letter or resolution followed by a number
 *
 * @baleen.javadoc
 */
public class DocumentNumber extends AbstractRegexAnnotator<DocumentReference> {

  // TODO: Allow users to specify document number pattern (and prefixes)
  private static final String DOCUMENT_REGEX =
      "(document|letter|resolution|executive order)( \\d+|s \\d+(, \\d+)*( )?(and \\d+)?)";

  /** New instance. */
  public DocumentNumber() {
    super(DOCUMENT_REGEX, false, 1.0);
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        Collections.emptySet(), ImmutableSet.of(DocumentReference.class));
  }

  @Override
  protected DocumentReference create(JCas jCas, Matcher matcher) {
    // TODO: Annotate each document separately
    return new DocumentReference(jCas);
  }
}
