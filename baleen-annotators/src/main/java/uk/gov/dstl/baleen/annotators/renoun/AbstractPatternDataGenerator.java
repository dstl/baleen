// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.renoun;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.resources.data.ReNounFact;
import uk.gov.dstl.baleen.types.language.Dependency;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.uima.BaleenSentenceAnnotator;
import uk.gov.dstl.baleen.uima.grammar.DependencyGraph;
import uk.gov.dstl.baleen.uima.grammar.DependencyTree;

/**
 * An abstract annotator for the generation of ReNoun dependency patterns based on known facts.
 *
 * <p>The facts are provided by the implementation.
 *
 * <p>This annotator looks for the fact elements, the source, target and attribute, and, if found,
 * assumes the sentence is a statement of the fact and extract the minimal dependency tree
 * containing those facts. These are stored to another mongo collection.
 *
 * @baleen.javadoc
 * @see http://emnlp2014.org/papers/pdf/EMNLP2014038.pdf
 */
public abstract class AbstractPatternDataGenerator extends BaleenSentenceAnnotator {

  /**
   * Connection to Mongo
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedMongoResource
   */
  public static final String KEY_MONGO = SharedMongoResource.RESOURCE_KEY;

  @ExternalResource(key = KEY_MONGO)
  protected SharedMongoResource mongoResource;

  /**
   * The name of the Mongo collection to output to
   *
   * @baleen.config renoun_patterns
   */
  public static final String PARAM_OUPUT_COLLECTION = "ouputCollection";

  @ConfigurationParameter(name = PARAM_OUPUT_COLLECTION, defaultValue = "renoun_patterns")
  private String patternsCollection;

  /**
   * The name of the field in Mongo that contains the fact attribute
   *
   * @baleen.config value
   */
  public static final String PARAM_ATTRIBUTE_FIELD = "attributeField";

  @ConfigurationParameter(name = PARAM_ATTRIBUTE_FIELD, defaultValue = "value")
  private String attributeField;

  /**
   * The name of the field in Mongo that contains the target values
   *
   * @baleen.config targetValue
   */
  public static final String PARAM_OBJECT_FIELD = "objectField";

  @ConfigurationParameter(name = PARAM_OBJECT_FIELD, defaultValue = "targetValue")
  private String objectField;

  private Set<ReNounFact> facts;

  private MongoCollection<Document> patterns;
  private DependencyGraph graph;

  @Override
  public void initialize(UimaContext context) throws ResourceInitializationException {
    super.initialize(context);
    facts = initializeFacts();
    MongoDatabase db = mongoResource.getDB();
    patterns = db.getCollection(patternsCollection);
  }

  protected abstract Set<ReNounFact> initializeFacts();

  @Override
  protected void preExtract(JCas jCas) throws AnalysisEngineProcessException {
    graph = DependencyGraph.build(jCas);
  }

  @Override
  protected void postExtract(JCas jCas) throws AnalysisEngineProcessException {
    graph = null;
  }

  @Override
  protected void doProcessSentence(JCas jCas, Sentence sentence)
      throws AnalysisEngineProcessException {

    int beginOffset = sentence.getBegin();
    String text = sentence.getCoveredText();
    facts.forEach(fact -> checkForFact(jCas, fact, text, beginOffset));
  }

  private void checkForFact(JCas jCas, ReNounFact fact, String text, int beginOffset) {

    int indexOfSubject = text.indexOf(fact.getSubject());
    if (indexOfSubject < 0) {
      return;
    }
    int indexOfObject = text.indexOf(fact.getObject());
    if (indexOfObject < 0) {
      return;
    }
    int indexOfAttribute = text.indexOf(fact.getAttribute());
    if (indexOfAttribute < 0) {
      return;
    }

    List<WordToken> subjectTokens = getTokens(jCas, beginOffset, indexOfSubject, fact.getSubject());
    List<WordToken> objectTokens = getTokens(jCas, beginOffset, indexOfObject, fact.getObject());
    List<WordToken> attributeTokens =
        getTokens(jCas, beginOffset, indexOfAttribute, fact.getAttribute());

    Optional<WordToken> sourceNode = graph.getHeadNode(subjectTokens);
    Optional<WordToken> targetNode = graph.getHeadNode(objectTokens);
    Optional<WordToken> attributeNode = graph.getHeadNode(attributeTokens);

    if (!sourceNode.isPresent() || !targetNode.isPresent() || !attributeNode.isPresent()) {
      return;
    }

    Map<WordToken, String> leafNodes =
        ImmutableMap.of(
            sourceNode.get(),
            "subject",
            targetNode.get(),
            "object",
            attributeNode.get(),
            "attribute");

    DependencyTree minimalTree = graph.minimalTree(leafNodes.keySet());

    Map<String, String> idMap =
        leafNodes
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(e -> Long.toString(e.getKey().getInternalId()), Entry::getValue));

    minimalTree.delexicalize(idMap.keySet());
    minimalTree.mapIds(idMap);

    storePattern(fact, text, minimalTree);
  }

  private List<WordToken> getTokens(JCas jCas, int beginOffset, int index, String source) {
    return JCasUtil.selectCovered(
        jCas, WordToken.class, beginOffset + index, beginOffset + index + source.length());
  }

  private void storePattern(ReNounFact fact, String sentence, DependencyTree tree) {

    ReNounFact extractedFact =
        new ReNounFact(fact.getSubject(), fact.getAttribute(), fact.getObject());
    extractedFact.setPattern(tree.toString());
    extractedFact.setSentence(sentence);
    extractedFact.save(patterns);
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(Sentence.class, WordToken.class, Dependency.class), Collections.emptySet());
  }
}
