package uk.gov.dstl.baleen.annotators.coreference.impl.sieves;

import java.util.List;

import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.coreference.impl.data.Cluster;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.Mention;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.MentionType;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.Person;

/**
 * Joins pronouns which are in the same sentence.
 * <p>
 * This is not part of the original paper, and it might have been taken care of during their
 * implementation. However it seems sensible.
 * <p>
 * There are probably areas of english where this does not work well. "Jim saw James and he was
 * going to town" (James = he?) vs "He saw James and he was going to town" (he = ?).
 *
 */
public class InSentencePronounSieve extends AbstractCoreferenceSieve {

	// TODO: Not implemented, as these seem ambiguous:
	// Ok if third singular in following sets
	// - he, his; or him, himself .
	// - I don't think we can do anything with she, her, hers,herself (since no equivalent of his so
	// her = him/his) which could be different in the sentence
	// Third plural: e.g. they, their, theirs; or them, themselves
	// Third neuter: e.g. it,its,; or itself

	/**
	 * Constructor for InSentencePronounSieve
	 */
	public InSentencePronounSieve(JCas jCas, List<Cluster> clusters, List<Mention> mentions) {
		super(jCas, clusters, mentions);
	}

	@Override
	public void sieve() {

		List<Mention> mentions = getMentions(MentionType.PRONOUN);
		
		for (int i = 0; i < mentions.size(); i++) {
			final Mention a = mentions.get(i);
			final String aText = a.getText();

			for (int j = i + 1; j < mentions.size(); j++) {
				final Mention b = mentions.get(j);
				final String bText = b.getText();

				if (a.getSentenceIndex() != b.getSentenceIndex()) {
					continue;
				}

				if(!(firstPerson(a, b) || secondPerson(a, b) || thirdPerson(a, b)) && aText.equalsIgnoreCase(bText)){
					// If the text is the same, then ok
					addToCluster(a, b);
				}
			}
		}
	}
	
	private boolean firstPerson(Mention a, Mention b){
		// Ok if both from FIRST single, e.g. i, me, mine, my, myself
		// Ok if both from FIRST plural, e.g. we, us, our, ours, ourselves
		if (a.getPerson() == Person.FIRST && b.getPerson() == Person.FIRST) {
			addToCluster(a, b);
			return true;
		}
		
		return false;
	}
	
	private boolean secondPerson(Mention a, Mention b){
		// Ok if from second {yourself, yourselves, you your yours} not mixing plural and
		// singular here
		if (a.getPerson() == Person.SECOND && b.getPerson() == Person.SECOND
				&& a.getMultiplicity() == b.getMultiplicity()) {
			addToCluster(a, b);
			return true;
		}
		
		return false;
	}
	
	private boolean thirdPerson(Mention a, Mention b){
		// Ok if from third, if you match on everything
		if (a.getPerson() == Person.THIRD && b.getPerson() == Person.THIRD
				&& a.getMultiplicity() == b.getMultiplicity()
				&& a.getGender() == b.getGender()) {
			addToCluster(a, b);
			return true;
		}
		
		return false;
	}
}
