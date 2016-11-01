//Dstl (c) Crown Copyright 2016
package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.DateTime;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.semantic.Temporal;

/**
 * Test the DateRange annotator
 */
public class DateTimeTest extends AbstractAnnotatorTest{

	public DateTimeTest() {
		super(DateTime.class);
	}
	
	@Test
	public void testIso() throws Exception{
		jCas.setDocumentText("It is currently 2016-10-05T11:07:22Z");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal ts1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("2016-10-05T11:07:22Z", ts1.getCoveredText());
		assertEquals(1475665642L, ts1.getTimestampStart());
		assertEquals(1475665643L, ts1.getTimestampStop());
		
		jCas.reset();
		
		jCas.setDocumentText("It is currently 2016-10-05T11:07:22");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal ts2 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("2016-10-05T11:07:22", ts2.getCoveredText());
		assertEquals(1475665642L, ts2.getTimestampStart());
		assertEquals(1475665643L, ts2.getTimestampStop());
		
		jCas.reset();
		
		jCas.setDocumentText("It is currently 2016-10-05T13:37:22+02:30");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal ts3 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("2016-10-05T13:37:22+02:30", ts3.getCoveredText());
		assertEquals(1475665642L, ts3.getTimestampStart());
		assertEquals(1475665643L, ts3.getTimestampStop());
		
		jCas.reset();
		
		jCas.setDocumentText("It is currently 2016-10-05T11:07:22.234");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal ts4 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("2016-10-05T11:07:22.234", ts4.getCoveredText());
		assertEquals(1475665642L, ts4.getTimestampStart());
		assertEquals(1475665643L, ts4.getTimestampStop());
	}
	
	@Test
	public void testTimeOnDate() throws Exception{
		jCas.setDocumentText("Be ready to go at 1100hrs on 5 October 2016");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal ts1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("1100hrs on 5 October 2016", ts1.getCoveredText());
		assertEquals(1475665200L, ts1.getTimestampStart());
		assertEquals(1475665260L, ts1.getTimestampStop());
		
		jCas.reset();
		
		jCas.setDocumentText("Be ready to go at 11:00:00hrs on 5 Oct 2016");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal ts2 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("11:00:00hrs on 5 Oct 2016", ts2.getCoveredText());
		assertEquals(1475665200L, ts2.getTimestampStart());
		assertEquals(1475665201L, ts2.getTimestampStop());
	}
	
	@Test
	public void testDayMonthTime() throws Exception{
		jCas.setDocumentText("It happened at 22 Apr 2014 1529Z");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal ts1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("22 Apr 2014 1529Z", ts1.getCoveredText());
		assertEquals(1398180540L, ts1.getTimestampStart());
		assertEquals(1398180600L, ts1.getTimestampStop());
		
		jCas.reset();
		
		jCas.setDocumentText("It happened at 22 April 2014 1529 EST");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal ts2 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("22 April 2014 1529 EST", ts2.getCoveredText());
		assertEquals(1398198540L, ts2.getTimestampStart());
		assertEquals(1398198600L, ts2.getTimestampStop());
		
		jCas.reset();
		
		jCas.setDocumentText("It happened at 22 April 2014 152947Z");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal ts3 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("22 April 2014 152947Z", ts3.getCoveredText());
		assertEquals(1398180587L, ts3.getTimestampStart());
		assertEquals(1398180588L, ts3.getTimestampStop());
	}
	
	@Test
	public void testMonthDayTime() throws Exception{
		jCas.setDocumentText("It happened at Apr 22, 2014 1529Z");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal ts1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("Apr 22, 2014 1529Z", ts1.getCoveredText());
		assertEquals(1398180540L, ts1.getTimestampStart());
		assertEquals(1398180600L, ts1.getTimestampStop());
		
		jCas.reset();
		
		jCas.setDocumentText("It happened at April 22 2014 1529 EST");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal ts2 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("April 22 2014 1529 EST", ts2.getCoveredText());
		assertEquals(1398198540L, ts2.getTimestampStart());
		assertEquals(1398198600L, ts2.getTimestampStop());
		
		jCas.reset();
		
		jCas.setDocumentText("It happened at April 22, 2014 152947Z");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal ts3 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("April 22, 2014 152947Z", ts3.getCoveredText());
		assertEquals(1398180587L, ts3.getTimestampStart());
		assertEquals(1398180588L, ts3.getTimestampStop());
	}
}
