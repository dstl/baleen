//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.regex.internals;

import java.util.regex.Matcher;

import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.helpers.QuantityUtils;
import uk.gov.dstl.baleen.annotators.regex.helpers.AbstractRegexAnnotator;
import uk.gov.dstl.baleen.types.common.Money;

/**
 * Extract monetary amounts from document content using regular expressions
 * 
 * <p>Document content is passed through a regular expression and monetary values are extracted (see source code for the regular expression).
 * The annotator will then try to determine the currency and convert textual multipliers (e.g. 30 million) into actual numbers.</p>
 * <p>Assumes that any £ signs refer to GBP and $ signs refer to USD. The regular expression is limited in what it can extract, so may miss some expressions of money.</p>
 * 
 * 
 */
public class MoneyRegex extends AbstractRegexAnnotator<Money> {
	private static final String MONEY_REGEX = "(([£$€\\u0024\\u00A2\\u00A3\\u00A4\\u00A5\\u09F2\\u09F3\\u0AF1\\u0BF9\\u0E3F\\u17DB\\u20A0\\u20A1\\u20A2\\u20A3\\u20A4\\u20A5\\u20A6\\u20A7\\u20A8\\u20A9\\u20AA\\u20AB\\u20AC\\u20AD\\u20AE\\u20AF\\u20B0\\u20B1\\uFDFC\\uFE69\\uFF04\\uFFE0\\uFFE1\\uFFE5\\uFFE6])(([0-9]{1,3}([,][0-9]{3})+([.][0-9]{0,2})?)|([0-9]{1,3}([.][0-9]{3})+([,][0-9]{1,2})?\\b)|([0-9]{1,3}([,.][0-9]{0,2})?)(\\h((thousand)|(million)|(billion)|(trillion)))?))|(\\b([0-9]{1,2})\\h?((pence)|(cent(s)?)|(p))\\b)|(([0-9]{1,3}([\\.,][0-9]{1,3})*)(\\h+(thousand|million|billion|trillion))*\\h((dollar(s)?)|(pound(s)?)|(euro(s)?))\\b)";

	/** New instance.
	 * 
	 */
	public MoneyRegex() {
		super(MONEY_REGEX, false, 1.0f);
	}
	
	@Override
	protected Money create(JCas jCas, Matcher matcher) {
		Money money = new Money(jCas);
		
		try{
			if(matcher.group(1) != null){	//e.g. $1234.56 or £100 million
				money.setCurrency(symbolToCurrency(matcher.group(2)));

				Double amount = Double.parseDouble(matcher.group(3).toLowerCase().replaceAll(",", "").replaceAll("\\h", "").replaceAll("thousand|million|billion|trillion",""));
				
				amount = QuantityUtils.scaleByMultipler(amount, matcher.group(13));
				money.setAmount(amount);
				
			}else if(matcher.group(18) != null){	//e.g. 37p
				//Can't determine currency type for cents, could be USD and EUR
				if(matcher.group(20).toLowerCase().startsWith("p")){
					money.setCurrency("GBP");
				}
				money.setAmount(Double.parseDouble(matcher.group(19))/100);
			}else if(matcher.group(25) != null){	//e.g. 42 euros
				if(matcher.group(30).toLowerCase().startsWith("euro")){
					money.setCurrency("EUR");
				}
				Double amount = Double.parseDouble(matcher.group(26).toLowerCase().replaceAll(",", "").replaceAll("\\h", "").replaceAll("thousand|million|billion|trillion",""));
				
				amount = QuantityUtils.scaleByMultipler(amount, matcher.group(29));
				money.setAmount(amount);
			}
		}catch(NumberFormatException nfe){
			getMonitor().warn("Unable to parse amount and currency of money entity - these properties will not be set", nfe);
		}
		return money;
	}
	
	private String symbolToCurrency(String symbol){
		if("£".equals(symbol) || "\\u00A3".equals(symbol)){
			return "GBP";
		}else if("$".equals(symbol) || "\\u0024".equals(symbol)){
			return "USD";
		}else if("€".equals(symbol) || "\\u20AC".equals(symbol)){
			return "EUR";
		}
		return null;
	}

}
