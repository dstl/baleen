//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.common.Quantity;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Find adjacent quantities of the same type and merge them
 * 
 * <p>This annotator will find adjacent quantities of the same type and merge them into a single quantity.
 * For example, 7lb 4oz should be annotated as a single entity, not two.</p>
 * 
 * 
 */
public class MergeAdjacentQuantities extends BaleenAnnotator {

	// Don't include new lines
	private final Pattern whitespace = Pattern.compile("[ \t]*");

	@Override
	public void doProcess(JCas jCas) throws AnalysisEngineProcessException {

		List<List<Quantity>> mergeables = findAllQuantitiesToMerge(jCas);

		// Remove lists where we duplicate the same unit e.g.
		// 7lb 3oz is acceptable
		// 1kg 1kg is not
		
		// Or if the subsequent value is greater than the first (normalised) e.g.
		// 15kg 6g is acceptable
		// 3oz 7lb is not
		Iterator<List<Quantity>> it = mergeables.iterator();

		while (it.hasNext()) {
			List<Quantity> list = it.next();
			Set<String> seenUnits = new HashSet<>(list.size());
			boolean remove = false;
			double lastValue = Double.NaN;
			for (Quantity q : list) {

				// Verify as per comment above
				if (seenUnits.contains(q.getUnit())
						|| (!Double.isNaN(lastValue) && q
								.getNormalizedQuantity() > lastValue)) {
					remove = true;
					break;
				}

				lastValue = q.getNormalizedQuantity();
				seenUnits.add(q.getUnit());
			}

			if (remove) {
				it.remove();
			}
		}

		// Create new annotations
		mergeQuantities(jCas, mergeables);
	}

	private void mergeQuantities(JCas jCas, List<List<Quantity>> mergeables) {
		for (List<Quantity> mergable : mergeables) {

			Double normalizedQuantity = 0.0;
			Double lowestConfidence = 1.0;

			Quantity first = mergable.get(0);
			String normalizedUnit = first.getNormalizedUnit();
			String type = first.getQuantityType();

			int begin = Integer.MAX_VALUE;
			int end = Integer.MIN_VALUE;

			// Determine the extent and remove the old annotations
			for (Quantity q : mergable) {

				if (begin > q.getBegin())
					begin = q.getBegin();
				if (end < q.getEnd())
					end = q.getEnd();

				normalizedQuantity += q.getNormalizedQuantity();

				if (q.getConfidence() < lowestConfidence)
					lowestConfidence = q.getConfidence();
			}

			// Build a new annotation
			Quantity merged = new Quantity(jCas);

			merged.setBegin(begin);
			merged.setEnd(end);

			merged.setValue(jCas.getDocumentText().substring(begin, end));
			merged.setConfidence(lowestConfidence);

			merged.setNormalizedQuantity(normalizedQuantity);
			merged.setNormalizedUnit(normalizedUnit);
			merged.setQuantityType(type);

			mergeWithNew(merged, mergable);
		}
	}

	private List<List<Quantity>> findAllQuantitiesToMerge(JCas jCas) {
		List<List<Quantity>> mergeables = new LinkedList<List<Quantity>>();
		Map<Quantity, List<Quantity>> toMerge = new HashMap<>();

		// Create a mapping of annotations to join together
		for (Quantity current : JCasUtil.select(jCas, Quantity.class)) {

			List<Quantity> following = JCasUtil.selectFollowing(jCas,
					Quantity.class, current, 1);
			if (following.isEmpty())
				break;
			Quantity next = following.get(0);

			String between = jCas.getDocumentText().substring(current.getEnd(),
					next.getBegin());

			if(current.getQuantityType() == null || current.getNormalizedUnit() == null)
				continue;
			
			// Check that the quantities are only separated by whitespace,
			// and that they have the same type and unit
			if (whitespace.matcher(between).matches()
					&& current.getQuantityType().equals(next.getQuantityType())
					&& current.getNormalizedUnit().equals(
							next.getNormalizedUnit())) {

				List<Quantity> list;
				if (toMerge.containsKey(current)) {
					list = toMerge.get(current);
				} else {
					list = new LinkedList<Quantity>();
					list.add(current);
					toMerge.put(current, list);
					mergeables.add(list);
				}
				list.add(next);
				toMerge.put(next, list);
			}
		}
		return mergeables;
	}

}
