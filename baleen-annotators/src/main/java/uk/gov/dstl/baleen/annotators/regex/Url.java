//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.regex;

import uk.gov.dstl.baleen.annotators.regex.internals.UrlRegex;

/**
 * For consistency and clarity of code, this class is a wrapper for {@link uk.gov.dstl.baleen.annotators.regex.internals.UrlRegex}.
 * Including UrlRegex directly in this package and refactoring it to Money would require us to fully qualify the type Url in the class.
 * 
 * 
 */
public class Url extends UrlRegex {

}
