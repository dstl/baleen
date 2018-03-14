// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import com.google.common.base.Splitter;

import uk.gov.dstl.baleen.resources.data.Gender;
import uk.gov.dstl.baleen.resources.data.Multiplicity;
import uk.gov.dstl.baleen.uima.BaleenResource;

/**
 * Resource for gender multiplicities
 *
 * <p>Due to the nature of the data the gender should be of reasonably high quality, but the
 * multiplicity is poor. This is because there is no singular mention of counts in the data, so its
 * impossible to understand how many times relatively a word is used a singular vs plural.
 *
 * <p>Patterns with numbers are ignored.
 *
 * <p>The implementation stores three maps (per gender / multiplicity) one for exact matches, one
 * for starts with and one for endsWith. To improve performance endsWith contains the reversed text
 * so it can be matched backwards.
 *
 * <p>This data was originally from http://www.clsp.jhu.edu/~sbergsma/Gender/ and the data is
 * licenced under the Creative Commons Unported License. See the associated paper: Shane Bergsma and
 * Dekang Lin, Bootstrapping Path-Based Pronoun Resolution, In Proceedings of the Conference on
 * Computational Lingustics / Association for Computational Linguistics (COLING/ACL-06), Sydney,
 * Australia, July 17-21, 2006.
 */
public class SharedGenderMultiplicityResource extends BaleenResource {
  // TODO: I'm not sure if the exact match is correct - should we look for an exact match or
  // should this be put into both start and end?
  // Perhaps the whole thing would be better as contains but that would be very slow. Better to
  // return UNKNOWN than guess

  private static final Splitter LINE_SPLITTER = Splitter.on(" ");
  private static final Splitter WORD_SPLITTER = Splitter.on(" ").trimResults().omitEmptyStrings();

  // TODO: These values are just guesses
  private static final int GENDER_SAMPLE_THRESHOLD = 20;
  private static final int PLURAL_THRESHOLD = 200;

  private final Map<String, Multiplicity> exactMultiplicity = new HashMap<>();
  private final Map<String, Multiplicity> endsWithMultiplicity = new HashMap<>();
  private final Map<String, Multiplicity> startsWithMultiplicity = new HashMap<>();

  private final Map<String, Gender> exactGender = new HashMap<>();
  private final Map<String, Gender> endsWithGender = new HashMap<>();
  private final Map<String, Gender> startsWithGender = new HashMap<>();

  @Override
  protected boolean doInitialize(ResourceSpecifier specifier, Map<String, Object> additionalParams)
      throws ResourceInitializationException {

    Arrays.asList(
            "gender.aa.gz",
            "gender.ab.gz",
            "gender.ac.gz",
            "gender.ad.gz",
            "gender.ae.gz",
            "gender.af.gz")
        .stream()
        .flatMap(
            f -> {
              try (BufferedReader reader =
                  new BufferedReader(
                      new InputStreamReader(
                          new GZIPInputStream(getClass().getResourceAsStream("gender/" + f)),
                          StandardCharsets.UTF_8))) {
                // Crazy, but if we return then the inputstream gets closed so the lines()
                // stream fails.
                return reader.lines().collect(Collectors.toList()).stream();
              } catch (final Exception e) {
                getMonitor().warn("Unable to load from gender file", e);
                return Stream.empty();
              }
            })
        .filter(s -> s.contains("\t"))
        // TODO; Currently ignore any of the numerical stuff its too tedious to work with
        .filter(s -> !s.contains("#"))
        .forEach(s -> loadFromGenderRow(s));

    return super.doInitialize(specifier, additionalParams);
  }

  private void loadFromGenderRow(String s) {
    try {
      final String[] line = s.split("\t", 2);
      final String np = line[0].trim().toLowerCase();
      final Iterable<String> counts = LINE_SPLITTER.split(line[1]);
      final Iterator<String> iterator = counts.iterator();

      final int m = Integer.parseInt(iterator.next());
      final int f = Integer.parseInt(iterator.next());
      final int n = Integer.parseInt(iterator.next());
      final int p = Integer.parseInt(iterator.next());

      final int genderTotal = m + f + n;

      if (genderTotal > GENDER_SAMPLE_THRESHOLD) {

        if (m > 2 * Math.max(f, n)) {
          saveGender(np, Gender.M);
        } else if (f > 2 * Math.max(m, n)) {
          saveGender(np, Gender.F);
        } else if (n > 2 * Math.max(m, f)) {
          saveGender(np, Gender.N);
        }
      }

      if (p > PLURAL_THRESHOLD) {
        // TODO: Since we don't have a singular count I guess we just have a
        // threshold here? I can't see how you compare to the m/f/n words

        saveMultiplicity(np, Multiplicity.PLURAL);

      } else if (genderTotal > GENDER_SAMPLE_THRESHOLD) {
        // If we've seen it a lot otherwise we assume it must be singular
        saveMultiplicity(np, Multiplicity.SINGULAR);
      }

    } catch (final Exception e) {
      getMonitor().warn("Unable to parse line {}", s, e);
    }
  }

  private void saveMultiplicity(String np, Multiplicity multiplicity) {
    final String key = np.replaceAll("!", "").trim();
    if (np.startsWith("!")) {
      endsWithMultiplicity.put(reverse(key), multiplicity);
    } else if (np.endsWith("!")) {
      startsWithMultiplicity.put(key, multiplicity);
    } else {
      exactMultiplicity.put(key, multiplicity);
    }
  }

  private void saveGender(String np, Gender gender) {
    final String key = np.replaceAll("!", "").trim();

    if (np.startsWith("!")) {
      endsWithGender.put(reverse(key), gender);
    } else if (np.endsWith("!")) {
      startsWithGender.put(key, gender);
    } else {
      exactGender.put(key, gender);
    }
  }

  /** Return the gender for a given string, if it is known, or UNKNOWN otherwise */
  public Gender lookupGender(String text) {
    return lookup(exactGender, startsWithGender, endsWithGender, text, Gender.UNKNOWN);
  }

  /** Return the multiplicity for a given string, if it is known, or UNKNOWN otherwise */
  public Multiplicity lookupMultiplicity(String text) {
    return lookup(
        exactMultiplicity,
        startsWithMultiplicity,
        endsWithMultiplicity,
        text,
        Multiplicity.UNKNOWN);
  }

  private <T> T lookup(
      Map<String, T> exact,
      Map<String, T> startsWith,
      Map<String, T> endsWith,
      String inputText,
      T defaultValue) {
    String text = inputText.toLowerCase();

    // Try an exact match
    T t = exact.get(text);
    if (t != null) {
      return t;
    }

    final List<String> words = WORD_SPLITTER.splitToList(text);

    // Try start
    t = lookup(startsWith, words);
    if (t != null) {
      return t;
    }

    // Try endWith
    final List<String> reversed = new ArrayList<>(words.size());
    for (int i = words.size() - 1; i > 0; i--) {
      final String word = reverse(words.get(i));
      reversed.add(word);
    }

    t = lookup(endsWith, reversed);
    if (t != null) {
      return t;
    }

    return defaultValue;
  }

  private <T> T lookup(Map<String, T> map, List<String> words) {
    for (int i = words.size() - 1; i >= 0; i--) {
      final String s = words.stream().skip(i).collect(Collectors.joining(" "));
      final T t = map.get(s);
      if (t != null) {
        return t;
      }
    }

    return null;
  }

  private String reverse(String s) {
    return new StringBuilder(s).reverse().toString();
  }
}
