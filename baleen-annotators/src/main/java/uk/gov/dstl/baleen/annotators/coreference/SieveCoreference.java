// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.coreference;

import java.util.*;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.coreference.impl.MentionDetector;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.Cluster;
import uk.gov.dstl.baleen.annotators.coreference.impl.data.Mention;
import uk.gov.dstl.baleen.annotators.coreference.impl.enhancers.*;
import uk.gov.dstl.baleen.annotators.coreference.impl.sieves.*;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.resources.SharedGenderMultiplicityResource;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.types.Base;
import uk.gov.dstl.baleen.types.common.*;
import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.military.MilitaryPlatform;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.types.semantic.Temporal;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;
import uk.gov.dstl.baleen.uima.grammar.DependencyGraph;
import uk.gov.dstl.baleen.uima.grammar.ParseTree;

/**
 * Resolves coreferent entities.
 *
 * <p>In effect the Stanford approach is a set of 10+ passes which address the different types of
 * coreference. At each stage mentions are related, each related mention is added to a cluster (a
 * set of mentions which are related). At the end of the process the clusters are joined
 * transitively and all mentions inside a cluster are considered coreferent.
 *
 * <p>A mention is a NP, entity or pronoun. In Stanford the largest NP is taken, within Baleen we
 * felt that entities are more important, therefore we take the largest NP which does not contain a
 * NP.
 *
 * <p>TODO: Review mention extraction
 *
 * <p>This is a partial implementation at present, and so will not perform as well as the
 * StanfordCoreNlp coreference. This is partially due to time constraints.
 *
 * <p>The following implementation details to date:
 *
 * <ul>
 *   <li>Mention detection: Done
 *   <li>Pass 1 Speaker Identification: TODO
 *   <li>Pass 2 Exact String Match: Done
 *   <li>Pass 3 Relaxed String Match: Done
 *   <li>Pass X: We added a pronoun match within the same sentence.
 *   <li>Pass 4 Precise Constructs: Done - appositive, predicate. relative pronoun, acronym. Not
 *       done - role appositive (since Baleen doens't have a role entity to mark up). Done elsewhere
 *       - demonym are covered in the NationalityToLocation annotator.
 *   <li>Pass 5-7 Strict Head Match: Done
 *   <li>Pass 8 Proper Head Noun Match: Done
 *   <li>Pass 9 Relaxed Head Match: Done
 *   <li>Pass 10 Pronoun Resolution: Done
 *   <li>Post process: Done
 *   <li>Output: Done
 * </ul>
 *
 * Attributes of mentions (gender, animacy, number) are included, but for animacy we could not get
 * the data (Ji and Lin, 2009) and it says for research use only anyway. As such we ignore the
 * dictionary lookup.
 *
 * <p>We discard any algorithms which are for a specific corpus (eg OntoNotes).
 *
 * <p>This is very much unoptimised. Each sieve will calculate over all entities, even though many
 * will already in the same cluster.
 *
 * <p>TODO: At the moment we don't do the clustering properly. We need just perform pairwise
 * operations repeated.
 *
 * <p>For more information see the various supporting papers.
 *
 * <ul>
 *   <li>http://nlp.stanford.edu/software/dcoref.shtml
 *   <li>http://www.mitpressjournals.org/doi/pdf/10.1162/COLI_a_00152
 *   <li>http://nlp.stanford.edu/pubs/discourse-referent-lifespans.pdf
 *   <li>http://nlp.stanford.edu/pubs/conllst2011-coref.pdf
 *   <li>http://nlp.stanford.edu/pubs/coreference-emnlp10.pdf
 * </ul>
 *
 * TODO: To really improve further, we need an analysis of what is missing higher up Baleen. For
 * example we don't have roles or the animacy information so "a doctor" is just a noun phrase and
 * hence could be mapped to it. If we had "person role" entity marker we would mark this an ANIMATE.
 *
 * @baleen.javadoc
 */
public class SieveCoreference extends BaleenAnnotator {
  /**
   * Connection to Stopwords Resource
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedStopwordResource
   */
  public static final String KEY_STOPWORDS = "stopwords";

  @ExternalResource(key = KEY_STOPWORDS)
  protected SharedStopwordResource stopwordResource;

  /**
   * GenderMultiplicityResource to provide information on gender and multiplicity from a dictionary.
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.GenderMultiplicityResource
   */
  public static final String KEY_GENDER_MULTIPLICITY = "genderMultiplicity";

  @ExternalResource(key = KEY_GENDER_MULTIPLICITY)
  private SharedGenderMultiplicityResource genderMultiplicityResource;

  /**
   * The stoplist to use. If the stoplist matches one of the enum's provided in {@link
   * uk.gov.dstl.baleen.resources.SharedStopwordResource.StopwordList}, then that list will be
   * loaded.
   *
   * <p>Otherwise, the string is taken to be a file path and that file is used. The format of the
   * file is expected to be one stopword per line.
   *
   * @baleen.config DEFAULT
   */
  public static final String PARAM_STOPLIST = "stoplist";

  @ConfigurationParameter(name = PARAM_STOPLIST, defaultValue = "DEFAULT")
  private String stoplist;

  /**
   * Perform only a single pass (of the provided index)
   *
   * <p>Only useful for unit testing.
   *
   * <p>-1 means all
   *
   * @baleen.config -1
   */
  public static final String PARAM_SINGLE_PASS = "pass";

  @ConfigurationParameter(name = PARAM_SINGLE_PASS, defaultValue = "-1")
  private int singlePass;

