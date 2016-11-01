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
		jCas.setDocumentText("It happened on the 15th October, Tuesday 11 Oct, and in September, but not on Wednesday 5 October 2016 or 3 Oct '16 or in January 2014.");
		processJCas();
		
		assertEquals(3, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal ts1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("15th October", ts1.getCoveredText());
		
		Temporal ts2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("Tuesday 11 Oct", ts2.getCoveredText());
		
		Temporal ts3 = JCasUtil.selectByIndex(jCas, Temporal.class, 2);
		assertEquals("September", ts3.getCoveredText());
	}
	
	@Test
	public void testDays() throws Exception{
		jCas.setDocumentText("Monday, Tuesday 11th, Wednesday 12th October, Thursday 13th October 2016, Fri 14 Oct, Sat 15 Oct 16");
		processJCas();
		
		for(Temporal t : JCasUtil.select(jCas, Temporal.class))
			System.err.println(t.getCoveredText());
		
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
}
