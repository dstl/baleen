/**
 * Contains annotators that annotate entities based on a Gazetteer
 *
 * <p>A gazetteer is essentially a list of terms we are interested in, and gazetteer annotators will
 * look for each of these terms in turn and annotate them within the document. Annotators in this
 * package largely all perform the same function, with the primary difference being the source of
 * the gazetteer.
 *
 * <p>A gazetteer approach gives high precision and recall on the terms it matches, but does require
 * someone to put together a comprehensive gazetteer in the first place as anything not in the
 * gazetteer will not be extracted. This also makes it sensitive to spelling mistakes, variations on
 * spelling, etc.
 */
// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.gazetteer;
