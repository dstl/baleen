//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.print;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;

import uk.gov.dstl.baleen.consumers.AbstractStructureConsumer;
import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.uima.utils.SelectorPath;
import uk.gov.dstl.baleen.uima.utils.select.ItemHierarchy;
import uk.gov.dstl.baleen.uima.utils.select.Node;
import uk.gov.dstl.baleen.uima.utils.select.NodeTraversor;
import uk.gov.dstl.baleen.uima.utils.select.NodeVisitor;

/**
 * Print out all structure.
 */
public class Selectors extends AbstractStructureConsumer {

	private String print(Node<Structure> root) {
		final StringBuilder sb = new StringBuilder();
		new NodeTraversor<>(new NodeVisitor<Structure>() {

			@Override
			public void head(Node<Structure> node, int depth) {
				sb.append(new SelectorPath(node.toPath()));
				sb.append("\n");

			}
		}).traverse(root);
		return sb.toString();
	}

	@Override
	protected void doProcess(ItemHierarchy<Structure> structureHierarchy) throws AnalysisEngineProcessException {
		final String result = print(structureHierarchy.getRoot());
		getMonitor().info("{}:\n{}", Structure.class.getName(), result);
	}

}
