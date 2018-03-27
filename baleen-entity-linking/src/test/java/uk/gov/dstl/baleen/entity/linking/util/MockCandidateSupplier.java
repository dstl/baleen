// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import uk.gov.dstl.baleen.entity.linking.Candidate;
import uk.gov.dstl.baleen.entity.linking.CandidateSupplier;
import uk.gov.dstl.baleen.entity.linking.EntityInformation;

@SuppressWarnings("rawtypes")
public class MockCandidateSupplier implements CandidateSupplier {

  @Override
  public Collection<Candidate> getCandidates(EntityInformation entityInformation) {
    Collection<Candidate> candidates = new HashSet<>();
    Candidate candidate =
        new Candidate() {
          @Override
          public String getId() {
            return "id";
          }

          @Override
          public String getName() {
            return "id";
          }

          @Override
          public Map<String, String> getKeyValuePairs() {
            return null;
          }
        };

    candidates.add(candidate);

    return candidates;
  }

  @Override
  public void configure(String[] argumentPairs) {}

  @Override
  public void close() {}
}
