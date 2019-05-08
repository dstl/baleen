// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.coreference;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.core.utils.BuilderUtils;
import uk.gov.dstl.baleen.entity.linking.*;
import uk.gov.dstl.baleen.entity.linking.collector.JCasInformationCollector;
import uk.gov.dstl.baleen.entity.linking.collector.ProperNounInformationCollector;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Annotator for linking Entities to a known data source
 *
 * <p>Entity linking is made up of an {@link InformationCollector}, a {@link CandidateSupplier} and
 * a {@link CandidateRanker}. By default the {@link ProperNounInformationCollector} is used, however
 * this requires part of speech tagging to also be in the pipeline, so an implementation that does
 * not require this is also supplied by {@link JCasInformationCollector}. A {@link
 * uk.gov.dstl.baleen.entity.linking.ranker.BagOfWordsCandidateRanker} is also used by default. To
 * configure, an entity type and a candidate supplier must be configured e.g.
 *
 * <pre>
 * - class: coreference.EntityLinkingAnnotator
 *   entityType: Person
 *   candidateSupplier: dbpedia.DBPediaPersonCandidateSupplier
 * </pre>
 *
 * @param <T> The Entity type
 */
public class EntityLinkingAnnotator<T extends Entity> extends BaleenAnnotator {

  private static final String SEMANTIC_ENTITY_PACKAGE = "uk.gov.dstl.baleen.types.semantic";
  private static final String COMMON_ENTITY_PACKAGE = "uk.gov.dstl.baleen.types.common";
  private static final String COLLECTOR_PACKAGE = "uk.gov.dstl.baleen.entity.linking.collector";
  private static final String RANKER_PACKAGE = "uk.gov.dstl.baleen.entity.linking.ranker";
  private static final String SUPPLIER_PACKAGE = "uk.gov.dstl.baleen.entity.linking.supplier";

  /**
   * Connection to Stopwords Resource
   *
   * @baleen.resource uk.gov.dstl.baleen.resources.SharedStopwordResource
   */
  public static final String KEY_STOPWORDS = "stopwords";

  @ExternalResource(key = KEY_STOPWORDS)
  private SharedStopwordResource stopwordResource;

  /**
   * The stoplist to use. If the stoplist matches one of the enum's provided in {@link
   * SharedStopwordResource.StopwordList}, then that list will be loaded.
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
   * The entity type class name. Can be a simple class name if the class is in
   * uk.gov.dstl.baleen.common or uk.gov.dstl.baleen.semantic, or a fully qualified name
   *
   * @baleen.config
   */
  public static final String PARAM_ENTITY_TYPE = "entityType";

  @ConfigurationParameter(name = PARAM_ENTITY_TYPE)
  private String entityTypeClassName;

  /**
   * The InformationCollector implementation class name. Can be a fully qualified name or simple
   * class name if a package is declared or the implementation is in
   * uk.gov.dstl.baleen.entity.linking.collector
   *
   * @baleen.config
   */
  public static final String PARAM_INFORMATION_COLLECTOR = "informationCollector";

  @ConfigurationParameter(
      name = PARAM_INFORMATION_COLLECTOR,
      mandatory = false,
      defaultValue = "ProperNounInformationCollector")
  private String informationCollectorClassName;

  /**
   * The CandidateSupplier implementation class name. Can be a fully qualified name or simple class
   * name if a package is declared or the implementation is in
   * uk.gov.dstl.baleen.entity.linking.supplier
   *
   * @baleen.config
   */
  public static final String PARAM_CANDIDATE_SUPPLIER = "candidateSupplier";

  @ConfigurationParameter(name = PARAM_CANDIDATE_SUPPLIER)
  private String candidateSupplierClassName;

  /**
   * The CandidateRanker implementation class name. Can be a fully qualified name or simple class
   * name if a package is declared or the implementation is in
   * uk.gov.dstl.baleen.entity.linking.ranker
   *
   * @baleen.config
   */
  public static final String PARAM_CANDIDATE_RANKER = "candidateRanker";

