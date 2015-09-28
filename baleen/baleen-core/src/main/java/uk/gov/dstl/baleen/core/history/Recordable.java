//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.history;

import org.apache.uima.cas.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

/** Identifies an (UIMA) annotation (or annotation subtype) as one which should be recorded
 * in the history system.
 *
 * You should manually add this to any annotation which is to be recorded by the history
 * system.
 *
 * The interface deliberately overlays onto the UIMA, such that the history system can be
 * independent any specific UIMA annotation class but not requiring any re-implementation.
 *
 * 
 *
 */
public interface Recordable {

	/** Get the internal id of the recordable.
	 *
	 * @return the internal id
	 */
	long getInternalId();

	/** Get the text between the begin and end offsets within the document.
	 * @return the covered text (may be empty, but not null)
	 */
	String getCoveredText();

	/** Get the beginning offset of the recordable.
	 * @return begin offset in the document
	 */
	int getBegin();

	/** Get the end marker of the recordable.
	 * @return end offset in the document
	 */
	int getEnd();

	/** Get the UIMA type. Within history system this is only ever used through getTypeName().
	 * @return
	 */
	@JsonIgnore
	Type getType();

	/** Get the name of the (UIMA) type.
	 * @return get type name (non-null)
	 */
	default String getTypeName() {
		return getType().getName();
	}

}
