// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.triage;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.triage.impl.ShannonEntropyCalculator;

public class ShannonEntropyCalculatorTest {

  private List<String> stringsToTest = new ArrayList<>();

  private ShannonEntropyCalculator<String> shannonEntropyCalculator;

  @Before
  public void setup() {
    stringsToTest.add("This");
    stringsToTest.add("is");
    stringsToTest.add("a");
    stringsToTest.add("selection");
    stringsToTest.add("of");
    stringsToTest.add("words");
    stringsToTest.add("in");
    stringsToTest.add("a");
    stringsToTest.add("sentence");

    shannonEntropyCalculator = new ShannonEntropyCalculator<>(stringsToTest);
  }

  @Test
  public void testShannonEntropyCalculation() {
    assertEquals(
        "Shannon Entropy of the sentence should be 2.9477",
        2.9477,
        0.00001,
        shannonEntropyCalculator.calculateShannonEntropy());
  }
}
