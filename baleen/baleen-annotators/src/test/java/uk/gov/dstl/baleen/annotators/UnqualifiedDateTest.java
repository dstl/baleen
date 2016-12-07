//Dstl (c) Crown Copyright 2016
package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.UnqualifiedDate;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.semantic.Temporal;

/**
 * Test the UnqualifiedDate annotator
 */
public class UnqualifiedDateTest extends AbstractAnnotatorTest{

	public UnqualifiedDateTest() {
		super(UnqualifiedDate.class);
	}
	
	@Test
	public void testMonths() throws Exception{
		jCas.setDocumentText("It happened on the 15th October, Tuesday 11 Oct, and in September, but not on Wednesday 5 October 2016 or 3 Oct '16 or in January 2014. 15th of October was the last day it happened.");
		processJCas();
		
		assertEquals(4, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal ts1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("15th October", ts1.getCoveredText());
		
		Temporal ts2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("Tuesday 11 Oct", ts2.getCoveredText());
		
		Temporal ts3 = JCasUtil.selectByIndex(jCas, Temporal.class, 2);
		assertEquals("September", ts3.getCoveredText());
		
		Temporal ts4 = JCasUtil.selectByIndex(jCas, Temporal.class, 3);
		assertEquals("15th of October", ts4.getCoveredText());
	}
	
	@Test
	public void testDays() throws Exception{
		jCas.setDocumentText("Monday, Tuesday 11th, Wednesday 12th October, Thursday 13th October 2016, Fri 14 Oct, Sat 15 Oct 16");
		processJCas();
		
		assertEquals(4, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal ts1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("Monday", ts1.getCoveredText());
		
		Temporal ts2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("Tuesday 11th", ts2.getCoveredText());
		
		Temporal ts3 = JCasUtil.selectByIndex(jCas, Temporal.class, 2);
		assertEquals("Wednesday 12th October", ts3.getCoveredText());
		
		Temporal ts4 = JCasUtil.selectByIndex(jCas, Temporal.class, 3);
		assertEquals("Fri 14 Oct", ts4.getCoveredText());
	}
	
	@Test
	public void testMonthsLowerCase() throws Exception{
		jCas.setDocumentText("It happened on the 15th october, tuesday 11 oct, and in september, but not on wednesday 5 october 2016 or 3 Oct '16 or in january 2014. 15th of october was the last day it happened.");
		
		processJCas();
		assertEquals(0, JCasUtil.select(jCas, Temporal.class).size());
		
		jCas.reset();
		jCas.setDocumentText("It happened on the 15th october, tuesday 11 oct, and in september, but not on wednesday 5 october 2016 or 3 Oct '16 or in january 2014. 15th of october was the last day it happened.");
		
		processJCas(UnqualifiedDate.PARAM_ALLOW_LOWERCASE, true);
		assertEquals(4, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal ts1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("15th october", ts1.getCoveredText());
		
		Temporal ts2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("tuesday 11 oct", ts2.getCoveredText());
		
		Temporal ts3 = JCasUtil.selectByIndex(jCas, Temporal.class, 2);
		assertEquals("september", ts3.getCoveredText());
		
		Temporal ts4 = JCasUtil.selectByIndex(jCas, Temporal.class, 3);
		assertEquals("15th of october", ts4.getCoveredText());
	}
	
	@Test
	public void testDaysLowerCase() throws Exception{
		jCas.setDocumentText("monday, tuesday 11th, wednesday 12th october, thursday 13th october 2016, fri 14 Oct, sat 15 oct 16");
		
		processJCas();
		assertEquals(0, JCasUtil.select(jCas, Temporal.class).size());
		
		jCas.reset();
		jCas.setDocumentText("monday, tuesday 11th, wednesday 12th october, thursday 13th october 2016, fri 14 oct, sat 15 oct 16");
		
		processJCas(UnqualifiedDate.PARAM_ALLOW_LOWERCASE, true);
		
		assertEquals(4, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal ts1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("monday", ts1.getCoveredText());
		
		Temporal ts2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("tuesday 11th", ts2.getCoveredText());
		
		Temporal ts3 = JCasUtil.selectByIndex(jCas, Temporal.class, 2);
		assertEquals("wednesday 12th october", ts3.getCoveredText());
		
		Temporal ts4 = JCasUtil.selectByIndex(jCas, Temporal.class, 3);
		assertEquals("fri 14 oct", ts4.getCoveredText());
	}
}
