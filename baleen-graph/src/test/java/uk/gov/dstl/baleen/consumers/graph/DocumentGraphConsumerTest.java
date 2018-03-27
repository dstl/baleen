// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.EVENT;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.FIELD_DOCUMENT_ID;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.MENTION;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.MENTION_OF;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.PARTICIPANT_IN;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.REFERENCE_TARGET;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.RELATION;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.graph.DocumentGraphFactory;
import uk.gov.dstl.baleen.graph.GraphFormat;
import uk.gov.dstl.baleen.graph.JCasTestGraphUtil;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Event;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Relation;

public class DocumentGraphConsumerTest extends AbstractAnnotatorTest {

  private Path tempDirectory;
  private File graphFile;
  private File propertiesFile;
  private Properties properties;

  public DocumentGraphConsumerTest() {
    super(DocumentGraphConsumer.class);
  }

  @Before
  public void setup() throws IOException {
    JCasTestGraphUtil.populateJcas(jCas);
    tempDirectory = Files.createTempDirectory(DocumentGraphConsumerTest.class.getSimpleName());
    tempDirectory.toFile().deleteOnExit();

    graphFile = tempDirectory.resolve("testgraph.json").toFile();
    graphFile.deleteOnExit();

    propertiesFile = tempDirectory.resolve("graph.properties").toFile();
    propertiesFile.deleteOnExit();

    properties = new Properties();
    properties.setProperty(
        Graph.GRAPH, "org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph");
    properties.setProperty(
        TinkerGraph.GREMLIN_TINKERGRAPH_GRAPH_FORMAT,
        GraphFormat.GRAPHSON.toString().toLowerCase());
    properties.setProperty(
        TinkerGraph.GREMLIN_TINKERGRAPH_GRAPH_LOCATION, graphFile.getAbsolutePath());
  }

  private void writeProperties() throws IOException, FileNotFoundException {
    try (OutputStream os = new FileOutputStream(propertiesFile)) {
      properties.store(os, "");
    }
  }

  @Test
  public void testDocumentGraphConsumer()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException,
          URISyntaxException {

    properties.setProperty(
        TinkerGraph.GREMLIN_TINKERGRAPH_DEFAULT_VERTEX_PROPERTY_CARDINALITY,
        VertexProperty.Cardinality.list.name());
    writeProperties();

    processJCas(
        DocumentGraphConsumer.PARAM_GRAPH_CONFIG,
        propertiesFile.getAbsolutePath(),
        DocumentGraphConsumer.PARAM_OUTPUT_RELATIONS_AS_LINKS,
        true);
    Graph graph = GraphFactory.open(propertiesFile.getAbsolutePath());

    String documentId = ConsumerUtils.getExternalId(getDocumentAnnotation(jCas), false);

    assertEquals(3, graph.traversal().V().hasLabel(REFERENCE_TARGET).count().next().intValue());
    assertEquals(1, graph.traversal().V().hasLabel(EVENT).count().next().intValue());
    assertEquals(4, graph.traversal().V().hasLabel(MENTION).count().next().intValue());
    assertEquals(4, graph.traversal().E().hasLabel(MENTION_OF).count().next().intValue());
    assertEquals(2, graph.traversal().E().hasLabel(RELATION).count().next().intValue());
    assertEquals(2, graph.traversal().E().hasLabel(PARTICIPANT_IN).count().next().intValue());

    assertEquals(8, IteratorUtils.count(graph.vertices()));
    assertEquals(8, IteratorUtils.count(graph.edges()));
    Location location = JCasUtil.selectSingle(jCas, Location.class);
    assertTrue(
        graph
            .traversal()
            .V(location.getExternalId())
            .hasLabel(MENTION)
            .has("value", location.getValue())
            .has(FIELD_DOCUMENT_ID, documentId)
            .has("geoJson", location.getGeoJson())
            .hasNext());

    JCasUtil.select(jCas, Relation.class)
        .forEach(
            r ->
                assertTrue(
                    graph
                        .traversal()
                        .E(r.getExternalId())
                        .hasLabel(RELATION)
                        .has("relationshipType", r.getRelationshipType())
                        .has("value", r.getValue())
                        .has(DocumentGraphFactory.FIELD_DOCUMENT_ID, documentId)
                        .hasNext()));

    JCasUtil.select(jCas, Person.class)
        .forEach(
            r ->
                assertTrue(
                    graph
                        .traversal()
                        .V(r.getExternalId())
                        .hasLabel(MENTION)
                        .has("type", Person.class.getSimpleName())
                        .has("value", r.getValue())
                        .has("gender", r.getGender())
                        .has(DocumentGraphFactory.FIELD_DOCUMENT_ID, documentId)
                        .hasNext()));

    Event event = JCasUtil.selectSingle(jCas, Event.class);
    assertTrue(
        graph
            .traversal()
            .V(event.getExternalId())
            .hasLabel(EVENT)
            .has("value", event.getValue())
            .has(DocumentGraphFactory.FIELD_DOCUMENT_ID, documentId)
            .hasNext());
  }