  @ConfigurationParameter(
      name = PARAM_CANDIDATE_RANKER,
      mandatory = false,
      defaultValue = "BagOfWordsCandidateRanker")
  private String candidateRankerClassName;

  /**
   * Configuration arguments for candidate supplier. This is mandatory for the
   * MongoCandidateSupplier. Mandatory fields for MongoCandidateSupplier are "collection" and
   * "searchField" Should be an array of Strings of key value pairs. For example, ["collection",
   * "peopleCollection", "language", "en", "port", "1234"]
   *
   * @baleen.config {}
   */
  public static final String PARAM_CANDIDATE_SUPPLIER_CONFIG_ARGUMENTS =
      "candidateSupplierArguments";

  @ConfigurationParameter(
      name = PARAM_CANDIDATE_SUPPLIER_CONFIG_ARGUMENTS,
      mandatory = false,
      defaultValue = {})
  private String[] candidateSupplierOptions;

  private Class<T> entityClass;
  private InformationCollector informationCollector;
  private CandidateSupplier<T> candidateSupplier;
  private CandidateRanker<T> candidateRanker;

  @Override
  @SuppressWarnings("unchecked")
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);

    Collection<String> stopwords = stopwordResource.getStopwords(stoplist);

    try {
      entityClass =
          BuilderUtils.getClassFromString(
              entityTypeClassName, SEMANTIC_ENTITY_PACKAGE, COMMON_ENTITY_PACKAGE);

      informationCollector =
          (InformationCollector)
              BuilderUtils.getClassFromString(informationCollectorClassName, COLLECTOR_PACKAGE)
                  .newInstance();

      candidateSupplier =
          (CandidateSupplier<T>)
              BuilderUtils.getClassFromString(candidateSupplierClassName, SUPPLIER_PACKAGE)
                  .newInstance();

      candidateSupplier.configure(candidateSupplierOptions);

      candidateRanker =
          (CandidateRanker<T>)
              BuilderUtils.getClassFromString(candidateRankerClassName, RANKER_PACKAGE)
                  .newInstance();

      candidateRanker.initialize(stopwords);

    } catch (Exception e) {
      throw new ResourceInitializationException(e);
    }
  }

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {

    Set<EntityInformation<T>> entityInformationSet =
        informationCollector.getEntityInformation(jCas, entityClass);

    entityInformationSet.forEach(
        entityInformation -> {
          Collection<Candidate> candidates = candidateSupplier.getCandidates(entityInformation);
          if (candidates.size() == 1) {
            setLinking(jCas, entityInformation, candidates.iterator().next().getId());
            return;
          }
          Optional<Candidate> topCandidatesOptional =
              candidateRanker.getTopCandidate(entityInformation, candidates);
          if (topCandidatesOptional.isPresent()) {
            Candidate topCandidate = topCandidatesOptional.get();
            setLinking(jCas, entityInformation, topCandidate.getId());
          }
        });
  }

  private void setLinking(JCas jCas, EntityInformation<T> entityInformation, String linking) {
    removeFromJCasIndex(entityInformation.getReferenceTarget());
    ReferenceTarget referenceTarget = new ReferenceTarget(jCas);
    referenceTarget.setLinking(linking);
    for (Entity e : entityInformation.getMentions()) {
      e.setReferent(referenceTarget);
    }
    addToJCasIndex(referenceTarget);
    addToJCasIndex(entityInformation.getMentions());
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(Entity.class, WordToken.class, ReferenceTarget.class),
        ImmutableSet.of(ReferenceTarget.class));
  }

  @Override
  protected void doDestroy() {
    try {
      candidateSupplier.close();
    } catch (Exception e) {
      getMonitor().warn("Error closing resources", e);
    }
    super.doDestroy();
  }
}
