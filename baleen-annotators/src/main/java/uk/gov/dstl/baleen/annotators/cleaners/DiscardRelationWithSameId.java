// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.cleaners;

import uk.gov.dstl.baleen.types.semantic.Relation;

/**
 * Discards relations based on duplicate external ids. One relation of each set of duplicates is
 * kept, the choice of which to keep is arbitrary
 */
public class DiscardRelationWithSameId extends AbstractDiscardWithSameId<Relation> {

  /** Instantiates a new discard relation with same id. */
  public DiscardRelationWithSameId() {
    super(Relation.class);
  }
}