  @Test
  public void testDocumentGraphConsumerCanCopeWithSameEntityExternalId()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException,
          URISyntaxException {

    properties.setProperty(
        TinkerGraph.GREMLIN_TINKERGRAPH_DEFAULT_VERTEX_PROPERTY_CARDINALITY,
        VertexProperty.Cardinality.list.name());
    writeProperties();

    Person p1 = Annotations.createPerson(jCas, 0, 4, "test");
    Person p2 = Annotations.createPerson(jCas, 0, 4, "test");

    assertEquals(p1.getExternalId(), p2.getExternalId());

    processJCas(
        DocumentGraphConsumer.PARAM_GRAPH_CONFIG,
        propertiesFile.getAbsolutePath(),
        DocumentGraphConsumer.PARAM_OUTPUT_RELATIONS_AS_LINKS,
        true);
    Graph graph = GraphFactory.open(propertiesFile.getAbsolutePath());

    assertTrue(graph.traversal().V(p1.getExternalId()).hasNext());
  }

  @Test
  public void testDocumentGraphConsumerCanCopeWithSameReferenceTargetExternalId()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException,
          URISyntaxException {

    properties.setProperty(
        TinkerGraph.GREMLIN_TINKERGRAPH_DEFAULT_VERTEX_PROPERTY_CARDINALITY,
        VertexProperty.Cardinality.list.name());
    writeProperties();

    Person p1 = Annotations.createPerson(jCas, 0, 4, "test");
    Person p2 = Annotations.createPerson(jCas, 0, 4, "test");
    Annotations.createReferenceTarget(jCas, p1, p2);
    Annotations.createReferenceTarget(jCas, p1, p2);

    String externalId = ConsumerUtils.getExternalId(ImmutableList.of(p1, p2));

    processJCas(
        DocumentGraphConsumer.PARAM_GRAPH_CONFIG,
        propertiesFile.getAbsolutePath(),
        DocumentGraphConsumer.PARAM_OUTPUT_RELATIONS_AS_LINKS,
        true,
        DocumentGraphConsumer.PARAM_OUTPUT_REFERENTS,
        true);
    Graph graph = GraphFactory.open(propertiesFile.getAbsolutePath());

    assertTrue(graph.traversal().V(externalId).hasNext());
  }

