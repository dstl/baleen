// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph;

import static com.google.common.base.Predicates.isNull;
import static com.google.common.base.Predicates.not;
import static org.apache.tinkerpop.gremlin.structure.T.id;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.ENTITY;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.EVENT;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.MENTION_OF;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.PARTICIPANT_IN;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.RELATION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Graph.Features;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.VertexProperty.Cardinality;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.graph.value.Longest;
import uk.gov.dstl.baleen.graph.value.Mode;
import uk.gov.dstl.baleen.graph.value.ValueStrategy;
import uk.gov.dstl.baleen.uima.UimaMonitor;

/**
 * A factory for creating a graph representation of the entities mentioned in a document.
 *
 * <p>Each entity that has an annotation in the document is a node in the graph. The attributes are
 * collected from all mentions and can be used to add properties to the entity node with different
 * aggregation options.
 *
 * <p>Events can be optionally included as nodes with links to their participants of type {@link
 * #PARTICIPANT_IN}
 *
 * <p>Relations are also included between the entities by extension of the relation annotations in
 * the document between the mentions.
 */
public class EntityGraphFactory {

  /** Mentions property name */
  public static final String MENTIONS_PROPERTY = "mentions";

  /** Most common value property name */
  public static final String MOST_COMMON_VALUE = "mostCommonValue";

  /** Longest value property name */
  public static final String LONGEST_VALUE = "longestValue";

  protected static final String ID_PROPERTY = "id";

  private static final String LINKING = "linking";

  private static final ValueStrategy<?, ?> MOST_COMMON_VALUE_STRATEGY = new Mode<>();

  private static final ValueStrategy<?, ?> LONGEST_VALUE_STRATEGY = new Longest();

  private final UimaMonitor monitor;

  private final DocumentGraphFactory factory;

  private final EntityGraphOptions options;

  /**
   * Constructor for EntityGraphFactory
   *
   * @param monitor to report
   * @param options to be used during creation of graphs
   */
  public EntityGraphFactory(final UimaMonitor monitor, EntityGraphOptions options) {

    this.monitor = monitor;
    this.options = options;

    DocumentGraphOptions documentOptions =
        new DocumentGraphOptions.Builder()
            .withDocument(false)
            .withRelationsAsLinks(true)
            .withContentHashAsId(options.isContentHashAsId())
            .withEvents(options.isOutputEvents())
            .withStopFeatures(options.getStopFeatures())
            .withTypeClasses(options.getTypeClasses())
            .build();

    factory = new DocumentGraphFactory(monitor, documentOptions);
  }

  /**
   * Create a document graph from the given jCas.
   *
   * @param jCas to create the document from
   * @return the document graph
   * @throws AnalysisEngineProcessException
   */
  public Graph create(JCas jCas) throws AnalysisEngineProcessException {
    Graph graph = createTransformGraph(options.isMultiValueProperties());
    load(jCas, graph, graph.features());
    return graph;
  }

  /**
   * Create a document graph from the given jCas.
   *
   * @param jCas to create the document from
   * @param features to use for the graph
   * @return the document graph
   * @throws AnalysisEngineProcessException
   */
  public Graph create(JCas jCas, Features features) throws AnalysisEngineProcessException {
    Graph graph = createTransformGraph(features.vertex().supportsMultiProperties());
    load(jCas, graph, features);
    return graph;
  }

  /**
   * Load the data form the jCas into the given graph.
   *
   * @param jCas to load the data from
   * @param graph to load the data into
   * @throws AnalysisEngineProcessException
   */
  public void load(JCas jCas, Graph graph, Features features)
      throws AnalysisEngineProcessException {

    try (Graph documentGraph = factory.create(jCas)) {
      GraphTraversalSource fromTraversal = documentGraph.traversal();

      GraphTraversalSource destTraversal = graph.traversal();

      mapEntities(features, fromTraversal, destTraversal);
      mapEvents(features, fromTraversal, destTraversal);
      mapRelations(features, fromTraversal, destTraversal);
    } catch (Exception e) {
      throw new AnalysisEngineProcessException(e);
    }
  }

  private void mapRelations(
      Features features, GraphTraversalSource fromTraversal, GraphTraversalSource destTraversal) {
    fromTraversal
        .V()
        .in(MENTION_OF)
        .outE(RELATION)
        .inV()
        .out(MENTION_OF)
        .path()
        .sideEffect(
            tp -> {
              Path path = tp.get();
              Vertex source = path.get(0);
              Edge relation = path.get(2);
              Vertex target = path.get(4);

              GraphTraversal<Vertex, Vertex> sourceTraversal = destTraversal.V(source.id());
              GraphTraversal<Vertex, Vertex> targetTraversal = destTraversal.V(target.id());

              if (sourceTraversal.hasNext() && targetTraversal.hasNext()) {
                Vertex destSource = sourceTraversal.next();
                Vertex destTarget = targetTraversal.next();

                Edge destRelation = destSource.addEdge(RELATION, destTarget, id, relation.id());
                copyProperties(features, relation, destRelation);
              }
            })
        .iterate();
  }

