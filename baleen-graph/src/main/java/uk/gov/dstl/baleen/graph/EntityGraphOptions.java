// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.graph.coerce.Id;
import uk.gov.dstl.baleen.graph.coerce.ValueCoercer;
import uk.gov.dstl.baleen.graph.value.ValueStrategy;
import uk.gov.dstl.baleen.graph.value.ValueStrategyProvider;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;

/**
 * Options for the {@link EntityGraphFactory}.
 *
 * <p>Use the {@link Builder} for construction.
 */
@SuppressWarnings("rawtypes")
public class EntityGraphOptions {

  private final boolean contentHashAsId;
  private final boolean outputEvents;
  private final boolean multiValueProperties;
  private final Set<String> stopFeatures;
  private final Set<Class<? extends Entity>> typeClasses;
  private final ValueStrategyProvider valueStrategyProvider;
  private final List<String> aggregateProperties;
  private final ValueCoercer valueCoerser;

  /*
   * Use builder to construct
   */
  private EntityGraphOptions(
      boolean contentHashAsId,
      boolean outputEvents,
      boolean multiValueProperties,
      Set<String> stopFeatures,
      Set<Class<? extends Entity>> typeClasses,
      ValueStrategyProvider valueStrategyProvider,
      List<String> aggregateProperties,
      ValueCoercer valueCoerser) {
    this.contentHashAsId = contentHashAsId;
    this.outputEvents = outputEvents;
    this.multiValueProperties = multiValueProperties;
    this.stopFeatures = stopFeatures;
    this.typeClasses = typeClasses;
    this.valueStrategyProvider = valueStrategyProvider;
    this.aggregateProperties = aggregateProperties;
    this.valueCoerser = valueCoerser;
  }

  /**
   * @return true if the content hash should be used for document id, the source is used otherwise.
   */
  public boolean isContentHashAsId() {
    return contentHashAsId;
  }

  /** @return true to include events in the output */
  public boolean isOutputEvents() {
    return outputEvents;
  }

  /** @return the features to ignore */
  public Set<String> getStopFeatures() {
    return stopFeatures;
  }

  /** @return true is multiple values allowed */
  public boolean isMultiValueProperties() {
    return multiValueProperties;
  }

  /** @return the set of annotation classes to include as nodes. */
  public Set<Class<? extends Entity>> getTypeClasses() {
    return typeClasses;
  }

  /** @return the value strategy provider */
  public ValueStrategyProvider getValueStrategyProvider() {
    return valueStrategyProvider;
  }

  /** @return the properties to aggregate */
  public List<String> getAggregateProperties() {
    return aggregateProperties;
  }

  /** @return the value coerser */
  public ValueCoercer getValueCoercer() {
    return valueCoerser;
  }

  /** A builder for {@link EntityGraphOptions} */
  public static class Builder {

    private static final ValueCoercer IDENTITY = new Id();

    private boolean contentHashAsId = false;
    private boolean outputEvents = true;
    private boolean multiValueProperties = false;
    private Set<Class<? extends Entity>> typeClasses;
    private Map<String, ValueStrategy<?, ?>> valueStrategies = new HashMap<>();
    private ValueStrategy<?, ?> defaultValueStrategy = new uk.gov.dstl.baleen.graph.value.List();
    private List<String> aggregateProperties = ImmutableList.of("begin", "end", "confidence");
    private final Set<String> stopFeatures = new HashSet<>(ConsumerUtils.getDefaultStopFeatures());
    private ValueCoercer valueCoercer = IDENTITY;

    /**
     * true to use the content hash as id, false to use the hash of the source (default false)
     *
     * @param value
     * @return this
     */
    public Builder withContentHashAsId(boolean value) {
      contentHashAsId = value;
      return this;
    }

    /**
     * true to include events (default true)
     *
     * @param value
     * @return this
     */
    public Builder withEvents(boolean value) {
      outputEvents = value;
      return this;
    }

    /**
     * true to allow vertexes to have multiple property values (default false)
     *
     * @param value
     * @return this
     */
    public Builder withMultiValueProperties(boolean value) {
      multiValueProperties = value;
      return this;
    }

    /**
     * set features to be ignored. Already include defaults from {@link
     * ConsumerUtils#getDefaultStopFeatures()}.
     *
     * @param values
     * @return this
     */
    public Builder withStopFeatures(String... values) {
      stopFeatures.addAll(Arrays.asList(values));
      return this;
    }

    /**
     * Use the given types, defaults to all {@link Entity} types
     *
     * @param typeClasses to be included
     * @return this
     */
    public Builder withTypeClasses(Set<Class<? extends Entity>> typeClasses) {
      this.typeClasses = typeClasses;
      return this;
    }

    /**
     * Use the given types, defaults to all {@link Entity} types
     *
     * @param typeClasses to be included
     * @return this
     */
    public Builder withValueStrategy(String key, ValueStrategy strategy) {
      valueStrategies.put(key, strategy);
      return this;
    }

    /**
     * Use the given types, defaults to all {@link Entity} types
     *
     * @param typeClasses to be included
     * @return this
     */
    public Builder withDefaultValueStrategy(ValueStrategy strategy) {
      defaultValueStrategy = strategy;
      return this;
    }

    /**
     * Use the given types, defaults to all {@link Entity} types
     *
     * @param typeClasses to be included
     * @return this
     */
    public Builder withAggregateProperties(String... properties) {
      aggregateProperties = Arrays.asList(properties);
      return this;
    }

    /**
     * Use the given value coerser, defaults to the identity function
     *
     * @param value to use
     * @return this
     */
    public Builder withValueCoercer(ValueCoercer value) {
      valueCoercer = value;
      return this;
    }

    /** @return the build {@link EntityGraphOptions} */
    public EntityGraphOptions build() {
      if (typeClasses == null) {
        typeClasses = TypeUtils.getAnnotationClasses(Entity.class);
      }

      ValueStrategyProvider valueStrategyProvider =
          key -> valueStrategies.getOrDefault(key, defaultValueStrategy);

      return new EntityGraphOptions(
          contentHashAsId,
          outputEvents,
          multiValueProperties,
          ImmutableSet.copyOf(stopFeatures),
          typeClasses,
          valueStrategyProvider,
          ImmutableList.copyOf(aggregateProperties),
          valueCoercer);
    }
  }

  /** @return a Builder to create {@link EntityGraphOptions}. */
  public static Builder builder() {
    return new EntityGraphOptions.Builder();
  }
}
