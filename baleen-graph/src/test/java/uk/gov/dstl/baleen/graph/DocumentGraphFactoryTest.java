// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.DOCUMENT;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.EVENT;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.FIELD_CONTENT;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.FIELD_DOCUMENT_CAVEATS;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.FIELD_DOCUMENT_CLASSIFICATION;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.FIELD_DOCUMENT_ID;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.FIELD_DOCUMENT_LANGUAGE;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.FIELD_DOCUMENT_RELEASABILITY;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.FIELD_DOCUMENT_SOURCE;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.FIELD_DOCUMENT_TIMESTAMP;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.FIELD_DOCUMENT_TYPE;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.FIELD_METADATA;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.FIELD_PUBLISHEDIDS;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.FIELD_PUBLISHEDIDS_ID;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.FIELD_PUBLISHEDIDS_TYPE;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.MENTION;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.MENTION_IN;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.MENTION_OF;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.PARTICIPANT_IN;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.REFERENCE_TARGET;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.RELATION;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.SOURCE;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.TARGET;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Graph.Variables;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.junit.Test;

import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Event;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.UimaMonitor;
import uk.gov.dstl.baleen.uima.UimaSupport;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;
import uk.gov.dstl.baleen.uima.utils.UimaTypesUtils;

public class DocumentGraphFactoryTest {

  private DocumentGraphFactory createfactory(DocumentGraphOptions options) {
    UimaMonitor monitor = new UimaMonitor("test", DocumentGraphFactoryTest.class);
    return new DocumentGraphFactory(monitor, options);
  }

  private void assertNoDocumentNode(Graph graph) {
    assertEquals(0, graph.traversal().V().hasLabel(DOCUMENT).count().next().intValue());
    assertEquals(0, graph.traversal().E().hasLabel(MENTION_IN).count().next().intValue());
  }

  private void assertRelationsNotRerified(Graph graph) {
    assertEquals(0, graph.traversal().E().hasLabel(SOURCE).count().next().intValue());
    assertEquals(0, graph.traversal().E().hasLabel(TARGET).count().next().intValue());
    assertEquals(0, graph.traversal().V().hasLabel(RELATION).count().next().intValue());
  }

  private void assertNoRelationEdges(Graph graph) {
    assertEquals(0, graph.traversal().E().hasLabel(RELATION).count().next().intValue());
  }

  @Test
  public void testDocumentGraphCreatesVerticiesAndEdgesFromJCas() throws UIMAException {

    DocumentGraphOptions options = DocumentGraphOptions.builder().build();
    DocumentGraphFactory factory = createfactory(options);

    JCas jCas = JCasFactory.createJCas();
    JCasTestGraphUtil.populateJcas(jCas);

    Graph graph = factory.create(jCas);

    assertEquals(3, graph.traversal().V().hasLabel(REFERENCE_TARGET).count().next().intValue());
    assertEquals(1, graph.traversal().V().hasLabel(EVENT).count().next().intValue());
    assertEquals(4, graph.traversal().V().hasLabel(MENTION).count().next().intValue());
    assertEquals(2, graph.traversal().V().hasLabel(RELATION).count().next().intValue());
    assertEquals(4, graph.traversal().E().hasLabel(MENTION_OF).count().next().intValue());
    assertEquals(2, graph.traversal().E().hasLabel(PARTICIPANT_IN).count().next().intValue());

    assertNoDocumentNode(graph);
    assertNoRelationEdges(graph);

    assertEquals(10, IteratorUtils.count(graph.vertices()));
    assertEquals(10, IteratorUtils.count(graph.edges()));
  }

  @Test
  public void testDocumentGraphWithoutRelations() throws UIMAException {

    DocumentGraphOptions options = DocumentGraphOptions.builder().withRelations(false).build();
    DocumentGraphFactory factory = createfactory(options);

    JCas jCas = JCasFactory.createJCas();
    JCasTestGraphUtil.populateJcas(jCas);

    Graph graph = factory.create(jCas);

    assertEquals(3, graph.traversal().V().hasLabel(REFERENCE_TARGET).count().next().intValue());
    assertEquals(1, graph.traversal().V().hasLabel(EVENT).count().next().intValue());
    assertEquals(4, graph.traversal().V().hasLabel(MENTION).count().next().intValue());
    assertEquals(4, graph.traversal().E().hasLabel(MENTION_OF).count().next().intValue());
    assertEquals(0, graph.traversal().E().hasLabel(RELATION).count().next().intValue());
    assertEquals(2, graph.traversal().E().hasLabel(PARTICIPANT_IN).count().next().intValue());

    assertNoDocumentNode(graph);
    assertRelationsNotRerified(graph);

    assertEquals(8, IteratorUtils.count(graph.vertices()));
    assertEquals(6, IteratorUtils.count(graph.edges()));
  }

