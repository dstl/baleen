// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.patterns.data;

import com.google.common.base.Objects;

import uk.gov.dstl.baleen.types.semantic.Relation;

/**
 * Wraps a relation, such that it can be used in equals / hashcode for stream.distinct / set
 * operations.
 */
public class RelationWrapper {
  private final Relation relation;

  /**
   * Instantiates a new relation wrapper.
   *
   * @param relation the relation
   */
  public RelationWrapper(final Relation relation) {
    this.relation = relation;
  }

  /**
   * Gets the relation.
   *
   * @return the relation
   */
  public Relation getRelation() {
    return relation;
  }

  // We specifically equals / hashcode on the specific aspects of Relation

  // TODO: We don't mind about the begin/end value as long as the relation specific info is
  // the same?

  @Override
  public boolean equals(final Object other) {
    if (!(other instanceof RelationWrapper)) {
      return false;
    } else {
      final Relation or = ((RelationWrapper) other).getRelation();
      if (relation.getBegin() != or.getBegin()) {
        return false;
      } else if (relation.getEnd() != or.getEnd()) {
        return false;
      } else if (!stringEquals(relation.getRelationshipType(), or.getRelationshipType())) {
        return false;
      } else if (!stringEquals(relation.getRelationSubType(), or.getRelationSubType())) {
        return false;
      } else if (relation.getSource() != or.getSource()) {
        return false;
      } else if (relation.getTarget() != or.getTarget()) {
        return false;
      }

      return true;
    }
  }

  private boolean stringEquals(String a, String b) {
    if (a == null && b == null) {
      return true;
    } else if ((a == null && b != null) || (a != null && b == null)) {
      return false;
    }

    return a.equals(b);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        relation.getBegin(),
        relation.getEnd(),
        relation.getRelationshipType(),
        relation.getRelationSubType(),
        relation.getSource(),
        relation.getTarget());
  }
}
