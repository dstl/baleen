//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.LatLon;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.types.TestCoordinate;
import uk.gov.dstl.baleen.types.geo.Coordinate;

/** Tests the DMS implementation of {@link LatLon}.
 * 
 */
public class LatLonDMSRegexTest extends AbstractAnnotatorTest{
	private static final String DMS = "dms";

	private static final String TYPE_POINT_COORDINATES_21_017777777777777_52_234444444444444 = "{\"type\":\"Point\",\"coordinates\":[-21.017777777777777,-52.23444444444444]}";
	private static final String TYPE_POINT_COORDINATES_21_017777777777777_52_234444444444443 = "{\"type\":\"Point\",\"coordinates\":[21.017777777777777,-52.23444444444444]}";
	private static final String TYPE_POINT_COORDINATES_21_017777777777777_52_234444444444442 = "{\"type\":\"Point\",\"coordinates\":[-21.017777777777777,52.23444444444444]}";
	private static final String TYPE_POINT_COORDINATES_43_20111111111111_22_90111111111111 = "{\"type\":\"Point\",\"coordinates\":[-43.20111111111111,-22.90111111111111]}";
	private static final String TYPE_POINT_COORDINATES_21_017777777777777_52_23444444444444 = "{\"type\":\"Point\",\"coordinates\":[21.017777777777777,52.23444444444444]}";
	private static final String TYPE_POINT_COORDINATES_21_016666666666666_52_233333333333334 = "{\"type\":\"Point\",\"coordinates\":[21.016666666666666,52.233333333333334]}";
	private static final String TYPE_POINT_COORDINATES_43_2_22_9 = "{\"type\":\"Point\",\"coordinates\":[-43.2,-22.9]}";
	private static final String TYPE_POINT_COORDINATES_0_1275_51_507222222222225 = "{\"type\":\"Point\",\"coordinates\":[-0.1275,51.507222222222225]}";

	public LatLonDMSRegexTest() {
		super(LatLon.class);
	}

	@Test
	public void testSymbols() throws Exception{
	
		jCas.setDocumentText("London is located in the UK, at 51°30'26\"N 0°7'39\"W. The following coordinates aren't valid: 12\234'56\"N; 12\"34'N 12\"34'S.");
		processJCas();
		
		assertAnnotations(1,  Coordinate.class, 
				new TestCoordinate(0, "51°30'26\"N 0°7'39\"W", DMS, TYPE_POINT_COORDINATES_0_1275_51_507222222222225));
	}
	
	@Test
	public void testSpaces() throws Exception{
	
		jCas.setDocumentText("London is located in the UK, at (51 30 26 N, 0 7 39 W). The following coordinates aren't valid: 12�34'56\"N; 12�34'N 12�34'S.");
		processJCas();
		
		assertAnnotations(1,  Coordinate.class, 
			new TestCoordinate(0, "51 30 26 N, 0 7 39 W", DMS, TYPE_POINT_COORDINATES_0_1275_51_507222222222225)
		);	
	}
	
	@Test
	public void testDegMin() throws Exception{
		
		jCas.setDocumentText("Warsaw is in Poland, at (5214N 02101E). Rio de Janeiro is in Brazil, at (2254S 04312W). The following coordinates aren't valid: (9999N 01234E); (9000S 36000W).");
		processJCas();		
		
		assertAnnotations(2,  Coordinate.class, 
				new TestCoordinate(0, "5214N 02101E", DMS, TYPE_POINT_COORDINATES_21_016666666666666_52_233333333333334),
				new TestCoordinate(1, "2254S 04312W", DMS, TYPE_POINT_COORDINATES_43_2_22_9)
		);
	}
	
	@Test
	public void testDegMinSec() throws Exception{
	
		jCas.setDocumentText("Warsaw is in Poland, at (521404N 0210104E). Rio de Janeiro is in Brazil, at (225404S 0431204W). The following coordinates aren't valid: (9999N 01234E); (9000S 36000W).");
		processJCas();

		assertAnnotations(2,  Coordinate.class, 
				new TestCoordinate(0, "521404N 0210104E", DMS, TYPE_POINT_COORDINATES_21_017777777777777_52_23444444444444),
				new TestCoordinate(1, "225404S 0431204W", DMS, TYPE_POINT_COORDINATES_43_20111111111111_22_90111111111111)
		);
	}
	
	@Test
	public void testNESW() throws Exception{
	
		jCas.setDocumentText("521404N 0210104E, 521404N 0210104W, 521404S 0210104E, 521404S 0210104W");
		processJCas();

		assertAnnotations(4,  Coordinate.class, 
				new TestCoordinate(0, "521404N 0210104E", DMS, TYPE_POINT_COORDINATES_21_017777777777777_52_23444444444444),
				new TestCoordinate(1, "521404N 0210104W", DMS, TYPE_POINT_COORDINATES_21_017777777777777_52_234444444444442),
				new TestCoordinate(2, "521404S 0210104E", DMS, TYPE_POINT_COORDINATES_21_017777777777777_52_234444444444443),
				new TestCoordinate(3, "521404S 0210104W", DMS, TYPE_POINT_COORDINATES_21_017777777777777_52_234444444444444)
		);
	}

	@Test
	public void testDegMinSecSlash() throws Exception{
	
		jCas.setDocumentText("Warsaw is in Poland, at (521404N/0210104E). Rio de Janeiro is in Brazil, at (225404S/0431204W). The following coordinates aren't valid: (9999N/01234E); (9000S/36000W).");
		processJCas();
		
		assertAnnotations(2,  Coordinate.class, 
				new TestCoordinate(0, "521404N/0210104E", DMS, TYPE_POINT_COORDINATES_21_017777777777777_52_23444444444444),
				new TestCoordinate(1, "225404S/0431204W", DMS, TYPE_POINT_COORDINATES_43_20111111111111_22_90111111111111)
		);
		
	}
}
