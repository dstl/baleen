// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Graph.Variables;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;

import com.google.common.collect.Multimap;

import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.consumers.utils.DefaultFields;
import uk.gov.dstl.baleen.consumers.utils.EntityRelationConverter;
import uk.gov.dstl.baleen.consumers.utils.IEntityConverterFields;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.metadata.PublishedId;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Event;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.types.structure.Document;
import uk.gov.dstl.baleen.uima.UimaMonitor;
import uk.gov.dstl.baleen.uima.UimaSupport;
import uk.gov.dstl.baleen.uima.utils.ReferentUtils;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;
import uk.gov.dstl.baleen.uima.utils.UimaTypesUtils;

/**
 * A factory for creating a graph representation of the annotations in a document.
 *
 * <p>Each entity annotation in the document is a node in the graph and it's attributes store the
 * annotation Features. The following graph features can be optionally added, see {@link
 * DocumentGraphOptions}.
 *
 * <p>ReferenceTargets as nodes with links to their mentions of type {@link #MENTION_OF}
 *
 * <p>Events as nodes with link to their participants of type {@link #PARTICIPANT_IN}
 *
 * <p>A node for the Document with links to all mentions of type {@link #MENTION_IN}
 *
 * <p>Finally, Relations can be included, these are also added as nodes with {@link #SOURCE} and
 * {@link #TARGET} links to the mentions they relate. Including these as nodes allows them to
 * contain all the Features of the relation and have links to the document node. There is also an
 * option to have these directly as links, this is a more natural representation however they can
 * not carry all the information in some graph representations.
 */
public class DocumentGraphFactory {

  /** Document type label */
  public static final String DOCUMENT = Document.class.getSimpleName();
  /** Mention type label */
  public static final String MENTION = "Mention";
  /** ReferenceTarget type label */
  public static final String REFERENCE_TARGET = ReferenceTarget.class.getSimpleName();
  /** Entity type label */
  public static final String ENTITY = Entity.class.getSimpleName();
  /** Event type label */
  public static final String EVENT = Event.class.getSimpleName();
  /** Relation type label */
  public static final String RELATION = Relation.class.getSimpleName();
  /** mentionOf link label */
  public static final String MENTION_OF = "mentionOf";
  /** mentionIn link label */
  public static final String MENTION_IN = "mentionIn";
  /** participantIn link label */
  public static final String PARTICIPANT_IN = "participantIn";
  /** source link label */
  public static final String SOURCE = "source";
  /** target link label */
  public static final String TARGET = "target";

  // Graph Fields
  public static final String FIELD_DOCUMENT_ID = "docId";
  public static final String FIELD_DOCUMENT_TYPE = "type";
  public static final String FIELD_DOCUMENT_SOURCE = "source";
  public static final String FIELD_DOCUMENT_LANGUAGE = "language";
  public static final String FIELD_DOCUMENT_TIMESTAMP = "timestamp";
  public static final String FIELD_DOCUMENT_CLASSIFICATION = "classification";
  public static final String FIELD_DOCUMENT_CAVEATS = "caveats";
  public static final String FIELD_DOCUMENT_RELEASABILITY = "releasability";
  public static final String FIELD_PUBLISHEDIDS = "publishedIds";
  public static final String FIELD_PUBLISHEDIDS_ID = "id";
  public static final String FIELD_PUBLISHEDIDS_TYPE = "type";
  public static final String FIELD_METADATA = "metadata";
  public static final String FIELD_CONTENT = "content";
  public static final String FIELD_LINKING = "linking";

  private final IEntityConverterFields fields = new DefaultFields();

  private final UimaMonitor monitor;
  private final DocumentGraphOptions options;

  /**
   * Constructor for DocumentGraphFactory
   *
   * @param monitor to report
   * @param options to be used during creation of graphs
   */
  public DocumentGraphFactory(final UimaMonitor monitor, final DocumentGraphOptions options) {
    this.monitor = monitor;
    this.options = options;
  }

