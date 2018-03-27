// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.odin;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.uima.cas.Type;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.clulab.struct.DirectedGraph;
import org.clulab.struct.Edge;
import org.clulab.struct.GraphMap;

import scala.Option;
import scala.collection.JavaConversions;

import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.annotators.language.MaltParser;
import uk.gov.dstl.baleen.types.language.Dependency;
import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordLemma;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;

/** A factory to create Odin {@link Sentence}s from the given {@link JCas}. */
public class SentenceFactory {

  /** The string used by Odin for missing values. */
  protected static final String MISSING_VALUE = OdinSentence.MISSING_VALUE;

  private final Map<uk.gov.dstl.baleen.types.language.Sentence, Collection<WordToken>> indexWords;
  private final Map<WordToken, Collection<Entity>> indexEntities;
  private final Map<WordToken, Collection<PhraseChunk>> indexChunks;
  private final Map<uk.gov.dstl.baleen.types.language.Sentence, Collection<Dependency>>
      indexDependency;

  /**
   * Construct the sentence factory for the given jCas.
   *
   * @param jCas to create senteces from
   */
  public SentenceFactory(JCas jCas) {
    this(
        JCasUtil.indexCovered(
            jCas, uk.gov.dstl.baleen.types.language.Sentence.class, WordToken.class),
        JCasUtil.indexCovering(jCas, WordToken.class, Entity.class),
        JCasUtil.indexCovering(jCas, WordToken.class, PhraseChunk.class),
        JCasUtil.indexCovered(
            jCas, uk.gov.dstl.baleen.types.language.Sentence.class, Dependency.class));
  }

  /**
   * Detailed constructor, with all indexes from the jCas prepared.
   *
   * @param indexWords sentence to word token index
   * @param indexEntities wordToken to entity index
   * @param indexChunks word token to phrase chunk index
   * @param indexDependency sentence to dependency index
   */
  protected SentenceFactory(
      Map<uk.gov.dstl.baleen.types.language.Sentence, Collection<WordToken>> indexWords,
      Map<WordToken, Collection<Entity>> indexEntities,
      Map<WordToken, Collection<PhraseChunk>> indexChunks,
      Map<uk.gov.dstl.baleen.types.language.Sentence, Collection<Dependency>> indexDependency) {
    this.indexWords = indexWords;
    this.indexEntities = indexEntities;
    this.indexChunks = indexChunks;
    this.indexDependency = indexDependency;
  }

  /**
   * Create the Odin sentences for the JCas.
   *
   * @return the Odin sentences of the jCas
   */
  public List<OdinSentence> create() {

    List<Entry<Sentence, Collection<WordToken>>> entrySet =
        indexWords
            .entrySet()
            .stream()
            .sorted(Comparator.comparing(e -> e.getKey().getBegin()))
            .collect(toList());

    List<OdinSentence> sentences = new ArrayList<>();
    for (Entry<uk.gov.dstl.baleen.types.language.Sentence, Collection<WordToken>> e : entrySet) {
      sentences.add(create(sentences.size(), e.getKey(), e.getValue()));
    }

    return sentences;
  }

  private OdinSentence create(
      int index, uk.gov.dstl.baleen.types.language.Sentence key, Collection<WordToken> value) {

    List<WordToken> tokens = new ArrayList<>(value);
    tokens.sort(Comparator.comparing(WordToken::getBegin));

    tokens.stream().map(WordToken::getCoveredText).collect(toList()).toArray(new String[0]);

    String[] words =
        tokens.stream().map(WordToken::getCoveredText).collect(toList()).toArray(new String[0]);
    int[] startOffsets = value.stream().map(WordToken::getBegin).mapToInt(i -> i).toArray();
    int[] endOffsets = value.stream().map(WordToken::getEnd).mapToInt(i -> i).toArray();

    List<Optional<Entity>> entities = value.stream().map(this::getEntity).collect(toList());

    OdinSentence odinSentence =
        new OdinSentence(index, key, tokens, entities, words, startOffsets, endOffsets);

    odinSentence.tags_$eq(
        Option.apply(
            value
                .stream()
                .map(WordToken::getPartOfSpeech)
                .collect(toList())
                .toArray(new String[0])));
    odinSentence.lemmas_$eq(
        Option.apply(value.stream().map(this::getLemma).collect(toList()).toArray(new String[0])));
    odinSentence.entities_$eq(
        Option.apply(
            value.stream().map(this::getEntityName).collect(toList()).toArray(new String[0])));
    odinSentence.norms_$eq(
        Option.apply(value.stream().map(this::getNorm).collect(toList()).toArray(new String[0])));
    odinSentence.chunks_$eq(
        Option.apply(value.stream().map(this::getChunks).collect(toList()).toArray(new String[0])));

    odinSentence.setDependencies(GraphMap.UNIVERSAL_ENHANCED(), getDependencies(key));

    return odinSentence;
  }

  private String getChunks(WordToken wt) {
    return getPhraseChunk(wt).map(PhraseChunk::getChunkType).orElse(MISSING_VALUE);
  }

  private String getEntityName(WordToken wt) {
    return getEntity(wt).map(Entity::getType).map(Type::getShortName).orElse(MISSING_VALUE);
  }

  private Optional<Entity> getEntity(WordToken wt) {
    Collection<Entity> entity = indexEntities.get(wt);
    if (entity.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(entity.iterator().next());
    }
  }

  private Optional<PhraseChunk> getPhraseChunk(WordToken wt) {
    Collection<PhraseChunk> entity = indexChunks.get(wt);
    if (entity.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(entity.iterator().next());
    }
  }

  private String getNorm(WordToken wt) {
    return getEntity(wt)
        .filter(Entity::getIsNormalised)
        .map(Entity::getValue)
        .orElse(MISSING_VALUE);
  }

  private String getLemma(final WordToken token) {
    final FSArray array = token.getLemmas();
    if (array == null || array.size() == 0) {
      return token.getCoveredText().toLowerCase();
    } else {
      return ((WordLemma) array.get(0)).getLemmaForm();
    }
  }

  private DirectedGraph<String> getDependencies(uk.gov.dstl.baleen.types.language.Sentence key) {

    List<WordToken> tokens = ImmutableList.copyOf(indexWords.get(key));
    Set<Object> roots = new HashSet<>();

    List<Edge<String>> edges =
        indexDependency
            .get(key)
            .stream()
            .peek(
                d -> {
                  if (MaltParser.ROOT.equals(d.getDependencyType())) {
                    roots.add(tokens.indexOf(d.getGovernor()));
                  }
                })
            .map(
                d -> {
                  int source = tokens.indexOf(d.getGovernor());
                  int destination = tokens.indexOf(d.getDependent());
                  return new Edge<>(source, destination, d.getDependencyType().toLowerCase());
                })
            .collect(toList());

    return new DirectedGraph<>(
        JavaConversions.asScalaBuffer(edges).toList(), JavaConversions.asScalaSet(roots).toSet());
  }
}
