package uk.gov.dstl.baleen.consumers.print;

import uk.gov.dstl.baleen.types.semantic.Relation;

/**
 * Print out all relations.
 */
public class Relations extends AbstractPrintConsumer<Relation> {

	/**
	 * Instantiates a new consumer.
	 */
	public Relations() {
		super(Relation.class);
	}

	@Override
	protected String print(Relation t) {
		final StringBuilder sb = new StringBuilder();
		writeLine(sb, "Value", t.getValue());
		writeLine(sb, "Type", t.getRelationshipType() + " [" + t.getRelationSubType() +"]");
		writeLine(sb, "Source", t.getSource());
		writeLine(sb, "Target", t.getTarget());
		return sb.toString();
	}
}
