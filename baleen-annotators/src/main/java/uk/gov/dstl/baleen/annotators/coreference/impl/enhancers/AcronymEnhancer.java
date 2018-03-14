// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.coreference.impl.enhancers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import uk.gov.dstl.baleen.annotators.coreference.impl.data.Mention;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.MentionType;
import uk.gov.dstl.baleen.types.language.WordToken;

/** Generates acronyms for a mention. */
public class AcronymEnhancer implements MentionEnhancer {

  @Override
  public void enhance(Mention mention) {
    // Stanford just use the uppercases but only on the NNP parts,
    // We are looking at everything in the next
    // If we have more than two upper cases, we do the lower case

    if (mention.getType() == MentionType.PRONOUN) {
      return;
    }

    Set<String> allAcronyms;

    if (mention.isAcronym()) {
      allAcronyms = Collections.singleton(mention.getText().toUpperCase());
    } else {

      final Collection<WordToken> words = mention.getWords();

      allAcronyms = new HashSet<>();

      allAcronyms.addAll(acronyms(mention));
      allAcronyms.addAll(acronymsNNP(words));
    }

    if (allAcronyms.isEmpty()) {
      allAcronyms = Collections.emptySet();
    }

    mention.setAcronym(allAcronyms);
  }

  /** Generate acronyms from covered text */
  private Set<String> acronyms(Mention mention) {
    final String text = mention.getText();

    final StringBuilder upperCase = new StringBuilder();
    final StringBuilder upperAndLowerCase = new StringBuilder();

    Set<String> acronyms = new HashSet<>();

    boolean considerNext = true;
    for (int i = 0; i < text.length(); i++) {
      final char c = text.charAt(i);
      if (considerNext) {
        if (Character.isUpperCase(c)) {
          upperCase.append(c);
          upperAndLowerCase.append(c);
        } else {
          upperAndLowerCase.append(c);
        }
        considerNext = false;
      }

      if (Character.isWhitespace(c)) {
        considerNext = true;
      }
    }

    // We require two upper case to avoid obvious captialisation (start of sentences)
    if (upperCase.length() > 2) {
      acronyms.add(upperCase.toString());
    } else if (upperCase.length() > 2 && upperAndLowerCase.length() != upperCase.length()) {
      acronyms.add(upperAndLowerCase.toString().toUpperCase());
    }

    return acronyms;
  }

  /** Create acronym based on just the NNS, but unlike Stanford use lower and upper case again */
  private Set<String> acronymsNNP(Collection<WordToken> words) {
    final StringBuilder upperCaseNNP = new StringBuilder();
    final StringBuilder upperAndLowerCaseNNP = new StringBuilder();

    Set<String> acronyms = new HashSet<>();

    words
        .stream()
        .filter(p -> "NNP".equalsIgnoreCase(p.getPartOfSpeech()))
        .map(w -> w.getCoveredText().charAt(0))
        .forEach(
            c -> {
              if (Character.isUpperCase(c)) {
                upperCaseNNP.append(c);
                upperAndLowerCaseNNP.append(c);
              } else {
                upperAndLowerCaseNNP.append(c);
              }
            });

    if (upperCaseNNP.length() > 2) {
      acronyms.add(upperCaseNNP.toString());
    } else if (upperCaseNNP.length() > 2
        && upperAndLowerCaseNNP.length() != upperCaseNNP.length()) {
      acronyms.add(upperAndLowerCaseNNP.toString().toUpperCase());
    }

    return acronyms;
  }
}
