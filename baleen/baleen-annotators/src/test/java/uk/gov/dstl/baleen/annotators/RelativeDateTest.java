//Dstl (c) Crown Copyright 2016
package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.ZoneOffset;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.RelativeDate;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.semantic.Temporal;

public class RelativeDateTest extends AbstractAnnotatorTest {
	private static final LocalDate relativeTo = LocalDate.of(2016, 10, 5);
	
	public RelativeDateTest(){
		super(RelativeDate.class);
	}
	
	private void setDocumentDate(){
		Metadata md = new Metadata(jCas);
		md.setKey("date");
		md.setValue("2016-10-05");
		md.addToIndexes();
	}
	
	@Test
	public void testToday() throws Exception{
		jCas.setDocumentText("Today is Wednesday");
		setDocumentDate();
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal t1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("Today", t1.getCoveredText());
		assertEquals(relativeTo.atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStart());
		assertEquals(relativeTo.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStop());
	}
	
	@Test
	public void testYesterday() throws Exception{
		jCas.setDocumentText("Yesterday was Tuesday, and the day before yesterday was Monday");
		setDocumentDate();
		processJCas();
		
		assertEquals(2, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal t1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("Yesterday", t1.getCoveredText());
		assertEquals(relativeTo.minusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStart());
		assertEquals(relativeTo.atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStop());
		
		Temporal t2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("day before yesterday", t2.getCoveredText());
		assertEquals(relativeTo.minusDays(2).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStart());
		assertEquals(relativeTo.minusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStop());
	}
	
	@Test
	public void testTomorrow() throws Exception{
		jCas.setDocumentText("Tomorrow was Thursday, and the day after tomorrow is Friday");
		setDocumentDate();
		processJCas();
		
		assertEquals(2, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal t1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("Tomorrow", t1.getCoveredText());
		assertEquals(relativeTo.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStart());
		assertEquals(relativeTo.plusDays(2).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStop());
		
		Temporal t2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("day after tomorrow", t2.getCoveredText());
		assertEquals(relativeTo.plusDays(2).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStart());
		assertEquals(relativeTo.plusDays(3).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStop());
	}
	
	@Test
	public void testThisX() throws Exception{
		jCas.setDocumentText("This week is part of this month, which is part of this year.");
		setDocumentDate();
		processJCas();
		
		assertEquals(3, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal t1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("This week", t1.getCoveredText());
		assertEquals(LocalDate.of(2016, 10, 3).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStart());
		assertEquals(LocalDate.of(2016, 10, 10).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStop());
		
		Temporal t2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("this month", t2.getCoveredText());
		assertEquals(LocalDate.of(2016, 10, 1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStart());
		assertEquals(LocalDate.of(2016, 11, 1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStop());
		
		Temporal t3 = JCasUtil.selectByIndex(jCas, Temporal.class, 2);
		assertEquals("this year", t3.getCoveredText());
		assertEquals(LocalDate.of(2016, 1, 1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t3.getTimestampStart());
		assertEquals(LocalDate.of(2017, 1, 1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t3.getTimestampStop());
	}
	
	@Test
	public void testNextLastDay() throws Exception{
		jCas.setDocumentText("Next Friday is in two days time. Last Wednesday was seven days ago.");
		setDocumentDate();
		processJCas();
		
		assertEquals(2, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal t1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("Next Friday", t1.getCoveredText());
		assertEquals(relativeTo.plusDays(2).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStart());
		assertEquals(relativeTo.plusDays(3).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStop());
		
		Temporal t2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("Last Wednesday", t2.getCoveredText());
		assertEquals(relativeTo.minusWeeks(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStart());
		assertEquals(relativeTo.minusDays(6).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStop());
	}
	
	@Test
	public void testNextLastWeek() throws Exception{
		jCas.setDocumentText("Next week begins on the 10th October, last week began on 26th September.");
		setDocumentDate();
		processJCas();
		
		assertEquals(2, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal t1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("Next week", t1.getCoveredText());
		assertEquals(LocalDate.of(2016, 10, 10).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStart());
		assertEquals(LocalDate.of(2016, 10, 17).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStop());
		
		Temporal t2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("last week", t2.getCoveredText());
		assertEquals(LocalDate.of(2016, 9, 26).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStart());
		assertEquals(LocalDate.of(2016, 10, 3).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStop());
	}
	
	@Test
	public void testNextLastWeekPeriod() throws Exception{
		jCas.setDocumentText("In the next week we expect to see results from what happened within the last week.");
		setDocumentDate();
		processJCas();
		
		assertEquals(2, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal t1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("In the next week", t1.getCoveredText());
		assertEquals(relativeTo.atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStart());
		assertEquals(relativeTo.plusDays(8).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStop());
		
		Temporal t2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("within the last week", t2.getCoveredText());
		assertEquals(relativeTo.minusDays(7).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStart());
		assertEquals(relativeTo.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStop());
	}
	
	@Test
	public void testNextLastWeekDay() throws Exception{
		jCas.setDocumentText("Tuesday next week we hope to do better than we did on Thursday last week.");
		setDocumentDate();
		processJCas();
		
		assertEquals(2, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal t1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("Tuesday next week", t1.getCoveredText());
		assertEquals(LocalDate.of(2016, 10, 11).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStart());
		assertEquals(LocalDate.of(2016, 10, 12).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStop());
		
		Temporal t2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("Thursday last week", t2.getCoveredText());
		assertEquals(LocalDate.of(2016, 9, 29).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStart());
		assertEquals(LocalDate.of(2016, 9, 30).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStop());
	}
	
	@Test
	public void testNextLastMonth() throws Exception{
		jCas.setDocumentText("Last month was September, and next month is November");
		setDocumentDate();
		processJCas();
		
		assertEquals(2, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal t1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("Last month", t1.getCoveredText());
		assertEquals(LocalDate.of(2016, 9, 1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStart());
		assertEquals(LocalDate.of(2016, 10, 1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStop());
		
		Temporal t2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("next month", t2.getCoveredText());
		assertEquals(LocalDate.of(2016, 11, 1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStart());
		assertEquals(LocalDate.of(2016, 12, 1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStop());
	}
	
	@Test
	public void testNextLastMonthPeriod() throws Exception{
		jCas.setDocumentText("In the last month something happened, but it's not expected to happen again within the next month");
		setDocumentDate();
		processJCas();
		
		assertEquals(2, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal t1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("In the last month", t1.getCoveredText());
		assertEquals(relativeTo.minusMonths(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStart());
		assertEquals(relativeTo.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStop());
		
		Temporal t2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("within the next month", t2.getCoveredText());
		assertEquals(relativeTo.atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStart());
		assertEquals(relativeTo.plusMonths(1).plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStop());
	}
	
	@Test
	public void testNextLastYear() throws Exception{
		jCas.setDocumentText("Next year is 2017, last year was 2016.");
		setDocumentDate();
		processJCas();
		
		assertEquals(2, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal t1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("Next year", t1.getCoveredText());
		assertEquals(LocalDate.of(2017, 1, 1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStart());
		assertEquals(LocalDate.of(2018, 1, 1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStop());
		
		Temporal t2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("last year", t2.getCoveredText());
		assertEquals(LocalDate.of(2015, 1, 1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStart());
		assertEquals(LocalDate.of(2016, 1, 1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStop());
	}
	
	@Test
	public void testNextLastYearPeriod() throws Exception{
		jCas.setDocumentText("In the next year, something will happen which didn't happen within the last year.");
		setDocumentDate();
		processJCas();
		
		assertEquals(2, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal t1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("In the next year", t1.getCoveredText());
		assertEquals(relativeTo.atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStart());
		assertEquals(relativeTo.plusYears(1).plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStop());
		
		Temporal t2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("within the last year", t2.getCoveredText());
		assertEquals(relativeTo.minusYears(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStart());
		assertEquals(relativeTo.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStop());
	}
	
	@Test
	public void testNextLastYearMonth() throws Exception{
		jCas.setDocumentText("October last year was cold, but June next year will probably be hot.");
		setDocumentDate();
		processJCas();
		
		assertEquals(2, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal t1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("October last year", t1.getCoveredText());
		assertEquals(LocalDate.of(2015, 10, 1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStart());
		assertEquals(LocalDate.of(2015, 11, 1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStop());
		
		Temporal t2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("June next year", t2.getCoveredText());
		assertEquals(LocalDate.of(2017, 6, 1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStart());
		assertEquals(LocalDate.of(2017, 7, 1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStop());
	}
	
	@Test
	public void testInTheNextX() throws Exception{
		jCas.setDocumentText("It could happen in the next 3 days, or within the next 2 weeks. Or it could have happened in the last 4 months or within the last 15 years.");
		setDocumentDate();
		processJCas();
		
		assertEquals(4, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal t1 = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertEquals("in the next 3 days", t1.getCoveredText());
		assertEquals(relativeTo.atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStart());
		assertEquals(relativeTo.plusDays(4).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t1.getTimestampStop());
		
		Temporal t2 = JCasUtil.selectByIndex(jCas, Temporal.class, 1);
		assertEquals("within the next 2 weeks", t2.getCoveredText());
		assertEquals(relativeTo.atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStart());
		assertEquals(relativeTo.plusWeeks(2).plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t2.getTimestampStop());
		
		Temporal t3 = JCasUtil.selectByIndex(jCas, Temporal.class, 2);
		assertEquals("in the last 4 months", t3.getCoveredText());
		assertEquals(relativeTo.minusMonths(4).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t3.getTimestampStart());
		assertEquals(relativeTo.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t3.getTimestampStop());
		
		Temporal t4 = JCasUtil.selectByIndex(jCas, Temporal.class, 3);
		assertEquals("within the last 15 years", t4.getCoveredText());
		assertEquals(relativeTo.minusYears(15).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t4.getTimestampStart());
		assertEquals(relativeTo.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC), t4.getTimestampStop());
	}
}
