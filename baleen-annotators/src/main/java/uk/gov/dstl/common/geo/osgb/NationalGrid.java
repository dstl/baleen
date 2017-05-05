//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.common.geo.osgb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.primitives.Doubles;

/** Convert National grid (TM 123412 23434) to OSGB Northing and Easting.
 * 
 */
public class NationalGrid {
	private static final Splitter WHITESPACE_SPLITTER = Splitter.on(CharMatcher.whitespace()).omitEmptyStrings().trimResults();
	private static final double SIZE_M = 100000;
	
	private static final class GridSquare {
		private final String reference;
		private final double northing;
		private final double easting;
		
		/**
		 * Constructor to create a new GridSquare for the given reference, easting and northing
		 * 
		 * @param reference
		 * @param easting
		 * @param northing
		 */
		public GridSquare(String reference, double easting, double northing) {
			this.reference = reference;
			this.northing = northing;
			this.easting = easting;	
		}
		
		/**
		 * Return the Reference of this grid square (e.g. SU)
		 */
		public String getReference() {
			return reference;
		}
		
		/**
		 * Is the given Easting-Northing pair inside this grid square?
		 * 
		 * @param en An array of Easting-Northing coordinates (in that order)
		 */
		public final boolean inside(double[] en) {
			return inside(en[0], en[1]);
		}	
		
		/**
		 * Is the given Easting-Northing pair inside this grid square?
		 * 
		 * @param e Easting
		 * @param n Northing
		 */
		public final boolean inside(double e, double n) {
			return northing <= n && n < northing + SIZE_M && easting <= e && e < easting + SIZE_M;
		}	
		
		/**
		 * Return the offset of the provided absolute Easting-Northing within this grid square
		 * 
		 * @param en An array of Easting-Northing coordinates (in that order)
		 */
		public double[] offsetEastingNorthing(double[] en) {
			return offsetEastingNorthing(en[0], en[1]);
		}
		
		/**
		 * Return the offset of the provided absolute Easting-Northing within this grid square
		 * 
		 * @param e Easting
		 * @param n Northing
		 */
		public double[] offsetEastingNorthing(double e, double n) {
			return new double[] { e - easting, n - northing };
		}

		/**
		 * Convert an Easting-Northing from a relative (to this grid square) pair to an absolute pair 
		 * 
		 * @param e Easting
		 * @param n Northing
		 */
		public double[] toEastingNorthing(double e, double n) {
			return new double[] { e + easting, n + northing };
		}

	}
	
	private static final Map<String, GridSquare> GRID_SQUARES;
	
	static {
		GRID_SQUARES = new HashMap<String, NationalGrid.GridSquare>();

		addGridSquare("SV", 0, 0);
		addGridSquare("SW", 1, 0);
		addGridSquare("SX", 2, 0);
		addGridSquare("SY", 3, 0);
		addGridSquare("SZ", 4, 0);
		addGridSquare("TV", 5, 0);
		addGridSquare("TW", 6, 0);

		addGridSquare("SQ", 0, 1);
		addGridSquare("SR", 1, 1);
		addGridSquare("SS", 2, 1);
		addGridSquare("ST", 3, 1);
		addGridSquare("SU", 4, 1);
		addGridSquare("TQ", 5, 1);
		addGridSquare("TR", 6, 1);

		addGridSquare("SL", 0, 2);
		addGridSquare("SM", 1, 2);
		addGridSquare("SN", 2, 2);
		addGridSquare("SO", 3, 2);
		addGridSquare("SP", 4, 2);
		addGridSquare("TL", 5, 2);
		addGridSquare("TM", 6, 2);

		addGridSquare("SF", 0, 3);
		addGridSquare("SG", 1, 3);
		addGridSquare("SH", 2, 3);
		addGridSquare("SJ", 3, 3);
		addGridSquare("SK", 4, 3);
		addGridSquare("TF", 5, 3);
		addGridSquare("TG", 6, 3);

		addGridSquare("SA", 0, 4);
		addGridSquare("SB", 1, 4);
		addGridSquare("SC", 2, 4);
		addGridSquare("SD", 3, 4);
		addGridSquare("SE", 4, 4);
		addGridSquare("TA", 5, 4);
		addGridSquare("TB", 6, 4);

		// n AND o
		
		addGridSquare("NV", 0, 5);
		addGridSquare("NW", 1, 5);
		addGridSquare("NX", 2, 5);
		addGridSquare("NY", 3, 5);
		addGridSquare("NZ", 4, 5);
		addGridSquare("OV", 5, 5);
		addGridSquare("OW", 6, 5);

		addGridSquare("NQ", 0, 6);
		addGridSquare("NR", 1, 6);
		addGridSquare("NS", 2, 6);
		addGridSquare("NT", 3, 6);
		addGridSquare("NU", 4, 6);
		addGridSquare("OQ", 5, 6);
		addGridSquare("OR", 6, 6);
		
		addGridSquare("NL", 0, 7);
		addGridSquare("NM", 1, 7);
		addGridSquare("NN", 2, 7);
		addGridSquare("NO", 3, 7);
		addGridSquare("NP", 4, 7);
		addGridSquare("OL", 5, 7);
		addGridSquare("OM", 6, 7);

		addGridSquare("NF", 0, 8);
		addGridSquare("NG", 1, 8);
		addGridSquare("NH", 2, 8);
		addGridSquare("NJ", 3, 8);
		addGridSquare("NK", 4, 8);
		addGridSquare("OF", 5, 8);
		addGridSquare("OG", 6, 8);

		addGridSquare("NA", 0, 9);
		addGridSquare("NB", 1, 9);
		addGridSquare("NC", 2, 9);
		addGridSquare("ND", 3, 9);
		addGridSquare("NE", 4, 9);
		addGridSquare("OA", 5, 9);
		addGridSquare("OB", 6, 9);

		
		// h & j
		
		addGridSquare("HV", 0, 10);
		addGridSquare("HW", 1, 10);
		addGridSquare("HX", 2, 10);
		addGridSquare("HY", 3, 10);
		addGridSquare("HZ", 4, 10);
		addGridSquare("JV", 5, 10);
		addGridSquare("JW", 6, 10);

		addGridSquare("HQ", 0, 11);
		addGridSquare("HR", 1, 11);
		addGridSquare("HS", 2, 11);
		addGridSquare("HT", 3, 11);
		addGridSquare("HU", 4, 11);
		addGridSquare("JQ", 5, 11);
		addGridSquare("JR", 6, 11);

		
		addGridSquare("HL", 0, 12);
		addGridSquare("HM", 1, 12);
		addGridSquare("HN", 2, 12);
		addGridSquare("HO", 3, 12);
		addGridSquare("HP", 4, 12);
		addGridSquare("JL", 5, 12);
		addGridSquare("JM", 6, 12);
	}
	
