// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.stats;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Sets the document language using Language Detector library.
 *
 * <p>Uses the Language Detector library to identify the language of the document from a random
 * sample of N-grams. If the language can't be detected, then <i>x-unspecified</i> is returned.
 */
public class DocumentLanguage extends BaleenAnnotator {
  private LanguageDetector languageDetector;
  private TextObjectFactory textObjectFactory;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    try {
      List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();
      languageDetector =
          LanguageDetectorBuilder.create(NgramExtractors.standard())
              .withProfiles(languageProfiles)
              .build();

      textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
    } catch (IOException ioe) {
      throw new ResourceInitializationException(ioe);
    }
  }

  @Override
  public void doProcess(JCas aJCas) throws AnalysisEngineProcessException {
    TextObject textObject = textObjectFactory.forText(aJCas.getDocumentText());
    Optional<LdLocale> lang = languageDetector.detect(textObject);

    if (lang.isPresent()) {
      aJCas.setDocumentLanguage(lang.get().getLanguage());
    }
  }

  @Override
  public void doDestroy() {
    textObjectFactory = null;
    languageDetector = null;
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        Collections.emptySet(), ImmutableSet.of(DocumentAnnotation.class));
  }
}
