// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.events;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.Base;
import uk.gov.dstl.baleen.types.language.Paragraph;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Event;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Temporal;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;

/**
 * Extract events in a simple but potentially noisy way.
 *
 * <p>Assumptions: - An Event is defined as something with a Location, Temporal and at least 1
 * Entity - Events can be extracted from Sentences or Paragraphs, depending on configuration -
 * Sentences and Paragraphs contain at most 2 Events - To reduce noise, Entities must be of the
 * following types - {@value CHEMICAL} - {@value MONEY} - {@value ORGANISATION} - {@value PERSON} -
 * {@value VEHICLE} - {@value WEAPON}
 *
 * <p>Using Paragraphs will produce more results, but these may be noisy extractions.
 */
public class SimpleEventExtractor extends BaleenAnnotator {

  /**
   * Use only the given types when retrieving Entities involved in an Event
   *
   * <p>Do not set for all available types (see class level JavaDoc).
   *
   * @baleen.config {@value CHEMICAL} {@value MONEY} {@value ORGANISATION} {@value PERSON} {@value
   *     VEHICLE} {@value WEAPON}
   */
  public static final String PARAM_TYPE_NAMES = "typeNames";

  @ConfigurationParameter(
    name = PARAM_TYPE_NAMES,
    mandatory = false,
    defaultValue = {CHEMICAL, MONEY, ORGANISATION, PERSON, VEHICLE, WEAPON}
  )
  protected String[] typeNames;

  /**
   * Parameter to decide which text block type to extract events from. Value can either be {@value
   * SENTENCES} (default) or {@value PARAGRAPHS}
   *
   * @baleen.config sentences
   */
  public static final String PARAM_BLOCKS_TO_EXTRACT_FROM = "extractFrom";

  @ConfigurationParameter(
    name = PARAM_BLOCKS_TO_EXTRACT_FROM,
    mandatory = false,
    defaultValue = SENTENCES
  )
  private String extractFrom;

  private static final String SENTENCES = "sentences";
  private static final String PARAGRAPHS = "paragraphs";
  private static final String CHEMICAL = "Chemical";
  private static final String MONEY = "Money";
  private static final String ORGANISATION = "Organisation";
  private static final String PERSON = "Person";
  private static final String VEHICLE = "Vehicle";
  private static final String WEAPON = "Weapon";

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleEventExtractor.class);

  private Class<? extends Base> sectionClass = Sentence.class;
  private Collection<Class<? extends Entity>> entityClasses;

  @Override
  public void initialize(UimaContext context) throws ResourceInitializationException {
    super.initialize(context);

    entityClasses = TypeUtils.getTypeClasses(Entity.class, typeNames);

    sectionClass = getSectionClass();
  }

  @Override
  public void doProcess(JCas jCas) throws AnalysisEngineProcessException {

    getMonitor().debug("Running SimpleEventExtractor");

    extractEventsFrom(jCas, JCasUtil.select(jCas, sectionClass));
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(
            Entity.class, Sentence.class, Paragraph.class, Temporal.class, Location.class),
        ImmutableSet.of(Event.class));
  }

  private Class<? extends Base> getSectionClass() throws ResourceInitializationException {
    if (extractFrom.equals(SENTENCES)) {
      return Sentence.class;
    } else if (extractFrom.equals(PARAGRAPHS)) {
      return Paragraph.class;
    }

    throw new ResourceInitializationException(
        new BaleenException(
            "Invalid "
                + PARAM_BLOCKS_TO_EXTRACT_FROM
                + " parameter. Should be "
                + "either "
                + SENTENCES
                + " (default) or "
                + PARAGRAPHS));
  }

  private <T extends Base> void extractEventsFrom(JCas jCas, Collection<T> sections) {

    Map<? extends Base, List<Location>> locationIndex =
        JCasUtil.indexCovered(jCas, sectionClass, Location.class);
    Map<? extends Base, List<Temporal>> temporalIndex =
        JCasUtil.indexCovered(jCas, sectionClass, Temporal.class);
    Map<? extends Base, List<Entity>> entityIndex =
        JCasUtil.indexCovered(jCas, sectionClass, Entity.class);

    for (T t : sections) {

      List<Entity> allEntitiesInSection = (List<Entity>) entityIndex.get(t);
      Set<Entity> relevantEntities = filterEntities(allEntitiesInSection);
      List<Location> locations = (List<Location>) locationIndex.get(t);
      List<Temporal> temporals = (List<Temporal>) temporalIndex.get(t);
      int begin = t.getBegin();
      int end = t.getEnd();

      if (!relevantEntities.isEmpty() && !locations.isEmpty() && !temporals.isEmpty()) {
        createEvent(jCas, begin, end, relevantEntities, locations, temporals, 0);
      }

      if (relevantEntities.size() > 1 && locations.size() > 1 && temporals.size() > 1) {
        createEvent(jCas, begin, end, relevantEntities, locations, temporals, 1);
      }
    }
  }

  private void createEvent(
      JCas jCas,
      int begin,
      int end,
      Set<Entity> relevantEntities,
      List<Location> locations,
      List<Temporal> temporals,
      int index) {

    String entitiesString = createEntitiesString(relevantEntities);
    String locationString = locations.get(index).getValue();
    String temporalString = temporals.get(index).getValue();

    String eventString = createEventString(entitiesString, locationString, temporalString);

    Event event = new Event(jCas);
    event.setBegin(begin);
    event.setEnd(end);
    event.setEntities(new FSArray(jCas, relevantEntities.size()));
    int i = 0;
    for (Entity entity : relevantEntities) {
      event.setEntities(i, entity);
      i++;
    }
    LOGGER.debug("Event: {}", eventString);
    addToJCasIndex(event);
  }

  private Set<Entity> filterEntities(Collection<Entity> allEntitiesInSection) {

    return allEntitiesInSection
        .stream()
        .filter(entity -> entityClasses.contains(entity.getClass()))
        .collect(Collectors.toSet());
  }

  private String createEntitiesString(Collection<Entity> entities) {
    StringBuilder sb = new StringBuilder();
    for (Entity entity : entities) {
      sb.append(entity.getValue()).append(" ");
    }
    return sb.toString();
  }

  private String createEventString(
      String entitiesString, String locationString, String temporalString) {

    return entitiesString + " " + locationString + " " + temporalString;
  }
}
