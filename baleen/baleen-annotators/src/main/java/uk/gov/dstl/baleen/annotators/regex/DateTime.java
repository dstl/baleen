//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.regex;

import uk.gov.dstl.baleen.annotators.regex.internals.DateTimeRegex;

/**
 * For consistency and clarity of code, this class is a wrapper for {@link uk.gov.dstl.baleen.annotators.regex.internals.DateTimeRegex}.
 * Including DateTimeRegex directly in this package and refactoring it to DateTime would require us to fully qualify the type DateTime in the class.
 * 
 * 
 */
public class DateTime extends DateTimeRegex {

}
