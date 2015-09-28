/**
 * Contains annotators that are based on statistical methods.
 * 
 * Statistical methods for entity extraction generally involve training a model to recognise what a certain entity type,
 * for example 'People', look like within the context of a sentence or a document.
 * <p>
 * This allows for a very flexible approach to entity extraction, where we don't require prior knowledge of what entities are likely to appear,
 * but does require that models are trained on appropriate training data for optimum performance.
 * Models trained on training data that isn't representative of the data being processed are likely to miss entities and extract incorrect entities.
 * <p>
 * The performance of statistical methods of extraction is usually measured with a metric known as the F-measure or F1 score.
 * The F-measure is a value between 0 and 1, which takes into account both the precision (how accurate the entities we've extracted are)
 * and recall (how many of the actual entities did we extract). An F-measure of 1 indicates perfect performance.
 */
//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.stats;