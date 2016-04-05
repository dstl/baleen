//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.helpers;

import java.util.regex.Matcher;

import org.apache.uima.jcas.JCas;
import org.springframework.util.StringUtils;

import uk.gov.dstl.baleen.types.common.Quantity;

/**
 * Helper to deal with creating quantities.
 * 
 * 
 *
 */
public class QuantityUtils {
	private QuantityUtils() {

	}

	/**
	 * Create a new quantity instance.
	 * 
	 * @param aJCas
	 *            the jCas instance
	 * @param matcher
	 *            the matcher which matched the regex
	 * @param unit
	 *            the quantity units (eg m,acre)
	 * @param scale
	 *            the scaling factor to apply to the amount to calculate the
	 *            normalized units (e.g. if normalizedUnit is kg and unit is g
	 *            then scaled is 1000). If 0, then no normalization is set.
	 * @param normalizedUnit
	 *            the normalized unit of the quantity.
	 * @param quantityType
	 *            the quantityType (eg weight, distance)
	 * @return
	 */
	public static Quantity createQuantity(JCas aJCas, Matcher matcher,
			String unit, double scale, String normalizedUnit,
			String quantityType) {
		if (countPeriods(matcher.group(0)) > 1)
			return null;

		Quantity quant = new Quantity(aJCas);

		quant.setConfidence(1.0f);

		quant.setBegin(matcher.start());
		quant.setEnd(matcher.end());
		quant.setValue(matcher.group(0));

		Double amount = Double
				.parseDouble(matcher.group(1).replaceAll(",", ""));

		amount = QuantityUtils.scaleByMultipler(amount, matcher.group(3));

		quant.setQuantity(amount);
		quant.setUnit(unit);
		
		if(Math.abs(scale) > 2*Double.MIN_VALUE){
			quant.setNormalizedQuantity(amount * scale);
			quant.setNormalizedUnit(normalizedUnit);
		}

		quant.setSubType(quantityType);

		return quant;
	}

	/**
	 * Count the number of periods in a value.
	 * 
	 * @param haystack
	 * @return number of periods
	 */
	public static int countPeriods(String haystack) {
		return StringUtils.countOccurrencesOf(haystack, ".");
	}

	/**
	 * Scale a value by a 'text' multiplier.
	 * 
	 * @param amount
	 *            the original value
	 * @param multiplier
	 *            hundred, thousand, million, billion, trillion
	 * @return amount * multiplier (or amount if multiplier is not known)
	 */
	public static double scaleByMultipler(double amount, String multiplier) {
		if (multiplier != null) {
			switch (multiplier.toLowerCase()) {
			case "hundred":
				return amount * 100;
			case "k":
			case "thousand":
				return amount * 1000;
			case "m":
			case "million":
				return amount * 1000000;
			case "b":
			case "billion":
				return amount * 1000000000;
			case "t":
			case "trillion":
				return amount * 1000000000000L;
			default:
				return amount;
			}
		}

		return amount;
	}

}