  /**
   * Create a document graph from the given jCas.
   *
   * @param jCas to create the document from
   * @return the document graph
   */
  public Graph create(JCas jCas) {
    TinkerGraph graph = TinkerGraph.open();
    load(jCas, graph);
    return graph;
  }

  /**
   * Load the data form the jCas into the given graph.
   *
   * @param jCas to load the data from
   * @param graph to load the data into
   */
  public void load(JCas jCas, Graph graph) {
    GraphTraversalSource traversal = graph.traversal();

    Optional<Vertex> document = traversal(jCas, traversal);

    if (options.isOutputContent()) {
      loadDocumentContent(jCas, graph, document);
    }

    if (options.isOutputMeta()) {
      loadGraphMetadata(jCas, graph, document);
    } else {
      monitor.debug("DocumentGraph metadata skiped");
    }
  }

  /**
   * Load the data form the jCas into the given graph traversal.
   *
   * @param jCas to load the data from
   * @param traversal to load the data into
   */
  public void load(JCas jCas, GraphTraversalSource traversal) {
    traversal(jCas, traversal);
  }

  private Optional<Vertex> traversal(JCas jCas, GraphTraversalSource traversal) {
    EntityRelationConverter converter =
        new EntityRelationConverter(monitor, options.getStopFeatures(), fields, false);

    loadMentions(jCas, traversal, converter);

    if (options.isOutputRelations()) {
      loadRelations(jCas, traversal, converter);
    } else {
      monitor.debug("DocumentGraph relations ommitted");
    }

    if (options.isOutputEvents()) {
      loadEvents(jCas, traversal, converter);
    } else {
      monitor.debug("DocumentGraph events ommitted");
    }

    return loadDocument(jCas, traversal);
  }

  private Optional<Vertex> loadDocument(JCas jCas, GraphTraversalSource traversal) {
    String documentId = getDocumentId(jCas);
    if (options.isOutputDocument()) {
      Vertex documentVert =
          traversal
              .addV(DOCUMENT)
              .property(T.id, coerce(documentId))
              .sideEffect(
                  tv -> {
                    Vertex documentVertex = tv.get();
                    getGraphMetadata(jCas).entrySet().stream()
                        .forEach(e -> setProperty(documentVertex, e.getKey(), e.getValue()));
                  })
              .next();

      traversal
          .V()
          .filter(v -> !v.get().equals(documentVert))
          .addE(MENTION_IN)
          .to(documentVert)
          .iterate();
      return Optional.of(documentVert);
    } else {
      traversal.V().property(FIELD_DOCUMENT_ID, coerce(documentId)).iterate();
      traversal.E().property(FIELD_DOCUMENT_ID, coerce(documentId)).iterate();
      return Optional.empty();
    }
  }

  private Object coerce(Object value) {
    return options.getValueCoercer().coerce(value);
  }

  private Map<String, Object> getGraphMetadata(JCas jCas) {

    Map<String, Object> variables = new HashMap<>();
    DocumentAnnotation da = UimaSupport.getDocumentAnnotation(jCas);

    setIfValue(variables, FIELD_DOCUMENT_TYPE, da.getDocType());
    setIfValue(variables, FIELD_DOCUMENT_SOURCE, da.getSourceUri());
    setIfValue(variables, FIELD_DOCUMENT_LANGUAGE, da.getLanguage());
    setIfValue(variables, FIELD_DOCUMENT_TIMESTAMP, new Date(da.getTimestamp()));

    setIfValue(variables, FIELD_DOCUMENT_CLASSIFICATION, da.getDocumentClassification());
    setIfListValue(
        variables, FIELD_DOCUMENT_CAVEATS, UimaTypesUtils.toList(da.getDocumentCaveats()));
    setIfListValue(
        variables,
        FIELD_DOCUMENT_RELEASABILITY,
        UimaTypesUtils.toList(da.getDocumentReleasability()));

    // Published Ids
    Collection<PublishedId> publishedIds = JCasUtil.select(jCas, PublishedId.class);
    if (!publishedIds.isEmpty()) {
      List<Map<String, String>> publishedList = new ArrayList<>();
      for (PublishedId pid : publishedIds) {
        Map<String, String> publishedId = new HashMap<>();
        publishedId.put(FIELD_PUBLISHEDIDS_ID, pid.getValue());
        publishedId.put(FIELD_PUBLISHEDIDS_TYPE, pid.getPublishedIdType());
        publishedList.add(publishedId);
      }
      variables.put(FIELD_PUBLISHEDIDS, publishedList);
    }

    // Meta data
    Collection<Metadata> selectedMeta = JCasUtil.select(jCas, Metadata.class);
    if (!selectedMeta.isEmpty()) {
      Map<String, List<Object>> metaMap = new HashMap<>();
      MultiValueMap multiMap = MultiValueMap.decorate(metaMap);
      for (Metadata metadata : selectedMeta) {
        multiMap.put(metadata.getKey(), metadata.getValue());
      }
      variables.put(FIELD_METADATA, metaMap);
    }
    setIfValue(variables, fields.getExternalId(), getDocumentId(jCas));

    return variables;
  }

