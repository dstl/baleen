package uk.gov.dstl.baleen.annotators.coreference.impl.sieves;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.uima.jcas.JCas;

import com.google.common.base.Splitter;

import uk.gov.dstl.baleen.annotators.coreference.impl.data.Cluster;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.Mention;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.MentionType;
import uk.gov.dstl.baleen.resources.utils.StopwordUtils;
import uk.gov.dstl.baleen.types.Base;

/**
 * Sieve based on looser matching of head terms.
 */
public class RelaxedHeadMatchSieve extends AbstractCoreferenceSieve {
	private final Pattern stopwordsPattern;
	private static final Splitter WHITESPACE_SPLITTER = Splitter.on(" ").omitEmptyStrings().trimResults();
	
	/**
	 * Constructor for RelaxedHeadMatchSieve
	 */
	public RelaxedHeadMatchSieve(JCas jCas, List<Cluster> clusters, List<Mention> mentions, Collection<String> stopwords) {
		super(jCas, clusters, mentions);
		this.stopwordsPattern = StopwordUtils.buildStopwordPattern(stopwords, false);
	}

	@Override
	public void sieve() {
		List<Mention> mentions = getMentions(MentionType.ENTITY);
		
		for (int i = 0; i < mentions.size(); i++) {
			final Mention a = mentions.get(i);

			for (int j = i + 1; j < mentions.size(); j++) {
				final Mention b = mentions.get(j);
				
				if(shouldAddToCluster(a, b)){
					addToCluster(a, b);
				}
			}
		}
	}

	// TODO: This should at a cluster level
	private boolean hasSubsetOfNonStopWords(Mention a, Mention b) {
		final List<String> aNonStop = getNonStopWords(a);
		final List<String> bNonStop = getNonStopWords(b);

		// TODO: This should not include the head word? See the paper for clarification.

		// NOTE: This is ordered, a is earlier than b and it is unusual to introduce more information
		// to an entity later in the document

		// NOTE: We enforce that the set isn't empty otherwise we aren't really testing anything
		return !aNonStop.isEmpty() && !bNonStop.isEmpty() && aNonStop.containsAll(bNonStop);
	}

	private List<String> getNonStopWords(Mention a) {
		return WHITESPACE_SPLITTER.splitToList(clean(a.getText().toLowerCase()));
	}

	private String clean(String text) {
		return text.replaceAll(stopwordsPattern.pattern(), "");
	}
	
	private boolean shouldAddToCluster(Mention a, Mention b){
		final Class<? extends Base> aClazz = a.getAnnotation().getClass();
		final Class<? extends Base> bClazz = b.getAnnotation().getClass();

		final String aText = a.getText();
		final String bHead = b.getHead();

		if (!hasHead(b))
			return false;
		
		// Not i-within-i
		if (a.overlaps(b)) {
			return false;
		}

		// We have the same or at least semantically same type of entity
		if (!aClazz.isAssignableFrom(bClazz) && !bClazz.isAssignableFrom(aClazz)) {
			return false;
		}

		// Word inclusion
		if (!hasSubsetOfNonStopWords(a, b)) {
			return false;
		}

		// Do we contain the head word?
		if (!aText.contains(bHead)) {
			return false;
		}
		
		return true;
	}
}
