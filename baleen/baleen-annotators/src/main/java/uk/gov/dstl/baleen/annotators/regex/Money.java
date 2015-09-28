//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.regex;

import uk.gov.dstl.baleen.annotators.regex.internals.MoneyRegex;

/**
 * For consistency and clarity of code, this class is a wrapper for {@link uk.gov.dstl.baleen.annotators.regex.internals.MoneyRegex}.
 * Including MoneyRegex directly in this package and refactoring it to Money would require us to fully qualify the type Money in the class.
 * 
 * 
 */
public class Money extends MoneyRegex {

}
