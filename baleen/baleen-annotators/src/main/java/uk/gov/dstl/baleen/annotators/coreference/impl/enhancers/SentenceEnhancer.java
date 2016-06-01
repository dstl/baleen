package uk.gov.dstl.baleen.annotators.coreference.impl.enhancers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.coreference.impl.data.Mention;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.MentionType;
import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;

/**
 * Adds the sentence and its index (count from the start) to each mention.
 * <p>
 * Thanks to UIMA this is a tremendous amount of work for such a simple task.
 */
public class SentenceEnhancer {

	/**
	 * Enhance the mentions by adding sentence information.
	 *
	 * @param jCas
	 *            the j cas
	 * @param mentions
	 *            the mentions
	 */
	public void enhance(JCas jCas, List<Mention> mentions) {
		// Create a map (mention annotation) to sentence

		final Set<WordToken> pronounAnnotation = mentions.stream()
				.filter(p -> p.getType() == MentionType.PRONOUN)
				.map(p -> (WordToken) p.getAnnotation())
				.collect(Collectors.toSet());

		final Set<Entity> entityAnnotation = mentions.stream()
				.filter(p -> p.getType() == MentionType.ENTITY)
				.map(p -> (Entity) p.getAnnotation())
				.collect(Collectors.toSet());

		final Set<PhraseChunk> npAnnotation = mentions.stream()
				.filter(p -> p.getType() == MentionType.NP)
				.map(p -> (PhraseChunk) p.getAnnotation())
				.collect(Collectors.toSet());

		final Map<WordToken, Collection<Sentence>> wordToSentence = JCasUtil.indexCovering(jCas, WordToken.class,
				Sentence.class).entrySet().stream()
				.filter(e -> pronounAnnotation.contains(e.getKey()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		final Map<Entity, Collection<Sentence>> entityToSentence = JCasUtil.indexCovering(jCas, Entity.class,
				Sentence.class).entrySet().stream()
				.filter(e -> entityAnnotation.contains(e.getKey()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		final Map<PhraseChunk, Collection<Sentence>> npToSentence = JCasUtil.indexCovering(jCas, PhraseChunk.class,
				Sentence.class).entrySet().stream()
				.filter(e -> npAnnotation.contains(e.getKey()))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		// Create a sentence count

		final List<Sentence> sentences = new ArrayList<Sentence>(JCasUtil.select(jCas, Sentence.class));
		final Map<Sentence, Integer> sentenceIndex = IntStream.range(0, sentences.size())
				.boxed()
				.collect(Collectors.toMap(i -> sentences.get(i), i -> i));

		// Map mentions to sentence index

		mentions.forEach(m -> {
			Collection<Sentence> collection = null;
			switch (m.getType()) {
			case ENTITY:
				collection = entityToSentence.get(m.getAnnotation());
				break;
			case PRONOUN:
				collection = wordToSentence.get(m.getAnnotation());
				break;
			case NP:
				collection = npToSentence.get(m.getAnnotation());
				break;
			default:
				collection = Collections.emptyList();
			}
			
			setSentence(m, collection, sentenceIndex);
		});
	}

	private void setSentence(Mention m, Collection<Sentence> sentenceCollection, Map<Sentence, Integer> sentenceIndex){
		Sentence s = null;
		
		if (!sentenceCollection.isEmpty()) {
			s = sentenceCollection.iterator().next();
		}
		
		if(s == null){
			m.setSentence(null);
			m.setSentenceIndex(Integer.MIN_VALUE);
		}else{
			m.setSentence(s);
			m.setSentenceIndex(sentenceIndex.getOrDefault(s, Integer.MIN_VALUE));
		}
	}
}
