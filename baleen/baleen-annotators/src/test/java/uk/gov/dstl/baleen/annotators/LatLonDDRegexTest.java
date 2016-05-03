//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.util.JCasUtil;
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
	private static final String TYPE_POINT_COORDINATES_56_3_2 = "{\"type\":\"Point\",\"coordinates\":[56.0,-3.2]}";
	private static final String TYPE_POINT_COORDINATES_75_0152_09_190 = "{\"type\":\"Point\",\"coordinates\":[-75.0152,-9.19]}";
	private static final String TYPE_POINT_COORDINATES_127_766_35_907 = "{\"type\":\"Point\",\"coordinates\":[127.7669,35.9078]}";

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
	public void test0MinDPString() throws Exception{
		
		jCas.setDocumentText("London is located in the UK, at 51.507, -0.125. Edinburgh is also in the UK, at -3.2,56.");
		processJCas("minDP", "0");
		
		assertAnnotations(2, Coordinate.class,
				new TestCoordinate(0, "51.507, -0.125", "dd", TYPE_POINT_COORDINATES_0_125_51_507),
				new TestCoordinate(1, "-3.2,56", "dd", TYPE_POINT_COORDINATES_56_3_2)
		);

	}
	
	@Test
	public void testDegreeSym() throws Exception{
		String docText = "London is located in the UK, at 51.507°, -0.125°. Edinburgh is also in the UK, at 55.9530,-3.1880."
				+ "But darkest Peru is at -9.19°, -75.0152° and South Korea at 35.9078°, 127.7669°.";
		jCas.setDocumentText(docText);
		processJCas();
		
		
		assertAnnotations(4, Coordinate.class,
				new TestCoordinate(0, "51.507°, -0.125°", "dd", TYPE_POINT_COORDINATES_0_125_51_507),
				new TestCoordinate(1, "55.9530,-3.1880", "dd", TYPE_POINT_COORDINATES_3_188_55_953),
				new TestCoordinate(2, "-9.19°, -75.0152°", "dd", TYPE_POINT_COORDINATES_75_0152_09_190),
				new TestCoordinate(3, "35.9078°, 127.7669°", "dd", TYPE_POINT_COORDINATES_127_766_35_907)
		);

	}
	
	@Test
	public void testCardinalSym() throws Exception{
		String docText = "London is located in the UK, at 51.507° N, 0.125°W. Edinburgh is also in the UK, at 55.9530°N,3.1880° W."
				+ "But darkest Peru is at 9.19° S, 75.0152°W and South Korea at 35.9078°N, 127.7669° E.";
		jCas.setDocumentText(docText);
		processJCas();
		
		
		assertAnnotations(4, Coordinate.class,
				new TestCoordinate(0, "51.507° N, 0.125°W", "dd", TYPE_POINT_COORDINATES_0_125_51_507),
				new TestCoordinate(1, "55.9530°N,3.1880° W", "dd", TYPE_POINT_COORDINATES_3_188_55_953),
				new TestCoordinate(2, "9.19° S, 75.0152°W", "dd", TYPE_POINT_COORDINATES_75_0152_09_190),
				new TestCoordinate(3, "35.9078°N, 127.7669° E", "dd", TYPE_POINT_COORDINATES_127_766_35_907)
		);

		Coordinate t = JCasUtil.selectByIndex(jCas, Coordinate.class, 0);
		assertEquals(false, t.getIsNormalised());
	}
	
	@Test
	public void testNormalize() throws Exception{
		String docText = "London is located in the UK, at 51.507° N, 0.125°W. Edinburgh is also in the UK, at 55.9530°N,3.1880° W."
				+ "But darkest Peru is at 9.19° S, 75.0152°W and South Korea at 35.9078°N, 127.7669° E.";
		jCas.setDocumentText(docText);
		processJCas("storeDecimalValue", true);
		
		
		assertAnnotations(4, Coordinate.class,
				new TestCoordinate(0, "51.507° N, 0.125°W", "dd", TYPE_POINT_COORDINATES_0_125_51_507),
				new TestCoordinate(1, "55.9530°N,3.1880° W", "dd", TYPE_POINT_COORDINATES_3_188_55_953),
				new TestCoordinate(2, "9.19° S, 75.0152°W", "dd", TYPE_POINT_COORDINATES_75_0152_09_190),
				new TestCoordinate(3, "35.9078°N, 127.7669° E", "dd", TYPE_POINT_COORDINATES_127_766_35_907)
		);

		Coordinate t = JCasUtil.selectByIndex(jCas, Coordinate.class, 0);
		assertEquals("51.507 -0.125", t.getValue());
		assertEquals(true, t.getIsNormalised());
		t = JCasUtil.selectByIndex(jCas, Coordinate.class, 1);
		assertEquals("55.953 -3.188", t.getValue());
		assertEquals(true, t.getIsNormalised());
		t = JCasUtil.selectByIndex(jCas, Coordinate.class, 2);
		assertEquals("-9.19 -75.0152", t.getValue());
		assertEquals(true, t.getIsNormalised());
		t = JCasUtil.selectByIndex(jCas, Coordinate.class, 3);
		assertEquals("35.9078 127.7669", t.getValue());
		assertEquals(true, t.getIsNormalised());
	}
	
	@Test
	public void testNormalizeCardinal() throws Exception{
		String docText = "London is located in the UK, at 51.507° N, 0.125°W. Edinburgh is also in the UK, at 55.9530°N,3.1880° W."
				+ "But darkest Peru is at 9.19° S, 75.0152°W and South Korea at 35.9078°N, 127.7669° E.";
		jCas.setDocumentText(docText);
		processJCas("storeDecimalValue", true, "storeCardinalPoint", true);
		
		
		assertAnnotations(4, Coordinate.class,
				new TestCoordinate(0, "51.507° N, 0.125°W", "dd", TYPE_POINT_COORDINATES_0_125_51_507),
				new TestCoordinate(1, "55.9530°N,3.1880° W", "dd", TYPE_POINT_COORDINATES_3_188_55_953),
				new TestCoordinate(2, "9.19° S, 75.0152°W", "dd", TYPE_POINT_COORDINATES_75_0152_09_190),
				new TestCoordinate(3, "35.9078°N, 127.7669° E", "dd", TYPE_POINT_COORDINATES_127_766_35_907)
		);

		Coordinate t = JCasUtil.selectByIndex(jCas, Coordinate.class, 0);
		assertEquals("51.507N 0.125W", t.getValue());
		assertEquals(true, t.getIsNormalised());
		t = JCasUtil.selectByIndex(jCas, Coordinate.class, 1);
		assertEquals("55.953N 3.188W", t.getValue());
		assertEquals(true, t.getIsNormalised());
		t = JCasUtil.selectByIndex(jCas, Coordinate.class, 2);
		assertEquals("9.19S 75.0152W", t.getValue());
		assertEquals(true, t.getIsNormalised());
		t = JCasUtil.selectByIndex(jCas, Coordinate.class, 3);
		assertEquals("35.9078N 127.7669E", t.getValue());
		assertEquals(true, t.getIsNormalised());
	}
	
	@Test
	public void testNormalizeLonFirst() throws Exception{
		String docText = "London is located in the UK, at 51.507° N, 0.125°W. Edinburgh is also in the UK, at 55.9530°N,3.1880° W."
				+ "But darkest Peru is at 9.19° S, 75.0152°W and South Korea at 35.9078°N, 127.7669° E.";
		jCas.setDocumentText(docText);
		processJCas("storeDecimalValue", true, "storeLongitudeFirst", true);
		
		
		assertAnnotations(4, Coordinate.class,
				new TestCoordinate(0, "51.507° N, 0.125°W", "dd", TYPE_POINT_COORDINATES_0_125_51_507),
				new TestCoordinate(1, "55.9530°N,3.1880° W", "dd", TYPE_POINT_COORDINATES_3_188_55_953),
				new TestCoordinate(2, "9.19° S, 75.0152°W", "dd", TYPE_POINT_COORDINATES_75_0152_09_190),
				new TestCoordinate(3, "35.9078°N, 127.7669° E", "dd", TYPE_POINT_COORDINATES_127_766_35_907)
		);

		Coordinate t = JCasUtil.selectByIndex(jCas, Coordinate.class, 0);
		assertEquals("-0.125 51.507", t.getValue());
		assertEquals(true, t.getIsNormalised());
		t = JCasUtil.selectByIndex(jCas, Coordinate.class, 1);
		assertEquals("-3.188 55.953", t.getValue());
		assertEquals(true, t.getIsNormalised());
		t = JCasUtil.selectByIndex(jCas, Coordinate.class, 2);
		assertEquals("-75.0152 -9.19", t.getValue());
		assertEquals(true, t.getIsNormalised());
		t = JCasUtil.selectByIndex(jCas, Coordinate.class, 3);
		assertEquals("127.7669 35.9078", t.getValue());
		assertEquals(true, t.getIsNormalised());
	}
	
	@Test
	public void testNormalizeLonFirstCard() throws Exception{
		String docText = "London is located in the UK, at 51.507° N, 0.125°W. Edinburgh is also in the UK, at 55.9530°N,3.1880° W."
				+ "But darkest Peru is at 9.19° S, 75.0152°W and South Korea at 35.9078°N, 127.7669° E.";
		jCas.setDocumentText(docText);
		processJCas("storeDecimalValue", true, "storeCardinalPoint", true, "storeLongitudeFirst", true);
		
		
		assertAnnotations(4, Coordinate.class,
				new TestCoordinate(0, "51.507° N, 0.125°W", "dd", TYPE_POINT_COORDINATES_0_125_51_507),
				new TestCoordinate(1, "55.9530°N,3.1880° W", "dd", TYPE_POINT_COORDINATES_3_188_55_953),
				new TestCoordinate(2, "9.19° S, 75.0152°W", "dd", TYPE_POINT_COORDINATES_75_0152_09_190),
				new TestCoordinate(3, "35.9078°N, 127.7669° E", "dd", TYPE_POINT_COORDINATES_127_766_35_907)
		);

		Coordinate t = JCasUtil.selectByIndex(jCas, Coordinate.class, 0);
		assertEquals("0.125W 51.507N", t.getValue());
		assertEquals(true, t.getIsNormalised());
		t = JCasUtil.selectByIndex(jCas, Coordinate.class, 1);
		assertEquals("3.188W 55.953N", t.getValue());
		assertEquals(true, t.getIsNormalised());
		t = JCasUtil.selectByIndex(jCas, Coordinate.class, 2);
		assertEquals("75.0152W 9.19S", t.getValue());
		assertEquals(true, t.getIsNormalised());
		t = JCasUtil.selectByIndex(jCas, Coordinate.class, 3);
		assertEquals("127.7669E 35.9078N", t.getValue());
		assertEquals(true, t.getIsNormalised());
	}
	
	@Test
	public void testMoney() throws Exception{
		
		jCas.setDocumentText("It may cost £2,000 a month to live in London, but that doesn't mean there are any coordiantes in this sentence!");
		processJCas();
		
		assertAnnotations(0, Coordinate.class);
	}

}
