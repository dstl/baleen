//Dstl (c) Crown Copyright 2016
package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.Date;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.TestEntity;
import uk.gov.dstl.baleen.types.semantic.Temporal;

/**
 * Test the DateRange annotator
 */
public class DateTest extends AbstractAnnotatorTest{

	public DateTest() {
		super(Date.class);
	}
	
	@Test
	public void testYears() throws Exception{
	jCas.setDocumentText("Woolworths was a retail chain from 1909-2008. We had very hot summers in 2009-11. 1969 was wet, as was the year '16.");
		processJCas();
		
		assertEquals(2, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal ts1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("1909-2008", ts1.getCoveredText());
		assertEquals(-1924992000L, ts1.getTimestampStart());
		assertEquals(1230768000L, ts1.getTimestampStop());
		
		Temporal ts2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("2009-11", ts2.getCoveredText());
		assertEquals(1230768000L, ts2.getTimestampStart());
		assertEquals(1325376000L, ts2.getTimestampStop());
	}

	@Test
	public void testMonthYears() throws Exception{
		jCas.setDocumentText("From January to November 2015, not a lot happened. From December 15-January '16, Christmas happened.");
		processJCas();
		
		assertEquals(2, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal ts1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("January to November 2015", ts1.getCoveredText());
		assertEquals(1420070400L, ts1.getTimestampStart());
		assertEquals(1448928000L, ts1.getTimestampStop());
		
		Temporal ts2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("December 15-January '16", ts2.getCoveredText());
		assertEquals(1448928000L, ts2.getTimestampStart());
		assertEquals(1454284800L, ts2.getTimestampStop());
	}
	
	@Test
	public void testDayMonthYears() throws Exception{
		jCas.setDocumentText("He is on duty from 3-10 October 2016, whilst she was on duty 27th September - Monday 3 Oct 16. The Christmas break fell between 21st December 2016 and 2 January 17. On 2/3 January '17 there was a storm, and it rained on 2nd and 5th January 2017.");
		processJCas();
		
		assertEquals(6, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal ts1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("3-10 October 2016", ts1.getCoveredText());
		assertEquals(1475452800L, ts1.getTimestampStart());
		assertEquals(1476144000L, ts1.getTimestampStop());
		
		Temporal ts2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("27th September - Monday 3 Oct 16", ts2.getCoveredText());
		assertEquals(1474934400L, ts2.getTimestampStart());
		assertEquals(1475539200L, ts2.getTimestampStop());
		
		Temporal ts3 = JCasUtil.selectByIndex(jCas, Temporal.class, 2);
		assertEquals("21st December 2016 and 2 January 17", ts3.getCoveredText());
		assertEquals(1482278400L, ts3.getTimestampStart());
		assertEquals(1483401600L, ts3.getTimestampStop());
		
		Temporal ts4 = JCasUtil.selectByIndex(jCas, Temporal.class, 3);
		assertEquals("2/3 January '17", ts4.getCoveredText());
		assertEquals(1483315200L, ts4.getTimestampStart());
		assertEquals(1483488000L, ts4.getTimestampStop());
		
		Temporal ts5 = JCasUtil.selectByIndex(jCas, Temporal.class, 4);
		assertEquals("2nd and 5th January 2017", ts5.getCoveredText());
		assertEquals("2nd January 2017", ts5.getValue());
		assertEquals(1483315200L, ts5.getTimestampStart());
		assertEquals(1483401600L, ts5.getTimestampStop());
		
		Temporal ts6 = JCasUtil.selectByIndex(jCas, Temporal.class, 5);
		assertEquals("5th January 2017", ts6.getCoveredText());
		assertEquals(1483574400L, ts6.getTimestampStart());
		assertEquals(1483660800L, ts6.getTimestampStop());
	}
	
	@Test
	public void testBadDayMonthYears() throws Exception{
		jCas.setDocumentText("She worked from 1st - 30th February 2015");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());	//Month will be extracted, but the invalid date won't be
		Temporal t = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("February 2015", t.getCoveredText());
	}
	
	@Test
	public void testDates() throws Exception{
		jCas.setDocumentText("Today is Tuesday 4th October 2016, or October 4 2016, or 2016-10-04, or maybe even 4/10/16.");
		processJCas();
		
		assertEquals(4, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal ts1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("Tuesday 4th October 2016", ts1.getCoveredText());
		assertEquals(1475539200L, ts1.getTimestampStart());
		assertEquals(1475625600L, ts1.getTimestampStop());
		
		Temporal ts2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("October 4 2016", ts2.getCoveredText());
		assertEquals(1475539200L, ts2.getTimestampStart());
		assertEquals(1475625600L, ts2.getTimestampStop());
		
		Temporal ts3 = JCasUtil.selectByIndex(jCas, Temporal.class, 2);
		assertEquals("2016-10-04", ts3.getCoveredText());
		assertEquals(1475539200L, ts3.getTimestampStart());
		assertEquals(1475625600L, ts3.getTimestampStop());
		
		Temporal ts4 = JCasUtil.selectByIndex(jCas, Temporal.class, 3);
		assertEquals("4/10/16", ts4.getCoveredText());
		assertEquals(1475539200L, ts4.getTimestampStart());
		assertEquals(1475625600L, ts4.getTimestampStop());
	}
	
	@Test
	public void testMonth() throws Exception{
		jCas.setDocumentText("It was during February 2015 that the event happened");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		Temporal t = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("February 2015", t.getCoveredText());
		assertEquals(1422748800L, t.getTimestampStart());
		assertEquals(1425168000L, t.getTimestampStop());
		
		jCas.reset();
		
		jCas.setDocumentText("It was during late February 2015 that the event happened");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		t = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("late February 2015", t.getCoveredText());
		assertEquals(1424476800L, t.getTimestampStart());
		assertEquals(1425168000L, t.getTimestampStop());
		
		jCas.reset();
		
		jCas.setDocumentText("It was at the beginning of February 2015 that the event happened");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		t = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("beginning of February 2015", t.getCoveredText());
		assertEquals(1422748800L, t.getTimestampStart());
		assertEquals(1423180800L, t.getTimestampStop());
	}
	
	@Test
	public void testYear() throws Exception{
		jCas.setDocumentText("The year was 1997, which is the year after 1996 (a leap year)");
		processJCas();
		
		assertEquals(2, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal t1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("1997", t1.getCoveredText());
		assertEquals(852076800L, t1.getTimestampStart());
		assertEquals(883612800L, t1.getTimestampStop());
		
		Temporal t2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("1996", t2.getCoveredText());
		assertEquals(820454400L, t2.getTimestampStart());
		assertEquals(852076800L, t2.getTimestampStop());
	}
	
	//The following tests were from the old Date regex, this shows we haven't lost capability with the rewrite
	@Test
	public void testFull() throws Exception{
		jCas.setDocumentText("Today is Monday 25th February 2013.");
		processJCas();
		
		assertAnnotations(1, Temporal.class,
			new TestEntity<>(0, "Monday 25th February 2013")
		);
	}
	
	@Test
	public void testShortYear() throws Exception{
		jCas.setDocumentText("Today is Monday 25th February 13.");
		processJCas();
		
		assertAnnotations(1, Temporal.class,
			new TestEntity<>(0, "Monday 25th February 13")
		);
	}
	
	@Test
	public void testShortDay() throws Exception{
		jCas.setDocumentText("Today is Mon 25th February 2013.");
		processJCas();
		
		assertAnnotations(1, Temporal.class,
			new TestEntity<>(0, "Mon 25th February 2013")
		);
	}
	
	@Test
	public void testNoDay() throws Exception{
		jCas.setDocumentText("Today is 25th February 2013.");
		processJCas();
		
		assertAnnotations(1, Temporal.class,
			new TestEntity<>(0, "25th February 2013")
		);
	}
	
	@Test
	public void testNoSuffix() throws Exception{
		jCas.setDocumentText("Today is Monday 25 February 2013.");
		processJCas();
		
		assertAnnotations(1, Temporal.class,
			new TestEntity<>(0, "Monday 25 February 2013")
		);
	}
}
