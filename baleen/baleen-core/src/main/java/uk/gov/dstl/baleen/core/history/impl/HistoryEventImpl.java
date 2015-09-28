//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.history.impl;

import java.util.Map;

import uk.gov.dstl.baleen.core.history.HistoryEvent;
import uk.gov.dstl.baleen.core.history.Recordable;

/** A bean implementation History event useful for deserialisation.
 * 
 * 
 */
public class HistoryEventImpl implements HistoryEvent {

	private long timestamp;
	private String eventType;
	private Recordable recordable;
	private String referrer;
	private Map<String,String> parameters;
	private String action;

	/** New instance.
	 *
	 */
	public HistoryEventImpl() {

	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	/** Set the event timestamp.
	 * @param timestamp
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String getEventType() {
		return eventType;
	}

	/** Set the event type.
	 * @param eventType
	 */
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	@Override
	public Recordable getRecordable() {
		return recordable;
	}

	/** Set the recordable.
	 * @param recordable
	 */
	public void setRecordable(Recordable recordable) {
		this.recordable = recordable;
	}

	@Override
	public String getReferrer() {
		return referrer;
	}

	/** Set the referrer.
	 * @param referrer
	 */
	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}

	@Override
	public String getAction() {
		return action;
	}

	/** Set the actions.
	 * @param action
	 */
	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public Map<String, String> getParameters() {
		return parameters;
	}

	/** Set the parameters.
	 * @param parameters
	 */
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

}
