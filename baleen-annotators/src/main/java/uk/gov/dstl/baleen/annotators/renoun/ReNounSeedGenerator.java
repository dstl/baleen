// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.renoun;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.resources.data.ReNounFact;
import uk.gov.dstl.baleen.types.semantic.Entity;

/**
 * An annotator for the generation of ReNoun seed dependency patterns based on a single known fact.
 *
 * <p>The fact provided by default is the (Google, CEO, Larry Page) fact from the paper.
 *
 * <p>This annotator looks for the fact elements, the source, target and attribute, and, if found,
 * assumes the sentence is a statement of the fact and extract the minimal dependency tree
 * containing those facts. These are stored to another mongo collection.
 *
 * <p>This annotator can be used to create the seed patters for the ReNoun system as the seed
 * patterns should be tuned to the dependency parser. This should be run in a simple pipeline with
 * the WordToken, PartOfSpeach and Dependency annotators. For example,
 *
 * <pre>
 * mongo:
 *  db: baleen-renoun
 *
 * # Supply the default document of fact sentences
 * collectionreader:
 * class: renoun.ReNounSeedDocument
 *
 * annotators:
 * # Ensure the language pasring is done in the pipeline
 * - language.OpenNLP
 * - language.MaltParser
 *
 * # ReNoun Seed Fact Extraction
 * - class: renoun.ReNounSeedGenerator
 *
 * # Save relations to Mongo
 * consumers:
 * - class: MongoRelations
 * collection: seeds
 * </pre>
 *
 * @baleen.javadoc
 * @see http://emnlp2014.org/papers/pdf/EMNLP2014038.pdf
 */
public class ReNounSeedGenerator extends AbstractPatternDataGenerator {

  /**
   * The name of the fact subject
   *
   * @baleen.config sourceValue
   */
  public static final String PARAM_SUBJECT = "subject";

  @ConfigurationParameter(name = PARAM_SUBJECT, defaultValue = "Google")
  private String subject;
  /**
   * The name of the fact attribute
   *
   * @baleen.config value
   */
  public static final String PARAM_ATTRIBUTE = "attribute";

  @ConfigurationParameter(name = PARAM_ATTRIBUTE, defaultValue = "CEO")
  private String attribute;

  /**
   * The name of the fact value
   *
   * @baleen.config targetValue
   */
  public static final String PARAM_OBJECT = "object";

  @ConfigurationParameter(name = PARAM_OBJECT, defaultValue = "Larry Page")
  private String object;

  @Override
  protected Set<ReNounFact> initializeFacts() {
    return ImmutableSet.of(new ReNounFact(subject, attribute, object));
  }

  @Override
  public void process(JCas jCas) throws AnalysisEngineProcessException {

    addEntities(jCas, subject);
    addEntities(jCas, attribute);

    super.process(jCas);
  }

  void addEntities(JCas jCas, String match) {
    Matcher matcher = Pattern.compile(match).matcher(jCas.getDocumentText());
    while (matcher.find()) {
      Entity a = new Entity(jCas);
      a.setBegin(matcher.start());
      a.setEnd(matcher.end());
      a.setValue(match);
      addToJCasIndex(a);
    }
  }
}
