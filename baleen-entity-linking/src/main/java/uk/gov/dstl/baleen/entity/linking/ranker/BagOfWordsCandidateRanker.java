// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.entity.linking.ranker;

import java.util.Collection;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import uk.gov.dstl.baleen.entity.linking.Candidate;
import uk.gov.dstl.baleen.entity.linking.CandidateRanker;
import uk.gov.dstl.baleen.entity.linking.EntityInformation;
import uk.gov.dstl.baleen.entity.linking.ranker.bag.BagOfWords;
import uk.gov.dstl.baleen.entity.linking.ranker.bag.BagOfWordsFactory;
import uk.gov.dstl.baleen.entity.linking.util.ScoredCandidate;
import uk.gov.dstl.baleen.types.semantic.Entity;

/**
 * A ranker that compares bags of word created from the {@link Candidate} to a bag of words created
 * from the {@link EntityInformation}.
 *
 * <p>It is assumed that words that occur more in the candidate are more important in defining that
 * candidate so are scored high if they also occur in the entity information.
 *
 * @param <T>
 */
public class BagOfWordsCandidateRanker<T extends Entity> implements CandidateRanker<T> {

  private static final int BOOST = 10;
  private BagOfWordsFactory bagOfWordsFactory;

  @Override
  public void initialize(Collection<String> stopwords) {
    bagOfWordsFactory = new BagOfWordsFactory(stopwords);
  }

  @Override
  public Optional<Candidate> getTopCandidate(
      EntityInformation<T> entityInformation, Collection<Candidate> candidates) {

    SortedSet<? extends Candidate> ranked = rankedCandidates(candidates, entityInformation);

    if (ranked.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.ofNullable(ranked.last());
    }
  }

  private SortedSet<? extends Candidate> rankedCandidates(
      Collection<Candidate> candidates, EntityInformation<T> entityInformation) {

    BagOfWords entityBag = bagOfWordsFactory.bagSentences(entityInformation.getSentences());

    TreeSet<ScoredCandidate> scored = new TreeSet<>();
    for (Candidate candidate : candidates) {
      scored.add(
          new ScoredCandidate(
              candidate,
              getExactMatchScore(entityInformation, candidate)
                  + getBagScore(entityBag, candidate)));
    }

    return scored;
  }

  private int getExactMatchScore(EntityInformation<T> information, Candidate candidate) {
    boolean hasExactMatch =
        information.getMentions().stream()
            .map(Entity::getValue)
            .anyMatch(candidate.getName()::equals);
    return hasExactMatch ? BOOST : 0;
  }

  private int getBagScore(BagOfWords entityBag, Candidate candidate) {
    BagOfWords candidateBag = bagOfWordsFactory.bagLines(candidate.getKeyValuePairs().values());
    return candidateBag.retain(entityBag).size();
  }
}
