//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.regex;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.uima.UimaContext;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.types.geo.Coordinate;
import au.com.bytecode.opencsv.CSVReader;

/**
 * Annotate UK postcodes using RegEx and geolocate them to a point
 * 
 * <p>The following regular expression is used to find potential UK postcodes in the document:</p>
 * <pre>\\b(GIR 0AA)|((([A-Z-[QVX]][0-9][0-9]?)|(([A-Z-[QVX]][A-Z-[IJZ]][0-9][0-9]?)|(([A-Z-[QVX]][0-9][A-HJKSTUW])|([A-Z-[IJZ]][0-9][ABEHMNPRVWXY])))) [0-9][A-Z-[CIKMOV]]{2})\\b</pre>
 * <p>Once found, it is compared to a CSV of UK Postcodes to retrieve LatLon information (accurate to ~11 metres). Any postcodes that aren't in the CSV are ignored and assumed to be mistakes.</p>
 * 
 * 
 */
public class Postcode extends AbstractRegexAnnotator<Coordinate> {

	private static final String POSTCODE_REGEX = "\\b(GIR 0AA)|((([A-Z-[QVX]][0-9][0-9]?)|(([A-Z-[QVX]][A-Z-[IJZ]][0-9][0-9]?)|(([A-Z-[QVX]][0-9][A-HJKSTUW])|([A-Z-[IJZ]][0-9][ABEHMNPRVWXY])))) [0-9][A-Z-[CIKMOV]]{2})\\b";
	private Map<String, String> postcodes = null;
	
	/** New instance.
	 * 
	 */
	public Postcode() {
		super(POSTCODE_REGEX, true, 1.0);
	}
	
	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		postcodes = new HashMap<String, String>();

		try(
			CSVReader reader = new CSVReader(new InputStreamReader(getClass().getResourceAsStream("ukpostcodes.csv")));
		)
		{
			String[] line;
			while ((line = reader.readNext()) != null) {
				if(line.length < 3){
					getMonitor().warn("Corrupt line found in ukpostcodes.csv - line will be skipped");
					continue;
				}
				
				Double[] lonlat = parseLonLat(line[1],line[2]);
				
				if(lonlat.length == 0){
					getMonitor().warn("Corrupt line found in ukpostcodes.csv - line will be skipped");
				}else{
					postcodes.put(line[0].toUpperCase(), lonlat[0]+","+lonlat[1]);
				}
			}
			
			getMonitor().debug(postcodes.size()+" postcodes loaded from CSV");
		}catch(IOException e){
			getMonitor().warn("Unable to load postcode data - geospatial data will not be available", e);
		}
	}
	
	private Double[] parseLonLat(String longitude, String latitude){
		try{
			Double lon = Double.parseDouble(longitude);
			Double lat = Double.parseDouble(latitude);
			
			return new Double[]{lon, lat};
		}catch(NumberFormatException nfe){
			getMonitor().warn("Unable to parse lon lat - line will be skipped", nfe);
		}
		
		return new Double[0];
	}
	
	@Override
	protected Coordinate create(JCas jCas, Matcher matcher) {
		Coordinate loc = new Coordinate(jCas);
		
		String pcLonlat = postcodes.get(matcher.group(0).replaceAll(" ", "").toUpperCase());
		if(pcLonlat != null){
			loc.setGeoJson("{\"type\": \"Point\", \"coordinates\": ["+pcLonlat+"]}");
			loc.setCoordinateValue(pcLonlat);
			loc.setSubType("postcode");
			return loc;
		} else if(postcodes.isEmpty()){
			return loc;
		} else {
			//Else skip as it's not valid and there are postcodes loaded in
			return null;
		}
	}
	
	@Override
	public void doDestroy(){
		postcodes = null;
	}
}
