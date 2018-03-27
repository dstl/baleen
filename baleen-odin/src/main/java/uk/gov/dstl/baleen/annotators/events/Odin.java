// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.events;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.clulab.odin.ExtractorEngine;
import org.clulab.odin.Mention;
import org.clulab.processors.Document;

import scala.collection.Seq;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.odin.DocumentFactory;
import uk.gov.dstl.baleen.odin.EventFactory;
import uk.gov.dstl.baleen.odin.OdinConfigurationProcessor;
import uk.gov.dstl.baleen.odin.TaxonomyFactory;
import uk.gov.dstl.baleen.types.language.Paragraph;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Event;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Temporal;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Extract events using Odin Event Extraction Framework.
 *
 * <p>Rules are provided by a yaml file and configured with the <code>rules</code> parameter. For
 * full information on the rule language see: <a
 * href="https://arxiv.org/pdf/1509.07513.pdf">https://arxiv.org/pdf/1509.07513.pdf</a>
 *
 * <p>The Baleen Entity and Event TypeSystem are added to the taxonomy automatically using the
 * simple names. Tagging rules are also added so types can be used in rules. The root types added
 * can be configured with the <code>types</code> parameter.
 */
public class Odin extends BaleenAnnotator {

  /**
   * The path to the rules file.
   *
   * @baleen.config
   */
  public static final String PARAM_RULES = "rules";

  @ConfigurationParameter(name = PARAM_RULES, mandatory = true)
  private String rulesFilePath;

  /**
   * The root types added to the taxonomy.
   *
   * @baleen.config Entity, Event
   */
  public static final String PARAM_TYPE_NAMES = "types";

  @ConfigurationParameter(
    name = PARAM_TYPE_NAMES,
    defaultValue = {"Entity", "Event"}
  )
  private String[] includedTypeNames;

  private ExtractorEngine ee;

  @Override
  public void initialize(UimaContext context) throws ResourceInitializationException {
    super.initialize(context);

    String rules;
    try {
      rules = readFile();

      TaxonomyFactory taxonomyFactory = new TaxonomyFactory(includedTypeNames);
      OdinConfigurationProcessor ruleProcessor =
          new OdinConfigurationProcessor(taxonomyFactory.create(), rules);
      ee = ExtractorEngine.fromRules(ruleProcessor.process());

    } catch (UIMAException | IOException e) {
      throw new ResourceInitializationException(e);
    }
  }

  @Override
  public void doProcess(JCas jCas) throws AnalysisEngineProcessException {

    Document document = new DocumentFactory(jCas).create();

    Seq<Mention> mentions = ee.extractFrom(document);

    new EventFactory(jCas).create(mentions);
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(
            Entity.class, Sentence.class, Paragraph.class, Temporal.class, Location.class),
        ImmutableSet.of(Event.class));
  }

  private String readFile() throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(rulesFilePath));
    return new String(encoded, Charset.defaultCharset());
  }
}
