//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.dictionary.Dictionary;
import uk.gov.dstl.baleen.uima.BaleenResource;

/**
 * A shared resource that provides access to WordNet.
 *
 * @baleen.javadoc
 */
public class SharedWordNetResource extends BaleenResource {
	private Dictionary dictionary;

	@Override
	protected boolean doInitialize(final ResourceSpecifier specifier, final Map<String, Object> additionalParams)
			throws ResourceInitializationException {

		try {
			dictionary = Dictionary.getDefaultResourceInstance();
		} catch (final JWNLException e) {
			throw new ResourceInitializationException(e);
		}

		return super.doInitialize(specifier, additionalParams);
	}

	@Override
	protected void doDestroy() {
		super.doDestroy();

		try {
			dictionary.close();
		} catch (final JWNLException e) {
			getLogger().warn("WordNet dictionary did not close cleanly", e);
		} finally {
			dictionary = null;
		}

	}

	/**
	 * Get the WordNet dictionary.
	 *
	 * @return Wordnet dictionary
	 */
	public Dictionary getDictionary() {
		return dictionary;
	}

	/**
	 * Lookup the word from the dictionary, performing lemmisation if required.
	 *
	 * @param pos
	 *            the pos
	 * @param word
	 *            the word
	 * @return the WordNet word, (as an optional)
	 */
	public Optional<IndexWord> lookupWord(final POS pos, final String word) {
		try {
			return Optional.ofNullable(dictionary.lookupIndexWord(pos, word));
		} catch (final JWNLException e) {
			getMonitor().warn("Lookup word failed", e);
			return Optional.empty();
		}
	}

	/**
	 * Get an exact lemma from the dictionary, .
	 *
	 * @param pos
	 *            the pos
	 * @param lemma
	 *            the lemma
	 * @return the WordNet word (as an optional)
	 */
	public Optional<IndexWord> getWord(final POS pos, final String lemma) {
		try {
			return Optional.ofNullable(dictionary.getIndexWord(pos, lemma));
		} catch (final JWNLException e) {
			getMonitor().warn("Get word failed", e);
			return Optional.empty();
		}
	}

	/**
	 * Gets the super senses of a word.
	 *
	 * The supersense is the original 'sense file' in which word was defined.
	 *
	 * @param pos
	 *            the pos
	 * @param word
	 *            the word
	 * @return the super senses
	 */
	public Stream<String> getSuperSenses(POS pos, String word) {
		final Optional<IndexWord> indexWord = lookupWord(pos, word);

		if (!indexWord.isPresent()) {
			return Stream.empty();
		} else {
			// NOTE: This was stream but it WordNet getSenses() somehow seems incompatible with
			// streams
			final List<Synset> senses = indexWord.get().getSenses();
			final Set<String> set = new HashSet<>();
			for (final Synset s : senses) {
				set.add(stripPOSFromSupersense(s.getLexFileName()));
			}
			return set.stream();
		}
	}

	/**
	 * Gets the best super sense for a word.
	 *
	 * @param pos
	 *            the pos
	 * @param word
	 *            the word
	 * @return the best super sense
	 */
	public Optional<String> getBestSuperSense(POS pos, String word) {
		final Optional<IndexWord> indexWord = lookupWord(pos, word);

		if (!indexWord.isPresent()) {
			return Optional.empty();
		} else {
			final List<Synset> senses = indexWord.get().getSenses();
			if (senses.isEmpty()) {
				return Optional.empty();
			} else {
				// At this stage we could do something clever, look at the gloss to see is there are
				// word overlaps
				// but we opt for a more predicatable concept of selecting the most commonly used
				// meaning sense.

				return Optional.of(stripPOSFromSupersense(senses.get(0).getLexFileName()));
			}
		}
	}

	/**
	 * Strip POS from supersense.
	 *
	 * Since the lexfile has filename "noun.cogition" we remove the "noun." so that verb based and
	 * noun based words of the same supersense have the same value.
	 *
	 * @param sense
	 *            the sense
	 * @return the string
	 */
	private String stripPOSFromSupersense(String sense) {
		final int index = sense.indexOf(".") + 1;
		if (index != 0) {
			return sense.substring(index);
		} else {
			return sense;
		}
	}

}