  /**
   * Should pronomial resolution (John - he) be performed.
   *
   * <p>This is the worst performing sieve in that is must 'guess' without any real rules what
   * entity the pronoun is referring to. We currently have little data about animacy etc which will
   * help (They - BBC ok, He - BBC not ok).
   *
   * <p>Currently a closest entity of the same type is used, but that won't perform well in many
   * cases.
   *
   * @baleen.config pronomial false
   */
  public static final String PARAM_INCLUDE_PRONOMIAL = "pronomial";

  @ConfigurationParameter(name = PARAM_INCLUDE_PRONOMIAL, defaultValue = "false")
  private boolean includePronomial;

  protected Collection<String> stopwords;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);
    stopwords = stopwordResource.getStopwords(stoplist);
  }

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {

    DependencyGraph dependencyGraph = DependencyGraph.build(jCas);
    ParseTree parseTree = ParseTree.build(jCas);

    // Detect mentions
    List<Mention> mentions = new MentionDetector(jCas, dependencyGraph).detect();

    // Extract head words and other aspects needed for later, determine acronyms, denonym, gender,
    // etc
    enhanceMention(mentions);

    List<Cluster> clusters = sieve(jCas, parseTree, mentions);

    // Post processing
    postProcess(clusters);

    // Output to reference targets
    outputReferenceTargets(jCas, clusters);
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(
            PhraseChunk.class,
            WordToken.class,
            Entity.class,
            Sentence.class,
            CommsIdentifier.class,
            DocumentReference.class,
            Frequency.class,
            Money.class,
            Url.class,
            Vehicle.class,
            Coordinate.class,
            MilitaryPlatform.class,
            Location.class,
            Temporal.class,
            Nationality.class,
            Person.class,
            Organisation.class),
        ImmutableSet.of(ReferenceTarget.class));
  }

  private void enhanceMention(List<Mention> mentions) {

    MentionEnhancer[] enhancers =
        new MentionEnhancer[] {
          new AcronymEnhancer(),
          new PersonEnhancer(),
          new MultiplicityEnhancer(genderMultiplicityResource),
          new GenderEnhancer(genderMultiplicityResource),
          new AnimacyEnhancer()
        };

    for (Mention mention : mentions) {

      for (MentionEnhancer enhancer : enhancers) {
        enhancer.enhance(mention);
      }
    }
  }

  private List<Cluster> sieve(JCas jCas, ParseTree parseTree, List<Mention> mentions) {

    List<Cluster> clusters = new ArrayList<>();

    CoreferenceSieve[] sieves =
        new CoreferenceSieve[] {
          new ExtractReferenceTargets(jCas, clusters, mentions), // Good
          // TODO: SpeakerIdentificationSieve not implemented
          new ExactStringMatchSieve(jCas, clusters, mentions), // Good
          new RelaxedStringMatchSieve(jCas, clusters, mentions), // Good
          new InSentencePronounSieve(jCas, clusters, mentions), // Good
          new PreciseConstructsSieve(jCas, parseTree, clusters, mentions), // Good
          // Pass A-C are all strict head with different params
          new StrictHeadMatchSieve(jCas, clusters, mentions, true, true, stopwords), // Good
          new StrictHeadMatchSieve(jCas, clusters, mentions, true, false, stopwords), // Good
          new StrictHeadMatchSieve(jCas, clusters, mentions, false, true, stopwords), // Good
          new ProperHeadMatchSieve(jCas, clusters, mentions), // Good
          new RelaxedHeadMatchSieve(jCas, clusters, mentions, stopwords), // Good
          includePronomial ? new PronounResolutionSieve(jCas, clusters, mentions) : null
          // Questionable - Needs more help from
          // Baleen entities yet and more data from animacy if its to work well.
        };

    if (singlePass >= 0 && sieves.length > singlePass) {
      sieves = new CoreferenceSieve[] {sieves[singlePass]};
      getMonitor()
          .info("Single pass mode {}: {}", singlePass, sieves[0].getClass().getSimpleName());
    }

    Arrays.stream(sieves).filter(Objects::nonNull).forEach(CoreferenceSieve::sieve);

    return clusters;
  }

  private void postProcess(List<Cluster> clusters) {

    // NOTE: The paper says the two rules are *only* used in OntoNotes:
    // 1. Remove singleton clusters
    // 2. Short mentions of appositive patterns
    // We implement 1, as it makes sense genreally and leave 2 as an OntoNotes specific
    // optimisation.

    Iterator<Cluster> iterator = clusters.iterator();
    while (iterator.hasNext()) {
      Cluster cluster = iterator.next();
      if (cluster.getSize() <= 1) {
        iterator.remove();
      }
    }
  }

  private void outputReferenceTargets(JCas jCas, List<Cluster> clusters) {

    // Merge the clusters together

    List<Cluster> merged = mergeClusters(clusters);

    // Remove all the previous reference targets as we've included them in our process

    ArrayList<ReferenceTarget> toRemove =
        new ArrayList<>(JCasUtil.select(jCas, ReferenceTarget.class));
    removeFromJCasIndex(toRemove);

    // Save clusters a referent targets

    merged.forEach(
        c -> {
          ReferenceTarget target = new ReferenceTarget(jCas);

          for (Mention m : c.getMentions()) {
            // We overwrite the referent target here, given that we used the initial target to
            // bootstrap our work
            // TODO: Could add an option not to override here.

            Base annotation = m.getAnnotation();
            annotation.setReferent(target);
          }

          addToJCasIndex(target);
        });
  }

  private List<Cluster> mergeClusters(List<Cluster> clusters) {
    List<Cluster> merged = new ArrayList<>(clusters.size());

    for (Cluster cluster : clusters) {

      boolean overlap = false;
      for (Cluster mergedCluster : merged) {
        if (mergedCluster.intersects(cluster)) {
          mergedCluster.add(cluster);
          overlap = true;
          break;
        }
      }

      if (!overlap) {
        merged.add(cluster);
      }
    }

    return merged;
  }
}
