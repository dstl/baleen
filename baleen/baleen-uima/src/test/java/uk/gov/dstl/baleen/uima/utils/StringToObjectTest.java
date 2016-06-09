//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.uima.utils;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import uk.gov.dstl.baleen.uima.utils.StringToObject;

/**
 * 
 */
public class StringToObjectTest {

	@Test
	public void testDate() {
		Date testDate = new Date();

		testDate = DateUtils.truncate(testDate, Calendar.SECOND);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		assertTrue(StringToObject.convertStringToObject(sdf.format(testDate)) instanceof Date);
		assertEquals(testDate, StringToObject.convertStringToObject(sdf.format(testDate)));

		sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		assertTrue(StringToObject.convertStringToObject(sdf.format(testDate)) instanceof Date);
		assertEquals(testDate, StringToObject.convertStringToObject(sdf.format(testDate)));

		sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		assertTrue(StringToObject.convertStringToObject(sdf.format(testDate)) instanceof Date);
		assertEquals(testDate, StringToObject.convertStringToObject(sdf.format(testDate)));
	}

	@Test
	public void testDateDisabled(){
		Date testDate = new Date();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

		Properties disableDate = new Properties();
		disableDate.put("allowDates", false);

		assertTrue(StringToObject.convertStringToObject(sdf.format(testDate), disableDate) instanceof String);
		assertEquals(sdf.format(testDate),StringToObject.convertStringToObject(sdf.format(testDate), disableDate));
	}

	@Test
	public void testInteger(){
		assertTrue(StringToObject.convertStringToObject("1") instanceof Integer);
		assertEquals(new Integer(1), StringToObject.convertStringToObject("1"));

		assertTrue(StringToObject.convertStringToObject("-5") instanceof Integer);
		assertEquals(new Integer(-5), StringToObject.convertStringToObject("-5"));
	}

	@Test
	public void testDouble(){
		assertTrue(StringToObject.convertStringToObject("1.0") instanceof Double);
		assertEquals(new Double(1.0), StringToObject.convertStringToObject("1.0"));

		assertTrue(StringToObject.convertStringToObject("-1.0") instanceof Double);
		assertEquals(new Double(-1.0), StringToObject.convertStringToObject("-1.0"));

		assertTrue(StringToObject.convertStringToObject("43.5") instanceof Double);
		assertEquals(new Double(43.5), StringToObject.convertStringToObject("43.5"));
	}

	@Test
	public void testNumberPrecedingZero(){
		Properties precedingZero = new Properties();
		precedingZero.put("precedingZeroIsntNumber", false);

		assertTrue(StringToObject.convertStringToObject("01234") instanceof String);
		assertTrue(StringToObject.convertStringToObject("01234", precedingZero) instanceof Integer);
		assertTrue(StringToObject.convertStringToObject("0.1234") instanceof Double);
		assertTrue(StringToObject.convertStringToObject("0.1234", precedingZero) instanceof Double);
	}

	@Test
	public void testBoolean(){
		assertTrue(StringToObject.convertStringToObject("true") instanceof Boolean);
		assertTrue((Boolean)StringToObject.convertStringToObject("true"));
		assertFalse((Boolean)StringToObject.convertStringToObject("false"));
		assertTrue((Boolean)StringToObject.convertStringToObject("True"));
		assertFalse((Boolean)StringToObject.convertStringToObject("False"));
		assertTrue((Boolean)StringToObject.convertStringToObject("TRUE"));
		assertFalse((Boolean)StringToObject.convertStringToObject("FALSE"));

		assertFalse(StringToObject.convertStringToObject("something") instanceof Boolean);
	}

	@Test
	public void testString(){
		assertTrue(StringToObject.convertStringToObject("something") instanceof String);
		assertTrue(StringToObject.convertStringToObject("") instanceof String);
	}

	@Test
	public void testNull(){
		assertNull(StringToObject.convertStringToObject(null));
	}
}
