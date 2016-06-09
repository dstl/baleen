//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.history.logging;

import java.time.Instant;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.history.AbstractBaleenHistory;
import uk.gov.dstl.baleen.core.history.DocumentHistory;
import uk.gov.dstl.baleen.core.history.HistoryEvent;
import uk.gov.dstl.baleen.exceptions.BaleenException;

/**
 * A history implement which outputs history events to the logger.
 *
 * This history system does not allow history to be read back into the
 * application, hence the get methods return empty collections.
 *
 * 
 * @baleen.javadoc
 */
public class LoggingBaleenHistory extends AbstractBaleenHistory {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingBaleenHistory.class);

	private static final String DEFAULT_FORMAT = "{} [{}-{}] {}:{}";

	/**
	 * The log level to output.
	 * Valid values are trace, debug, info, warn and error.
	 * 
	 * @baleen.config info
	 */
	public static final String PARAM_LEVEL = "history.level";
	@ConfigurationParameter(name = PARAM_LEVEL, defaultValue = "info")
	private String loggerLevel;

	/**
	 * The name of the logger
	 * 
	 * @baleen.config history
	 */
	public static final String PARAM_NAME = "history.name";
	@ConfigurationParameter(name = PARAM_NAME, defaultValue = "history")
	private String loggerName;

	private Logger historyLogger;

	/**
	 * New instance
	 *
	 */
	public LoggingBaleenHistory() {
		//Empty constructor, do nothing
	}

	@Override
	protected void initialize() throws BaleenException {
		super.initialize();

		// (re)setting this way makes sure it's non-null
		setLevel(loggerLevel);

		historyLogger = LoggerFactory.getLogger(loggerName);

		LOGGER.info("Configured a logging history with name '{}' at level '{}'", loggerName, loggerLevel);
	}

	@Override
	public void destroy() {
		super.destroy();
		historyLogger = null;
	}

	@Override
	public DocumentHistory getHistory(String documentId) {
		return new LoggingDocumentHistory(this, documentId);
	}

	@Override
	public void closeHistory(String documentId) {
		// Do nothing
	}

	/** Set the current logging level.
	 * @param level (trace,debug,warn,error)
	 */
	public void setLevel(String level) {
		this.loggerLevel = level != null ? level : "info";
	}

	/** Get the current logging level
	 * @return the level
	 */
	public String getLevel() {
		return this.loggerLevel;
	}



	/**
	 * Add the event to the specific document.
	 *
	 * @param documentId
	 *            the document id
	 * @param event
	 *            the event to add to the document
	 */
	public void add(String documentId, HistoryEvent event) {
		if (historyLogger == null) {
			LOGGER.error("Logging history event withouth an initialised logger");
			return;
		}

		switch (loggerLevel) {
		case "warn":
			historyLogger.warn(DEFAULT_FORMAT, Instant.ofEpochMilli(event.getTimestamp()), documentId, event
					.getRecordable().getInternalId(), event.getReferrer(), event.getAction());
			break;
		case "error":
			historyLogger.error(DEFAULT_FORMAT, Instant.ofEpochMilli(event.getTimestamp()), documentId, event
					.getRecordable().getInternalId(), event.getReferrer(), event.getAction());
			break;
		case "trace":
			historyLogger.trace(DEFAULT_FORMAT, Instant.ofEpochMilli(event.getTimestamp()), documentId, event
					.getRecordable().getInternalId(), event.getReferrer(), event.getAction());
			break;
		case "info":
		default:
			historyLogger.info(DEFAULT_FORMAT, Instant.ofEpochMilli(event.getTimestamp()), documentId, event
					.getRecordable().getInternalId(), event.getReferrer(), event.getAction());
			break;
		}
	}

}
