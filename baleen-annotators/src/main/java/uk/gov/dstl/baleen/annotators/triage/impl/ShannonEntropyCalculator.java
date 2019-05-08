// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.triage.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.gov.dstl.baleen.annotators.helpers.MathUtils;

/**
 * Class for generating the Shannon entropy of a list of items
 *
 * @param <T> The type of the items the Shannon entropy will be calculated for
 */
public class ShannonEntropyCalculator<T> {

  private final List<T> items;
  private static final int LOG_BASE = 2;

  /**
   * The constructor
   *
   * @param items The list of items that the Shannon entropy will be calculated for
   */
  public ShannonEntropyCalculator(List<T> items) {
    this.items = items;
  }

  /**
   * Calculates the Shannon entropy of the list of items
   *
   * @return The Shannon entropy
   */
  public double calculateShannonEntropy() {

    Stream<Double> probabilitiesTimesLogProbabilities =
        getItemProbabilities().values().stream()
            .map(probability -> probability * MathUtils.logarithm(LOG_BASE, probability));

    return -probabilitiesTimesLogProbabilities.mapToDouble(Double::doubleValue).sum();
  }

  private Map<T, Double> getItemProbabilities() {

    return items.stream()
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
        .entrySet()
        .stream()
        .collect(
            Collectors.toMap(Entry::getKey, e -> (double) e.getValue() / (double) items.size()));
  }
}
