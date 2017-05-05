//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.template;

/**
 * Hold the name and value of a field extracted by templates
 */
public class ExtractedField {

	private String name;

	private String value;

	/**
	 * Default constructor
	 */
	public ExtractedField() {
		// for reflective construction in Jackson
	}

	/**
	 * Constructor with explicit name and value
	 */
	public ExtractedField(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Get field name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set field name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get field value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set field value
	 */
	public void setValue(String value) {
		this.value = value;
	}

}