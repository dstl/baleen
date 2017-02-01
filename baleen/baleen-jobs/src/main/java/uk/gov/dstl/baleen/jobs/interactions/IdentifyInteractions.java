package uk.gov.dstl.baleen.jobs.interactions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.bson.Document;

import com.google.common.base.Strings;
import com.mongodb.client.MongoCollection;

import uk.gov.dstl.baleen.jobs.interactions.data.InteractionWord;
import uk.gov.dstl.baleen.jobs.interactions.data.PatternReference;
import uk.gov.dstl.baleen.jobs.interactions.data.Word;
import uk.gov.dstl.baleen.jobs.interactions.impl.InteractionIdentifier;
import uk.gov.dstl.baleen.jobs.interactions.io.CsvInteractionWriter;
import uk.gov.dstl.baleen.jobs.interactions.io.InteractionWriter;
import uk.gov.dstl.baleen.jobs.interactions.io.MonitorInteractionWriter;
import uk.gov.dstl.baleen.resources.SharedMongoResource;
import uk.gov.dstl.baleen.resources.SharedWordNetResource;
import uk.gov.dstl.baleen.resources.utils.WordNetUtils;
import uk.gov.dstl.baleen.uima.BaleenTask;
import uk.gov.dstl.baleen.uima.JobSettings;

/**
 * Identify interaction words based on a Mongo collection of patterns.
 * <p>
 * This requires a Wordnet dictionary and a Mongo resource (to read from). The Mongo collection
 * should hold patterns which have been extracted by a pipeline containing {@Link MongoPatternSaver}
 * <p>
 * See {@link InteractionIdentifier} for more details of the implementation.
 * <p>
 * The relationship types are based on Wordnet supersenses (meaning the original file in which the
 * word is defined). This provides a group of around 40 definitions.
 * <p>
 * The output of this process is a CSV (format defined by {@link CsvInteractionWriter}.
 *
 * <pre>
 * mongo:
 *   db: baleen
 *   host: localhost
 *
 * job:
 *   tasks:
 *   - class: interactions.IdentifyInteractions
 *     filename: output/interactions.csv
 * </pre>
 *
 * Typically you will want to edit / review the CSV file, then run {@link EnhanceInteractions} and
 * then {@link UploadInteractionsToMongo}.
 *
 * @baleen.javadoc
 */
public class IdentifyInteractions extends BaleenTask {

	/**
	 * Connection to Mongo
	 *
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedMongoResource
	 */
	public static final String KEY_MONGO = "mongo";
	@ExternalResource(key = KEY_MONGO)
	private SharedMongoResource mongo;

	/**
	 * Connection to Wordnet
	 *
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedWordNetResource
	 */
	public static final String KEY_WORDNET = "wordnet";
	@ExternalResource(key = KEY_WORDNET)
	private SharedWordNetResource wordnet;

	/**
	 *
	 * The name of the Mongo collection to read
	 *
	 * @baleen.config patterns
	 */
	public static final String KEY_PATTERN_COLLECTION = "patternCollection";
	@ConfigurationParameter(name = KEY_PATTERN_COLLECTION, defaultValue = "patterns")
	private String patternCollection;

	/**
	 * Minimum number of patterns to be considered a cluster.
	 *
	 * Ie the number of evidence points we need to start to consider an interaction.
	 *
	 * @baleen.config minPatterns 2
	 */
	public static final String KEY_MIN_PATTERNS_IN_CLUSTER = "minPatterns";
	@ConfigurationParameter(name = KEY_MIN_PATTERNS_IN_CLUSTER, defaultValue = "2")
	private int minPatternsInCluster;

	/**
	 * Minimum number of occurances of a word in a cluster before its considered potentally the
	 * interaction word.
	 *
	 * Note that this should be equal to or higher than minPatterns.
	 *
	 * @baleen.config minOccurances 2
	 */
	public static final String KEY_MIN_OCCURANCE = "minOccurances";
	@ConfigurationParameter(name = KEY_MIN_OCCURANCE, defaultValue = "2")
	private int minWordOccurance;

