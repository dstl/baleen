//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.history;

import java.util.Map;
import java.util.Optional;


/** The base interface for a history event.
 *
 * 
 *
 */
public interface HistoryEvent  {

	// About this event

	/** Get the timestamp associated with this event.
	 * @return timestamp of event
	 */
	long getTimestamp();

	/** Get a unique identifier for the type of this event.
	 * @return the event type
	 */
	String getEventType();

	// Relating to the recordable (i.e. entity / annotation)

	/** Get the recordable associated with this event.
	 *
	 * @return the recordable
	 */
	Recordable getRecordable();

	// Relating to what happened

	/** Get the object which was caused this event.
	 *
	 * @return the referrer
	 */
	String getReferrer();

	/** A description of the action which was applied.
	 *
	 * This should be formatted to be both machine and human readable.
	 *
	 * Data relating to the action (e.g. which other annotations where involved)
	 * should be added to the parameters.
	 *
	 * @return the action
	 */
	String getAction();

	/** Parameters which provide an detail on the environment in which the action
	 * occurred.
	 *
	 * For example when merging two entities it could contain the IDs of the entities.
	 *
	 * @return the parameters (non-null, but may be empty)
	 */
	Map<String, String> getParameters();

	/** Helper to get a specific parameter by name.
	 * @param key the parameter name
	 * @return optional of the value found
	 */
	default Optional<String> getParameters(String key) {
		return Optional.ofNullable(getParameters().get(key));
	}

	/** Helper to get a specific parameter by name, with a default.
	 * @param key the parameter name
	 * @param defaultValue the value to return if the parameter is not found
	 * @return the value if found, or null
	 */
	default String getParameters(String key, String defaultValue) {
		return getParameters().getOrDefault(key, defaultValue);
	}

}
