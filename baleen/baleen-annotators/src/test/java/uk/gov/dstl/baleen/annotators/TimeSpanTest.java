//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.cleaners.AddTimeSpans;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.annotators.testing.types.TestTimeSpan;
import uk.gov.dstl.baleen.types.temporal.TimeSpan;

/**
 * 
 */
public class TimeSpanTest extends AbstractAnnotatorTest {
	
	private static final String DECEMBER_2012 = "December 2012";
	private static final String TEXT = "The world was supposed to end in December 2012. It didn't, and it's now June.";

	public TimeSpanTest() {
		super(AddTimeSpans.class);
	}

	@Test
	public void testYear() throws Exception{
		jCas.setDocumentText("The world was supposed to end several times in 2012. It didn't, and we're now half way through '13.");
		
		Annotations.createDateType(jCas, 47, 51, "2012");
		Annotations.createDateType(jCas, 96, 98, "13");
		
		processJCas();

		assertAnnotations(2, TimeSpan.class, 
				new TestTimeSpan(0, "2012", 1325376000000L, 1356998399999L),
				new TestTimeSpan(1, "13", 1356998400000L, 1388534399999L)
				);
	
	}
	
	@Test
	public void testMonth() throws Exception {
		jCas.setDocumentText(TEXT);


		Annotations.createDateType(jCas, 33, 46, DECEMBER_2012);
		Annotations.createDateType(jCas, 72, 76, "June");

		processJCas();

		YearMonth ym = Year.now().atMonth(Month.JUNE);
		LocalDateTime start = ym.atDay(1).atStartOfDay();
		LocalDateTime end = start.plusMonths(1).minusNanos(1);
		
		assertAnnotations(2, TimeSpan.class, new TestTimeSpan(0,
				DECEMBER_2012, 1354320000000L, 1356998399999L),
				new TestTimeSpan(1, "June", start.toInstant(ZoneOffset.UTC).toEpochMilli(), end.toInstant(ZoneOffset.UTC).toEpochMilli()));
	}
	
	@Test
	public void testMonthDontGuessYear() throws Exception {
		jCas.setDocumentText(TEXT);

		Annotations.createDateType(jCas, 33, 46, DECEMBER_2012);
		Annotations.createDateType(jCas, 72, 76, "June");

		processJCas("guessYear", false);

		YearMonth ym = Year.now().atMonth(Month.JUNE);
		LocalDateTime start = ym.atDay(1).atStartOfDay();
		LocalDateTime end = start.plusMonths(1).minusNanos(1);
		
		assertAnnotations(2, TimeSpan.class, new TestTimeSpan(0,
				DECEMBER_2012, 1354320000000L, 1356998399999L),
				new TestTimeSpan(1, "June", start.toInstant(ZoneOffset.UTC).toEpochMilli(), end.toInstant(ZoneOffset.UTC).toEpochMilli()));
	}
	
	@Test
	public void testMonthDontGuessYearDocInfo() throws Exception {
		jCas.setDocumentText(TEXT);

		Annotations.createDateType(jCas, 33, 46, DECEMBER_2012);
		Annotations.createDateType(jCas, 72, 76, "June");
		
		Annotations.createMetadata(jCas, "dateOfInformation", "8 Aug 2013");
		Annotations.createMetadata(jCas, "dateOfReport", "9 AUG 2013");
		Annotations.createMetadata(jCas, "wrongDate", "9 AUG 2019");

		
		processJCas("guessYear", false);

		YearMonth ym = Year.of(2013).atMonth(Month.JUNE);
		LocalDateTime start = ym.atDay(1).atStartOfDay();
		LocalDateTime end = start.plusMonths(1).minusNanos(1);
		
		assertAnnotations(2, TimeSpan.class, new TestTimeSpan(0,
				DECEMBER_2012, 1354320000000L, 1356998399999L),
				new TestTimeSpan(1, "June", start.toInstant(ZoneOffset.UTC).toEpochMilli(), end.toInstant(ZoneOffset.UTC).toEpochMilli()));
	}

}