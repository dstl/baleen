//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.print;

import uk.gov.dstl.baleen.consumers.AbstractStructureConsumer;
import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.uima.utils.AnnotationNode;
import uk.gov.dstl.baleen.uima.utils.select.ItemHierarchy;
import uk.gov.dstl.baleen.uima.utils.select.Node;

/**
 * Print out all structure.
 */
public class Structures extends AbstractStructureConsumer {

	private void print(int level, StringBuilder sb, Node<Structure> n) {
		startTag(level, sb, n.getItem());
		int offset = 0;
		for (final Node<Structure> child : n.getChildren()) {
			addText(level + 1, sb, n.getItem(), offset, AnnotationNode.getBegin(child) - AnnotationNode.getBegin(n));
			print(level + 1, sb, child);
			offset = AnnotationNode.getEnd(child) - AnnotationNode.getBegin(n);
		}
		addText(level + 1, sb, n.getItem(), offset, AnnotationNode.getEnd(n) - AnnotationNode.getBegin(n));
		endTag(level, sb, n.getItem());
	}

	private void addText(int level, StringBuilder sb, Structure element, int start, int end) {

		if (element != null && start < end) {
			final String coveredText = element.getCoveredText();
			if (end > coveredText.length()) {
				sb.append("ERROR IN DOCUMENT TREE");
			}
			indent(level, sb);
			sb.append(coveredText.substring(start, end));
		}
	}

	private void endTag(int level, final StringBuilder sb, Structure element) {
		if (element != null) {
			indent(level, sb);
			sb.append("</");
			sb.append(element.getClass().getSimpleName());
			sb.append(">");
		}
	}

	private void indent(int size, StringBuilder sb) {
		sb.append("\n");
		for (int i = 0; i < size - 1; i++) {
			sb.append("\t");
		}
	}

	private String print(Node<Structure> parent) {
		final StringBuilder sb = new StringBuilder();
		print(0, sb, parent);
		return sb.toString();
	}

	private void startTag(int level, final StringBuilder sb, Structure element) {
		if (element != null) {
			indent(level, sb);
			sb.append("<");
			sb.append(element.getClass().getSimpleName());
			sb.append(">");
		}
	}

	@Override
	protected void doProcess(ItemHierarchy<Structure> structureHierarchy) {
		final String result = print(structureHierarchy.getRoot());
		getMonitor().info("{}:\n{}", Structure.class.getName(), result);
	}

}
