// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking;

import java.util.Collection;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.semantic.Entity;

/**
 * Interface for candidate suppliers.
 *
 * @param <T> The Entity type
 */
public interface CandidateSupplier<T extends Entity> {

  /**
   * Retrieve a collection of candidates (eg from DBPedia or Mongo)
   *
   * @param entityInformation The supplied information about the entity of interest
   * @return a Collection of Candidates
   */
  Collection<Candidate> getCandidates(EntityInformation<T> entityInformation);

  /**
   * Configure the CandidateSupplier
   *
   * @param argumentPairs The key value pairs to be used as arguments
   * @throws BaleenException
   */
  void configure(String[] argumentPairs) throws BaleenException;

  /**
   * Close any resource used
   *
   * @throws BaleenException
   */
  void close() throws BaleenException;
}
