//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.patterns.data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;

/**
 * A java bean corresponding to an extracted Pattern.
 *
 * A pattern is a range of text between two entities.
 */
public final class PatternExtract {

	/** The start. */
	private final int start;

	/** The end. */
	private final int end;

	/** The from. */
	private final Entity from;

	/** The to. */
	private final Entity to;

	/** The words. */
	private List<WordToken> words;

	/**
	 * Instantiates a new pattern extract.
	 *
	 * @param from
	 *            the first entity (start of the pattern)
	 * @param to
	 *            the second entity (end of the pattern)
	 * @param start
	 *            the start index
	 * @param end
	 *            the end index
	 */
	public PatternExtract(final Entity from, final Entity to, final int start, final int end) {
		this.from = from;
		this.to = to;
		this.start = start;
		this.end = end;
	}

	/**
	 * Get the first entity.
	 *
	 * @return entity
	 */
	public Entity getFrom() {
		return from;
	}

	/**
	 * Gets the second entitys
	 *
	 * @return entity
	 */
	public Entity getTo() {
		return to;
	}

	/**
	 * Gets the start.
	 *
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * Gets the end.
	 *
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * Sets the word tokens (which form the pattern, and are beneath the start-end range).
	 *
	 * @param words
	 *            the new word tokens
	 */
	public void setWordTokens(final List<WordToken> words) {
		this.words = words;
	}

	/**
	 * Gets the word tokens (must have been previously set)
	 *
	 * @return the word tokens
	 */
	public List<WordToken> getWordTokens() {
		return words;
	}

	/**
	 * Determine if any of the needles are contained in this covering document text.
	 *
	 * @param documentText
	 *            the document text
	 * @param needles
	 *            the needles
	 * @return true, if successful
	 */
	public boolean contains(final String documentText, final String... needles) {
		final String text = getCoveredText(documentText);
		return Arrays.stream(needles).anyMatch(text::contains);
	}

	/**
	 * Gets the covered text.
	 *
	 * @param documentText
	 *            the document text
	 * @return the covered text
	 */
	public String getCoveredText(final String documentText) {
		return documentText.substring(start, end);
	}

	/**
	 * Gets the text formed of the concatenated word tokens.
	 *
	 * Hence this a 'sanitised text' rather than the covered text.
	 *
	 * @return the text
	 */
	public String getText() {
		if (words == null) {
			return "";
		}

		return words.stream()
				.map(w -> w.getCoveredText()).collect(Collectors.joining(" "));
	}

	/**
	 * Checks if is empty, based on the word tokens (not range, start/end, etc)
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		return words == null || words.isEmpty();
	}

}