//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.testing.types;

import static org.junit.Assert.assertEquals;
import uk.gov.dstl.baleen.annotators.testing.TestAnnotation;
import uk.gov.dstl.baleen.types.common.Money;

public class TestMoney extends TestAnnotation<Money> {

	private double amount;
	private String currency;

	public TestMoney(int index, String text, String currency, double amount) {
		super(index, text);
		this.currency = currency;
		this.amount = amount;
	}
	
	public String getCurrency() {
		return currency;
	}
	
	public double getAmount() {
		return amount;
	}
	
	@Override
	public void validate(Money t) {
		super.validate(t);
		assertEquals(currency, t.getCurrency());
		assertEquals(amount, t.getAmount(), 0.001);
	}

}
