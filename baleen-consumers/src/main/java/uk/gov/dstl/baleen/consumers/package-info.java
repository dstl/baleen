/**
 * Contains consumers that will do something with the processed CAS.
 * 
 * <p>CAS Consumers, although confusingly named, are what UIMA uses to output processed CAS objects, for example to a Mongo database.
 * The output may not be the CAS itself, but analysis or information about the CAS (for example, a count of the entities extracted).</p>
 * <p>Consumers will be run by Baleen after all the annotators have run.</p>
 */
//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers;