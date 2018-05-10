// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.jobs.renoun;

import static org.junit.Assert.assertEquals;
import static uk.gov.dstl.baleen.jobs.renoun.ScoredPattern.COHERENCE_KEY;
import static uk.gov.dstl.baleen.jobs.renoun.ScoredPattern.FREQUENCY_KEY;
import static uk.gov.dstl.baleen.jobs.renoun.ScoredPattern.PATTERN_FACT_FIELD;

import org.bson.Document;
import org.junit.Test;

public class ScoredPatternTest {

  @Test
  public void testScoredPatternIsCreatedFromDocument() {

    Document document =
        new Document()
            .append(PATTERN_FACT_FIELD, "pattern1")
            .append(FREQUENCY_KEY, 2)
            .append(COHERENCE_KEY, 0.998);

    ScoredPattern scoredPattern = new ScoredPattern(document);

    assertEquals(
        "ScoredPattern's coherence should be 0.998", 0.998, scoredPattern.getCoherence(), 0.0);

    assertEquals("Scored Pattern's frequency should be 2", 2, scoredPattern.getFrequency());
  }
}
