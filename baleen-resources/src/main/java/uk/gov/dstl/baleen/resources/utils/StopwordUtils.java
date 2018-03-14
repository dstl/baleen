// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.resources.utils;

import java.util.Collection;
import java.util.StringJoiner;
import java.util.regex.Pattern;

/** Helper methods for working with stopwords */
public class StopwordUtils {
  /** Private constructor as this is a helper class that shouldn't be instantiated */
  private StopwordUtils() {
    // Do nothing
  }

  /**
   * Build a regular expression that matches any of the current list of stopwords, along with any
   * additional terms provided.
   *
   * <p>Any additional terms provided are not escaped, so you can provide your own regular
   * expressions to include in the pattern.
   */
  public static Pattern buildStopwordPattern(
      Collection<String> stopwords, Boolean caseSensitive, String... additionalTerms) {
    StringJoiner sj = new StringJoiner("|");
    for (String s : stopwords) {
      sj.add(Pattern.quote(s));
    }

    if (additionalTerms != null) {
      for (String s : additionalTerms) {
        sj.add(s);
      }
    }

    if (caseSensitive) {
      return Pattern.compile("\\b(" + sj.toString() + ")\\b");
    } else {
      return Pattern.compile("\\b(" + sj.toString() + ")\\b", Pattern.CASE_INSENSITIVE);
    }
  }

  /** Returns true if word is a stopword */
  public static boolean isStopWord(
      String word, Collection<String> stopwords, Boolean caseSensitive) {
    if (!caseSensitive) {
      return stopwords.stream().filter(s -> s.equalsIgnoreCase(word)).count() >= 1;
    } else {
      return stopwords.contains(word);
    }
  }
}
