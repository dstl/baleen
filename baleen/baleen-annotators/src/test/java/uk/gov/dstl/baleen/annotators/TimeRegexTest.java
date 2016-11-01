//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.Time;
import uk.gov.dstl.baleen.annotators.testing.AnnotatorTestBase;
import uk.gov.dstl.baleen.types.semantic.Temporal;


/** Tests {@link TimeRegex}.
 * 
 *
 */
public class TimeRegexTest extends AnnotatorTestBase{

	private AnalysisEngine timeRegexAE;

	@Before
	public void beforeTest() throws UIMAException{
		jCas = JCasFactory.createJCas();
		timeRegexAE = AnalysisEngineFactory.createEngine(Time.class);

	}

	// Handles frequent testing method calls
	public void timeRegexCountAndValueCheck(Double expectedCount, String... expectedValues) throws Exception{
		// Check the correct number of references is extracted
		assertEquals(expectedCount, Double.valueOf(JCasUtil.select(jCas, Temporal.class).size()));

		// Loop the matches and check their values
		for (int i = 0; i < expectedValues.length; i++) {
			String expectedVal = expectedValues[i];

			// Get the value
			Temporal t1 = JCasUtil.selectByIndex(jCas, Temporal.class, i);
			assertNotNull(t1);

			// Check that the covered text is what we're expecting
			assertEquals(expectedVal, t1.getCoveredText());

			// Check that the value and the covered text are the same
			assertEquals(t1.getValue(), t1.getCoveredText());
		}
	}

	//Catch a single reference in 12hr clock format - morning
	@Test
	public void testSingleTime12hrClockMorning() throws Exception{

		jCas.setDocumentText("It is currently 07:00.");
		timeRegexAE.process(jCas);
		timeRegexCountAndValueCheck(1d, "07:00");
	}

	//Catch a single reference in 12hr clock format - evening 
	@Test
	public void testSingleTime12hrClockEvening() throws Exception{

		jCas.setDocumentText("It is currently 20:00.");
		timeRegexAE.process(jCas);
		timeRegexCountAndValueCheck(1d, "20:00");
	}

	// Catch the am/pm qualifier
	@Test
	public void testSingleTimeWithAMQualifier() throws Exception{

		jCas.setDocumentText("It is currently 12:22pm");
		timeRegexAE.process(jCas);
		timeRegexCountAndValueCheck(1d, "12:22pm");
	}

	//Catch informal time references: midnight, noon, midday. 
	@Test
	public void testSingleTimeInformal() throws Exception{

		jCas.setDocumentText("I don't know whether its midnight, noon or midday today.");
		timeRegexAE.process(jCas);
		timeRegexCountAndValueCheck(3d, "midnight", "noon", "midday");
	}

	//Catch a single time with a timezone qualifier 
	@Test
	public void testSingleGmtTimeZone() throws Exception{

		jCas.setDocumentText("It is currently 11:00 GMT");
		timeRegexAE.process(jCas);
		timeRegexCountAndValueCheck(1d, "11:00 GMT");
	}

	//Catch Central Europe Time zone 
	@Test
	public void testSingleGmtOtherTimeZone() throws Exception{

		jCas.setDocumentText("It is currently 11:00 CET");
		timeRegexAE.process(jCas);
		timeRegexCountAndValueCheck(1d, "11:00 CET");
	}

	//Ensure we catch multiple time references in 1 document 
	@Test
	public void testMultipleTimeReferences() throws Exception{

		jCas.setDocumentText("It was close to noon, about 12:03pm, 8 hours before 20:00.");
		timeRegexAE.process(jCas);
		timeRegexCountAndValueCheck(3d, "noon", "12:03pm", "20:00");
	}

	//Catch a single reference in 24hr clock
	@Test
	public void testSingleTime24hrClock() throws Exception{

		jCas.setDocumentText("It is currently 1700hrs.");
		timeRegexAE.process(jCas);
		timeRegexCountAndValueCheck(1d, "1700hrs");
	}

	//Catch a single reference in 24hr clock
	@Test
	public void testSingleTime24hrClockTimeZone() throws Exception{

		jCas.setDocumentText("It is currently 1700hrs CET.");
		timeRegexAE.process(jCas);
		timeRegexCountAndValueCheck(1d, "1700hrs CET");
	}

	//Catch a reference containing a colon
	@Test
	public void testYearAndTimeColon() throws Exception{
		jCas.setDocumentText("The year was 2016, the time was 20:16");
		timeRegexAE.process(jCas);
		timeRegexCountAndValueCheck(1d, "20:16");
	}
	
	//Catch a reference suffixed with hrs
	@Test
	public void testYearAndTimeHrs() throws Exception{
		jCas.setDocumentText("The year was 2016, the time was 2016hrs");
		timeRegexAE.process(jCas);
		timeRegexCountAndValueCheck(1d, "2016hrs");
	}
	
	//Test requireAlpha = false
	@Test
	public void testAlphaOnlyFalse() throws Exception{
		AnalysisEngine timeRegexAlphaAE = AnalysisEngineFactory.createEngine(Time.class, Time.PARAM_REQUIRE_ALPHA, false);
		jCas.setDocumentText("The year was 2016, the time was 2016hrs");
		timeRegexAlphaAE.process(jCas);
		timeRegexCountAndValueCheck(2d, "2016", "2016hrs");
	}
}