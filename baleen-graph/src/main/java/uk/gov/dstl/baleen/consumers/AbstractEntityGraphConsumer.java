// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers;

import java.util.Set;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.consumers.utils.SourceUtils;
import uk.gov.dstl.baleen.core.utils.BuilderUtils;
import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.graph.EntityGraphFactory;
import uk.gov.dstl.baleen.graph.EntityGraphOptions;
import uk.gov.dstl.baleen.graph.value.ValueStrategy;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;

/**
 * Base abstract consumer for the entity graph. Contains the properties for the {@link
 * EntityGraphOptions}.
 */
public abstract class AbstractEntityGraphConsumer extends AbstractGraphConsumer {

  private static final String DEFAULT_VALUE_STRATEGY_PACKAGE =
      ValueStrategy.class.getPackage().getName();

  /**
   * If the target graph can not store multiple values then the list of values can be mapped to a
   * single value. This array defines a map of attribute name to valueStrategy to use (in preference
   * to the default strategy).
   *
   * @baleen.config age, Max, name, Longest,
   */
  public static final String PARAM_VALUE_STRATEGY = "valueStrategy";

  @ConfigurationParameter(name = PARAM_VALUE_STRATEGY, mandatory = false)
  protected String[] valueStrategyTypes;

  /**
   * If the target graph can not store multiple values then the list of values can be mapped to a
   * single value. This is the default mapping strategy to use
   *
   * @baleen.config
   */
  public static final String PARAM_DEFAULT_VALUE_STRATEGY = "defaultValueStrategy";

  @ConfigurationParameter(name = PARAM_DEFAULT_VALUE_STRATEGY, defaultValue = "List")
  protected String defaultValueStrategyType;

  /**
   * A list of property values to aggregate.
   *
   * <p>Some values do not make sense separated, such as begin and end value of the offset. These
   * values can be aggregates form the mention into a single object to retain their meaning.
   *
   * @baleen.config
   */
  public static final String PARAM_AGGREGATE_PROPERTIES = "aggregate";

  @ConfigurationParameter(
      name = PARAM_AGGREGATE_PROPERTIES,
      defaultValue = {"begin", "end", "confidence"})
  private String[] aggregate;

  /**
   * Allow graph to have multiple values for the same property.
   *
   * <p>Some graph implementations will support this, many do not.
   *
   * @baleen.config
   */
  public static final String PARAM_MULTI_VALUE_PROPERTIES = "multiValueProperties";

  @ConfigurationParameter(name = PARAM_MULTI_VALUE_PROPERTIES, defaultValue = "false")
  protected boolean multiValueProperties;

  private EntityGraphFactory factory;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);
    try {

      Set<Class<? extends Entity>> typeClasses = TypeUtils.getTypeClasses(Entity.class, typeNames);

      EntityGraphOptions.Builder builder =
          EntityGraphOptions.builder()
              .withContentHashAsId(contentHashAsId)
              .withEvents(outputEvents)
              .withStopFeatures(filterFeatures)
              .withAggregateProperties(aggregate)
              .withTypeClasses(typeClasses)
              .withMultiValueProperties(multiValueProperties)
              .withDefaultValueStrategy(createValueStrategy(defaultValueStrategyType))
              .withValueCoercer(valueCoercer);

      if (valueStrategyTypes != null) {
        for (int i = 0; i < valueStrategyTypes.length; i += 2) {
          builder.withValueStrategy(
              valueStrategyTypes[i], createValueStrategy(valueStrategyTypes[i + 1]));
        }
      }

      addOptions(builder);

      factory = new EntityGraphFactory(getMonitor(), builder.build());

    } catch (Exception e) {
      throw new ResourceInitializationException(e);
    }
  }

  /**
   * Chance for implementations to manipulate the options
   *
   * @param builder
   */
  protected void addOptions(EntityGraphOptions.Builder builder) {
    // Do nothing if not overridden
  }

  private ValueStrategy<?, ?> createValueStrategy(String valueStrategyType)
      throws ResourceInitializationException {
    try {
      Class<? extends ValueStrategy<?, ?>> classFromString =
          BuilderUtils.getClassFromString(valueStrategyType, DEFAULT_VALUE_STRATEGY_PACKAGE);
      return classFromString.newInstance();
    } catch (InvalidParameterException | InstantiationException | IllegalAccessException e) {
      throw new ResourceInitializationException(e);
    }
  }

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
    String documentSourceName = SourceUtils.getDocumentSourceBaseName(jCas);
    Graph entityGraph = factory.create(jCas);
    processGraph(documentSourceName, entityGraph);
  }

  /**
   * This method is called once the data from the document has been added to the entity graph.
   *
   * @param graph containing the aggregated document information
   * @param documentSourceName the source name for the document
   * @throws AnalysisEngineProcessException
   */
  protected abstract void processGraph(String documentSourceName, Graph graph)
      throws AnalysisEngineProcessException;
}
