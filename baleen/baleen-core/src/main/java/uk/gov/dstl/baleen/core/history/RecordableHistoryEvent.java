//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.history;

import java.util.Collections;
import java.util.Map;


/** A basic implementation of a history event which is based around a recordable.
 *
 * 
 *
 */
public class RecordableHistoryEvent implements HistoryEvent {

	private final Recordable recordable;
	private final String action;
	private final long timestamp;
	private final String referrer;
	private final String eventType;
	private final Map<String, String> parameters;

	/** New instance with now as the current time.
	 * @param eventType
	 * @param recordable
	 * @param referrer
	 * @param action
	 */
	public RecordableHistoryEvent(String eventType, Recordable recordable, String referrer, String action) {
		this(eventType, System.currentTimeMillis(), recordable, referrer, action);
	}

	/** New instance.
	 * @param eventType the type of event
	 * @param timestamp the timestamp of the event being applied
	 * @param recordable the recordable to which this history event applies
	 * @param referrer the object which changes the recordable
	 * @param action a description of the action which was applied
	 */
	public RecordableHistoryEvent(String eventType, long timestamp, Recordable recordable, String referrer, String action) {
		this(eventType, timestamp, recordable, referrer, action, Collections.emptyMap());
	}

	/** New instance with now as the current time.
	 * @param eventType
	 * @param recordable
	 * @param referrer
	 * @param action
	 * @param parameters the parameters
	 */
	public RecordableHistoryEvent(String eventType, Recordable recordable, String referrer, String action, Map<String, String> parameters) {
		this(eventType, System.currentTimeMillis(), recordable, referrer, action, parameters);
	}

	/** New instance.
	 * @param eventType the type of event
	 * @param timestamp the timestamp of the event being applied
	 * @param recordable the recordable to which this history event applies
	 * @param referrer the object which changes the recordable
	 * @param action a description of the action which was applied
	 * @param parameters the parameters
	 */
	public RecordableHistoryEvent(String eventType, long timestamp, Recordable recordable, String referrer, String action, Map<String, String> parameters) {
		this.eventType = eventType;
		this.timestamp = timestamp;
		this.recordable = recordable;
		this.referrer = referrer;
		this.action = action;
		this.parameters = parameters;
	}

	@Override
	public Recordable getRecordable() {
		return recordable;
	}

	@Override
	public String getAction() {
		return action;
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String getEventType() {
		return eventType;
	}

	@Override
	public String getReferrer() {
		return referrer;
	}

	@Override
	public Map<String, String> getParameters() {
		return parameters;
	}

}
