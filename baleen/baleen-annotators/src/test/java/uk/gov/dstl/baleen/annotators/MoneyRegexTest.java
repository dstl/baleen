//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.internals.MoneyRegex;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.types.TestMoney;
import uk.gov.dstl.baleen.types.common.Money;

/**
 * 
 */
public class MoneyRegexTest extends AbstractAnnotatorTest {
	
	public MoneyRegexTest() {
		super(MoneyRegex.class);
	}

	@Test
	public void testPounds() throws Exception{
		jCas.setDocumentText("James could have won £3.5 million on the lottery last night, but didn't.");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Money.class).size());
		
		assertAnnotations(1, Money.class, 
				new TestMoney(0, "£3.5 million", "GBP", 3500000.0));
	}
	
	@Test
	public void testPoundsUnicode() throws Exception{
		jCas.setDocumentText("James could have won \u00A33.5 million on the lottery last night, but didn't.");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Money.class).size());
		
		assertAnnotations(1, Money.class, 
				new TestMoney(0, "\u00A33.5 million", "GBP", 3500000.0));
	}
	
	@Test
	public void testPence() throws Exception{
		jCas.setDocumentText("A chocolate bar costs 37p");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Money.class).size());
		
		assertAnnotations(1, Money.class, 
				new TestMoney(0, "37p", "GBP", 0.37));
	}
	
	@Test
	public void testEuros() throws Exception{
		jCas.setDocumentText("James could have won €3.5 million on the lottery last night, but didn't.");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Money.class).size());
		
		assertAnnotations(1, Money.class, 
				new TestMoney(0, "€3.5 million", "EUR", 3500000.0));
	}
	
	@Test
	public void testEurosText() throws Exception{
		jCas.setDocumentText("James could have won 4 euros on the lottery last night, but didn't. He won 3.5 million euros instead.");
		processJCas();
		
		assertEquals(2, JCasUtil.select(jCas, Money.class).size());
		
		assertAnnotations(2, Money.class, 
				new TestMoney(0, "4 euros", "EUR", 4.0),
				new TestMoney(1, "3.5 million euros", "EUR", 3500000));
	}
	
	@Test
	public void testEurosUnicode() throws Exception{
		jCas.setDocumentText("James could have won \u20AC3.5 million on the lottery last night, but didn't.");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Money.class).size());
		
		assertAnnotations(1, Money.class, 
				new TestMoney(0, "\u20AC3.5 million", "EUR", 3500000.0));
	}
	
	@Test
	public void testDollars() throws Exception{
		jCas.setDocumentText("James could have won $3.5 million on the lottery last night, but didn't.");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Money.class).size());
		
		assertAnnotations(1, Money.class, 
				new TestMoney(0, "$3.5 million", "USD", 3500000.0));
	}
	
	@Test
	public void testDollarsUnicode() throws Exception{
		jCas.setDocumentText("James could have won \u00243.5 million on the lottery last night, but didn't.");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Money.class).size());
		
		assertAnnotations(1, Money.class, 
				new TestMoney(0, "\u00243.5 million", "USD", 3500000.0));
	}
	
	@Test
	public void testBengaliRupee() throws Exception{
		jCas.setDocumentText("James could have won \u09F33.5 million on the lottery last night, but didn't.");
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Money.class).size());
		
		assertAnnotations(1, Money.class, 
				new TestMoney(0, "\u09F33.5 million", null, 3500000.0));
	}

}