  @Test
  public void testDocumentGraphConsumerCanCopeWithSameEventExternalId()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException,
          URISyntaxException {

    properties.setProperty(
        TinkerGraph.GREMLIN_TINKERGRAPH_DEFAULT_VERTEX_PROPERTY_CARDINALITY,
        VertexProperty.Cardinality.list.name());
    writeProperties();

    Person p1 = Annotations.createPerson(jCas, 0, 4, "test");
    Person p2 = Annotations.createPerson(jCas, 0, 4, "test");

    Event e1 = new Event(jCas);
    e1.setBegin(0);
    e1.setEnd(4);
    e1.setValue("test");
    e1.setEntities(new FSArray(jCas, 2));
    e1.setEntities(0, p1);
    e1.setEntities(1, p2);
    e1.addToIndexes(jCas);

    Event e2 = new Event(jCas);
    e2.setBegin(0);
    e2.setEnd(4);
    e2.setValue("test");
    e2.setEntities(new FSArray(jCas, 2));
    e2.setEntities(0, p1);
    e2.setEntities(1, p2);
    e2.addToIndexes(jCas);

    assertEquals(e1.getExternalId(), e2.getExternalId());

    processJCas(
        DocumentGraphConsumer.PARAM_GRAPH_CONFIG,
        propertiesFile.getAbsolutePath(),
        DocumentGraphConsumer.PARAM_OUTPUT_RELATIONS_AS_LINKS,
        true,
        DocumentGraphConsumer.PARAM_OUTPUT_EVENTS,
        true);
    Graph graph = GraphFactory.open(propertiesFile.getAbsolutePath());

    assertTrue(graph.traversal().V(e1.getExternalId()).hasNext());
  }

  @Test
  public void testDocumentGraphConsumerCanCopeWithSameRelationExternalId()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException,
          URISyntaxException {

    properties.setProperty(
        TinkerGraph.GREMLIN_TINKERGRAPH_DEFAULT_VERTEX_PROPERTY_CARDINALITY,
        VertexProperty.Cardinality.list.name());
    writeProperties();

    Person p1 = Annotations.createPerson(jCas, 0, 10, "source");
    Person p2 = Annotations.createPerson(jCas, 10, 20, "target");

    Relation r1 = new Relation(jCas);
    r1.setBegin(0);
    r1.setEnd(4);
    r1.setValue("test");
    r1.setSource(p1);
    r1.setTarget(p2);
    r1.addToIndexes(jCas);

    Relation r2 = new Relation(jCas);
    r2.setBegin(0);
    r2.setEnd(4);
    r2.setValue("test");
    r2.setSource(p1);
    r2.setTarget(p2);
    r2.addToIndexes(jCas);

    assertEquals(r1.getExternalId(), r2.getExternalId());

    processJCas(
        DocumentGraphConsumer.PARAM_GRAPH_CONFIG,
        propertiesFile.getAbsolutePath(),
        DocumentGraphConsumer.PARAM_OUTPUT_RELATIONS_AS_LINKS,
        true);
    Graph graph = GraphFactory.open(propertiesFile.getAbsolutePath());

    assertTrue(graph.traversal().E(r1.getExternalId()).hasNext());
  }

  @Test
  public void testDocumentGraphConsumerRelationAsLinkCanCopeWithSameRelationExternalId()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException,
          URISyntaxException {

    properties.setProperty(
        TinkerGraph.GREMLIN_TINKERGRAPH_DEFAULT_VERTEX_PROPERTY_CARDINALITY,
        VertexProperty.Cardinality.list.name());
    writeProperties();

    Person p1 = Annotations.createPerson(jCas, 0, 10, "source");
    Person p2 = Annotations.createPerson(jCas, 10, 20, "target");

    Relation r1 = new Relation(jCas);
    r1.setBegin(0);
    r1.setEnd(4);
    r1.setValue("test");
    r1.setSource(p1);
    r1.setTarget(p2);
    r1.addToIndexes(jCas);

    Relation r2 = new Relation(jCas);
    r2.setBegin(0);
    r2.setEnd(4);
    r2.setValue("test");
    r2.setSource(p1);
    r2.setTarget(p2);
    r2.addToIndexes(jCas);

    assertEquals(r1.getExternalId(), r2.getExternalId());

    processJCas(
        DocumentGraphConsumer.PARAM_GRAPH_CONFIG,
        propertiesFile.getAbsolutePath(),
        DocumentGraphConsumer.PARAM_OUTPUT_RELATIONS_AS_LINKS,
        false);
    Graph graph = GraphFactory.open(propertiesFile.getAbsolutePath());

    assertTrue(graph.traversal().V(r1.getExternalId()).hasNext());
  }
}
