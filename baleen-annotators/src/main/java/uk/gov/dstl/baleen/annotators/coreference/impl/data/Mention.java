//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.coreference.impl.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;

import uk.gov.dstl.baleen.resources.data.Gender;
import uk.gov.dstl.baleen.resources.data.Multiplicity;
import uk.gov.dstl.baleen.types.Base;
import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;

/**
 * A Mention that may be coreferenced.
 */
public class Mention {

	/** The annotation. */
	private final Base annotation;

	/** The type. */
	private final MentionType type;

	/** The clusters. */
	private final Set<Cluster> clusters = new HashSet<>();

	private Set<String> acronyms;

	private WordToken headWordToken;

	private List<WordToken> words;

	private Person person = Person.UNKNOWN;

	private Animacy animacy = Animacy.UNKNOWN;

	private Gender gender = Gender.UNKNOWN;

	private Multiplicity multiplicity = Multiplicity.UNKNOWN;

	private int sentenceIndex = Integer.MIN_VALUE;

	private Sentence sentence = null;

	private Mention(Base annotation, MentionType type) {
		this.annotation = annotation;
		this.type = type;
	}

	/**
	 * Instantiates a new mention, of type PRONOUN.
	 */
	public Mention(WordToken annotation) {
		this(annotation, MentionType.PRONOUN);
	}

	/**
	 * Instantiates a new mention, of type ENTITY.
	 */
	public Mention(Entity annotation) {
		this(annotation, MentionType.ENTITY);
	}

	/**
	 * Instantiates a new mention, of type NP.
	 */
	public Mention(PhraseChunk annotation) {
		this(annotation, MentionType.NP);
	}

	/**
	 * Gets the annotation.
	 *
	 * @return the annotation
	 */
	public Base getAnnotation() {
		return annotation;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public MentionType getType() {
		return type;
	}

	/**
	 * Gets the clusters
	 * 
	 * @return the clusters
	 */
	public Set<Cluster> getClusters() {
		return clusters;
	}

	/**
	 * @return true if there are clusters
	 */
	public boolean hasClusters() {
		return !clusters.isEmpty();
	}

	/**
	 * Returns any cluster, or null if no clusters are present 
	 */
	public Cluster getAnyCluster() {
		if (!clusters.isEmpty()) {
			return clusters.iterator().next();
		} else {
			return null;
		}
	}

	/**
	 * Adds the to cluster - use cluster.add(mention) as this will not update the cluster.
	 *
	 * @param cluster
	 *            the cluster
	 */
	public void addToCluster(Cluster cluster) {
		clusters.add(cluster);
	}

	/**
	 * Clear clusters - should not be used outside Coreference (will not remove from the cluster)
	 */
	public void clearClusters() {
		clusters.clear();
	}

	/**
	 * Get the covered text
	 */
	public String getText() {
		return annotation.getCoveredText();
	}

	/**
	 * Sets the head word token of the mention
	 */
	public void setHeadWordToken(WordToken headWordToken) {
		this.headWordToken = headWordToken;
	}

	/**
	 * Returns the head word token
	 */
	public WordToken getHeadWordToken() {
		return headWordToken;
	}

	/**
	 * Returns the head word as a string
	 */
	public String getHead() {
		return getHeadWordToken() != null ? getHeadWordToken().getCoveredText() : null;
	}

	/**
	 * Returns true is the covered text contains no whitespace and is entirely upper case
	 */
	public boolean isAcronym() {
		return !StringUtils.containsWhitespace(getText())
				&& org.apache.commons.lang3.StringUtils.isAllUpperCase(getText());
	}

	/**
	 * Sets the acronyms associated with this mention
	 */
	public void setAcronym(Set<String> acronyms) {
		this.acronyms = acronyms;
	}

	/**
	 * Returns the acronyms associated with this mention
	 */
	public Set<String> getAcronyms() {
		return acronyms;
	}

	/**
	 * Returns true if the provided mention overlaps with this mention
	 */
	public boolean overlaps(Mention mention) {
		final Base a = getAnnotation();
		final Base b = mention.getAnnotation();
		return !(a.getEnd() < b.getBegin() || b.getEnd() < a.getBegin());
	}

	@Override
	public String toString() {
		return getText() + " [" + type + "]";
	}

	/**
	 * Set the multiplicity of this mention
	 */
	public void setMultiplicity(Multiplicity multiplicity) {
		this.multiplicity = multiplicity;
	}

	/**
	 * Get the multiplicity of this mention
	 */
	public Multiplicity getMultiplicity() {
		return multiplicity;
	}

	/**
	 * Set the words associated with this mention
	 */
	public void setWords(List<WordToken> words) {
		this.words = words;
	}
	
	/**
	 * Get the words associated with this mention
	 */
	public List<WordToken> getWords() {
		return words;
	}
	
	/**
	 * Set the person associated with this mention
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * Get the person associated with this mention
	 */
	public Person getPerson() {
		return person;
	}

	/**
	 * Set the animacy associated with this mention
	 */
	public void setAnimacy(Animacy animacy) {
		this.animacy = animacy;
	}

	/**
	 * Get the animacy associated with this mention
	 */
	public Animacy getAnimacy() {
		return animacy;
	}

	/**
	 * Set the gender associated with this mention
	 */
	public Gender getGender() {
		return gender;
	}

	/**
	 * Get the gender associated with this mention
	 */
	public void setGender(Gender gender) {
		this.gender = gender;
	}

	/**
	 * Set the sentence index associated with this mention
	 */
	public void setSentenceIndex(int index) {
		this.sentenceIndex = index;
	}

	/**
	 * Get the sentence index associated with this mention
	 */
	public int getSentenceIndex() {
		return sentenceIndex;
	}

	/**
	 * Set the sentence associated with this mention
	 */
	public void setSentence(Sentence sentence) {
		this.sentence = sentence;
	}

	/**
	 * Get the sentence associated with this mention
	 */
	public Sentence getSentence() {
		return sentence;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (annotation == null ? 0 : annotation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Mention other = (Mention) obj;
		if (annotation == null) {
			if (other.annotation != null) {
				return false;
			}
		} else if (!annotation.equals(other.annotation)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns true if the provided mention has compatible attributes with this mention
	 */
	public boolean isAttributeCompatible(Mention b) {
		// The paper also mentions NER labels, but I can't see how they could be (other than what is
		// down in people)
		// eg is Person entity we have already have it as a Animate so it won't match "it".
		if (getType() == MentionType.ENTITY && b.getType() == MentionType.ENTITY) {
			Class<? extends Base> aClass = getAnnotation().getClass();
			Class<? extends Base> bClass = b.getAnnotation().getClass();

			// Stop if they are different types semantically
			// That could still mean you consider an Entity (super type) to a Person (sub type)
			// so could be even more strict here and want aClass = bClass.
			if (!aClass.isAssignableFrom(bClass) && !bClass.isAssignableFrom(aClass)) {
				return false;
			}
		}

		// You can be more or less lenient here..
		// gender is our worst dataset so I think its safer to be lenient
		return Gender.lenientEquals(getGender(), b.getGender())
				&& Animacy.strictEquals(getAnimacy(), b.getAnimacy())
				&& Multiplicity.strictEquals(getMultiplicity(), b.getMultiplicity())
				&& Person.strictEquals(getPerson(), b.getPerson());
	}

}