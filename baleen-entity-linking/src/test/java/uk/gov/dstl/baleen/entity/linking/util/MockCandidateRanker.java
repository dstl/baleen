// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.util;

import java.util.Collection;
import java.util.Optional;

import uk.gov.dstl.baleen.entity.linking.Candidate;
import uk.gov.dstl.baleen.entity.linking.CandidateRanker;
import uk.gov.dstl.baleen.entity.linking.EntityInformation;
import uk.gov.dstl.baleen.types.semantic.Entity;

public class MockCandidateRanker<T extends Entity> implements CandidateRanker<T> {

  @Override
  public Optional<Candidate> getTopCandidate(
      EntityInformation<T> entityInformation, Collection<Candidate> candidates) {
    return Optional.of(candidates.iterator().next());
  }

  @Override
  public void initialize(Collection<String> stopwords) {
    // IGNORE
  }
}
