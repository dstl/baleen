// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.util;

/**
 * StringTriple helper for building SPARQL queries. Triples in SPARQL are of the form: Subject,
 * Predicate, Object
 */
public class StringTriple {

  private final String subject;
  private final String predicate;
  private final String object;

  /**
   * The Constructor
   *
   * @param subject The subject
   * @param predicate The predicate
   * @param object The object
   */
  public StringTriple(String subject, String predicate, String object) {
    this.subject = subject;
    this.predicate = predicate;
    this.object = object;
  }

  /** @return subject */
  public String getSubject() {
    return subject;
  }

  /** @return predicate */
  public String getPredicate() {
    return predicate;
  }

  /** @return object */
  public String getObject() {
    return object;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || o.getClass() != getClass()) {
      return false;
    }
    StringTriple st = (StringTriple) o;
    return subject.equals(st.getSubject())
        && predicate.equals(st.getPredicate())
        && object.equals(st.getObject());
  }

  @Override
  public int hashCode() {
    return subject.hashCode() + predicate.hashCode() + object.hashCode();
  }
}
