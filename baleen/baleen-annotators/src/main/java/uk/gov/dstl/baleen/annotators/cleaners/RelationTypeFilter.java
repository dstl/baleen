package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.mongodb.DBCollection;

import uk.gov.dstl.baleen.annotators.patterns.data.RelationConstraint;
import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Removes relationships that don't match UIMA type constraints.
 * <p>
 * Many relationships will only make sense between specific entity types. For example (Person, went
 * to, Location) not (DateTime, went to, Location). This filter allows for relational type
 * constraints.
 * <p>
 * Since relationship extractors may have different capabilities (e.g. finding the direction of
 * relationships, discovering new unknown relationships) there are several configuration parameters
 * which relax the strictness of filtering.
 * <p>
 * Mongo constraint documents are formed as:
 *
 * <pre>
 *    {
 *    	source: 'type of source',
 *    	target: 'type of source',
 *    	type: 'relation type',
 *    }
 * </pre>
 *
 * See {@link UploadInteractionsToMongo} and {@link MongoInteractionWriter} for information how to
 * create this collection.
 *
 * @baleen.javadoc
 */
public class RelationTypeFilter extends BaleenAnnotator {

	/**
	 * Connection to Mongo
	 *
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedMongoResource
	 */
	public static final String KEY_MONGO = "mongo";
	@ExternalResource(key = KEY_MONGO)
	private SharedMongoResource mongo;

	/**
	 * The name of the Mongo collection containing the relation types
	 *
	 * @baleen.config gazetteer
	 */
	public static final String PARAM_COLLECTION = "collection";
	@ConfigurationParameter(name = PARAM_COLLECTION, defaultValue = "relationTypes")
	private String collection;

	/**
	 * The name of the field in Mongo that contains the relation type
	 *
	 * @baleen.config type
	 */
	public static final String PARAM_TYPE_FIELD = "typeField";
	@ConfigurationParameter(name = PARAM_TYPE_FIELD, defaultValue = "type")
	private String typeField;

	/**
	 * The name of the field in Mongo that contains the relation sub type
	 *
	 * @baleen.config type
	 */
	public static final String PARAM_SUBTYPE_FIELD = "subTypeField";
	@ConfigurationParameter(name = PARAM_SUBTYPE_FIELD, defaultValue = "subType")
	private String subTypeField;

	/**
	 * The name of the field in Mongo that contains the relation source type
	 *
	 * @baleen.config source
	 */
	public static final String PARAM_SOURCE_FIELD = "sourceField";
	@ConfigurationParameter(name = PARAM_SOURCE_FIELD, defaultValue = "source")
	private String sourceField;

	/**
	 * The name of the field in Mongo that contains the relation source type
	 *
	 * @baleen.config target
	 */
	public static final String PARAM_TARGET_FIELD = "targetField";
	@ConfigurationParameter(name = PARAM_TARGET_FIELD, defaultValue = "target")
	private String targetField;

	/**
	 * The name of the field in Mongo that contains the relation pos
	 *
	 * @baleen.config posField pos
	 */
	public static final String PARAM_POS_FIELD = "posField";
	@ConfigurationParameter(name = PARAM_POS_FIELD, defaultValue = "pos")
	private String posField;

	/**
	 * Determines strictness of filtering.
	 *
	 * In strict mode the relationship type must be defined and the source and target type the same
	 * in order to pass the filter. In non-strict mode, if the relationship type has no constraints
	 * then the relationship will pass. If the relationship type has constraints then these must be
	 * adhered too.
	 *
	 * @baleen.config false
	 */
	public static final String PARAM_STRICT = "strict";
	@ConfigurationParameter(name = PARAM_STRICT, defaultValue = "false")
	private boolean strict;

	/**
	 * Determines if relations can be considered symmetric (source and target swapped)
	 *
	 * @baleen.config true
	 */
	public static final String PARAM_SYMMETRIC = "symmetric";
	@ConfigurationParameter(name = PARAM_SYMMETRIC, defaultValue = "true")
	private boolean symetric;

	private final Map<String, Set<RelationConstraint>> constraints = new HashMap<>();

	@Override
	public void doInitialize(final UimaContext aContext) throws ResourceInitializationException {
		super.doInitialize(aContext);

		final DBCollection dbCollection = mongo.getDB().getCollection(collection);

		dbCollection.find().forEach(o -> {
			final RelationConstraint constraint = new RelationConstraint((String) o.get(typeField),
					(String) o.get(subTypeField),
					(String) o.get(posField),
					(String) o.get(sourceField),
					(String) o.get(targetField));

			if (constraint.isValid()) {
				Set<RelationConstraint> set = constraints.get(constraint.getType());
				if (set == null) {
					set = new HashSet<>();
					constraints.put(constraint.getType().toLowerCase(), set);
				}
				set.add(constraint);
			}

		});
	}

	@Override
	protected void doProcess(final JCas jCas) throws AnalysisEngineProcessException {

		final List<Relation> toRemove = new ArrayList<>();

		for (final Relation relation : JCasUtil.select(jCas, Relation.class)) {
			final String type = relation.getRelationshipType().toLowerCase();
			final Set<RelationConstraint> rcs = constraints.get(type);

			boolean remove;
			if (rcs == null || rcs.isEmpty()) {

				// In strict mode we remove
				if (strict) {
					remove = true;
				} else {
					remove = false;
				}

			} else {
				remove = !checkValid(rcs, relation);
			}

			if (remove) {
				toRemove.add(relation);
			}
		}

		removeFromJCasIndex(toRemove);

	}

	/**
	 * Check if the relation is valid against the constraints.
	 *
	 * @param rcs
	 *            the rcs
	 * @param relation
	 *            the relation
	 * @return true, if successful
	 */
	private boolean checkValid(final Set<RelationConstraint> rcs, final Relation relation) {
		return rcs.stream()
				.anyMatch(p -> p.matches(relation, symetric));
	}

}
