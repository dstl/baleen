//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Annotate MGRS coordinates within a document using regular expressions
 *
 * <p>
 * Military Grid Reference System (MGRS) coordinates are extracted from the
 * document content using the following regular expression:
 * </p>
 * <p>Military Grid Reference System (MGRS) coordinates are extracted from the document content using the following regular expression:</p>
 * <pre>\\b[0-6]?[0-9]\\h*([C-HJ-NP-X])\\h*[A-HJ-NP-Z][A-HJ-NP-V]\\h*(([0-9]{5}\\h*[0-9]{5})|([0-9]{4}\\h*[0-9]{4})|([0-9]{3}\\h*[0-9]{3})|([0-9]{2}\\h*[0-9]{2}))\\b</pre>
 * <p>The lower left corner of the MGRS grid cell is identified, and then the lower left corner of the adjacent grid cells are identified in order to convert the cell into a GeoJSON polygon.</p>
 * <p>Some date strings, e.g. 19MAR1968, are also valid MGRS coordinates. These can be ignored by setting the ignoreDates parameter.
 * If ignoreDates is true, then the following MGRS is used to exclude dates:</p>
 * <pre>([0-2]?[0-9]|3[01])\\h*(JAN|FEB|MAR|JUN|JUL|SEP|DEC)\\h*([0-9]{2}|[0-9]{4})</pre>
 * <p>Does not currently convert MGRS to LatLon (TEXT-140).</p>
 *
 * 
 * @baleen.javadoc
 */
public class Mgrs extends BaleenAnnotator {
	private final Pattern mgrsPattern = Pattern.compile("\\b[0-6]?[0-9]\\h*([C-HJ-NP-X])\\h*[A-HJ-NP-Z][A-HJ-NP-V]\\h*(([0-9]{5}\\h*[0-9]{5})|([0-9]{4}\\h*[0-9]{4})|([0-9]{3}\\h*[0-9]{3})|([0-9]{2}\\h*[0-9]{2}))\\b");
	private final Pattern datesPattern = Pattern.compile("([0-2]?[0-9]|3[01])\\h*(JAN|FEB|MAR|JUN|JUL|SEP|DEC)\\h*([0-9]{2}|[0-9]{4})");

	/**
	 * Should MGRS coordinates that may refer to dates be ignored?
	 *
	 * @baleen.config false
	 */
	public static final String PARAM_IGNORE_DATES = "ignoreDates";
	@ConfigurationParameter(name = PARAM_IGNORE_DATES, defaultValue = "false")
	private boolean ignoreDates;

	@Override
	public void doProcess(JCas aJCas) throws AnalysisEngineProcessException {
		String text = aJCas.getDocumentText();

		Matcher matcher = mgrsPattern.matcher(text);

		while (matcher.find()) {

			if (ignoreDates) {
				Matcher dateMatcher = datesPattern.matcher(matcher.group(0));
				if (dateMatcher.matches()) {
					getMonitor().info("Discarding possible MGRS coordinate '{}' as it resembles a date",
							matcher.group(0));
					continue;
				}
			}

			Coordinate loc = new Coordinate(aJCas);

			loc.setConfidence(1.0f);

			loc.setBegin(matcher.start());
			loc.setEnd(matcher.end());
			loc.setValue(matcher.group(0));

			loc.setSubType("mgrs");

			enhanceCoordinate(matcher, loc);

			addToJCasIndex(loc);
		}
	}

	/**
	 * Allows child classes to implement additional extraction to enhance the
	 * coordinate (eg to add lat lon)
	 * 
	 * @param matcher
	 * @param loc
	 */
	protected void enhanceCoordinate(Matcher matcher, Coordinate loc) {
		// Do nothing
	}

}
