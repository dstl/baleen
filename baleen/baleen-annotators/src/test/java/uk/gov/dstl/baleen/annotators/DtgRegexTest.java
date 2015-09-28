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
import uk.gov.dstl.baleen.types.temporal.DateTime;

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
		
		assertEquals(1, JCasUtil.select(jCas, DateTime.class).size());
		
		DateTime dt = JCasUtil.selectByIndex(jCas, DateTime.class, 0);
		assertNotNull(dt);
		assertEquals("251137Z FEB 13", dt.getCoveredText());
		assertEquals("251137Z FEB 13", dt.getValue());
		
		LocalDateTime date = LocalDateTime.of(2013, Month.FEBRUARY, 25, 11, 37);
		assertEquals(date.toInstant(ZoneOffset.UTC).toEpochMilli(), dt.getParsedValue());
	}
	
	@Test
	public void testBad() throws Exception{
		jCas.setDocumentText("This test was written at 301137Z FEB 13");
		processJCas();
		
		assertEquals(0, JCasUtil.select(jCas, DateTime.class).size());
		
		jCas.reset();
		jCas.setDocumentText("This test was written at 901137Z FEB 13");
		processJCas();
		
		assertEquals(0, JCasUtil.select(jCas, DateTime.class).size());
	}
}
