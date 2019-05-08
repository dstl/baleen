// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.odin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.clulab.struct.CorefChains;
import org.clulab.struct.CorefMention;

import scala.Option;
import scala.Some;
import scala.collection.JavaConversions;

import com.google.common.collect.Multimap;

import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.uima.utils.ReferentUtils;

/** Uses the jCas and a {@link SentenceFactory} to create {@link OdinDocument}s */
public class DocumentFactory {

  private final JCas jCas;
  private final SentenceFactory sentenceFactory;
  private final Map<Entity, List<Sentence>> entityIndex;
  private final Multimap<ReferenceTarget, Entity> referentMap;

  /**
   * Construct the document factory for the given jCas
   *
   * @param jCas to base the document on
   */
  public DocumentFactory(JCas jCas) {
    this(jCas, new SentenceFactory(jCas));
  }

  /**
   * Construct the document factory for the given jCas and {@link SentenceFactory}
   *
   * @param jCas to base the document on
   * @param sentenceFactory to use
   */
  public DocumentFactory(JCas jCas, SentenceFactory sentenceFactory) {
    this(
        jCas,
        JCasUtil.indexCovering(jCas, Entity.class, Sentence.class),
        ReferentUtils.createReferentMap(jCas, Entity.class),
        sentenceFactory);
  }

  /**
   * Construct the document factory for the given jCas, {@link SentenceFactory} and supporting
   * indexes.
   *
   * <p>Indexes much be for the given jCas, recommend using {@link #DocumentFactory(JCas)}
   *
   * @param jCas to base the document on
   * @param entityIndex index of entities to sentence
   * @param referentMap multi-map of reference targets to entities
   * @param sentenceFactory to use
   */
  public DocumentFactory(
      JCas jCas,
      Map<Entity, List<Sentence>> entityIndex,
      Multimap<ReferenceTarget, Entity> referentMap,
      SentenceFactory sentenceFactory) {
    this.jCas = jCas;
    this.entityIndex = entityIndex;
    this.referentMap = referentMap;
    this.sentenceFactory = sentenceFactory;
  }

  /** the {@link OdinDocument} that represents the jCas */
  public OdinDocument create() {
    OdinDocument document = new OdinDocument(sentenceFactory.create());
    document.text_$eq(Some.apply(jCas.getDocumentText()));
    int chainId = 0;
    List<CorefMention> mentions = new ArrayList<>();
    for (Entry<ReferenceTarget, Collection<Entity>> coref : referentMap.asMap().entrySet()) {
      for (Entity entity : coref.getValue()) {
        Collection<Sentence> collection = entityIndex.get(entity);
        if (!collection.isEmpty()) {
          OdinSentence sentence = document.findSentence(collection.iterator().next());
          mentions.add(sentence.corefMention(entity, chainId));
        }
      }
      chainId++;
    }

    CorefChains chains = CorefChains.apply(JavaConversions.iterableAsScalaIterable(mentions));
    document.coreferenceChains_$eq(Option.apply(chains));
    return document;
  }
}