  @Test
  public void testDocumentGraphWithoutReferents() throws UIMAException {

    DocumentGraphOptions options =
        DocumentGraphOptions.builder().withReferenceTargets(false).build();
    DocumentGraphFactory factory = createfactory(options);

    JCas jCas = JCasFactory.createJCas();
    JCasTestGraphUtil.populateJcas(jCas);

    Graph graph = factory.create(jCas);

    assertEquals(0, graph.traversal().V().hasLabel(REFERENCE_TARGET).count().next().intValue());
    assertEquals(1, graph.traversal().V().hasLabel(EVENT).count().next().intValue());
    assertEquals(4, graph.traversal().V().hasLabel(MENTION).count().next().intValue());
    assertEquals(2, graph.traversal().V().hasLabel(RELATION).count().next().intValue());
    assertEquals(0, graph.traversal().E().hasLabel(MENTION_OF).count().next().intValue());
    assertEquals(2, graph.traversal().E().hasLabel(PARTICIPANT_IN).count().next().intValue());

    assertNoDocumentNode(graph);

    assertEquals(7, IteratorUtils.count(graph.vertices()));
    assertEquals(6, IteratorUtils.count(graph.edges()));
  }

  @Test
  public void testDocumentGraphWithDocument() throws UIMAException {

    DocumentGraphOptions options = DocumentGraphOptions.builder().withDocument(true).build();
    DocumentGraphFactory factory = createfactory(options);

    JCas jCas = JCasFactory.createJCas();
    JCasTestGraphUtil.populateJcas(jCas);

    Graph graph = factory.create(jCas);

    assertEquals(1, graph.traversal().V().hasLabel(DOCUMENT).count().next().intValue());
    assertEquals(10, graph.traversal().E().hasLabel(MENTION_IN).count().next().intValue());
    assertEquals(2, graph.traversal().V().hasLabel(RELATION).count().next().intValue());
    assertEquals(2, graph.traversal().E().hasLabel(SOURCE).count().next().intValue());
    assertEquals(2, graph.traversal().E().hasLabel(TARGET).count().next().intValue());

    assertEquals(3, graph.traversal().V().hasLabel(REFERENCE_TARGET).count().next().intValue());
    assertEquals(1, graph.traversal().V().hasLabel(EVENT).count().next().intValue());
    assertEquals(4, graph.traversal().V().hasLabel(MENTION).count().next().intValue());
    assertEquals(4, graph.traversal().E().hasLabel(MENTION_OF).count().next().intValue());
    assertEquals(0, graph.traversal().E().hasLabel(RELATION).count().next().intValue());
    assertEquals(2, graph.traversal().E().hasLabel(PARTICIPANT_IN).count().next().intValue());

    assertEquals(11, IteratorUtils.count(graph.vertices()));
    assertEquals(20, IteratorUtils.count(graph.edges()));

    DocumentAnnotation da = UimaSupport.getDocumentAnnotation(jCas);
    String documentId = ConsumerUtils.getExternalId(da, false);

    Vertex documentVert = graph.traversal().V(documentId).next();
    Map<String, Object> properties = new HashMap<>();
    documentVert.properties().forEachRemaining(vp -> properties.put(vp.key(), vp.value()));

    assertMetadata(jCas, properties);
  }

  @Test
  public void testDocumentGraphWithoutEvents() throws UIMAException {

    DocumentGraphOptions options = DocumentGraphOptions.builder().withEvents(false).build();
    DocumentGraphFactory factory = createfactory(options);

    JCas jCas = JCasFactory.createJCas();
    JCasTestGraphUtil.populateJcas(jCas);
    Graph graph = factory.create(jCas);

    assertEquals(3, graph.traversal().V().hasLabel(REFERENCE_TARGET).count().next().intValue());
    assertEquals(0, graph.traversal().V().hasLabel(EVENT).count().next().intValue());
    assertEquals(4, graph.traversal().V().hasLabel(MENTION).count().next().intValue());
    assertEquals(2, graph.traversal().V().hasLabel(RELATION).count().next().intValue());
    assertEquals(4, graph.traversal().E().hasLabel(MENTION_OF).count().next().intValue());
    assertEquals(0, graph.traversal().E().hasLabel(PARTICIPANT_IN).count().next().intValue());

    assertNoDocumentNode(graph);
    assertNoRelationEdges(graph);

    assertEquals(9, IteratorUtils.count(graph.vertices()));
    assertEquals(8, IteratorUtils.count(graph.edges()));
  }

  @Test
  public void testDocumentGraphWithTypeFiltering() throws UIMAException {

    Set<Class<? extends Entity>> typeClasses =
        TypeUtils.getTypeClasses(Entity.class, Person.class.getSimpleName());
    DocumentGraphOptions options =
        DocumentGraphOptions.builder().withTypeClasses(typeClasses).build();
    DocumentGraphFactory factory = createfactory(options);

    JCas jCas = JCasFactory.createJCas();
    JCasTestGraphUtil.populateJcas(jCas);
    Graph graph = factory.create(jCas);

    assertEquals(2, graph.traversal().V().hasLabel(REFERENCE_TARGET).count().next().intValue());
    assertEquals(1, graph.traversal().V().hasLabel(EVENT).count().next().intValue());
    assertEquals(3, graph.traversal().V().hasLabel(MENTION).count().next().intValue());
    assertEquals(1, graph.traversal().V().hasLabel(RELATION).count().next().intValue());
    assertEquals(3, graph.traversal().E().hasLabel(MENTION_OF).count().next().intValue());
    assertEquals(2, graph.traversal().E().hasLabel(PARTICIPANT_IN).count().next().intValue());

    assertNoDocumentNode(graph);
    assertNoRelationEdges(graph);

    assertEquals(7, IteratorUtils.count(graph.vertices()));
    assertEquals(7, IteratorUtils.count(graph.edges()));
  }

