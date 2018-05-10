// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.consumers.graph;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.has;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.ENTITY;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.EVENT;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.PARTICIPANT_IN;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.RELATION;
import static uk.gov.dstl.baleen.graph.EntityGraphFactory.MENTIONS_PROPERTY;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.graph.DocumentGraphFactory;
import uk.gov.dstl.baleen.graph.GraphFormat;
import uk.gov.dstl.baleen.graph.JCasTestGraphUtil;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Event;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.utils.ReferentUtils;

public class EntityGraphConsumerTest extends AbstractAnnotatorTest {

  private Path tempDirectory;
  private File graphFile;
  private File propertiesFile;
  private Properties properties;

  public EntityGraphConsumerTest() {
    super(EntityGraphConsumer.class);
  }

  @Before
  public void setup() throws IOException {
    JCasTestGraphUtil.populateJcas(jCas);
    tempDirectory = Files.createTempDirectory(EntityGraphConsumerTest.class.getSimpleName());
    tempDirectory.toFile().deleteOnExit();

    graphFile = tempDirectory.resolve("testgraph.json").toFile();
    graphFile.deleteOnExit();

    propertiesFile = tempDirectory.resolve("graph.properties").toFile();
    propertiesFile.deleteOnExit();

    properties = new Properties();
    properties.setProperty(Graph.GRAPH, TinkerGraph.class.getName());
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
  public void testMultiGraphTransform()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException,
          URISyntaxException {

    properties.setProperty(
        TinkerGraph.GREMLIN_TINKERGRAPH_DEFAULT_VERTEX_PROPERTY_CARDINALITY,
        VertexProperty.Cardinality.list.name());
    writeProperties();

    processJCas(
        EntityGraphConsumer.PARAM_GRAPH_CONFIG,
        propertiesFile.getAbsolutePath(),
        EntityGraphConsumer.PARAM_MULTI_VALUE_PROPERTIES,
        true);
    Graph graph = GraphFactory.open(propertiesFile.getAbsolutePath());

    String documentId = ConsumerUtils.getExternalId(getDocumentAnnotation(jCas), false);

    final GraphTraversalSource traversal = graph.traversal();

    assertEquals(3, traversal.V().hasLabel(ENTITY).count().next().intValue());
    assertEquals(1, traversal.V().hasLabel(EVENT).count().next().intValue());
    assertEquals(2, traversal.E().hasLabel(PARTICIPANT_IN).count().next().intValue());
    assertEquals(2, traversal.E().hasLabel(RELATION).count().next().intValue());

    assertEquals(4, IteratorUtils.count(graph.vertices()));
    assertEquals(4, IteratorUtils.count(graph.edges()));

    Multimap<ReferenceTarget, Entity> targets =
        ReferentUtils.createReferentMap(jCas, Entity.class, false);
    targets
        .entries()
        .forEach(
            e ->
                assertTrue(
                    traversal
                        .V()
                        .hasLabel(ENTITY)
                        .has("type")
                        .has(MENTIONS_PROPERTY)
                        .has("value")
                        .not(has("begin"))
                        .not(has("end"))
                        .has(DocumentGraphFactory.FIELD_DOCUMENT_ID, documentId)
                        .hasNext()));

    Event event = JCasUtil.selectSingle(jCas, Event.class);
    assertTrue(
        traversal
            .V(event.getExternalId())
            .hasLabel(EVENT)
            .has("value", event.getValue())
            .not(has("begin"))
            .not(has("end"))
            .has(MENTIONS_PROPERTY)
            .has(DocumentGraphFactory.FIELD_DOCUMENT_ID, documentId)
            .hasNext());

    JCasUtil.select(jCas, Relation.class)
        .forEach(
            r ->
                assertTrue(
                    traversal
                        .E(r.getExternalId())
                        .hasLabel(RELATION)
                        .has("relationshipType", r.getRelationshipType())
                        .has("value", r.getValue())
                        .not(has("begin"))
                        .not(has("end"))
                        .has(MENTIONS_PROPERTY)
                        .has(DocumentGraphFactory.FIELD_DOCUMENT_ID, documentId)
                        .hasNext()));

    Map<String, Object> mention = new HashMap<>();
    mention.put("id", "cb7ba8e02c88dcdc832f181c1336ce54334f9bb125bd90371a6d59d098844f23");
    mention.put("begin", 25);
    mention.put("end", 35);
    mention.put("confidence", 0.9d);
    assertTrue(
        traversal
            .V()
            .has("value", "He")
            .has("value", "John Smith")
            .has(MENTIONS_PROPERTY, mention)
            .hasNext());
  }

  @Test
  public void testSingleGraphTransform()
      throws AnalysisEngineProcessException, ResourceInitializationException, IOException,
          URISyntaxException {

    properties.setProperty(
        TinkerGraph.GREMLIN_TINKERGRAPH_DEFAULT_VERTEX_PROPERTY_CARDINALITY,
        VertexProperty.Cardinality.single.name());
    writeProperties();

    processJCas(
        EntityGraphConsumer.PARAM_GRAPH_CONFIG,
        propertiesFile.getAbsolutePath(),
        EntityGraphConsumer.PARAM_VALUE_STRATEGY,
        new String[] {"value", "Longest", "gender", "Mode"});
    Graph graph = GraphFactory.open(propertiesFile.getAbsolutePath());

    String documentId = ConsumerUtils.getExternalId(getDocumentAnnotation(jCas), false);

    final GraphTraversalSource traversal = graph.traversal();

    assertEquals(3, traversal.V().hasLabel(ENTITY).count().next().intValue());
    assertEquals(1, traversal.V().hasLabel(EVENT).count().next().intValue());
    assertEquals(2, traversal.E().hasLabel(PARTICIPANT_IN).count().next().intValue());
    assertEquals(2, traversal.E().hasLabel(RELATION).count().next().intValue());

    assertEquals(4, IteratorUtils.count(graph.vertices()));
    assertEquals(4, IteratorUtils.count(graph.edges()));

    JCasUtil.select(jCas, Entity.class)
        .forEach(
            e ->
                assertTrue(
                    traversal
                        .V()
                        .hasLabel(ENTITY)
                        .has("type")
                        .has(MENTIONS_PROPERTY)
                        .has("value")
                        .not(has("begin"))
                        .not(has("end"))
                        .hasNext()));

    Event event = JCasUtil.selectSingle(jCas, Event.class);
    assertTrue(
        traversal
            .V(event.getExternalId())
            .hasLabel(EVENT)
            .has("value", event.getValue())
            .not(has("begin"))
            .not(has("end"))
            .has(MENTIONS_PROPERTY)
            .hasNext());

    JCasUtil.select(jCas, Relation.class)
        .forEach(
            r ->
                assertTrue(
                    traversal
                        .E(r.getExternalId())
                        .hasLabel(RELATION)
                        .has("relationshipType", r.getRelationshipType())
                        .has("value", r.getValue())
                        .not(has("begin"))
                        .not(has("end"))
                        .has(MENTIONS_PROPERTY)
                        .hasNext()));

    assertTrue(
        traversal
            .V()
            .has("gender", "Male")
            .has("value", "John Smith")
            .not(has("value", "He"))
            .has(DocumentGraphFactory.FIELD_DOCUMENT_ID, ImmutableList.of(documentId, documentId))
            .hasNext());
  }
}