  private void mapEvents(
      Features features, GraphTraversalSource fromTraversal, GraphTraversalSource destTraversal) {

    fromTraversal
        .V()
        .hasLabel(DocumentGraphFactory.EVENT)
        .sideEffect(
            tv -> {
              Vertex origEvent = tv.get();
              Vertex transEvent = destTraversal.addV(EVENT).property(id, origEvent.id()).next();
              copyProperties(features, origEvent, transEvent);
              origEvent
                  .edges(Direction.BOTH)
                  .forEachRemaining(
                      origEdge ->
                          destTraversal
                              .V(origEdge.inVertex().id())
                              .addE(PARTICIPANT_IN)
                              .to(transEvent)
                              .iterate());
            })
        .iterate();
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private void mapEntities(
      Features features, GraphTraversalSource fromTraversal, GraphTraversalSource destTraversal) {

    fromTraversal
        .V()
        .hasLabel(DocumentGraphFactory.REFERENCE_TARGET)
        .forEachRemaining(
            v -> {
              List<Map<String, Object>> mentions = new ArrayList<>();

              Map<Object, Object> properties =
                  fromTraversal
                      .V(v.id())
                      .in(MENTION_OF)
                      .sideEffect(vt -> mentions.add(aggregateProperties(vt.get())))
                      .properties()
                      .group()
                      .by(t -> ((VertexProperty) t).key())
                      .next();

              Map<String, List<Object>> valueMap =
                  properties
                      .entrySet()
                      .stream()
                      .collect(
                          Collectors.toMap(
                              e -> (String) e.getKey(),
                              e ->
                                  ((List<VertexProperty<?>>) e.getValue())
                                      .stream()
                                      .map(VertexProperty::value)
                                      .collect(Collectors.toList())));

              List<String> aggregateProperties = options.getAggregateProperties();

              destTraversal
                  .addV(ENTITY)
                  .property(id, v.id())
                  .sideEffect(
                      vt -> {
                        Vertex entityVert = vt.get();
                        addProperty(features, entityVert, MENTIONS_PROPERTY, mentions);
                        setProperty(entityVert, LINKING, v.property(LINKING).value());

                        valueMap
                            .entrySet()
                            .stream()
                            .filter(e -> !aggregateProperties.contains(e.getKey()))
                            .forEach(
                                e -> addProperty(features, entityVert, e.getKey(), e.getValue()));

                        List<Object> values = valueMap.get("value");
                        addSingleProperty(
                            entityVert, LONGEST_VALUE, values, LONGEST_VALUE_STRATEGY);
                        addSingleProperty(
                            entityVert, MOST_COMMON_VALUE, values, MOST_COMMON_VALUE_STRATEGY);
                      })
                  .next();
            });
  }

  private Map<String, Object> aggregateProperties(Element element) {
    Map<String, Object> mention = new HashMap<>();
    mention.put(ID_PROPERTY, element.id());
    options
        .getAggregateProperties()
        .forEach(p -> element.property(p).ifPresent(value -> mention.put(p, value)));
    return mention;
  }

  private Graph createTransformGraph(boolean multiValue) {
    BaseConfiguration configuration = new BaseConfiguration();
    configuration.setProperty(Graph.GRAPH, TinkerGraph.class.getName());
    if (multiValue) {
      configuration.setProperty(
          TinkerGraph.GREMLIN_TINKERGRAPH_DEFAULT_VERTEX_PROPERTY_CARDINALITY,
          VertexProperty.Cardinality.list.name());
    } else {
      configuration.setProperty(
          TinkerGraph.GREMLIN_TINKERGRAPH_DEFAULT_VERTEX_PROPERTY_CARDINALITY,
          VertexProperty.Cardinality.single.name());
    }

    return TinkerGraph.open(configuration);
  }

  private void copyProperties(Features features, Element from, Element to) {
    addProperty(
        features, to, MENTIONS_PROPERTY, Collections.singletonList(aggregateProperties(from)));
    List<String> aggregateProperties = options.getAggregateProperties();
    ElementHelper.propertyValueMap(from)
        .entrySet()
        .stream()
        .filter(e -> !aggregateProperties.contains(e.getKey()))
        .forEach(
            property -> {
              Object value = property.getValue();
              if (property instanceof List) {
                addProperty(features, to, property.getKey(), (List<?>) value);
              } else {
                setProperty(to, property.getKey(), value);
              }
            });
  }

  @SuppressWarnings("rawtypes")
  protected void addProperty(Features features, Element v, String key, List<?> values) {
    if (v instanceof Vertex && !Cardinality.single.equals(features.vertex().getCardinality(key))) {
      values.stream().filter(not(isNull())).forEach(value -> setProperty(v, key, value));
    } else {
      ValueStrategy valueStrategy = options.getValueStrategyProvider().get(key);
      addSingleProperty(v, key, values, valueStrategy);
    }
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private void addSingleProperty(
      Element v, String key, List<?> values, ValueStrategy valueStrategy) {
    try {
      Optional value = valueStrategy.aggregate(values);
      if (value.isPresent()) {
        setProperty(v, key, value.get());
      }

    } catch (Exception e) {
      monitor.warn("Error while aggregated property " + key + " from " + values.toString(), e);
    }
  }

  protected Object setProperty(Element v, String key, Object value) {
    return v.property(key, options.getValueCoercer().coerce(value));
  }
}
