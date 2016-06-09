package uk.gov.dstl.baleen.annotators.coreference.impl.enhancers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import uk.gov.dstl.baleen.annotators.coreference.impl.data.Mention;
import uk.gov.dstl.baleen.resources.SharedGenderMultiplicityResource;
import uk.gov.dstl.baleen.resources.data.Multiplicity;
import uk.gov.dstl.baleen.types.common.Organisation;
import uk.gov.dstl.baleen.types.language.WordToken;

/**
 * Add multiplicity information to mentions.
 */
public class MultiplicityEnhancer implements MentionEnhancer {

	private static final Map<String, Multiplicity> PRONOUN_MAP = new HashMap<>();
	private final SharedGenderMultiplicityResource multiplicityResource;

	static {
		// See Person

		Arrays.asList("i", "he", "her", "herself", "hers", "her", "him", "himself", "his", "it", "its", "itself", "me",
				"myself", "mine", "my", "one", "oneself", "one's", "she", "yourself").stream()
				.forEach(s -> PRONOUN_MAP.put(s, Multiplicity.SINGULAR));
		Arrays.asList("ours", "our", "ourself", "ourselves", "their", "theirs", "them", "themself", "themselves",
				"they", "us", "we", "yourself", "yourselves").stream()
				.forEach(s -> PRONOUN_MAP.put(s, Multiplicity.PLURAL));
	}

	/**
	 * Constructor for MultiplicityEnhancer
	 */
	public MultiplicityEnhancer(SharedGenderMultiplicityResource multiplicityResource) {
		this.multiplicityResource = multiplicityResource;
	}

	@Override
	public void enhance(Mention mention) {
		switch (mention.getType()) {
		case PRONOUN:
			mention.setMultiplicity(PRONOUN_MAP.getOrDefault(mention.getText().toLowerCase(), Multiplicity.UNKNOWN));
			return;
		case ENTITY:
			mention.setMultiplicity(getEntityMultiplicity(mention));
			break;
		case NP:
			mention.setMultiplicity(getNounPhraseMultiplicity(mention));
			break;
		default:
			return;
		}

		// TODO: Should we always check our resource and then override the multiplicity?

		if (mention.getMultiplicity() == Multiplicity.UNKNOWN) {
			final Multiplicity assignedMultiplicity = multiplicityResource.lookupMultiplicity(mention.getText());
			mention.setMultiplicity(assignedMultiplicity);
		}
	}
	
	private Multiplicity getEntityMultiplicity(Mention mention){
		// Assumed singular, unless organisation
		if (mention.getAnnotation() instanceof Organisation) {
			return Multiplicity.UNKNOWN;
		} else {
			return Multiplicity.SINGULAR;
		}
	}
	
	private Multiplicity getNounPhraseMultiplicity(Mention mention){
		Multiplicity m = Multiplicity.UNKNOWN;
		final WordToken head = mention.getHeadWordToken();
		if (head != null) {
			if ("NNS".equalsIgnoreCase(head.getPartOfSpeech())
					|| "NPS".equalsIgnoreCase(mention.getHeadWordToken().getPartOfSpeech())) {
				m = Multiplicity.PLURAL;
			} else {
				m = Multiplicity.SINGULAR;
			}
		}
		
		return m;
	}

}
