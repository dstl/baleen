//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.history.elasticsearch;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

import uk.gov.dstl.baleen.core.history.HistoryEvent;

/** This is a Java representation of the data structure of ES, used for
 * serialisation and deserialisation.
 * 
 *
 */
public class ESHistory {

	private String documentId;
	private Collection<HistoryEvent> events;

	/** New instance.
	 *
	 */
	public ESHistory() {

	}

	/** New instance with the specified content.
	 * @param documentId the document
	 * @param events the events in the history
	 */
	public ESHistory(String documentId, Collection<HistoryEvent> events) {
		this.documentId = documentId;
		this.events = events;
	}

	/** Set the document id.
	 * @param documentId
	 */
	@JsonProperty("docId")
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	/** Get the document id.
	 * @return the document i
	 */
	@JsonProperty("docId")
	public String getDocumentId() {
		return documentId;
	}

	/** Get the history events.
	 * @return the event (non-null, but may be empty)
	 */
	@JsonProperty("events")
	public Collection<HistoryEvent> getEvents() {
		if(events == null) {
			events = Lists.newLinkedList();
		}
		return events;
	}

	/** Set the events.
	 * @param events the events
	 */
	@JsonProperty("events")
	public void setEvents(Collection<HistoryEvent> events) {
		this.events = events;
	}

}
