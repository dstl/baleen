package uk.gov.dstl.baleen.annotators.misc.helpers;

import opennlp.tools.stemmer.Stemmer;

/**
* A no-op Stemmer which returns the same string as it is passed.
*/
public class NoOpStemmer implements Stemmer{
	@Override
	public CharSequence stem(CharSequence cs) {
		return cs;
	}
}
