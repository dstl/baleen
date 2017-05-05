//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.csv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.resources.utils.StopwordUtils;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;

/**
 * Write coreference information to a CSV.
 * <p>
 * The format is as follows:
 * <ul>
 * <li>source
 * <li>id
 * <li>reference
 * <li>type
 * <li>text
 * <li>value
 * <li>EntityCount
 * <li>then EntityCount * Entities (value, type)
 * <li>nonEntityNonStopWordsCount
 * <li>nonEntityNonStopWordsCount * nonEntityNonStopWords ( (format word then pos)
 * <li>NonStopWordsNotCoveredByEntitiesCount
 * <li>then NonStopWordsNotCoveredByEntitiesCount * NonStopWordsNotCoveredByEntities (format word
 * then pos)
 * </ul>
 *
 * @baleen.javadoc
 */
public class Coreference extends AbstractCsvConsumer {
	/**
	 * The stoplist to use. If the stoplist matches one of the enum's provided in
	 * {@link uk.gov.dstl.baleen.resources.SharedStopwordResource#StopwordList}, then
	 * that list will be loaded.
	 * 
	 * Otherwise, the string is taken to be a file path and that file is used.
	 * The format of the file is expected to be one stopword per line.
	 * 
	 * @baleen.config DEFAULT
	 */
	public static final String PARAM_STOPLIST = "stoplist";
	@ConfigurationParameter(name = PARAM_STOPLIST, defaultValue="DEFAULT")
	protected String stoplist;
	
	/**
	 * Connection to Stopwords Resource
	 * 
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedStopwordResource
	 */
	public static final String KEY_STOPWORDS = "stopwords";
	@ExternalResource(key = KEY_STOPWORDS)
	protected SharedStopwordResource stopwordResource;

	protected Collection<String> stopwords;
	
	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		super.doInitialize(aContext);
		
		try{
			stopwords = stopwordResource.getStopwords(SharedStopwordResource.StopwordList.valueOf(stoplist));
		}catch(IOException ioe){
			getMonitor().error("Unable to load stopwords", ioe);
			throw new ResourceInitializationException(ioe);
		}
		
		write("source", "id", "reference", "type", "value",
				"EntityCount then Entities... "
						+ "then nonEntityNonStopWords (format word then pos) "
						+ "then NonStopWordsNotCoveredByEntitiesCount "
						+ "then (format word then pos)...");
	}

	@Override
	protected void write(JCas jCas) {

		final String source = getDocumentAnnotation(jCas).getSourceUri();

		// For each entity we need to find all the other sentences they are contained in

		// This should be all entities and sentences
		final Map<Entity, Collection<Sentence>> coveringSentence = JCasUtil.indexCovering(jCas, Entity.class,
				Sentence.class);
		final Map<Sentence, Collection<Entity>> coveredEntities = JCasUtil.indexCovered(jCas, Sentence.class,
				Entity.class);
		final Map<Sentence, Collection<WordToken>> coveredTokens = JCasUtil.indexCovered(jCas, Sentence.class,
				WordToken.class);
		final Map<WordToken, Collection<Entity>> coveringEntity = JCasUtil.indexCovering(jCas, WordToken.class,
				Entity.class);

		JCasUtil.select(jCas, Entity.class).stream()
				.map(e -> convertEntityToRow(source, coveringSentence, coveredEntities, coveredTokens, coveringEntity,
						e))
				.filter(s -> s.length > 0)
				.forEach(this::write);
	}

	private String[] convertEntityToRow(final String source, final Map<Entity, Collection<Sentence>> coveringSentence,
			final Map<Sentence, Collection<Entity>> coveredEntities,
			final Map<Sentence, Collection<WordToken>> coveredTokens,
			final Map<WordToken, Collection<Entity>> coveringEntity, Entity e) {
		final List<String> list = new ArrayList<>();

		Sentence sentence = null;
		final Collection<Sentence> sentences = coveringSentence.get(e);
		if (!sentences.isEmpty()) {
			sentence = sentences.iterator().next();
		} else {
			getMonitor().error("Entity without sentence {}", e.getCoveredText());
			return new String[0];
		}

		list.add(source);
		list.add(e.getExternalId());

		if (e.getReferent() != null) {
			list.add(Long.toString(e.getReferent().getInternalId()));
		} else {
			list.add("");
		}

		list.add(e.getType().getShortName());
		list.add(normalize(e.getValue()));

		final Collection<Entity> entities = coveredEntities.get(sentence);

		// Entities
		final int entityCountIndex = list.size();
		int entityCount = 0;
		list.add("0");

		for (final Entity x : entities) {
			if (x.getInternalId() != e.getInternalId()) {
				list.add(normalize(x.getValue()));
				list.add(x.getType().getShortName());
				entityCount++;
			}
		}
		list.set(entityCountIndex, Integer.toString(entityCount));

		// Add (non-stop) words - separate out the entities from the other words

		final List<WordToken> entityNonStopWords = new ArrayList<>();
		final List<WordToken> nonEntityNonStopWords = new ArrayList<>();

		for (final WordToken t : coveredTokens.get(sentence)) {
			// Filter out entities
			final String word = t.getCoveredText();
			if (StopwordUtils.isStopWord(word, stopwords, false)) {

				final Collection<Entity> collection = coveringEntity.get(t);
				if (collection == null || collection.isEmpty()) {
					nonEntityNonStopWords.add(t);
				} else if (!collection.stream().anyMatch(x -> e.getInternalId() == x.getInternalId())) {
					// Output any entity other than the one we are processing
					entityNonStopWords.add(t);
				}
			}
		}

		// Output

		list.add(Integer.toString(entityNonStopWords.size()));
		entityNonStopWords.forEach(t -> {
			list.add(normalize(t.getCoveredText()));
			list.add(t.getPartOfSpeech());
		});

		list.add(Integer.toString(nonEntityNonStopWords.size()));
		nonEntityNonStopWords.forEach(t -> {
			list.add(normalize(t.getCoveredText()));
			list.add(t.getPartOfSpeech());
		});

		return list.toArray(new String[list.size()]);
	}

}