// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.language;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.clulab.processors.Document;
import org.clulab.processors.clu.CluProcessor;

import com.google.common.collect.ImmutableSet;
import com.typesafe.config.ConfigFactory;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.odin.DocumentConverter;
import uk.gov.dstl.baleen.types.language.Dependency;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Extract language elements using Odin Extraction Framework.
 *
 * <p>
 */
public class OdinParser extends BaleenAnnotator {

  private CluProcessor processor;

  @Override
  public void initialize(UimaContext context) throws ResourceInitializationException {
    super.initialize(context);
    processor = new CluProcessor(ConfigFactory.load("cluprocessoropen"));
  }

  @Override
  public void doProcess(JCas jCas) throws AnalysisEngineProcessException {

    Document document = processor.annotate(jCas.getDocumentText(), false);

    DocumentConverter converter = new DocumentConverter(jCas, document);
    converter.convert();
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(), ImmutableSet.of(Sentence.class, WordToken.class, Dependency.class));
  }
}
