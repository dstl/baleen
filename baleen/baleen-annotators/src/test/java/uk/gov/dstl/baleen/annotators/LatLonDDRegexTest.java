//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.LatLon;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.types.TestCoordinate;
import uk.gov.dstl.baleen.types.geo.Coordinate;

/** Tests the DD implementation of {@link LatLon}.
 * 
 */
public class LatLonDDRegexTest extends AbstractAnnotatorTest{
	
	private static final String TYPE_POINT_COORDINATES_0_125_51_507 = "{\"type\":\"Point\",\"coordinates\":[-0.125,51.507]}";
	private static final String TYPE_POINT_COORDINATES_3_188_55_953 = "{\"type\":\"Point\",\"coordinates\":[-3.188,55.953]}";

	public LatLonDDRegexTest() {
		super(LatLon.class);
	}

	@Test
	public void testLatlon() throws Exception{
	
		jCas.setDocumentText("London is located in the UK, at 51.507, -0.125. Edinburgh is also in the UK, at 55.953,-3.188. The following coordinates aren't valid: 987.654,32.1; 12.3,456.789.");
		processJCas();
		
		
		assertAnnotations(2, Coordinate.class,
				new TestCoordinate(0, "51.507, -0.125", "dd", TYPE_POINT_COORDINATES_0_125_51_507),
				new TestCoordinate(1, "55.953,-3.188", "dd", TYPE_POINT_COORDINATES_3_188_55_953)
		);

	}
	
	@Test
	public void testNoDelimiter() throws Exception{
	
		jCas.setDocumentText("London is located in the UK, at 51.507, -0.125. Edinburgh is also in the UK, at 55.953,-3.188. The following coordinates aren't valid: 87.65432.1; 12.3-56.789.");
		processJCas();
		
		
		assertAnnotations(2, Coordinate.class,
				new TestCoordinate(0, "51.507, -0.125", "dd", TYPE_POINT_COORDINATES_0_125_51_507),
				new TestCoordinate(1, "55.953,-3.188", "dd", TYPE_POINT_COORDINATES_3_188_55_953)
		);

	}
	
	@Test
	public void testLonlat() throws Exception{
		
		jCas.setDocumentText("London is located in the UK, at -0.125, 51.507. Edinburgh is also in the UK, at -3.188,55.953.");
		processJCas("lonlat", true);
		
		assertAnnotations(2, Coordinate.class,
				new TestCoordinate(0, "-0.125, 51.507", "dd", TYPE_POINT_COORDINATES_0_125_51_507),
				new TestCoordinate(1, "-3.188,55.953", "dd", TYPE_POINT_COORDINATES_3_188_55_953)
		);

	}
	
	@Test
	public void testMinDPString() throws Exception{
		
		jCas.setDocumentText("London is located in the UK, at 51.507, -0.125. Edinburgh is also in the UK, at -3.2,56.0.");
		processJCas("minDP", "2");
		
		assertAnnotations(1, Coordinate.class,
				new TestCoordinate(0, "51.507, -0.125", "dd", TYPE_POINT_COORDINATES_0_125_51_507)
		);

	}
	
	@Test
	public void testMoney() throws Exception{
		
		jCas.setDocumentText("It may cost £2,000 a month to live in London, but that doesn't mean there are any coordiantes in this sentence!");
		processJCas();
		
		assertAnnotations(0, Coordinate.class);
	}

}