  private void loadGraphMetadata(JCas jCas, Graph graph, Optional<Vertex> document) {
    if (!document.isPresent()) {
      Variables variables = graph.variables();
      getGraphMetadata(jCas).entrySet().stream()
          .forEach(e -> variables.set(e.getKey(), e.getValue()));
    }
  }

  private void loadDocumentContent(JCas jCas, Graph graph, Optional<Vertex> document) {
    if (document.isPresent()) {
      document.get().property(FIELD_CONTENT, coerce(jCas.getDocumentText()));
    } else {
      graph.variables().set(FIELD_CONTENT, jCas.getDocumentText());
    }
  }

  private void setIfListValue(Map<String, Object> variables, String key, List<?> value) {
    if (CollectionUtils.isNotEmpty(value)) {
      variables.put(key, value);
    }
  }

  private void setIfValue(Map<String, Object> variables, String key, Object value) {
    if (value != null) {
      variables.put(key, value);
    }
  }

  private String getDocumentId(JCas jCas) {
    return ConsumerUtils.getExternalId(
        UimaSupport.getDocumentAnnotation(jCas), options.isContentHashAsId());
  }

  private void loadMentions(
      JCas jCas, GraphTraversalSource traversal, EntityRelationConverter converter) {

    List<Entity> annotations =
        TypeUtils.filterAnnotations(JCasUtil.select(jCas, Entity.class), options.getTypeClasses());

    Multimap<ReferenceTarget, Entity> targetted =
        ReferentUtils.createReferentMap(jCas, annotations, false);

    targetted.asMap().entrySet().stream()
        .forEach(
            e -> {
              Set<String> types = new HashSet<>();
              List<Vertex> mentions =
                  e.getValue().stream()
                      .map(converter::convertEntity)
                      .peek(mention -> types.add(mention.get(fields.getType()).toString()))
                      .filter(
                          mention ->
                              !traversal.V(coerce(mention.get(fields.getExternalId()))).hasNext())
                      .map(
                          mention ->
                              traversal
                                  .addV(MENTION)
                                  .property(T.id, coerce(mention.get(fields.getExternalId())))
                                  .sideEffect(vt -> addMentionProperties(mention, vt))
                                  .next())
                      .collect(Collectors.toList());

              if (options.isOutputReferenceTargets()) {
                String refId = ConsumerUtils.getExternalId(e.getValue());
                if (!traversal.V(coerce(refId)).hasNext()) {
                  String linking = e.getKey().getLinking() == null ? "" : e.getKey().getLinking();
                  traversal
                      .addV(REFERENCE_TARGET)
                      .property(FIELD_LINKING, coerce(linking))
                      .property(T.id, coerce(refId))
                      .next();
                  mentions.forEach(
                      mentionVertex ->
                          traversal
                              .V(mentionVertex.id())
                              .as(MENTION)
                              .V(refId)
                              .as(REFERENCE_TARGET)
                              .addE(MENTION_OF)
                              .from(MENTION)
                              .to(REFERENCE_TARGET)
                              .next());
                }
              }
            });
  }

