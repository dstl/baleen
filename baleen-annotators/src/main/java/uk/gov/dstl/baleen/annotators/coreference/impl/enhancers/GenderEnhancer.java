// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.coreference.impl.enhancers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import uk.gov.dstl.baleen.annotators.coreference.impl.data.Mention;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.MentionType;
import uk.gov.dstl.baleen.resources.SharedGenderMultiplicityResource;
import uk.gov.dstl.baleen.resources.data.Gender;
import uk.gov.dstl.baleen.types.Base;
import uk.gov.dstl.baleen.types.common.Nationality;
import uk.gov.dstl.baleen.types.common.Person;

/** Adds gender information to a mention. */
public class GenderEnhancer implements MentionEnhancer {
  private static final Map<String, Gender> PRONOUN_MAP = new HashMap<>();
  private static final Map<String, Gender> TITLE_MAP = new HashMap<>();
  private final SharedGenderMultiplicityResource genderResource;

  static {
    Arrays.asList("he", "him", "his", "himself").stream()
        .forEach(s -> PRONOUN_MAP.put(s, Gender.M));
    Arrays.asList("she", "her", "hers", "herself").stream()
        .forEach(s -> PRONOUN_MAP.put(s, Gender.F));
    Arrays.asList("it", "its", "itself", "when", "where", "there", "here").stream()
        .forEach(s -> PRONOUN_MAP.put(s, Gender.N));

    Arrays.asList(
            "mr",
            "master",
            "sir",
            "lord",
            "baron",
            "count",
            "duke",
            "prince",
            "king",
            "father",
            "fr",
            "brother",
            "abbott",
            "his royal highness",
            "his majesty",
            "emperor",
            "tsar")
        .stream()
        .forEach(s -> TITLE_MAP.put(s, Gender.M));
    Arrays.asList(
            "mrs",
            "miss",
            "ms",
            "dame",
            "lady",
            "baroness",
            "countess",
            "duchess",
            "princess",
            "queen",
            "mother",
            "sister",
            "abbess",
            "her royal highness",
            "her majesty",
            "empress",
            "tsarista")
        .stream()
        .forEach(s -> TITLE_MAP.put(s, Gender.F));
  }

  /** Constructor for GenderEnhancer */
  public GenderEnhancer(SharedGenderMultiplicityResource genderResource) {
    this.genderResource = genderResource;
  }

  @Override
  public void enhance(Mention mention) {
    if (mention.getType() == MentionType.PRONOUN) {
      mention.setGender(PRONOUN_MAP.getOrDefault(mention.getText().toLowerCase(), Gender.UNKNOWN));
    } else if (mention.getType() == MentionType.ENTITY) {
      final Base annotation = mention.getAnnotation();

      if (annotation instanceof Person) {
        final Person p = (Person) annotation;

        Gender gender = getGenderFromTitle(p.getTitle());
        if (gender == Gender.UNKNOWN) {
          gender = genderResource.lookupGender(mention.getText());
        }

        mention.setGender(gender);
      } else if (annotation instanceof Nationality) {
        mention.setGender(Gender.UNKNOWN);
      } else {
        mention.setGender(Gender.N);
      }
    } else {
      final Gender gender = genderResource.lookupGender(mention.getText());
      mention.setGender(gender);
    }
  }

  /**
   * Determine the gender of a title (e.g. Mr would return Gender.M), or return Gender.UNKNOWN if
   * not known
   */
  public static Gender getGenderFromTitle(String title) {
    if (title == null) return Gender.UNKNOWN;

    Gender gender = TITLE_MAP.get(title.trim().toLowerCase());

    if (gender == null) {
      for (String titlePart : title.split("\\h+")) {
        gender = TITLE_MAP.get(titlePart.trim().toLowerCase());

        if (gender != null) return gender;
      }

      return Gender.UNKNOWN;
    } else {
      return gender;
    }
  }
}
