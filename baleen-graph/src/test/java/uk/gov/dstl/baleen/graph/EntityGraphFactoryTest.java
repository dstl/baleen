// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.graph;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.has;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.ENTITY;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.EVENT;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.PARTICIPANT_IN;
import static uk.gov.dstl.baleen.graph.DocumentGraphFactory.RELATION;
import static uk.gov.dstl.baleen.graph.EntityGraphFactory.LONGEST_VALUE;
import static uk.gov.dstl.baleen.graph.EntityGraphFactory.MENTIONS_PROPERTY;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import uk.gov.dstl.baleen.consumers.utils.ConsumerUtils;
import uk.gov.dstl.baleen.graph.value.Longest;
import uk.gov.dstl.baleen.graph.value.Mode;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Event;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.UimaMonitor;
import uk.gov.dstl.baleen.uima.UimaSupport;
import uk.gov.dstl.baleen.uima.utils.ReferentUtils;

public class EntityGraphFactoryTest {

  private EntityGraphFactory createfactory(EntityGraphOptions options) {
    UimaMonitor monitor = new UimaMonitor("test", EntityGraphFactoryTest.class);
    return new EntityGraphFactory(monitor, options);
  }

  @Test
  public void testMultiGraphTransform() throws UIMAException {

    EntityGraphOptions options =
        EntityGraphOptions.builder().withMultiValueProperties(true).build();

    EntityGraphFactory factory = createfactory(options);

    JCas jCas = JCasFactory.createJCas();
    JCasTestGraphUtil.populateJcas(jCas);

    Graph graph = factory.create(jCas);

    String documentId =
        ConsumerUtils.getExternalId(
            UimaSupport.getDocumentAnnotation(jCas), options.isContentHashAsId());

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
  public void testSingleGraphTransform() throws UIMAException {

    EntityGraphOptions options =
        EntityGraphOptions.builder()
            .withValueStrategy("value", new Longest())
            .withValueStrategy("gender", new Mode<String>())
            .build();
    EntityGraphFactory factory = createfactory(options);

    JCas jCas = JCasFactory.createJCas();
    JCasTestGraphUtil.populateJcas(jCas);

    Graph graph = factory.create(jCas);
    String documentId =
        ConsumerUtils.getExternalId(
            UimaSupport.getDocumentAnnotation(jCas), options.isContentHashAsId());

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
            .has(LONGEST_VALUE, "John Smith")
            .has(DocumentGraphFactory.FIELD_DOCUMENT_ID, ImmutableList.of(documentId, documentId))
            .hasNext());
  }

  @Test
  public void testNoEvents() throws UIMAException {

    EntityGraphOptions options = EntityGraphOptions.builder().withEvents(false).build();
    EntityGraphFactory factory = createfactory(options);

    JCas jCas = JCasFactory.createJCas();
    JCasTestGraphUtil.populateJcas(jCas);

    Graph graph = factory.create(jCas);

    final GraphTraversalSource traversal = graph.traversal();

    assertEquals(3, traversal.V().hasLabel(ENTITY).count().next().intValue());
    assertEquals(0, traversal.V().hasLabel(EVENT).count().next().intValue());
    assertEquals(0, traversal.E().hasLabel(PARTICIPANT_IN).count().next().intValue());
    assertEquals(2, traversal.E().hasLabel(RELATION).count().next().intValue());

    assertEquals(3, IteratorUtils.count(graph.vertices()));
    assertEquals(2, IteratorUtils.count(graph.edges()));
  }

  @Test
  public void testStopFetures() throws UIMAException {

    EntityGraphOptions options = EntityGraphOptions.builder().withStopFeatures("gender").build();
    EntityGraphFactory factory = createfactory(options);

    JCas jCas = JCasFactory.createJCas();
    JCasTestGraphUtil.populateJcas(jCas);

    Graph graph = factory.create(jCas);

    final GraphTraversalSource traversal = graph.traversal();

    assertTrue(traversal.V().not(has("gender")).hasNext());
  }

  @Test
  public void testValueCoerser() throws UIMAException {

    EntityGraphOptions options = EntityGraphOptions.builder().withValueCoercer((v) -> 1).build();
    EntityGraphFactory factory = createfactory(options);

    JCas jCas = JCasFactory.createJCas();
    JCasTestGraphUtil.populateJcas(jCas);

    Graph graph = factory.create(jCas);

    final GraphTraversalSource traversal = graph.traversal();

    traversal
        .V()
        .forEachRemaining(
            v -> {
              Set<Object> values =
                  ElementHelper.propertyValueMap(v).values().stream().collect(Collectors.toSet());
              assertAllValuesCoerceTo1(v, values);
            });

    traversal
        .E()
        .forEachRemaining(
            v -> {
              Set<Object> values =
                  ElementHelper.propertyValueMap(v).values().stream().collect(Collectors.toSet());
              assertTrue(values.contains(1) && values.size() == 1 || values.size() == 0);
            });
  }

  private void assertAllValuesCoerceTo1(Vertex v, Set<Object> values) {
    assertTrue(v.toString(), values.contains(1));
    assertEquals(ElementHelper.propertyValueMap(v).toString(), 1, values.size());
  }
}