  private void addMentionProperties(Map<String, Object> mention, Traverser<Vertex> vt) {
    Vertex vertex = vt.get();
    mention.entrySet().stream()
        .filter(property -> property.getValue() != null)
        .filter(property -> !property.getKey().equals(fields.getExternalId()))
        .forEach(property -> vertex.property(property.getKey(), coerce(property.getValue())));
  }

  private void loadRelations(
      JCas jCas, GraphTraversalSource traversal, EntityRelationConverter converter) {
    JCasUtil.select(jCas, Relation.class).stream()
        .map(converter::convertRelation)
        .forEach(relation -> loadRelation(traversal, relation));
  }

  private void loadRelation(GraphTraversalSource traversal, Map<String, Object> relation) {
    String sourceId = (String) relation.get(SOURCE);
    String targetId = (String) relation.get(TARGET);

    GraphTraversal<Vertex, Vertex> relationTraversal =
        traversal.V(sourceId).as(SOURCE).V(targetId).as(TARGET);

    Object relationId = relation.get(fields.getExternalId());

    if (options.isOutputRelationAsLinks() && !traversal.E(coerce(relationId)).hasNext()) {
      relationTraversal
          .addE(RELATION)
          .from(SOURCE)
          .to(TARGET)
          .property(T.id, coerce(relationId))
          .sideEffect(rt -> addRelationProperties(relation, rt))
          .tryNext();
    } else if (!options.isOutputRelationAsLinks() && !traversal.V(coerce(relationId)).hasNext()) {
      relationTraversal
          .addV(RELATION)
          .property(T.id, coerce(relationId))
          .as(RELATION)
          .sideEffect(vt -> addRelationProperties(relation, vt))
          .addE(SOURCE)
          .from(SOURCE)
          .to(RELATION)
          .addE(TARGET)
          .from(RELATION)
          .to(TARGET)
          .tryNext();
    } else {
      monitor.debug("Relation {} duplicated: {}", relationId, relation);
    }
  }

  private void addRelationProperties(
      Map<String, Object> relation, Traverser<? extends Element> relationElement) {
    Element rel = relationElement.get();
    relation.entrySet().stream()
        .filter(property -> property.getValue() != null)
        .filter(property -> !property.getKey().equals(fields.getExternalId()))
        .forEach(property -> setProperty(rel, property.getKey(), property.getValue()));
  }

  @SuppressWarnings("unchecked")
  private void loadEvents(
      JCas jCas, GraphTraversalSource traversal, EntityRelationConverter converter) {

    JCasUtil.select(jCas, Event.class).stream()
        .map(converter::convertEvent)
        .filter(e -> !traversal.V(coerce(e.get(fields.getExternalId()))).hasNext())
        .forEach(
            e -> {
              final Vertex eventVert =
                  traversal
                      .addV(EVENT)
                      .property(T.id, coerce(e.get(fields.getExternalId())))
                      .sideEffect(
                          vt ->
                              e.entrySet().stream()
                                  .filter(property -> property.getValue() != null)
                                  .filter(
                                      property -> !property.getKey().equals(fields.getExternalId()))
                                  .filter(property -> !property.getKey().equals("entities"))
                                  .forEach(
                                      property ->
                                          setProperty(
                                              vt.get(),
                                              property.getKey(),
                                              coerce(property.getValue()))))
                      .next();

              Object object = e.get("entities");
              if (object instanceof List<?>) {
                List<String> entities = (List<String>) object;
                traversal
                    .V(entities.toArray(new Object[0]))
                    .addE(PARTICIPANT_IN)
                    .to(eventVert)
                    .iterate();
              } else {
                monitor.warn("Event entities of unxepected type");
              }
            });
  }

  protected Object setProperty(Element v, String key, Object value) {
    return v.property(key, coerce(value));
  }
}
