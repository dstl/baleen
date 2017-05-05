//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.templates;

import java.util.List;

import uk.gov.dstl.baleen.uima.utils.SelectorPath;

/**
 * Class to contain the possible search strategies for repeating record units.
 * <p>
 * Due to the constraints on where the record annotations can be placed
 * different strategies to find the repeating parts are required.
 *
 *
 */
public class RepeatSearch {

	/**
	 * The full structure between the previous structure and next structure from
	 * the record annotation
	 */
	private List<SelectorPath> coveredRepeat;

	/**
	 * The minimal covered structure of the record annotation, in some cases
	 * this is a better candidate for the repeat.
	 */
	private SelectorPath minimalRepeat;

	/**
	 * Constructor for the repeat search candidates
	 * 
	 * @param coveredRepeat
	 *            the covered repeat
	 * @param minimalRepeat
	 *            the minimal repeat
	 */
	public RepeatSearch(List<SelectorPath> coveredRepeat, SelectorPath minimalRepeat) {
		this.coveredRepeat = coveredRepeat;
		this.minimalRepeat = minimalRepeat;
	}

	/**
	 * Get the covered repeat candidate
	 *
	 * @return the covered repeat
	 */
	public List<SelectorPath> getCoveredRepeat() {
		return coveredRepeat;
	}

	/**
	 * Get the minimal repeat candidate
	 *
	 * @return the minimal repeat
	 */
	public SelectorPath getMinimalRepeat() {
		return minimalRepeat;
	}

}