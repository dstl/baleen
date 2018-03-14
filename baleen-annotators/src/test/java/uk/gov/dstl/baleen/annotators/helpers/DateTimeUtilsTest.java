// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.Year;

import org.junit.Test;

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
    for (String s : strings) {
      assertEquals(expected, DateTimeUtils.asDay(s));
    }
  }

  private void assertMonth(Month expected, String... strings) {
    for (String s : strings) {
      assertEquals(expected, DateTimeUtils.asMonth(s));
    }
  }

  @Test
  public void testAsMonth() {
    assertMonth(Month.JANUARY, "Jan", "January", "1", "01");
    assertMonth(Month.FEBRUARY, "Feb", "FebUARy", "2", "02", "FEBRUARY");
    assertMonth(Month.MARCH, "Mar", "March", "3", "03");
    assertMonth(Month.APRIL, "Apr", "AprIL", "4", "04");
    assertMonth(Month.MAY, "May", "5", "05");
    assertMonth(Month.JUNE, "Jun", "June", "6", "06");
    assertMonth(Month.JULY, "Jul", "July", "7", "07");
    assertMonth(Month.AUGUST, "Aug", "AugUST", "8", "08");
    assertMonth(Month.SEPTEMBER, "Sep", "SePT", "September", "9", "09");
    assertMonth(Month.OCTOBER, "oct", "October", "10");
    assertMonth(Month.NOVEMBER, "NoV", "November", "11");
    assertMonth(Month.DECEMBER, "dec", "December", "Christmas", "12");
    assertMonth(null, "ma", "j", "movember", "0", "13");
  }

  @Test
  public void testAsYear() {
    assertEquals(Year.of(2006), DateTimeUtils.asYear("2006"));
    assertEquals(Year.of(2006), DateTimeUtils.asYear("06"));
    assertEquals(Year.of(1984), DateTimeUtils.asYear("'84"));

    assertNull(DateTimeUtils.asYear("last year"));
  }

  @Test
  public void testSuffixCorrect() {
    assertTrue(DateTimeUtils.suffixCorrect(3, ""));
    assertTrue(DateTimeUtils.suffixCorrect(3, null));

    assertTrue(DateTimeUtils.suffixCorrect(1, "st"));
    assertTrue(DateTimeUtils.suffixCorrect(21, "st"));
    assertTrue(DateTimeUtils.suffixCorrect(2, "nd"));
    assertTrue(DateTimeUtils.suffixCorrect(22, "nd"));
    assertTrue(DateTimeUtils.suffixCorrect(3, "rd"));
    assertTrue(DateTimeUtils.suffixCorrect(23, "rd"));
    assertTrue(DateTimeUtils.suffixCorrect(1642341, "st"));
    assertTrue(DateTimeUtils.suffixCorrect(11, "th"));
    assertTrue(DateTimeUtils.suffixCorrect(2011, "th"));

    assertFalse(DateTimeUtils.suffixCorrect(1, "th"));
    assertFalse(DateTimeUtils.suffixCorrect(2, "rd"));
    assertFalse(DateTimeUtils.suffixCorrect(3, "st"));
    assertFalse(DateTimeUtils.suffixCorrect(4, "nd"));
    assertFalse(DateTimeUtils.suffixCorrect(11, "st"));
    assertFalse(DateTimeUtils.suffixCorrect(12, "nd"));
    assertFalse(DateTimeUtils.suffixCorrect(13, "rd"));
    assertFalse(DateTimeUtils.suffixCorrect(2011, "st"));
    assertFalse(DateTimeUtils.suffixCorrect(2012, "nd"));
    assertFalse(DateTimeUtils.suffixCorrect(2013, "rd"));
  }
}
