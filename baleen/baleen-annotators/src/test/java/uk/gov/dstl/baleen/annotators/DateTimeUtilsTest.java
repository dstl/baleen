//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.*;

import java.time.DayOfWeek;
import java.time.Month;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.helpers.DateTimeUtils;

public class DateTimeUtilsTest {

	@Test
	public void testAsDay() {
		assertDayOfWeek(DayOfWeek.MONDAY, "mon", "Mon", "Monday");
		assertDayOfWeek(DayOfWeek.TUESDAY, "tue", "TuES", "Tuesday");
		assertDayOfWeek(DayOfWeek.WEDNESDAY, "Wed", "Wednesday");
		assertDayOfWeek(DayOfWeek.THURSDAY, "Thur", "THUrs", "Thursday");
		assertDayOfWeek(DayOfWeek.FRIDAY, "Fri", "FRIday", "Friday");
		assertDayOfWeek(DayOfWeek.SATURDAY, "sAt", "Sat", "Saturday");
		assertDayOfWeek(DayOfWeek.SUNDAY, "sun", "SUN", "Sunday");
		assertDayOfWeek(null, "m", "t", "w", "tuesnesday");
	}

	private void assertDayOfWeek(DayOfWeek expected, String... strings) {
		for(String s: strings) {
			assertEquals(expected, DateTimeUtils.asDay(s));
		}
	}
	
	private void assertMonth(Month expected, String... strings) {
		for(String s: strings) {
			assertEquals(expected, DateTimeUtils.asMonth(s));
		}
	}

	@Test
	public void testAsMonth() {
		assertMonth(Month.JANUARY,"Jan","January","1","01");
		assertMonth(Month.FEBRUARY,"Feb", "FebUARy","2","02", "FEBRUARY");
		assertMonth(Month.MARCH,"Mar", "March","3","03");
		assertMonth(Month.APRIL,"Apr", "AprIL","4","04");
		assertMonth(Month.MAY,"May","5","05");
		assertMonth(Month.JUNE,"Jun","June","6","06");
		assertMonth(Month.JULY,"Jul", "July","7","07");
		assertMonth(Month.AUGUST,"Aug", "AugUST","8","08");
		assertMonth(Month.SEPTEMBER,"Sep", "SePT", "September","9","09");
		assertMonth(Month.OCTOBER,"oct", "October","10");
		assertMonth(Month.NOVEMBER,"NoV", "November","11");
		assertMonth(Month.DECEMBER,"dec", "December", "Christmas","12");
		assertMonth(null,"ma","j","movember","0","13");
	}

}
