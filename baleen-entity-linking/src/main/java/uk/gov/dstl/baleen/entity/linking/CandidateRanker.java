// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking;

import java.util.Collection;
import java.util.Optional;

import uk.gov.dstl.baleen.types.semantic.Entity;

/**
 * Interface for ranking a collection of candidates
 *
 * @param <T> The Entity type
 */
public interface CandidateRanker<T extends Entity> {

  /**
   * Get the top candidate as an Optional
   *
   * @param entityInformation The entity information the candidates relate to
   * @param candidates The collection of candidates to be ranked
   * @return An Optional of type Candidate
   */
  Optional<Candidate> getTopCandidate(
      EntityInformation<T> entityInformation, Collection<Candidate> candidates);

  /**
   * Supply stopwords to the ranker (if required)
   *
   * <p>This is present as configuration can not be passed directly to these classes.
   *
   * @param stopwords
   */
  void initialize(Collection<String> stopwords);
}
