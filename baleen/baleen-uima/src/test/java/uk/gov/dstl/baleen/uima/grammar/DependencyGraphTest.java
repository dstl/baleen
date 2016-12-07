package uk.gov.dstl.baleen.uima.grammar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Set;

import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.types.language.Dependency;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;

public class DependencyGraphTest {

	private JCas jCas;
	private Dependency dText;
	private Dependency dOf;
	private Dependency dSample;
	private Dependency dA;
	private WordToken a;
	private WordToken sample;
	private WordToken of;
	private WordToken text;

	@Before
	public void setUp() throws Exception {
		jCas = JCasFactory.createJCas();
		jCas.setDocumentText("A sample of text.");

		// Note this dependency grammar is not accurate!

		a = new WordToken(jCas, 0, 1);
		a.addToIndexes();
		sample = new WordToken(jCas, 2, 8);
		sample.addToIndexes();
		of = new WordToken(jCas, 9, 11);
		of.addToIndexes();
		text = new WordToken(jCas, 12, 16);
		text.addToIndexes();

		dA = new Dependency(jCas, 0, 1);
		dA.setDependent(a);
		dA.setGovernor(sample);
		dA.addToIndexes();
		dSample = new Dependency(jCas, 2, 8);
		dSample.setGovernor(sample);
		dSample.setDependent(sample);
		dSample.setDependencyType("ROOT");
		dSample.addToIndexes();
		dOf = new Dependency(jCas, 9, 11);
		dOf.setGovernor(text);
		dOf.setDependent(of);
		dOf.addToIndexes();
		dText = new Dependency(jCas, 12, 16);
		dText.setGovernor(sample);
		dText.setDependent(text);
		dText.addToIndexes();

	}

	@Test
	public void testExtractWordsMultiHop() {
		final DependencyGraph graph = DependencyGraph.build(jCas);

		final Set<WordToken> fromDependencies = graph.extractWords(3, dA);
		Assert.assertEquals(4, fromDependencies.size());
	}

	@Test
	public void testExtractWordsNone() {
		final DependencyGraph graph = DependencyGraph.build(jCas);

		final Set<WordToken> fromDependencies = graph.extractWords(-1, dA);
		Assert.assertEquals(0, fromDependencies.size());

		final Set<WordToken> fromWords = graph.nearestWords(-1, a);
		Assert.assertEquals(0, fromWords.size());
	}

	@Test
	public void testExtractWordsMissingWord() {
		final DependencyGraph graph = DependencyGraph.build(jCas);

		final Set<WordToken> fromWords = graph.nearestWords(10, new WordToken(jCas));
		// We include the word itself (even though its not in...?)
		Assert.assertEquals(1, fromWords.size());
	}

	@Test
	public void testExtractWordsArray() {
		final DependencyGraph graph = DependencyGraph.build(jCas);

		final Set<WordToken> fromDependencies = graph.extractWords(1, dOf);
		Assert.assertEquals(2, fromDependencies.size());
		Assert.assertTrue(fromDependencies.contains(of));
		Assert.assertTrue(fromDependencies.contains(text));

		final Set<WordToken> fromWords = graph.nearestWords(1, sample);
		Assert.assertEquals(3, fromWords.size());
		Assert.assertFalse(fromWords.contains(of));

		final Set<WordToken> fromTwo = graph.nearestWords(1, sample, of);
		Assert.assertEquals(4, fromTwo.size());
	}

	@Test
	public void testBuild() {
		final DependencyGraph graph = DependencyGraph.build(jCas);
		Assert.assertNotNull(graph);
		graph.log();

		assertEquals(4, graph.getWords().size());
		assertEquals(1, graph.getDependents(a).size());
		assertEquals(1, graph.getEdges(a).count());
		assertEquals(0, graph.getGovernors(a).size());

		// 0 as the root is not included in the graph
		assertEquals(0, graph.getDependents(sample).size());
		assertEquals(2, graph.getEdges(sample).count());
		assertEquals(2, graph.getGovernors(sample).size());

	}

	@Test
	public void testFilter() {
		final DependencyGraph graph = DependencyGraph.build(jCas);

		final DependencyGraph subgraph = graph
				.filter(p -> p == a || p == sample);

		subgraph.log();

		assertEquals(2, subgraph.getWords().size());
		assertEquals(1, subgraph.getGovernors(sample).size());

	}

	@Test
	public void testBuildCovered() {
		// Create a fake sub-sentence
		final Sentence s = new Sentence(jCas);
		s.setBegin(0);
		s.setEnd(sample.getEnd());

		final DependencyGraph graph = DependencyGraph.build(jCas, s);
		Assert.assertNotNull(graph);
		graph.log();

		assertEquals(2, graph.getWords().size());
		assertEquals(1, graph.getDependents(a).size());
		assertEquals(1, graph.getEdges(a).count());
		assertEquals(0, graph.getGovernors(a).size());

		// 0 as the root is not included in the graph
		assertEquals(0, graph.getDependents(sample).size());
		assertEquals(1, graph.getEdges(sample).count());
		assertEquals(1, graph.getGovernors(sample).size());

	}

	@Test
	public void testShortestPath() {
		final DependencyGraph graph = DependencyGraph.build(jCas);

		assertTrue(graph.shortestPath(Collections.singletonList(a), Collections.singletonList(of), 1).isEmpty());
		assertTrue(graph.shortestPath(Collections.singletonList(a), Collections.singletonList(of), 2).isEmpty());
		assertFalse(graph.shortestPath(Collections.singletonList(a), Collections.singletonList(of), 5).isEmpty());

	}

	int traverseCount = 0;

	@Test
	public void testTravese() {
		final DependencyGraph graph = DependencyGraph.build(jCas);

		graph.traverse(1, Collections.singletonList(dA), (d, f, t, h) -> {
			traverseCount++;
			return true;
		});

		assertEquals(1, traverseCount);
	}

	@Test
	public void testTraveseWithTerminate() {
		final DependencyGraph graph = DependencyGraph.build(jCas);
		traverseCount = 0;

		graph.traverse(10, Collections.singletonList(dA), (d, f, t, h) -> {
			traverseCount++;
			return false;
		});

		// 1 as go in both direction (dA a -> sample)
		assertEquals(1, traverseCount);
	}

	@Test
	public void testTraveseMultiple() {
		final DependencyGraph graph = DependencyGraph.build(jCas);
		traverseCount = 0;

		graph.traverse(10, Collections.singletonList(dA), (d, f, t, h) -> {
			// TODO: Ideally test the content so that the history is correct, but its hard to
			// predict

			traverseCount++;
			return true;
		});

		assertEquals(4, traverseCount);

	}
}
