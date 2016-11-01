//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.Dtg;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.semantic.Temporal;

/**
 * 
 */
public class DtgRegexTest extends AbstractAnnotatorTest {


	public DtgRegexTest() {
		super(Dtg.class);
	}

	@Test
	public void test() throws Exception{
	
		jCas.setDocumentText("This test was written at 251137Z FEB 13");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Temporal.class).size());
		
		Temporal dt = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertNotNull(dt);
		assertEquals("251137Z FEB 13", dt.getCoveredText());
		assertEquals("251137Z FEB 13", dt.getValue());
		assertEquals("EXACT", dt.getPrecision());
		assertEquals("SINGLE", dt.getScope());
		assertEquals("DATETIME", dt.getTemporalType());
		
		LocalDateTime date = LocalDateTime.of(2013, Month.FEBRUARY, 25, 11, 37);
		assertEquals(date.toInstant(ZoneOffset.UTC).getEpochSecond(), dt.getTimestampStart());
		assertEquals(date.plusMinutes(1).toInstant(ZoneOffset.UTC).getEpochSecond(), dt.getTimestampStop());
	}
	
	@Test
	public void test2() throws Exception{

		jCas.setDocumentText("Report Title: An example report\nDTG: 04 1558D Sep 10");
		processJCas();

		Temporal dt = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertNotNull(dt);
		assertEquals("04 1558D Sep 10", dt.getCoveredText());
		assertEquals("04 1558D Sep 10", dt.getValue());
		assertEquals("EXACT", dt.getPrecision());
		assertEquals("SINGLE", dt.getScope());
		assertEquals("DATETIME", dt.getTemporalType());

		LocalDateTime date = LocalDateTime.of(2010, Month.SEPTEMBER, 4, 11, 58);
		assertEquals(date.toInstant(ZoneOffset.UTC).getEpochSecond(), dt.getTimestampStart());
		assertEquals(date.plusMinutes(1).toInstant(ZoneOffset.UTC).getEpochSecond(), dt.getTimestampStop());
	}
	
	@Test
	public void test3() throws Exception{

		jCas.setDocumentText("Report Title: An example report\nDTG: 04 1558D*SEP 10");
		processJCas();

		Temporal dt = JCasUtil.selectByIndex(jCas, Temporal.class, 0);
		assertNotNull(dt);
		assertEquals("04 1558D*SEP 10", dt.getCoveredText());
		assertEquals("04 1558D*SEP 10", dt.getValue());
		assertEquals("EXACT", dt.getPrecision());
		assertEquals("SINGLE", dt.getScope());
		assertEquals("DATETIME", dt.getTemporalType());

		LocalDateTime date = LocalDateTime.of(2010, Month.SEPTEMBER, 4, 11, 28);
		assertEquals(date.toInstant(ZoneOffset.UTC).getEpochSecond(), dt.getTimestampStart());
		assertEquals(date.plusMinutes(1).toInstant(ZoneOffset.UTC).getEpochSecond(), dt.getTimestampStop());
	}
	
	@Test
	public void testBad() throws Exception{
		jCas.setDocumentText("This test was written at 301137Z FEB 13");
		processJCas();
		
		assertEquals(0, JCasUtil.select(jCas, Temporal.class).size());
		
		jCas.reset();
		jCas.setDocumentText("This test was written at 901137Z FEB 13");
		processJCas();
		
		assertEquals(0, JCasUtil.select(jCas, Temporal.class).size());
	}
}
