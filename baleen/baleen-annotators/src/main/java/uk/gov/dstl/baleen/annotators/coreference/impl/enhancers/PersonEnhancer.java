package uk.gov.dstl.baleen.annotators.coreference.impl.enhancers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import uk.gov.dstl.baleen.annotators.coreference.impl.data.Mention;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.MentionType;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.Person;

/**
 * Add person information to mentions.
 */
public class PersonEnhancer implements MentionEnhancer {

	private static final Map<String, Person> MAP = new HashMap<>();

	static {
		// See Person

		Arrays.asList("i", "me", "mine", "my", "myself", "we", "us", "our", "ours", "ourselves")
				.stream().forEach(s -> MAP.put(s, Person.FIRST));
		Arrays.asList("yourself", "yourselves", "you", "your", "yours")
				.stream().forEach(s -> MAP.put(s, Person.SECOND));
		Arrays.asList("he", "him", "his", "she", "her", "hers", "himself", "herself",
				"they", "them", "their", "theirs", "themselves", "it", "its", "itself", "one", "one's", "oneself")
				.stream().forEach(s -> MAP.put(s, Person.THIRD));
	}

	@Override
	public void enhance(Mention mention) {

		if (mention.getType() == MentionType.PRONOUN) {
			mention.setPerson(MAP.getOrDefault(mention.getText().toLowerCase(), Person.UNKNOWN));
		} else if (mention.getType() == MentionType.ENTITY) {
			// TODO: reallt this should be for entities which can play this role - temporal is
			// questioable?
			mention.setPerson(Person.THIRD);
		} else {
			mention.setPerson(Person.UNKNOWN);
		}

	}

}
