package uk.gov.dstl.baleen.annotators.coreference.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import uk.gov.dstl.baleen.annotators.coreference.impl.data.Mention;
import uk.gov.dstl.baleen.annotators.coreference.impl.enhancers.SentenceEnhancer;
import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.grammar.DependencyGraph;

/**
 * Extract mentions from the jCas.
 */
public class MentionDetector {

	private final JCas jCas;
	private final DependencyGraph dependencyGraph;
	private List<WordToken> pronouns;
	private Collection<Entity> entities;

	/**
	 * Create a new mention detector using the specified JCas and DependencyGraph
	 */
	public MentionDetector(JCas jCas, DependencyGraph dependencyGraph) {
		this.jCas = jCas;
		this.dependencyGraph = dependencyGraph;
	}

	/**
	 * Detect mentions in the JCas and DependencyGraph (provided in the constructor) and return them as a list
	 */
	public List<Mention> detect() {
		setup();

		final List<Mention> mentions = new ArrayList<>(pronouns.size() + entities.size());

		detectPronouns(mentions);

		detectEntities(mentions);

		detectPhrases(mentions);

		new SentenceEnhancer().enhance(jCas, mentions);

		return mentions;
	}

	private void setup() {
		pronouns = JCasUtil.select(jCas, WordToken.class).stream()
				.filter(w -> w.getPartOfSpeech().startsWith("PP") || w.getPartOfSpeech().startsWith("WP")
						|| w.getPartOfSpeech().startsWith("PRP"))
				.collect(Collectors.toList());

		entities = JCasUtil.select(jCas, Entity.class);

	}

	private void detectPronouns(List<Mention> mentions) {
		pronouns.stream()
				.map(Mention::new)
				.map(m -> {
					final List<WordToken> list = Collections.singletonList((WordToken) m.getAnnotation());
					m.setWords(list);
					return m;
				}).forEach(mentions::add);

	}

	private void detectEntities(Collection<Mention> mentions) {
		entities.stream()
				.map(Mention::new)
				.map(m -> {
					final Collection<WordToken> list = JCasUtil.selectCovered(jCas, WordToken.class, m.getAnnotation());
					m.setWords(new ArrayList<WordToken>(list));
					m.setHeadWordToken(determineHead(m.getWords()));
					return m;
				}).forEach(mentions::add);

	}

	private WordToken determineHead(List<WordToken> words) {
		// A dependency grammar approach to head word extraction
		// - find the Noun in the noun phrase which is the link out of the words
		// - this seems to be the head word
		// TODO: Investigate other approachces Collin 1999, etc. Do they give the same/better results?

		if (words.size() == 1) {
			return words.get(0);
		} else {
			final List<WordToken> candidates = identifyHeadCandidates(words);

			if (candidates.isEmpty()) {
				return null;
			}

			// TODO: No idea if its it possible to get more than one if all things work.
			// I think this would be a case of marking an entity which cross the NP boundary and is likely wrong.
			// TODO: Not sure if we should pull out compound words here... (its a head word but even so)

			return candidates.get(0);
		}
	}
	
	private List<WordToken> identifyHeadCandidates(List<WordToken> words){
		final List<WordToken> candidates = new LinkedList<WordToken>();
		
		for (final WordToken word : words) {
			if (word.getPartOfSpeech().startsWith("N")) {
				final Stream<WordToken> edges = dependencyGraph.getEdges(word);
				if (edges.anyMatch(p -> !words.contains(p))) {
					candidates.add(word);
				}
			}
		}
		
		return candidates;
	}

	private void detectPhrases(List<Mention> mentions) {

		// Limit to noun phrases
		final List<PhraseChunk> phrases = JCasUtil.select(jCas, PhraseChunk.class).stream()
				.filter(p -> p.getChunkType().startsWith("N"))
				.collect(Collectors.toList());

		// Remove any noun phrases which cover entities
		JCasUtil.indexCovering(jCas, Entity.class, PhraseChunk.class).values()
				.stream()
				.flatMap(e -> e.stream())
				.forEach(phrases::remove);

		final Map<PhraseChunk, Collection<WordToken>> phraseToWord = JCasUtil.indexCovered(jCas, PhraseChunk.class,
				WordToken.class);

		// Create an index for head words
		final Multimap<WordToken, PhraseChunk> headToChunk = HashMultimap.create();
		phrases.stream()
				.forEach(p -> {
					final Collection<WordToken> collection = phraseToWord.get(p);
					final WordToken head = determineHead(new ArrayList<>(collection));
					if (head != null) {
						headToChunk.put(head, p);
					}
					// TODO: What should we do to those without heads?
				});

		// Paper: keep the largest noun phrase which has the same head word.
		headToChunk.asMap().entrySet().stream()
				.filter(e -> e.getValue().size() == 1)
				.forEach(e -> {
					PhraseChunk largest = null;
					int largestSize = 0;

					for (final PhraseChunk p : e.getValue()) {
						// the head is always common word, so we know they overlap
						final int size = p.getEnd() - p.getBegin();
						if (largest == null || largestSize < size) {
							largest = p;
							largestSize = size;
						}
					}

					// Remove all the small ones
					for (final PhraseChunk p : e.getValue()) {
						if (!p.equals(largest)) {
							phrases.removeAll(headToChunk.values());
						}
					}
				});

		// Remove all phrases based on their single content
		JCasUtil.indexCovering(jCas, PhraseChunk.class,
				WordToken.class)
				.entrySet()
				.stream()
				.filter(e -> e.getValue().size() == 1)
				.filter(e -> filterBySingleContent(e.getValue().iterator().next()))
				.map(Entry::getKey)
				.forEach(phrases::remove);

		// TODO: Remove all pronouns which are covered by the phrases? I think not...
		// TODO: Paper removes It if possible (see Appendix B for regex)
		// TODO: Paper removes static list of stop words (but we should determine that ourselves)
		// TODO: Paper removes partivit or quantifier (millions of people). Unsure why though.

		phrases.stream()
				.map(Mention::new)
				.map(m -> {
					final List<WordToken> words = new ArrayList<>(phraseToWord.get(m.getAnnotation()));
					// TODO: We already calculated this early (for headToWord), but we just redo again here. Would be nice to reuse
					m.setWords(words);
					m.setHeadWordToken(determineHead(words));
					return m;
				}).forEach(mentions::add);
	}
	
	private boolean filterBySingleContent(WordToken t){
		if (pronouns.contains(t)) {
			// Remove NP which are
			return true;
		} else{
			// Paper: Remove cardinal / numerics
			return "CD".equalsIgnoreCase(t.getPartOfSpeech());
		}
	}
}
