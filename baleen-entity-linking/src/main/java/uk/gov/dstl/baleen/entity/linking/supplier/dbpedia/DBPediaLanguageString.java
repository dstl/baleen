// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.supplier.dbpedia;

import org.apache.jena.rdf.model.RDFNode;

/** Class to represent the returned strings from DBPedia with language tags */
public class DBPediaLanguageString {

  private final String languageString;

  /**
   * Construct a language string
   *
   * @param languageString
   */
  public DBPediaLanguageString(String languageString) {
    this.languageString = languageString;
  }

  /**
   * Construct a language string from the RDFNode
   *
   * @param rdfNode
   */
  public DBPediaLanguageString(RDFNode rdfNode) {
    this(rdfNode.toString());
  }

  /** @return the language id */
  public String language() {
    int lastIndexOf = languageString.lastIndexOf('@');
    if (lastIndexOf > -1) {
      return languageString.substring(lastIndexOf);
    }
    return "";
  }

  /** @return the raw text */
  public String raw() {
    int lastIndexOf = languageString.lastIndexOf('@');
    if (lastIndexOf > -1) {
      return languageString.substring(0, lastIndexOf);
    }
    return languageString;
  }

  @Override
  public String toString() {
    return languageString;
  }
}
