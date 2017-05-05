//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.structural;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.types.structure.Structure;
import uk.gov.dstl.baleen.types.structure.Table;
import uk.gov.dstl.baleen.types.structure.TableCell;
import uk.gov.dstl.baleen.uima.utils.StructureHierarchy;
import uk.gov.dstl.baleen.uima.utils.StructureUtil;
import uk.gov.dstl.baleen.uima.utils.select.ItemHierarchy;
import uk.gov.dstl.baleen.uima.utils.select.Node;
import uk.gov.dstl.baleen.uima.utils.select.Nodes;

/**
 * Helper class for working with {@link Table}s.
 *
 * <p>
 * This is an example of how structure selectors can be used
 */
public class Tables {

	private List<Pattern> columnPatterns = new ArrayList<>();
	private Nodes<Structure> nodes;

	/**
	 * Constructor for tables helper
	 *
	 * @param jCas
	 *            the jcas
	 * @throws AnalysisEngineProcessException
	 *             if class creation error
	 */
	public Tables(JCas jCas) throws AnalysisEngineProcessException {
		try {
			ItemHierarchy<Structure> hierarchy = StructureHierarchy.build(jCas, StructureUtil.getStructureClasses());
			nodes = hierarchy.getRoot().select("Table");
		} catch (ResourceInitializationException e) {
			throw new AnalysisEngineProcessException("Can not create structure helper", null, e);
		}
	}

	/**
	 * Filter to tables with a column matching the given name.
	 * <p>
	 * This uses the table header to find column names.
	 *
	 *
	 * @param p
	 *            the pattern to match
	 * @return this for builder pattern
	 */
	public Tables withColumn(String p) {
		return withColumn(Pattern.compile(p));
	}

	/**
	 * Filter to tables with a column matching the given name.
	 * <p>
	 * This uses the table header to find column names.
	 *
	 *
	 * @param p
	 *            the pattern to match
	 * @return this for builder pattern
	 */
	public Tables withColumn(Pattern p) {
		nodes = nodes.select(":has(TableHeader:matches(" + p.pattern() + "))");
		columnPatterns.add(p);
		return this;
	}

	/**
	 * Get the rows of the tables filtered to the specified columns and in given
	 * column order.
	 *
	 * @return a stream of the filtered rows (ie list of TableCells)
	 */
	public Stream<List<TableCell>> getFilteredRows() {
		return nodes.stream().flatMap(table -> {

			List<Nodes<Structure>> columns = columnPatterns.stream()
					.map(p -> nodes.select("TableHeader TableCell:matches(" + p.pattern() + ")"))
					.map(p -> p.get(0))
					.map(Node::getSiblingIndex)
					.map(i -> nodes.select("TableBody > TableRow > TableCell:nth-child(" + (i + 1) + ")"))
					.collect(Collectors.toList());

			if (columns.isEmpty()) {
				return Stream.empty();
			}
			// validate that the input lists are all the same size.
			int numItems = columns.get(0).size();
			for (int i = 1; i < columns.size(); i++) {
				if (columns.get(i).size() != numItems) {
					// non-uniform-length list at index i
					return Stream.empty();
				}
			}

			List<List<TableCell>> result = new ArrayList<>();

			for (int i = 0; i < numItems; i++) {

				// create a tuple of the i-th entries of each list
				List<TableCell> row = new ArrayList<>(columns.size());
				for (Nodes<Structure> column : columns) {
					row.add((TableCell) column.get(i).getItem());
				}
				result.add(row);
			}

			return result.stream();

		});

	}

	/**
	 * Get the cells of the tables filtered to the specified columns.
	 *
	 * @return a stream of the filtered table cells
	 */
	public Stream<TableCell> getFilteredCells() {
		return getFilteredRows().flatMap(List::stream);
	}

	/**
	 * Get the tables which pass the specified column filters.
	 *
	 * @return a stream of the filtered tables
	 */
	public Stream<Table> getTables() {
		return nodes.stream().map(t -> (Table) t.getItem());
	}

	/**
	 * Get the full rows of the tables which pass the specified column filters.
	 *
	 * @return a stream of the filtered table cells
	 */
	public Stream<List<TableCell>> getRows() {
		return nodes.select("TableBody > TableRow").stream()
				.map(tr -> tr.getChildren().stream().map(tc -> (TableCell) tc.getItem()).collect(Collectors.toList()));
	}

	/**
	 * Get all the cells of the tables which pass the specified column filters.
	 *
	 * @return a stream of the table cells
	 */
	public Stream<TableCell> getCells() {
		return getRows().flatMap(List::stream);
	}

}
