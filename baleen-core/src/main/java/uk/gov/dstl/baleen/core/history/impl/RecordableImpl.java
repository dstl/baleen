//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.history.impl;

import org.apache.uima.cas.Type;

import uk.gov.dstl.baleen.core.history.Recordable;

/** A implementation of recordable which can be used when deserialising
 * data from a databases.
 *
 * You can't deserialise to the original recordable since that is likely to be
 * a proxy to a full entity.
 *
 * This implementation will return null for getType().
 *
 * 
 *
 */

public class RecordableImpl implements Recordable {

	private long internalId;
	private String coveredText;
	private int begin;
	private int end;
	private String typeName;

	/** New instance
	 *
	 */
	public RecordableImpl() {
		//Empty constructor, do nothing
	}

	/** New instance.
	 * @param id
	 * @param text
	 * @param begin
	 * @param end
	 * @param type
	 */
	public RecordableImpl(long id, String text, int begin, int end, String type) {
		this.internalId = id;
		this.coveredText = text;
		this.begin = begin;
		this.end = end;
		this.typeName = type;
	}


	@Override
	public long getInternalId() {
		return internalId;
	}

	/** Set the internal id.
	 * @param internalId the id
	 */
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}


	@Override
	public String getCoveredText() {
		return coveredText;
	}

	/** Set the covered text.
	 * @param coveredText the text
	 */
	public void setCoveredText(String coveredText) {
		this.coveredText = coveredText;
	}

	@Override
	public int getBegin() {
		return begin;
	}

	/** Set the beginning offset (within the document).
	 * @param begin offset
	 */
	public void setBegin(int begin) {
		this.begin = begin;
	}

	/** Set the end offset (within the document).
	 * @param end offset
	 */
	public void setEnd(int end) {
		this.end = end;
	}

	@Override
	public int getEnd() {
		return end;
	}

	@Override
	public Type getType() {
		return null;
	}

	@Override
	public String getTypeName() {
		return typeName;
	}

	/** Sets the name of the type.
	 * @param typeName the type name (equivalent to getType().getName() on an Annotation).
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
}
