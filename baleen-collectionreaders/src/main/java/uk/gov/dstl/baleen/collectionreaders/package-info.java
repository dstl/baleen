/**
 * Contains collection readers to read data into the Baleen pipeline.
 *
 * <p>Collection Readers are used to read in a data set and pass 'documents' from it into the Baleen
 * pipeline. In Baleen, we have modified the UIMA concept of a Collection Reader so that it is
 * permanently looking for new content; that is, the doHasNext() method on BaleenCollectionReader
 * will be continuously polled for new documents.
 */
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.collectionreaders;