	/**
	 * The similarity threshold between two patterns (before they are consider the same). (High is
	 * more similar)
	 *
	 * @baleen.config patterns 0.2
	 */
	public static final String KEY_THRESHOLD = "threshold";
	@ConfigurationParameter(name = KEY_THRESHOLD, defaultValue = "0.2")
	private double threshold;

	/**
	 * Log the information on completion
	 *
	 * @baleen.config patterns false
	 */
	public static final String KEY_OUTPUT = "log";
	@ConfigurationParameter(name = KEY_OUTPUT, defaultValue = "false")
	private boolean outputToLog;

	/**
	 * Save the data to csv, with filename prefixed by tje value.
	 *
	 * Leave this blank for no output.
	 *
	 * @baleen.config csv interactions-
	 */
	public static final String KEY_CSV_FILENAME = "filename";
	@ConfigurationParameter(name = KEY_CSV_FILENAME, defaultValue = "interactions.csv")
	private String csvFilename;

	private final List<InteractionWriter> interactionWriters = new ArrayList<>();

	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		super.doInitialize(aContext);

		if (outputToLog) {
			interactionWriters.add(new MonitorInteractionWriter(getMonitor()));
		}

		if (!Strings.isNullOrEmpty(csvFilename)) {
			interactionWriters.add(new CsvInteractionWriter(csvFilename));
		}
	}

	@Override
	protected void execute(JobSettings settings) throws AnalysisEngineProcessException {
		final InteractionIdentifier identifier = new InteractionIdentifier(getMonitor(), minPatternsInCluster,
				minWordOccurance,
				threshold);
		getMonitor().info("Loading patterns from Mongo");
		final List<PatternReference> patterns = readPatternsFromMongo();
		getMonitor().info("Found {} patterns", patterns.size());
		getMonitor().info("Extracting interaction words...");
		final Stream<InteractionWord> words = identifier.process(patterns);
		getMonitor().info("Writing interaction words...");
		write(words);
		getMonitor().info("Interaction identification complete");

	}

	/**
	 * Read patterns from mongo.
	 *
	 * @return the list
	 */
	private List<PatternReference> readPatternsFromMongo() {
		// TODO: Ideally this would do something in a more streaming manner, as there are likely to
		// be lots of examples. Loading all patterns into memory might be prohibitive.

		final MongoCollection<Document> collection = mongo.getDB().getCollection(patternCollection);

		final List<PatternReference> patterns = new ArrayList<>((int) collection.count());

		for(Document doc : collection.find()){
			@SuppressWarnings("unchecked")
			List<Document> list = doc.get("words", List.class);
			
			final List<Word> tokens = list.stream().map(l -> {
				final String pos = l.getString("pos");
				String lemma = l.getString("lemma");

				// Fall back to actual text if no lemma
				if (lemma == null) {
					lemma = l.getString("text");
				}

				return new Word(lemma.trim().toLowerCase(), WordNetUtils.toPos(pos));
			}).filter(w -> w.getPos() != null)
					.collect(Collectors.toList());

			final PatternReference pattern = new PatternReference(doc.get("_id").toString(), tokens);
			pattern.setSourceType(((Document) doc.get("source")).getString("type"));
			pattern.setTargetType(((Document) doc.get("target")).getString("type"));
			patterns.add(pattern);
		}

		return patterns;

	}

	/**
	 * Write/save patterns to the writers.
	 *
	 * @param words
	 *            the words
	 */
	private void write(Stream<InteractionWord> words) {

		interactionWriters.forEach(w -> {
			try {
				w.initialise();
			} catch (final IOException e) {
				getMonitor().error("Unable to initialise writer", e);
			}
		});

		words.flatMap(interaction -> {
			final String lemma = interaction.getWord().getLemma();

			final String relationshipType = wordnet.getBestSuperSense(interaction.getWord().getPos(), lemma)
					.orElse(lemma);

			return interaction.toRelations(relationshipType, lemma);
		})
				.distinct()
				.forEach(r -> interactionWriters.forEach(w -> {
					try {
						w.write(r);
					} catch (final IOException e) {
						getMonitor().warn("Unable to initialise writer", e);
					}
				}));

		interactionWriters.forEach(w -> w.destroy());
	}

}
