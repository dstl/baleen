package uk.gov.dstl.baleen.consumers;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.types.Base;
import uk.gov.dstl.baleen.types.language.Pattern;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.BaleenConsumer;

/**
 * Saves patterns in a JCas to a Mongo collection.
 * <p>
 * Use this after a pattern extraction annotator (eg PatternExtractor) in order to create a training
 * set in Mongo for offline analysis. In other words this annotator will save UIMA Pattern types to
 * Mongo.
 * <p>
 * This will clear the existing collection, unless clear = false is set as configuration parameter.
 * <p>
 * Note this is BaleenConsumer but, like all consumers, it can be used as annotator. So if you wish
 * to save midway through a pipeline, clear the result and then create more patterns that is
 * supported with Baleen.
 *
 * @baleen.javadoc
 */
public class MongoPatternSaver extends BaleenConsumer {

	/**
	 * Connection to Mongo
	 *
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedMongoResource
	 */
	public static final String KEY_MONGO = "mongo";
	@ExternalResource(key = KEY_MONGO)
	private SharedMongoResource mongo;

	/**
	 * The name of the Mongo collection to hold the patterns
	 *
	 * @baleen.config patterns
	 */
	public static final String KEY_COLLECTION = "collection";
	@ConfigurationParameter(name = KEY_COLLECTION, defaultValue = "patterns")
	private String collection;

	/**
	 * Clears the output pattern collection before saving.
	 *
	 * @baleen.config true
	 */
	public static final String KEY_CLEAR = "clear";
	@ConfigurationParameter(name = KEY_CLEAR, defaultValue = "true")
	private Boolean clear;

	private DBCollection dbCollection;

	@Override
	public void doInitialize(final UimaContext aContext) throws ResourceInitializationException {
		super.doInitialize(aContext);

		dbCollection = mongo.getDB().getCollection(collection);

		// Delete the whole database
		if (clear) {
			dbCollection.remove(new BasicDBObject());
		}

	}

	@Override
	protected void doProcess(final JCas jCas) throws AnalysisEngineProcessException {

		for (final Pattern pattern : JCasUtil.select(jCas, Pattern.class)) {
			final Base source = pattern.getSource();
			final Base target = pattern.getTarget();

			if (source instanceof Entity && target instanceof Entity) {
				final DBObject object = new BasicDBObject()
						.append("source", saveEntity((Entity) source))
						.append("target", saveEntity((Entity) target))
						.append("words", saveWords(pattern));

				dbCollection.save(object);
			}
		}
	}

	/**
	 * Save words.
	 *
	 * @param pattern
	 *            the pattern
	 * @return the DB object
	 */
	private DBObject saveWords(final Pattern pattern) {
		final BasicDBList list = new BasicDBList();
		for (int i = 0; i < pattern.getWords().size(); i++) {
			final WordToken w = pattern.getWords(i);
			final BasicDBObject o = new BasicDBObject()
					.append("text", w.getCoveredText())
					.append("pos", w.getPartOfSpeech());

			if (w.getLemmas() != null && w.getLemmas().size() >= 1) {
				o.put("lemma", w.getLemmas(0).getLemmaForm());
			}

			list.add(o);
		}
		return list;
	}

	/**
	 * Save entity.
	 *
	 * @param entity
	 *            the entity
	 * @return the DB object
	 */
	private DBObject saveEntity(final Entity entity) {
		return new BasicDBObject()
				.append("text", entity.getCoveredText())
				.append("type", entity.getTypeName());
	}

}