	private NationalGrid() {
		//Utility class - private constructor
	}
	
	private static void addGridSquare(String reference, int x, int y) {
		GRID_SQUARES.put(reference, new GridSquare(reference, x*SIZE_M, y*SIZE_M));
	}
	
	/** Convert from a string to northing and easting
	 * @param ng the national grid string
	 * @return array [n,e]
	 */
	public static double[] fromNationalGrid(String ng) {
		String trimmed = ng.trim();
		String ref = trimmed.substring(0,2);
		
		GridSquare gridSquare = GRID_SQUARES.get(ref);
		
		if(gridSquare == null) {
			throw new IllegalArgumentException("Invalid NG: "+trimmed);
		}

		List<String> list = WHITESPACE_SPLITTER.splitToList(trimmed.substring(2));
		
		Double n = null;
		Double e = null;
		if(list.size() >= 2) {
			// Have two values so use them
			e = parseDoubleWithCoordPrecision(list.get(0));
			n = parseDoubleWithCoordPrecision(list.get(1));
		} else if(list.size() == 1) {
			// Consolidated value
			String[] ret = splitConsolidated(list.get(0));
			e = parseDoubleWithCoordPrecision(ret[0]);
			n = parseDoubleWithCoordPrecision(ret[1]);
		} else {
			throw new IllegalArgumentException("Invalid NG coords "+ng);
		}
		
		if(n == null || e == null) { 
			throw new IllegalArgumentException("Unable to extract NE from "+ng);
		}

		return gridSquare.toEastingNorthing(e, n);
	}
	
	/**
	 * Split a string into two Strings
	 * 
	 * @param s The string to split
	 * @return An array, containing the Easting and Northing (in that order) split from a string
	 */
	private static String[] splitConsolidated(String s){
		if((s.length() % 2) != 0) {
			throw new IllegalArgumentException("Differing size of northing and easting, unable to determine valid ref "+s);
		} 
		
		int index = s.length() / 2;		
		return new String[]{
			s.substring(0, index),	//Easting
			s.substring(index)		//Northing
		};
	}
	
	private static Double parseDoubleWithCoordPrecision(String s){
		int precedingZeroes = 0;
		String t = s;
		while(t.startsWith("0")){
			precedingZeroes++;
			t = t.substring(1);
		}
		
		Double c = Doubles.tryParse(t);
		if(c == null){
			return null;
		}
		double multiplier = Math.pow(10, 4 - precedingZeroes - Math.floor(Math.log10(c)));
		return c * multiplier;
	}
	
	/** Convert EastingsNorthings to a NationalGrid reference.
	 * 
	 * This is not a very optimised implementation. 
	 * 
	 * @param en array of {e,n}
	 * @return optional empty if the ne has no representation
	 */
	public static Optional<String> toNationalGrid(double[] en) {
		for(GridSquare gs : GRID_SQUARES.values()) {
			if(gs.inside(en)) {
				double[] offset = gs.offsetEastingNorthing(en);
				return Optional.of( String.format("%s %05.0f %05.0f", gs.getReference(), offset[0], offset[1]) );
			}
		}
		return Optional.empty();
	}

}
