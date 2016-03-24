package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.internals.MoneyRegex;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.Money;

public class MoneyRegexTest extends AbstractAnnotatorTest {
	public MoneyRegexTest() {
		super(MoneyRegex.class);
	}
	
	@Test
	public void test() throws Exception{
		assertFoundMoney("The price is £3", 3.0, "GBP");
		assertFoundMoney("The price is £3.50", 3.5, "GBP");
		assertFoundMoney("The price is £3000", 3000.0, "GBP");
		assertFoundMoney("The price is £3,000", 3000.0, "GBP");
		assertFoundMoney("The price is £2,999.99", 2999.99, "GBP");
		assertFoundMoney("The price is £2999.99", 2999.99, "GBP");
		assertFoundMoney("The price is £2 999.99", 2999.99, "GBP");
		assertFoundMoney("The price is £2,999,999.99", 2999999.99, "GBP");
		assertFoundMoney("The price is £2,999,999", 2999999.0, "GBP");
		assertFoundMoney("The price is $1,234,567.89", 1234567.89, "USD");
		assertFoundMoney("The price is $1 234 567.89", 1234567.89, "USD");
		assertFoundMoney("The price is 1€", 1.0, "EUR");
		assertFoundMoney("The price is 1 €", 1.0, "EUR");
		assertFoundMoney("The price is 1,23 €", 1.23, "EUR");
		assertFoundMoney("The price is 1.23 €", 1.23, "EUR");
		assertFoundMoney("The price is 123 456,78 €", 123456.78, "EUR");
		assertFoundMoney("The price is 123,456.78 €", 123456.78, "EUR");
		assertFoundMoney("The price is 123.456,78 €", 123456.78, "EUR");
		assertFoundMoney("The price is 123.456.789,99 €", 123456789.99, "EUR");
		assertFoundMoney("The price is 123.456.789 €", 123456789.0, "EUR");
		assertFoundMoney("The price is 1.234 €", 1234.0, "EUR");
		assertFoundMoney("The price is 50p", 0.5, "GBP");
		assertFoundMoney("The price is £1.99p", 1.99, "GBP");
		assertFoundMoney("The price is 47¢", 0.47, "USD");
		assertFoundMoney("The price is 47c", 0.47, "EUR");
		assertFoundMoney("The price is $1.99¢", 1.99, "USD");
		assertFoundMoney("The price is £37 million", 37000000.0, "GBP");
		assertFoundMoney("The price is £37m", 37000000.0, "GBP");
		assertFoundMoney("The price is $20k", 20000.0, "USD");
		assertFoundMoney("The price is 47 thousand €", 47000.0, "EUR");
		assertFoundMoney("The price is 47 € thousand", 47000.0, "EUR");
		assertFoundMoney("The price is ¥3000", 3000.0, "JPY");
		assertFoundMoney("The price is SEK 47,000", 47000.0, "SEK");
		assertFoundMoney("The price is 47,000 IQD", 47000.0, "IQD");
		assertFoundMoney("The price is \u0024100", 100.0, "USD");
		assertFoundMoney("The price is \u20AC100", 100.0, "EUR");
		assertFoundMoney("The price is \u00A3100", 100.0, "GBP");
		assertFoundMoney("The price is \u00A5100", 100.0, "JPY");
		assertFoundMoney("The price is 47 Fr", 47.0, "CHF");
		assertFoundMoney("The price is Fr 47", 47.0, "CHF");
		assertFoundMoney("£47", 47.0, "GBP");
		
		assertEquals("£1.25", assertFoundMoney("The price is £1.25 and not a penny less", 1.25, "GBP"));
		assertEquals("£1.25", assertFoundMoney("The price is £1.25.", 1.25, "GBP"));
		assertEquals("£1.25", assertFoundMoney("The price is £1.25\nshe said.", 1.25, "GBP"));
		assertEquals("£1.25", assertFoundMoney("£1.25, the price is.", 1.25, "GBP"));
	}
	
	@Test
	public void testFalse() throws Exception{
		assertNotFoundMoney("£47.00.00");
		assertNotFoundMoney("$47,000.000,00");
		assertNotFoundMoney("€47.000,000.00");
		assertNotFoundMoney("£47,000,000.000.000");
		assertNotFoundMoney("47");
		assertNotFoundMoney("47.00");
	}
	
	private String assertFoundMoney(String text, Double amount, String currency) throws Exception{
		jCas.reset();
		
		jCas.setDocumentText(text);
		processJCas();
		
		assertEquals(1, JCasUtil.select(jCas, Money.class).size());
		Money m = JCasUtil.selectByIndex(jCas, Money.class, 0);
		
		assertEquals(amount, m.getAmount(), 0.00000001);
		assertEquals(currency, m.getCurrency());
		
		return m.getValue();
	}
	
	private void assertNotFoundMoney(String text) throws Exception{
		jCas.reset();
		
		jCas.setDocumentText(text);
		processJCas();
		
		assertEquals(0, JCasUtil.select(jCas, Money.class).size());
	}
}
