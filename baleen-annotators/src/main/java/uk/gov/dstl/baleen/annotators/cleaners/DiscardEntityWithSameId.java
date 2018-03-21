// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.cleaners;

import uk.gov.dstl.baleen.types.semantic.Entity;

/**
 * Discards entities based on duplicate external ids. One entity of each set of duplicates is kept,
 * the choice of which to keep is arbitrary
 */
public class DiscardEntityWithSameId extends AbstractDiscardWithSameId<Entity> {

  /** Instantiates a new discard entity with same id. */
  public DiscardEntityWithSameId() {
    super(Entity.class);
  }
}
