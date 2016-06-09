package uk.gov.dstl.baleen.annotators.coreference.impl.sieves;

/**
 * A sieve stage in the coreference pipeline.
 */
@FunctionalInterface
public interface CoreferenceSieve {

	/**
	 * Apply the sieve.
	 */
	void sieve();

}
