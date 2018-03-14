// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.stats;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;

/**
 * Annotate entities using a user specified OpenNLP NER model
 *
 * <p>An OpenNLP model specified by the user is loaded into a NameFinderME object. This is then ran
 * over the document content and entities are extracted. These entities are then assigned to a user
 * defined type that inherits from the Entity type. Common properties, such as the value and
 * confidence, are set, but type specific properties are not set (e.g. currency for Money). If no
 * type is specified, a generic Entity is created.
 *
 * @baleen.javadoc
 */
public class OpenNLP extends BaleenAnnotator {

  public static final String MODEL_KEY = "namedEntityModel";

  private NameFinderME nameFinder;
  private Class<? extends Entity> et = null;

  /**
   * The model to use for entity extraction
   *
   * @baleen.config model.bin
   */
  public static final String PARAM_MODEL = "model";

  @ConfigurationParameter(name = PARAM_MODEL, defaultValue = "model.bin")
  private String model;

  /**
   * The entity type to annotate matches with
   *
   * @baleen.config uk.gov.dstl.baleen.types.semantic.Entity
   */
  public static final String PARAM_TYPE = "type";

  @ConfigurationParameter(
    name = PARAM_TYPE,
    defaultValue = "uk.gov.dstl.baleen.types.semantic.Entity"
  )
  private String type;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    try {
      et =
          TypeUtils.getEntityClass(
              type, JCasFactory.createJCas(TypeSystemSingleton.getTypeSystemDescriptionInstance()));
    } catch (UIMAException | BaleenException e) {
      throw new ResourceInitializationException(e);
    }

    try (InputStream modelIn = new FileInputStream(new File(model)); ) {
      nameFinder = new NameFinderME(new TokenNameFinderModel(modelIn));
    } catch (IOException e) {
      throw new ResourceInitializationException(e);
    }
  }

  @Override
  public void doProcess(JCas aJCas) throws AnalysisEngineProcessException {
    Collection<Sentence> sentences = JCasUtil.select(aJCas, Sentence.class);

    for (Sentence sentence : sentences) {

      List<WordToken> sentenceTokens = JCasUtil.selectCovered(WordToken.class, sentence);

      List<String> sentenceTokenContent = new ArrayList<String>();
      for (WordToken token : sentenceTokens) {
        sentenceTokenContent.add(token.getCoveredText());
      }

      Span[] names =
          nameFinder.find(
              (String[])
                  Arrays.copyOf(
                      sentenceTokenContent.toArray(), sentenceTokenContent.size(), String[].class));
      double[] probs = nameFinder.probs();
      for (int i = 0; i < names.length; i++) {
        Span name = names[i];

        Entity entity;
        try {
          entity = et.getConstructor(JCas.class).newInstance(aJCas);
        } catch (Exception e) {
          throw new AnalysisEngineProcessException(e);
        }

        entity.setConfidence((float) probs[i]);

        entity.setBegin(sentenceTokens.get(name.getStart()).getBegin());
        entity.setEnd(sentenceTokens.get(name.getEnd() - 1).getEnd());

        entity.setValue(entity.getCoveredText());

        addToJCasIndex(entity);
      }
    }

    nameFinder.clearAdaptiveData();
  }

  @Override
  public void doDestroy() {
    nameFinder = null;
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(Sentence.class, WordToken.class), ImmutableSet.of(et));
  }
}
