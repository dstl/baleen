// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.coreference.impl.enhancers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uk.gov.dstl.baleen.annotators.coreference.impl.data.Animacy;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.Mention;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.MentionType;
import uk.gov.dstl.baleen.types.Base;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;
import uk.gov.dstl.baleen.types.common.DocumentReference;
import uk.gov.dstl.baleen.types.common.Frequency;
import uk.gov.dstl.baleen.types.common.Money;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.common.Url;
import uk.gov.dstl.baleen.types.common.Vehicle;
import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.baleen.types.military.MilitaryPlatform;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Temporal;

/** Adds animacy information to a mention. */
public class AnimacyEnhancer implements MentionEnhancer {

  private static final Map<String, Animacy> PRONOUN_MAP = new HashMap<>();

  private static final Map<Class<? extends Base>, Animacy> SEMANTIC_MAP = new HashMap<>();

  static {
    Arrays.asList(
            "i",
            "me",
            "mine",
            "my",
            "myself",
            "we",
            "us",
            "our",
            "ours",
            "ourselves",
            "yourself",
            "yourselves",
            "you",
            "your",
            "yours",
            "he",
            "him",
            "his",
            "she",
            "her",
            "hers",
            "himself",
            "herself",
            "one",
            "one's",
            "they",
            "them",
            "their",
            "theirs",
            "themselves",
            "who",
            "whose")
        .stream()
        .forEach(s -> PRONOUN_MAP.put(s, Animacy.ANIMATE));
    Arrays.asList("it", "its", "itself", "when", "where", "there", "here").stream()
        .forEach(s -> PRONOUN_MAP.put(s, Animacy.INANIMATE));

    Arrays.asList(
            CommsIdentifier.class,
            DocumentReference.class,
            Frequency.class,
            Money.class,
            Url.class,
            Vehicle.class,
            Coordinate.class,
            MilitaryPlatform.class,
            Location.class,
            Temporal.class)
        .stream()
        .forEach(s -> SEMANTIC_MAP.put(s, Animacy.INANIMATE));

    Arrays.asList(Person.class).stream().forEach(s -> SEMANTIC_MAP.put(s, Animacy.ANIMATE));

    // Organisation.class and Nationality could be either
  }

  @Override
  public void enhance(Mention mention) {
    if (mention.getType() == MentionType.PRONOUN) {
      mention.setAnimacy(
          PRONOUN_MAP.getOrDefault(mention.getText().toLowerCase(), Animacy.UNKNOWN));
    } else if (mention.getType() == MentionType.ENTITY) {
      final Class<? extends Base> entityClazz = mention.getAnnotation().getClass();
      for (final Entry<Class<? extends Base>, Animacy> entry : SEMANTIC_MAP.entrySet()) {
        if (entry.getKey().isAssignableFrom(entityClazz)) {
          mention.setAnimacy(entry.getValue());
          return;
        }
      }
      mention.setAnimacy(Animacy.UNKNOWN);
    } else {
      // TODO: Based on some database (if we can find or generate one)
      mention.setAnimacy(Animacy.UNKNOWN);
    }
  }
}
