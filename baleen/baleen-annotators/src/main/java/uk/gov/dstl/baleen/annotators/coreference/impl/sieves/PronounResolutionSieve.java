package uk.gov.dstl.baleen.annotators.coreference.impl.sieves;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.uima.jcas.JCas;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import uk.gov.dstl.baleen.annotators.coreference.impl.data.Animacy;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.Cluster;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.Mention;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.MentionType;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.Person;
import uk.gov.dstl.baleen.resources.data.Gender;

/**
 * Attempts to connect pronouns to an entity.
 * <p>
 * This is very difficult problem which may perform suboptimally in the current implementation.
 */
public class PronounResolutionSieve extends AbstractCoreferenceSieve {

	private static final int MAX_SENTENCE_DISTANCE = 3;
	
	/**
	 * Constructor for PronounResolutionSieve
	 */
	public PronounResolutionSieve(JCas jCas, List<Cluster> clusters, List<Mention> mentions) {
		super(jCas, clusters, mentions);
	}

	@Override
	public void sieve() {

		//Find potential entity->pronoun matches
		final Multimap<Mention, Mention> potential = HashMultimap.create();

		// Check that all mentions have a valid sentence index
		List<Mention> filteredMentions = getMentions().stream().filter(m -> m.getSentenceIndex() >= 0).collect(Collectors.toList());
		
		for (int i = 0; i < filteredMentions.size(); i++) {
			final Mention a = filteredMentions.get(i);

			for (int j = i + 1; j < filteredMentions.size(); j++) {
				final Mention b = filteredMentions.get(j);

				if(!validateMentions(a, b)){
					continue;
				}

				resolvePronoun(a, b, potential);
			}
		}

		// For each of the matches we need to select the best one

		potential.asMap().entrySet().stream().forEach(e -> addBestAsMatch(e.getKey(), e.getValue()));
	}
	
	/**
	 * If the pronoun meets the conditions in this function, it is *likely* to be a person
	 */
	private boolean isPronounPerson(Mention pronoun){
		return pronoun.getPerson() != Person.UNKNOWN	//Acceptable values: FIRST, SECOND and THIRD
				&& (pronoun.getGender() == Gender.M	|| pronoun.getGender() == Gender.F)
				|| pronoun.getAnimacy() == Animacy.ANIMATE;
	}
	
	private boolean validateMentions(Mention a, Mention b){
		//We are coreferencing pronouns only, so a OR b must be a pronoun, but not both 
		if (!((a.getType() == MentionType.PRONOUN && b.getType() != MentionType.PRONOUN) || (a.getType() != MentionType.PRONOUN && b.getType() == MentionType.PRONOUN))){
			return false;
		}

		// Not in paper: No overlap it makes little sense
		if (a.overlaps(b)) {
			return false;
		}

		// Are the attributes compatible (gender=gender, etc)
		if (!a.isAttributeCompatible(b)) {
			return false;
		}
		
		return true;
	}
	
	private void resolvePronoun(Mention a, Mention b, Multimap<Mention, Mention> potential){
		final Mention pronoun = a.getType() == MentionType.PRONOUN ? a : b;
		final Mention other = a.getType() == MentionType.PRONOUN ? b : a;

		// Not in paper: If the pronoun is before the other that's odd, (He said Hello. John
		// did.)
		if (pronoun.getAnnotation().getEnd() < other.getAnnotation().getBegin()) {
			return;
		}

		// Not in paper: We found poor results for "it" really because it never refers to something that Baleen annotates
		// It would be good for say money but currently we'll just drop it
		if (pronoun.getText().toLowerCase().startsWith("it")) {
			return;
		}

		//Originally there was code here to handle a and b both being pronouns (this is described in the paper)
		//However, we are excluding links between two pronouns in validateMentions, so this code was removed.
		
		// Paper: Only consider within three
		// Not in paper: And the pronoun must be after the mention
		final int sentenceDistance = pronoun.getSentenceIndex() - other.getSentenceIndex();
		if (sentenceDistance < 0 || sentenceDistance > MAX_SENTENCE_DISTANCE) {
			return;
		}

		// Not in paper: If the same sentence the pronoun should be after
		if (sentenceDistance == 0 && pronoun.getAnnotation().getEnd() <= other.getAnnotation().getBegin()) {
			return;
		}
		

		if (isPronounPerson(pronoun) && !(other.getAnnotation() instanceof uk.gov.dstl.baleen.types.common.Person || other.getAnnotation() instanceof uk.gov.dstl.baleen.types.common.Nationality)) {
			return;
		}

		// Similarly to avoid if we have a neutral we can't link to a person
		if (pronoun.getGender() == Gender.N && other.getAnnotation() instanceof uk.gov.dstl.baleen.types.common.Person) {
			return;
		}

		// TODO: There might be many more of these simple constraints on our semantic types...

		potential.put(pronoun, other);
	}
	
	private void addBestAsMatch(Mention key, Collection<Mention> potentialMatches) {

		final Collection<Mention> matched;
		if (potentialMatches.size() > 1) {
			List<Mention> list = new ArrayList<Mention>(potentialMatches);
			Collections.sort(list, (a, b) -> {
				if (a.overlaps(b)) {
					return 0;
				}

				// Use in-sentence word distance
				if (a.getAnnotation().getEnd() <= b.getAnnotation().getBegin()) {
					return b.getAnnotation().getBegin() - a.getAnnotation().getEnd();
				} else {
					return b.getAnnotation().getEnd() - a.getAnnotation().getBegin();
				}
			});

			matched = list;
		} else {
			// Either empty or just one...
			matched = potentialMatches;
		}

		// Get the first (nearest) which doesn't overlap
		Optional<Mention> match = matched.stream()
				.filter(m -> !key.overlaps(m))
				.findFirst();

		if (match.isPresent()) {
			addToCluster(key, match.get());
		}
	}
}
