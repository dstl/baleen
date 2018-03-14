// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.orderers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.jcas.tcas.Annotation;
import org.junit.Test;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

import uk.gov.dstl.baleen.types.common.Buzzword;
import uk.gov.dstl.baleen.types.common.Organisation;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.baleen.types.semantic.Location;

public class DependencyGraphTest {
  @Test
  public void testOverlap() {
    Set<Class<? extends Annotation>> a = new HashSet<>();
    Set<Class<? extends Annotation>> b = new HashSet<>();
    Set<Class<? extends Annotation>> c = new HashSet<>();
    Set<Class<? extends Annotation>> d = new HashSet<>();

    a.add(Person.class);
    a.add(Organisation.class);
    b.add(Location.class);
    b.add(Buzzword.class);
    c.add(Coordinate.class);
    d.add(Location.class);

    assertFalse(DependencyGraph.overlaps(a, b));
    assertFalse(DependencyGraph.overlaps(a, c));
    assertTrue(DependencyGraph.overlaps(b, c));
    assertTrue(DependencyGraph.overlaps(b, d));
  }

  @Test
  public void testRemoveLoops() {
    Graph<String, String> graph = new SparseMultigraph<>();
    graph.addVertex("v1");
    graph.addVertex("v2");
    graph.addVertex("v3");

    graph.addEdge("e1", "v1", "v2", EdgeType.DIRECTED);
    graph.addEdge("e2", "v2", "v1", EdgeType.DIRECTED);
    graph.addEdge("e3", "v1", "v3", EdgeType.DIRECTED);

    DependencyGraph.removeLoops(graph);

    assertEquals(3, graph.getVertexCount());
    assertEquals(2, graph.getEdgeCount());

    assertTrue(graph.getEdges().contains("e1"));
    assertTrue(graph.getEdges().contains("e3"));
  }

  @Test
  public void testRemoveLayer() {
    Graph<String, String> graph = new SparseMultigraph<>();
    graph.addVertex("v1");
    graph.addVertex("v2");
    graph.addVertex("v3");

    graph.addEdge("e1", "v1", "v2", EdgeType.DIRECTED);
    graph.addEdge("e2", "v2", "v1", EdgeType.DIRECTED);
    graph.addEdge("e3", "v3", "v2", EdgeType.DIRECTED);

    DependencyGraph.removeLayer(graph);

    assertEquals(2, graph.getVertexCount());
    assertEquals(2, graph.getEdgeCount());

    assertTrue(graph.getVertices().contains("v1"));
    assertTrue(graph.getVertices().contains("v2"));

    assertTrue(graph.getEdges().contains("e1"));
    assertTrue(graph.getEdges().contains("e2"));
  }
}
