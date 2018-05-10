// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.rdf;

/** An enum for the (Jena) RDF formats supported. */
public enum RdfFormat {

  /** Terse RDF Triple Language. Output is similar in form to SPARQL */
  TURTLE("TURTLE", ".ttl"),

  /**
   * Standard RDF XML serialisation
   *
   * @see https://www.w3.org/TR/rdf-syntax-grammar/
   */
  RDF_XML("RDF/XML", ".rdf"),

  /** Abbreviated RDF XML serialisation */
  RDF_XML_ABBREV("RDF/XML-ABBREV", ".rdf"),

  /** Each line is a tripple in the form "Subject Predicate Object ." */
  N_TRIPLES("N-TRIPLES", ".nt"),

  /**
   * A JSON representation of the RDF
   *
   * @see https://jena.apache.org/documentation/io/rdf-json.html
   */
  RDF_JSON("RDF/JSON", ".json"),

  /**
   * JSON for Linked Data
   *
   * @see https://json-ld.org/
   */
  JSONLD("JSON-LD", ".jsonld"),

  /** Notation3. A Human readable triple format. */
  N3("N3", ".n3");

  private final String key;
  private final String ext;

  private RdfFormat(String key, String ext) {
    this.key = key;
    this.ext = ext;
  }

  /** @return the key string for this format. */
  public String getKey() {
    return key;
  }

  /** @return the file extension for the format */
  public String getExt() {
    return ext;
  }
}
