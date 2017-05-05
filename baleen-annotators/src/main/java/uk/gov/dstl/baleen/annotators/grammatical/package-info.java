/**
 * Contains annotators that use grammatical techniques to annotate entities.
 * 
 * <p>We can use grammatical features within the text to identify potential entities that might be missed by other annotators.
 * For example, the {@link QuantityNPEntity} annotator looks for noun phrases that follow from quantities,
 * potentially indicating the entity which the quantity refers to. This is a flexible approach and is largely agnostic to the type of document, but can be limited in identifying the type of entity.</p>
 * <p>Classes in this package are likely to have required the language annotators to have been run, see {@link uk.gov.dstl.baleen.annotators.language}.</p>
 */
//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.grammatical;