//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.regex;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.core.utils.ConfigUtils;
import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Annotate Latitude-Longitude coordinates in decimal (DD) and degrees-minutes-seconds (DMS) format using regular expressions.
 * 
 * <p><b>Decimal degree (DD)</b></p>
 * <p>The document content is run through a regular expression matcher looking for latitude-longitude pairs in DD
 * (e.g. 51.068787 -1.794472 or 51.068787° -1.794472° or 51.068787°N 1.794472°W).
 * If the minimum number of decimal places required is 0, the following regular expressions are used:</p>
 * <pre>(-?\\d{1,3}(\\.\\d+)?)(,\\h*|\\h+)(-?\\d{1,3}(\\.\\d+)?)</pre>
 * <pre>(-?\\d{1,3}(\\.\\d+)?)°(,\\h*|\\h+)(-?\\d{1,3}(\\.\\d+)?)°</pre>
 * <pre>\\b(\\d{1,3}(\\.\\d+)?)°( )?([NSEW])(,\\h*|\\h+)(\\d{1,3}(\\.\\d+)?)°( )?([NSEW])</pre>
 * 
 * <p>If the minimum number of decimal places required is greater than 0, the following regular expressions are used
 * where x is the minimum number of decimal places:</p>
 * <pre>(-?\\d{1,3}(\\.\\d{x,}))(,\\h*|\\h+)(-?\\d{1,3}(\\.\\d{x,}))</pre>
 * <pre>(-?\\d{1,3}(\\.\\d{x,}))°(,\\h*|\\h+)(-?\\d{1,3}(\\.\\d{x,}))°</pre>
 * <pre>\\b(\\d{1,3}(\\.\\d{x,}))°( )?([NSEW])(,\\h*|\\h+)(\\d{1,3}(\\.\\d{x,}))°( )?([NSEW])</pre>
 * 
 * <p>The latitude and longitude are extracted (optionally the user can specify it should be longitude and latitude instead)
 * and a GeoJSON object is built representing this location.</p>
 * <p>Coordinates that are preceded by a £, $ or € symbol are skipped as they are assumed to be monetary values rather than coordinates (e.g. £3,000).</p>
 * <p>The regular expressions used when searching for Digital Degree formats that use the minus sign for southern and western hemispheres do not
 * include word boundaries when looking for matches because this can cause the expression to fail to extract a first negative sign.
 * This means the annotator will extract, for example, currency strings (e.g. $40,000).</p>
 * 
 * <p><b>Degrees-minutes-seconds (DMS)</b></p>
 * <p>The document content is run through a regular expression matcher looking for latitude-longitude pairs in DMS format
 * (e.g. 51°4'7.6332"N 1°47'40.0992"W) that match the following regular expression:</p>
 * <pre>\\b(\\d{1,3})°(\\d{1,2})'(\\d{1,2}(\\.\\d*)?)\"([NSEW])[,/\\h]*(\\d{1,3})°(\\d{1,2})'(\\d{1,2}(\\.\\d*)?)\"([NSEW])\\b</pre>
 * <p>A similar regex is used to find pairs that have spaces instead of symbols in them (e.g. 10 12 14 N, 11 13 15 E),
 * and another regex to find pairs with no spaces.</p>
 * <p>The following regexes are also used to pick up other formats:</p>
 * <ul>
 * <li><pre>\\b(lat|latitude)\\h*(\\d{1,2})°\\h*(\\d{1,2}(\\.\\d+)?)'(\\h*(\\d{1,2}(\\.\\d+)?)\")?\\h*([NS])\\.?,?\\h*(lon|long|longitude)\\h*(\\d{1,3})°\\h*(\\d{1,2}(\\.\\d+)?)'(\\h*(\\d{1,2}(\\.\\d+)?)\")?\\h*([EW])\\b</pre></li>
 * <li><pre>\\b(lat|latitude)\\h*(\\d{1,2})°\\h*(\\d{1,2})'\\.(\\d+)\\h*([NS])\\.?,?\\h*(lon|long|longitude)\\h*(\\d{1,3})°\\h*(\\d{1,2})'\\.(\\d+)\\h*([EW])\\b</pre></li>
 * </ul>
 * <p>Some validation is done on the extracted text, then the latitude and longitude are extracted and a GeoJSON object is
 * built representing this location.</p>
 * 
 * 
 * @baleen.javadoc
 */
public class LatLon extends BaleenAnnotator {

	private final Pattern llDMSPattern = Pattern
			.compile("\\b(\\d{1,3})°(\\d{1,2})'(\\d{1,2}(\\.\\d+)?)\"([NSEW])[,/\\h]*(\\d{1,3})°(\\d{1,2})'(\\d{1,2}(\\.\\d+)?)\"([NSEW])\\b");
	private final Pattern llDMSSpacePattern = Pattern
			.compile("\\b(\\d{1,3}) (\\d{1,2}) (\\d{1,2}(\\.\\d+)?) ([NSEW])[,/\\h]*(\\d{1,3}) (\\d{1,2}) (\\d{1,2}(\\.\\d+)?) ([NSEW])\\b");
	private final Pattern llDMSNumericPattern = Pattern
			.compile("\\b(\\d{2,3})(\\d{2})(\\d{2})?( )?([NSEW])[,/\\h]*(\\d{2,3})(\\d{2})(\\d{2})?( )?([NSEW])\\b");
	private final Pattern llDMSPunctuationPattern = Pattern
			.compile("\\b(\\d{2,3})-(\\d{2}),(\\d{2})?( )?([NSEW])[,/\\h]*(\\d{2,3})-(\\d{2}),(\\d{2})?( )?([NSEW])\\b");
	
	private final Pattern llDMSTextPattern = Pattern
			.compile("\\b((lat|latitude)\\h*)?(\\d{1,2})°\\h*(\\d{1,2}(\\.\\d+)?)'(\\h*(\\d{1,2}(\\.\\d+)?)\")?\\h*([NS])\\.?,?\\h*(lon|long|longitude)?\\h*(\\d{1,3})°\\h*(\\d{1,2}(\\.\\d+)?)'(\\h*(\\d{1,2}(\\.\\d+)?)\")?\\h*([EW])\\b", Pattern.CASE_INSENSITIVE);
	private final Pattern llDMTextPattern = Pattern
			.compile("\\b((lat|latitude)\\h*)?(\\d{1,2})°\\h*(\\d{1,2})'\\.(\\d+)\\h*([NS])\\.?,?\\h*(lon|long|longitude)?\\h*(\\d{1,3})°\\h*(\\d{1,2})'\\.(\\d+)\\h*([EW])\\b", Pattern.CASE_INSENSITIVE);

	/**
	 * Tell the annotator that coordinates are specified with the longitude first rather than the latitude.
	 * 
	 * @baleen.config false
	 */
	public static final String PARAM_LONLAT = "lonlat";
	@ConfigurationParameter(name = PARAM_LONLAT, defaultValue = "false")
	private boolean lonlat;

	/**
	 * Use the coordinate translated into decimal as the string to store in the
	 * Value field of the Entity, allowing a normalised format for all located
	 * coordinates. This option will also format the values to a maximum of 7.d.p.
	 * The use of cardinal point strings and the ordering are defined
	 * by other resources.
	 * 
	 * @baleen.config false
	 */
	public static final String PARAM_STORE_DECIMAL = "storeDecimalValue";
	@ConfigurationParameter(name = PARAM_STORE_DECIMAL, defaultValue = "false")
	private boolean storeDecimalValue;
	
	/**
	 * Store the appropriate cardinal points (N, S, E, W) with digital degree
	 * values when normalising the text stored in the value field.
	 * 
	 * @baleen.config false
	 */
	public static final String PARAM_STORE_CARDINAL = "storeCardinalPoint";
	@ConfigurationParameter(name = PARAM_STORE_CARDINAL, defaultValue = "false")
	private boolean storeCardinalPoint;
	
	/**
	 * Store the digital degree values with the longitude first instead
	 * of the default latitude first when normalising the text stored in
	 * the value field.
	 * 
	 * @baleen.config false
	 */
	public static final String PARAM_STORE_LON_FIRST = "storeLongitudeFirst";
	@ConfigurationParameter(name = PARAM_STORE_LON_FIRST, defaultValue = "false")
	private boolean storeLongitudeFirst;
	
	/**
	 * The minimum number of decimal places required when parsing Decimal Degrees.
	 * If 0, then any number of decimal places is accepted.
	 * 
	 * @baleen.config 2
	 */
	public static final String PARAM_MIN_DP = "minDP";
	@ConfigurationParameter(name = PARAM_MIN_DP, defaultValue = "2")
	private String minDPString;
	
	//Parse the minDP config parameter into this variable to avoid issues with parameter types
	private int minDP;

	/**
	 * Variable to hold the regular expression pattern for Digital Degrees
	 */
	private Pattern llDDPattern;

	/**
	 * Variable to hold the regular expression pattern for Digital Degrees with degree symbol
	 */
	private Pattern llDDSymPattern;

	/**
	 * Variable to hold the regular expression pattern for Digital Degrees with NSEW symbols
	 */
	private Pattern llDDCardPattern;

	/**
	 * List of currency symbols to check for when excluding monetary values
	 */
	private List<String> currencySymbols = Arrays.asList("£", "$", "€");

	/**
	 * Set of already found coordinates (per document) to avoid different patterns picking out the same coordinate
	 */
	private Set<String> found;
	
	/**
	 * Initialise the annotator - primarily, this sets the regular expression to
	 * the correct pattern for the user specified minDP (minimum decimal places)
	 */
	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {

		minDP = ConfigUtils.stringToInteger(minDPString, 2);
		
		if (minDP == 0) {
			// No word boundary characters as that excludes negative signs
			llDDPattern = Pattern
					.compile("(-?\\d{1,3}(\\.\\d+)?)(,\\h*|\\h+)(-?\\d{1,3}(\\.\\d+)?)");
			llDDSymPattern = Pattern
					.compile("(-?\\d{1,3}(\\.\\d+)?)°(,\\h*|\\h+)(-?\\d{1,3}(\\.\\d+)?)°");
			llDDCardPattern = Pattern
					.compile("\\b(\\d{1,3}(\\.\\d+)?)°( )?([NSEW])(,\\h*|\\h+)(\\d{1,3}(\\.\\d+)?)°( )?([NSEW])");
		} else {
			llDDPattern = Pattern.compile("(-?\\d{1,3}(\\.\\d{" + minDP
					+ ",}))(,\\h*|\\h+)(-?\\d{1,3}(\\.\\d{" + minDP + ",}))");
			llDDSymPattern = Pattern.compile("(-?\\d{1,3}(\\.\\d{" + minDP
					+ ",}))°(,\\h*|\\h+)(-?\\d{1,3}(\\.\\d{" + minDP + ",}))°");
			llDDCardPattern = Pattern.compile("\\b(\\d{1,3}(\\.\\d{" + minDP
					+ ",}))°( )?([NSEW])(,\\h*|\\h+)(\\d{1,3}(\\.\\d{" + minDP + ",}))°( )?([NSEW])");
		}
	}

	/**
	 * Extract decimal coordinate pairs from the document, and add validated
	 * coordinates to the CAS
	 */
	@Override
	public void doProcess(JCas aJCas) throws AnalysisEngineProcessException {
		found = new HashSet<>();
		String text = normalizeQuotesAndDots(aJCas.getDocumentText());
		
		processDD(aJCas, text);
		processDDCard(aJCas, text);
		processDMS(aJCas);
		processDMSText(aJCas);
	}

	/**
	 * Searches the text for digital degree strings
	 * This method handles digital degree strings consisting of a
	 * pair of decimal numbers separated by whitespace or a comma
	 * and optionally with the degree symbol. The pair is assumed
	 * to be in the order Latitude, Longitude unless the resource
	 * lonlat has been set to true.
	 * @param aJCas the JCas object to hold the created Coordinate
	 *              annotation.
	 * @param text  the text string to search for coordinates.
	 */
	private void processDD(JCas aJCas, String text) {

		Pattern[] patterns = new Pattern[] { llDDPattern, llDDSymPattern };
		for (Pattern p : patterns) {
			Matcher matcher = p.matcher(text);

			while (matcher.find()) {
				if (currencySymbols.contains(text.substring(matcher.start(1) - 1,
						matcher.start(1)))) {
					getMonitor()
					.info("Skipping coordinate as it is preceded by a currency symbol");
					continue;
				}

				try {
					Double lat;
					Double lon;

					if (!lonlat) {
						lat = Double.parseDouble(matcher.group(1));
						lon = Double.parseDouble(matcher.group(4));
					} else {
						lon = Double.parseDouble(matcher.group(1));
						lat = Double.parseDouble(matcher.group(4));
					}

					addCoordinate(aJCas, matcher, lon, lat, "dd");

				} catch (NumberFormatException e) {
					getMonitor().warn("Couldn't parse extracted coordinates - coordinate will be skipped", e);
				}
			}
		}
	}

	/**
	 * Searches the text for digital degree strings with cardinal points.
	 * This method handles processing of digital degree strings
	 * consisting of a pair of decimal numbers separated by whitespace
	 * or a comma and each number is followed by a degree symbol and
	 * a letter representing its cardinal compass point, i.e. N, S, E, W.
	 * The cardinal point is used to determine which value is latitude and
	 * which is longitude.
	 * 
	 * @param aJCas the JCas object to hold the created Coordinate
	 *              annotation.
	 * @param text  the text string to search for coordinates.
	 */
	private void processDDCard(JCas aJCas, String text) {

		Matcher matcher = llDDCardPattern.matcher(text);
		while (matcher.find()) {
			// If no valid cardinal point letter then skip it
			if(!isValidPair(matcher.group(4), matcher.group(9))){
				continue;
			}

			try {
				Double lat;
				Double lon;

				// Assume latitude first
				lat = Double.parseDouble(matcher.group(1));
				lon = Double.parseDouble(matcher.group(6));
				if ("E".equals(matcher.group(4))
						|| "W".equals(matcher.group(4))) {
					// Actually longitude first so swap values
					Double tmp = lat;
					lat = lon;
					lon = tmp;
				}

				if(flipLon(matcher.group(4), matcher.group(9))){
					lon = -lon;
				}

				if(flipLat(matcher.group(4), matcher.group(9))){
					lat = -lat;
				}

				addCoordinate(aJCas, matcher, lon, lat, "dd");

			} catch (NumberFormatException e) {
				getMonitor().warn("Couldn't parse extracted coordinates - coordinate will be skipped", e);
			}
		}
	}

	private void processDMS(JCas aJCas) throws AnalysisEngineProcessException {
		String text = normalizeQuotesAndDots(aJCas.getDocumentText());

		Pattern[] patterns = new Pattern[] { llDMSPattern, llDMSSpacePattern, llDMSNumericPattern, llDMSPunctuationPattern };

		for (Pattern p : patterns) {
			Matcher matcher = p.matcher(text);

			while (matcher.find()) {
				if(!isValidPair(matcher.group(5), matcher.group(10))){
					continue;
				}
				
				try {
					double[] lonLat = determineLonLatDMS(matcher);
					addCoordinate(aJCas, matcher, lonLat[0], lonLat[1], "dms");
				} catch (NumberFormatException e) {
					getMonitor().warn("Couldn't parse extracted coordinates - coordinate will be skipped", e);
				}
			}
		}
	}
	
	private void processDMSText(JCas aJCas) throws AnalysisEngineProcessException {
		String text = normalizeQuotesAndDots(aJCas.getDocumentText());

		Matcher m = llDMSTextPattern.matcher(text);
		while(m.find()){
			Double lat = Double.parseDouble(m.group(3));
			lat += Double.parseDouble(m.group(4))/60;
			if(m.group(7) != null)
				lat += Double.parseDouble(m.group(7))/3600;
			if("S".equalsIgnoreCase(m.group(9)))
				lat = -lat;
			
			Double lon = Double.parseDouble(m.group(11));
			lon += Double.parseDouble(m.group(12))/60;
			if(m.group(15) != null)
				lon += Double.parseDouble(m.group(15))/3600;
			if("W".equalsIgnoreCase(m.group(17)))
				lon = -lon;
			
			addCoordinate(aJCas, m, lon, lat, "dms");
		}
		
		m = llDMTextPattern.matcher(text);
		while(m.find()){
			Double lat = Double.parseDouble(m.group(3));
			lat += Double.parseDouble(m.group(4))/60;
			lat += Double.parseDouble(m.group(5))/3600;
			if("S".equalsIgnoreCase(m.group(6)))
				lat = -lat;
			
			Double lon = Double.parseDouble(m.group(8));
			lon += Double.parseDouble(m.group(9))/60;
			lon += Double.parseDouble(m.group(10))/3600;
			if("S".equalsIgnoreCase(m.group(11)))
				lon = -lon;
			
			addCoordinate(aJCas, m, lon, lat, "dms");
		}
	}

	private double[] determineLonLatDMS(Matcher matcher){
		Double lat = 0.0;
		Double lon = 0.0;
		
		lat = dmsToDeg(Integer.parseInt(matcher.group(1)),
			Integer.parseInt(matcher.group(2)),
			parseOrNull(matcher.group(3)));

		lon = dmsToDeg(Integer.parseInt(matcher.group(6)),
			Integer.parseInt(matcher.group(7)),
			parseOrNull(matcher.group(8)));
		
		if ("E".equals(matcher.group(5))
				|| "W".equals(matcher.group(5))) {
			Double tmp = lat;
			lat = lon;
			lon = tmp;
		}

		if(flipLon(matcher.group(5), matcher.group(10))){
			lon = -lon;
		}

		if(flipLat(matcher.group(5), matcher.group(10))){
			lat = -lat;
		}
		
		return new double[]{lon, lat};
	}
	
	/**
	 * Determines whether we have both a North/South and an East/West directional indicator present 
	 */
	private boolean isValidPair(String... parameters){
		boolean nFound = false;
		boolean eFound = false;
		
		for(String s : parameters){
			if("N".equalsIgnoreCase(s) || "S".equalsIgnoreCase(s)){
				nFound = true;
			}else if("E".equalsIgnoreCase(s) || "W".equalsIgnoreCase(s)){
				eFound = true;
			}
		}
		
		return nFound && eFound;
	}
	
	private boolean flipLat(String... parameters){
		for(String s : parameters){
			if("S".equalsIgnoreCase(s)){
				return true;
			}
		}
		
		return false;
	}
	
	private boolean flipLon(String... parameters){
		for(String s : parameters){
			if("W".equalsIgnoreCase(s)){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Converts a Degrees Minutes Seconds coordinate into a decimal degree value
	 * The conversion is ignorant of the cardinality (N, S, E, W) so degrees
	 * value should be positive and the conversion of an S latitude or W longitude
	 * coordinate to a negative value should be carried out by the calling function.
	 * 
	 * @param d number of degrees. It is assumed this is a positive value
	 * @param m number of minutes
	 * @param s number of seconds, or null if no seconds supplied
	 * @return the decimal degree value for the degrees minutes seconds
	 */
	private double dmsToDeg(Integer d, Integer m, Double s) {
		double seconds = m * 60.0;
		if(s != null){
			seconds += s;
		}
		return d + (seconds / 3600);
	}
	
	private Double parseOrNull(String s){
		if(s != null){
			return Double.parseDouble(s);
		}else{
			return null;
		}
	}

	private void addCoordinate(JCas aJCas, Matcher matcher, Double lon, Double lat, String coordinateType) {
		if (lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180) {
			String textLoc = matcher.start() + "," + matcher.end();

			if(found.add(textLoc)){			
				Coordinate loc = new Coordinate(aJCas);

				loc.setConfidence(1.0f);

				loc.setBegin(matcher.start());
				loc.setEnd(matcher.end());
				if (storeDecimalValue) {
					addNormalisedValue(lat, lon, loc);
				} else {
					loc.setValue(matcher.group(0));
				}

				String coords = "[" + lon + "," + lat + "]";
	
				loc.setGeoJson("{\"type\":\"Point\",\"coordinates\":"
						+ coords + "}");

				loc.setCoordinateValue(lon + "," + lat);
				loc.setSubType(coordinateType);
	
				addToJCasIndex(loc);
			}
		}
	}
	/*
	 * Formats the decimal degree values into a normalised form
	 * This method sets the Value field of the JCas Coordinate
	 * object to a normalised string rather than a copy of the
	 * original text in the document string.
	 * The format consist of decimal degree values. The use of
	 * cardinal point strings and the order is controlled by
	 * resource.
	 * 
	 * @param lat the latitude as a decimal degree (negative is S)
	 * @param lon the longitude as a decimal degree (negative is W)
	 * @param loc  the coordinate location to hold the normalised string
	 */
	private void addNormalisedValue(double lat, double lon, Coordinate loc) {
		String pattern = "###.#######";
		double normLat = lat;
		double normLon = lon;
		String latString = "";
		String lonString = "";

		DecimalFormat df = new DecimalFormat(pattern);
		if (storeCardinalPoint) {
			String cardLat = "N";
			String cardLon = "E";
			if (normLat < 0) {
				normLat = -normLat;
				cardLat = "S";
			}
			if (normLon < 0) {
				normLon = -normLon;
				cardLon = "W";
			}
			latString = df.format(normLat) + cardLat;
			lonString = df.format(normLon) + cardLon;
		}
		else {
			latString = df.format(normLat);
			lonString = df.format(normLon);			
		}
		String firstCoord = (storeLongitudeFirst) ? lonString : latString;
		String secondCoord = (storeLongitudeFirst) ? latString : lonString;
		loc.setValue(firstCoord + " " + secondCoord);
		loc.setIsNormalised(true);
	}
	
	/**
	 * Replace smart quotes, curly quotes, back ticks and mid-dots with standard quotes and dots
	 * to simplify the required regular expressions.
	 */
	public static String normalizeQuotesAndDots(String s){
		return s.replaceAll("[\\u201C\\u201D\\u2033\\u02BA\\u301E\\u3003]", "\"").replaceAll("[\\u2018\\u2019\\u2032\\u00B4\\u02B9`]", "'").replaceAll("[\\u00B7]", ".");
	}
}
