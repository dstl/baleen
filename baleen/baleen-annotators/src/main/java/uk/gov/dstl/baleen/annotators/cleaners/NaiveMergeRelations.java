package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Removes multiple copies of the same relation within a document.
 *
 * This is a naive and simple approach which can hide many issues - it is effectively performing
 * relationship coreference and deduplication based solely at a relationship level. The algorithm
 * works by looking is the relationship types are the same, and if the entities are the same (here
 * as well is difficult, this is based on entities having the same type and value which may be
 * incorrect for multiple John Smiths).
 *
 * This only really useful if you want to ensure that from a single document you get only a single
 * relationship of the same type, subtype between the same two entities because you want to
 * (naively) push data into database and not have to consider this in future algorithms (focusing on
 * counting the same relations appearing in different documents).
 *
 */
public class NaiveMergeRelations extends BaleenAnnotator {

	/**
	 * Symmetric relations (x ~ y and y ~ x are considered the same) if true
	 *
	 * @baleen.config true
	 */
	public static final String KEY_SYMMETRIC = "symmetric";
	@ConfigurationParameter(name = KEY_SYMMETRIC, defaultValue = "true")
	private Boolean symmetric;

	@Override
	protected void doProcess(final JCas jCas) throws AnalysisEngineProcessException {
		final List<Relation> relations = new ArrayList<>(JCasUtil.select(jCas, Relation.class));

		final Set<Relation> toRemove = new HashSet<>();

		for (int i = 0; i < relations.size(); i++) {
			final Relation a = relations.get(i);

			if (!toRemove.contains(a)) {
				toRemove.addAll(findSameRelations(a, relations.subList(i + 1, relations.size())));
			}
		}

		removeFromJCasIndex(toRemove);
	}

	/**
	 * Finds any relations from the list <em>relations</em> that is the same as <em>a</em>
	 */
	private List<Relation> findSameRelations(Relation a, List<Relation> relations){
		return relations.stream().filter(b -> isSame(a, b)).collect(Collectors.toList());
	}
	
	/**
	 * Checks if relations are the same.
	 *
	 * @param a
	 *            the first relation
	 * @param b
	 *            the second relation
	 * @return true, if is same
	 */
	private boolean isSame(final Relation a, final Relation b) {
		boolean sameSourceTarget = false;
		if(isSame(a.getSource(), b.getSource()) && isSame(a.getTarget(), b.getTarget())){
			sameSourceTarget = true;
		}else if(symmetric && isSame(a.getSource(), b.getTarget()) && isSame(a.getTarget(), b.getSource())){
			//Symmetric, so source and target could be switched
			sameSourceTarget = true;
		}
		
		return sameSourceTarget
			&& isSame(a.getRelationshipType(), b.getRelationshipType())
			&& isSame(a.getRelationSubType(), b.getRelationSubType());
	}

	/**
	 * Checks if entity is the same
	 *
	 * @param a
	 *            the first entity
	 * @param b
	 *            the second entity
	 * @return true, if is same
	 */
	private boolean isSame(final Entity a, final Entity b) {
		if (a == null && b == null) {
			return true;
		}

		if (a == null || b == null) {
			// implies b != null (as a != b)
			return false;
		}

		return a.getType().equals(b.getType()) && isSame(a.getValue(), b.getValue());
	}

	/**
	 * Checks if two strings are the same.
	 *
	 * @param a
	 *            first string
	 * @param b
	 *            second string
	 * @return true, if is same
	 */
	private boolean isSame(final String a, final String b) {
		if (a == null && b == null) {
			return true;
		} else if (a == null || b == null) {
			return false;
		} else {
			return a.equalsIgnoreCase(b);
		}
	}

}
