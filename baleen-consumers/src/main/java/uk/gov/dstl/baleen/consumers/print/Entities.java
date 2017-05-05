//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.print;

import uk.gov.dstl.baleen.types.semantic.Entity;

/**
 * Print out all entities.
 */
public class Entities extends AbstractPrintConsumer<Entity> {

	/**
	 * Instantiates a new consumer.
	 */
	public Entities() {
		super(Entity.class);
	}

	@Override
	protected String print(Entity t) {
		final StringBuilder sb = new StringBuilder();

		writeLine(sb, "Value", t.getValue());
		writeLine(sb, "Type", t.getTypeName());
		writeLine(sb, "Span", String.format("%d -> %d", t.getBegin(), t.getEnd()));

		return sb.toString();
	}

}