  @Test
  public void testDocumentGraphHasCorrectPropertiesFromJCas() throws UIMAException {

    DocumentGraphOptions options = DocumentGraphOptions.builder().build();
    DocumentGraphFactory factory = createfactory(options);

    JCas jCas = JCasFactory.createJCas();
    JCasTestGraphUtil.populateJcas(jCas);

    Graph graph = factory.create(jCas);

    String documentId =
        ConsumerUtils.getExternalId(
            UimaSupport.getDocumentAnnotation(jCas), options.isContentHashAsId());

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
                        .V(r.getExternalId())
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
  public void testDocumentGraphCreatesMetadataFromJCas() throws UIMAException {

    DocumentGraphOptions options = DocumentGraphOptions.builder().withMeta(true).build();
    DocumentGraphFactory factory = createfactory(options);

    JCas jCas = JCasFactory.createJCas();
    JCasTestGraphUtil.populateJcas(jCas);

    Graph graph = factory.create(jCas);

    assertMetadata(jCas, graph.variables().asMap());
  }

  @SuppressWarnings("unchecked")
  private void assertMetadata(JCas jCas, Map<String, Object> variables) {
    DocumentAnnotation da = UimaSupport.getDocumentAnnotation(jCas);
    String documentId = ConsumerUtils.getExternalId(da, false);

    assertEquals(da.getDocType(), variables.get(FIELD_DOCUMENT_TYPE));
    assertEquals(da.getSourceUri(), variables.get(FIELD_DOCUMENT_SOURCE));
    assertEquals(da.getLanguage(), variables.get(FIELD_DOCUMENT_LANGUAGE));
    assertEquals(new Date(da.getTimestamp()), variables.get(FIELD_DOCUMENT_TIMESTAMP));

    assertEquals(da.getDocumentClassification(), variables.get(FIELD_DOCUMENT_CLASSIFICATION));
    assertEquals(
        UimaTypesUtils.toList(da.getDocumentCaveats()), variables.get(FIELD_DOCUMENT_CAVEATS));

    assertFalse(variables.containsKey(FIELD_DOCUMENT_RELEASABILITY));

    Map<String, String> publishedId =
        ((List<Map<String, String>>) variables.get(FIELD_PUBLISHEDIDS)).get(0);
    assertEquals("12", publishedId.get(FIELD_PUBLISHEDIDS_ID));
    assertEquals("test", publishedId.get(FIELD_PUBLISHEDIDS_TYPE));

    Map<String, Collection<Object>> meta =
        (Map<String, Collection<Object>>) variables.get(FIELD_METADATA);
    assertTrue(meta.get("test").contains("1"));
    assertTrue(meta.get("test").contains("2"));
    assertEquals(2, meta.get("test").size());

    assertNull(variables.get(FIELD_CONTENT));
    assertEquals(documentId, variables.get("externalId"));
  }

  @Test
  public void testDocumentGraphMetadataHasContentFromJCas() throws UIMAException {
    DocumentGraphOptions options = DocumentGraphOptions.builder().withContent(true).build();
    DocumentGraphFactory factory = createfactory(options);

    JCas jCas = JCasFactory.createJCas();
    JCasTestGraphUtil.populateJcas(jCas);

    Graph graph = factory.create(jCas);

    Variables variables = graph.variables();
    assertEquals(JCasTestGraphUtil.CONTENT, variables.get(FIELD_CONTENT).orElse(""));
  }

  @Test
  public void testDocumentGraphWithRelationsAsLinks() throws UIMAException {

    DocumentGraphOptions options =
        DocumentGraphOptions.builder().withRelationsAsLinks(true).build();
    DocumentGraphFactory factory = createfactory(options);

    JCas jCas = JCasFactory.createJCas();
    JCasTestGraphUtil.populateJcas(jCas);

    Graph graph = factory.create(jCas);

    assertEquals(3, graph.traversal().V().hasLabel(REFERENCE_TARGET).count().next().intValue());
    assertEquals(1, graph.traversal().V().hasLabel(EVENT).count().next().intValue());
    assertEquals(4, graph.traversal().V().hasLabel(MENTION).count().next().intValue());
    assertEquals(4, graph.traversal().E().hasLabel(MENTION_OF).count().next().intValue());
    assertEquals(2, graph.traversal().E().hasLabel(RELATION).count().next().intValue());
    assertEquals(2, graph.traversal().E().hasLabel(PARTICIPANT_IN).count().next().intValue());

    assertNoDocumentNode(graph);
    assertRelationsNotRerified(graph);

    assertEquals(8, IteratorUtils.count(graph.vertices()));
    assertEquals(8, IteratorUtils.count(graph.edges()));
  }
}
