/**
 * Contains annotators that clean up after other annotators, for example removing nested
 * annotations.
 *
 * <p>Generally, annotators act independently of other annotators. This can lead to nested
 * annotations or the same annotation being pulled out several times. In addition to this, what is
 * extracted by annotators isn't always clean or standardised. To address both these issues, we use
 * cleaners at the end of the pipeline to examine existing annotations and tidy them up.
 *
 * <p>Classes in this package will generally go at the end of the pipeline, once all the other
 * annotators have done the extraction.
 */
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;
