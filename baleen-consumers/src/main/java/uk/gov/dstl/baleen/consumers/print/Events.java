//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.print;

import uk.gov.dstl.baleen.types.semantic.Event;

/**
 * Print out all events.
 */
public class Events extends AbstractPrintConsumer<Event> {

	/**
	 * Instantiates a new consumer.
	 */
	public Events() {
		super(Event.class);
	}

	@Override
	protected String print(Event t) {
		final StringBuilder sb = new StringBuilder();

		writeLine(sb, "Value", t.getValue());
		writeLine(sb, "Tokens", t.getTokens());
		writeLine(sb, "Type", t.getEventType());
		writeLine(sb, "Entities", t.getEntities());
		writeLine(sb, "Arguments", t.getArguments());

		return sb.toString();
	}

}