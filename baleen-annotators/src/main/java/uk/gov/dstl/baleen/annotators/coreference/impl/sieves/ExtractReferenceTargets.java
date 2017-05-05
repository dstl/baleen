//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.coreference.impl.sieves;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.annotators.coreference.impl.data.Cluster;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.Mention;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;

/**
 * Reads the reference targets from the mentions and creates clusters from them.
 */
public class ExtractReferenceTargets extends AbstractCoreferenceSieve {

	/**
	 * Constructor for ExtractReferenceTargets
	 */
	public ExtractReferenceTargets(JCas jCas, List<Cluster> clusters, List<Mention> mentions) {
		super(jCas, clusters, mentions);
	}

	@Override
	public void sieve() {

		Map<Long, Cluster> map = new HashMap<Long, Cluster>();

		getMentions().stream().forEach(m -> {
			ReferenceTarget referent = m.getAnnotation().getReferent();

			if (referent != null) {
				long id = referent.getInternalId();

				if (!map.containsKey(id)) {
					map.put(id, new Cluster(m));
				} else {
					map.get(id).add(m);

				}
			}
		});

		getClusters().addAll(map.values());
	}

}