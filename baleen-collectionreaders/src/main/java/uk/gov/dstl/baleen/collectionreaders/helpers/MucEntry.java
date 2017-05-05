//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders.helpers;

/**
 * Represents a entry (report / message) in Muc3.
 */
public class MucEntry {
	private final String id;

	private String text;

	/**
	 * Instantiates a new muc entry.
	 *
	 * @param id
	 *            the id
	 * @param text
	 *            the text
	 */
	public MucEntry(String id, String text) {
		this.id = id;
		this.text = text;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text.
	 *
	 * @param text
	 *            the new text
	 */
	public void setText(String text) {
		this.text = text;
	}

}