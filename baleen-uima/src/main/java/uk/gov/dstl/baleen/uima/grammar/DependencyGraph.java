// Dstl (c) Crown Copyright 2017
// Modified by Committed Software Copyright (c) 2018, opensource@committed.io
package uk.gov.dstl.baleen.uima.grammar;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.SetMultimap;

import uk.gov.dstl.baleen.types.language.Dependency;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;

/**
 * A graph of grammar dependencies within an annotated jCas.
 *
 * <p>Whilst UIMA annotations can store the output of a dependency grammar it is difficult to work
 * with and slow to query. This class builds a cache which means finding nearest neighbours (based
 * on dependency distance) faster and easier.
 *
 * <p>The JCAS must have been annotated by a dependency grammar (e.g. MaltParser, ClearNlp) before
 * passing to build().
 */
public class DependencyGraph {

  private static final Logger LOGGER = LoggerFactory.getLogger(DependencyGraph.class);

  private static final DependencyNode DUMMY_NODE = new DependencyNode("DUMMY", null, null);

  private static final List<String> ROOT_POS =
      ImmutableList.of("JJ", "RB", "NN", "PP", "IN", "V", "VB", "VV", "VH", "WP");

  private final SetMultimap<WordToken, Edge> edges;

  private final SetMultimap<String, WordToken> partOfSpeech;
  private final Map<WordToken, Dependency> dependents;
  private final SetMultimap<WordToken, Dependency> governors;

  /** Instantiates a new dependency graph. */
  private DependencyGraph() {
    edges = HashMultimap.create();
    dependents = new HashMap<>();
    governors = HashMultimap.create();
    partOfSpeech = HashMultimap.create();
  }

  /**
   * Instantiates a new dependency graph.
   *
   * @param edges the edges
   * @param dependentMap the dependent map
   * @param governorMap the governor map
   */
  private DependencyGraph(
      SetMultimap<WordToken, Edge> edges,
      Map<WordToken, Dependency> dependentMap,
      SetMultimap<WordToken, Dependency> governorMap,
      SetMultimap<String, WordToken> partOfSpeechMap) {
    this.edges = edges;
    dependents = dependentMap;
    governors = governorMap;
    partOfSpeech = partOfSpeechMap;
  }

  /**
   * Gets the dependencies where the word is the dependent.
   *
   * @param word the word
   * @return the dependents
   */
  public Dependency getDependency(WordToken word) {
    return dependents.get(word);
  }

  /**
   * Gets the dependencies where the word is the governor.
   *
   * @param word the word
   * @return the governors
   */
  public Set<Dependency> getGovernors(WordToken word) {
    return Collections.unmodifiableSet(governors.get(word));
  }

  /**
   * Gets the edges to/from this word.
   *
   * @param word the word
   * @return the edges
   */
  public Stream<WordToken> getEdges(WordToken word) {
    return edges.get(word).stream().map(e -> e.getOther(word));
  }

  /**
   * Adds the edge.
   *
   * @param dependency the dependency
   */
  private void addEdge(final Dependency dependency) {
    final WordToken governor = dependency.getGovernor();
    final WordToken dependent = dependency.getDependent();
    final Edge edge = new Edge(dependent, dependency, governor);
    edges.put(governor, edge);
    edges.put(dependent, edge);
    dependents.put(dependent, dependency);
    governors.put(governor, dependency);
    addPartOfSpeech(governor);
    addPartOfSpeech(dependent);
  }

  private void addPartOfSpeech(final WordToken wt) {
    String pos = wt.getPartOfSpeech();
    partOfSpeech.put(pos, wt);
    ROOT_POS.forEach(
        root -> {
          if (pos.startsWith(root)) {
            partOfSpeech.put(root, wt);
          }
        });
  }

  /**
   * Find the nearest neighbours within dependency distance links of the provided start
   * dependencies.
   *
   * @param distance the dependency distance
   * @param start array / of words to start from
   * @return the (set of) words within range
   */
  public Set<WordToken> extractWords(final int distance, final Dependency... start) {
    return extractWords(distance, d -> true, start);
  }

  /**
   * Find the nearest neighbours within dependency distance links of the provided start
   * dependencies.
   *
   * @param distance the dependency distance
   * @param predicate the predicate
   * @param start array / of words to start from
   * @return the (set of) words within range
   */
  public Set<WordToken> extractWords(
      final int distance, Predicate<Dependency> predicate, final Dependency... start) {
    return extractWords(distance, predicate, Arrays.asList(start));
  }

  /**
   * Find the nearest neighbours within dependency distance links of the provide start dependencies.
   *
   * @param distance the dependency distance
   * @param predicate the predicate
   * @param start the start words (as list)
   * @return the (set of) words within range
   */
  public Set<WordToken> extractWords(
      final int distance, Predicate<Dependency> predicate, final Collection<Dependency> start) {
    final Set<WordToken> words = new HashSet<>();

    if (distance <= 0) {
      return words;
    }

    final int governorDistance = distance - 1;
    for (final Dependency d : start) {
      if (governorDistance > 0) {
        extractWords(words, governorDistance, predicate, d.getGovernor());
      }
      extractWords(words, distance, predicate, d.getDependent());
    }

    return words;
  }

  /**
   * Find the nearest neighbours within dependency distance links of the provided start
   * dependencies.
   *
   * @param distance the dependency distance
   * @param start array / of words to start from
   * @return the (set of) words within range
   */
  public Set<WordToken> nearestWords(final int distance, final WordToken... start) {
    return nearestWords(distance, d -> true, Arrays.asList(start));
  }

  /**
   * Find the nearest neighbours within dependency distance links of the provided start
   * dependencies.
   *
   * @param distance the dependency distance
   * @param predicate the predicate
   * @param start array / of words to start from
   * @return the (set of) words within range
   */
  public Set<WordToken> nearestWords(
      final int distance, Predicate<Dependency> predicate, final WordToken... start) {
    return nearestWords(distance, predicate, Arrays.asList(start));
  }

  /**
   * Find the nearest neighbours within dependency distance links of the provide start dependencies.
   *
   * @param distance the dependency distance
   * @param predicate the predicate
   * @param start the start words (as list)
   * @return the (set of) words within range
   */
  public Set<WordToken> nearestWords(
      final int distance, Predicate<Dependency> predicate, final Collection<WordToken> start) {
    final Set<WordToken> words = new HashSet<>();

    if (distance <= 0) {
      return words;
    }

    for (final WordToken d : start) {
      extractWords(words, distance, predicate, d);
    }

    return words;
  }

  /**
   * Extract words recursively following the graph.
   *
   * @param collector the collector
   * @param distance the distance
   * @param predicate the predicate
   * @param token the token
   */
  private void extractWords(
      final Set<WordToken> collector,
      final int distance,
      Predicate<Dependency> predicate,
      final WordToken token) {
    // The word itself
    collector.add(token);

    // TODO: Depth first, We potentially revisit the same node repeatedly,
    // so this could be more efficient.

    final List<WordToken> set =
        edges
            .get(token)
            .stream()
            .filter(e -> predicate.test(e.getDependency()))
            .map(e -> e.getOther(token))
            .collect(Collectors.toList());

    if (set != null) {
      collector.addAll(set);

      final int newDistance = distance - 1;
      if (newDistance > 0) {
        set.forEach(a -> extractWords(collector, newDistance, predicate, a));
      }
    }
  }

  /** Log the dependency graph to the logger for debugging. */
  public void log() {
    final StringBuilder sb = new StringBuilder();
    edges
        .asMap()
        .entrySet()
        .stream()
        .forEach(
            e -> {
              sb.append("\t");
              sb.append(e.getKey().getCoveredText());
              sb.append(": ");
              e.getValue()
                  .stream()
                  .map(x -> x.getOther(e.getKey()))
                  .forEach(w -> sb.append(" " + w.getCoveredText()));
              sb.append("\n");
            });

    final StringBuilder governorSb = new StringBuilder();
    governors
        .asMap()
        .entrySet()
        .stream()
        .forEach(
            e -> {
              governorSb.append("\t");
              governorSb.append(e.getKey().getCoveredText());
              governorSb.append(": ");
              e.getValue()
                  .stream()
                  .forEach(
                      w ->
                          governorSb.append(
                              " " + w.getCoveredText() + "[" + w.getDependencyType() + "]"));
              governorSb.append("\n");
            });

    final StringBuilder dependentSb = new StringBuilder();
    dependents
        .entrySet()
        .stream()
        .forEach(
            e -> {
              dependentSb.append("\t");
              dependentSb.append(e.getKey().getCoveredText());
              dependentSb.append(": ");
              Dependency w = e.getValue();
              dependentSb.append(" " + w.getCoveredText() + "[" + w.getDependencyType() + "]");
              dependentSb.append("\n");
            });

    DependencyGraph.LOGGER.info(
        "Dependency graph:  Edges:\n{}\n  Governors\n{}\n  Dependent\n{}",
        sb,
        governorSb,
        dependentSb);
  }

  /**
   * Create a new (sub) graph where words are only those matched by the filter.
   *
   * @param predicate the predicate
   * @return the new filtered dependency graph
   */
  public DependencyGraph filter(Predicate<WordToken> predicate) {
    final SetMultimap<WordToken, Edge> filteredEdges = HashMultimap.create();
    final Map<WordToken, Dependency> filteredDependent = new HashMap<>();
    final SetMultimap<WordToken, Dependency> filteredGovernor = HashMultimap.create();
    final SetMultimap<String, WordToken> filteredPartOfSpeech = HashMultimap.create();

    edges
        .asMap()
        .entrySet()
        .stream()
        .filter(w -> predicate.test(w.getKey()))
        .forEach(
            e -> {
              final WordToken key = e.getKey();
              e.getValue()
                  .stream()
                  .filter(edge -> predicate.test(edge.getOther(key)))
                  .forEach(v -> filteredEdges.put(key, v));
            });

    governors
        .asMap()
        .keySet()
        .stream()
        .filter(predicate)
        .forEach(
            k -> {
              final List<Dependency> filtered =
                  governors
                      .get(k)
                      .stream()
                      .filter(
                          d -> predicate.test(d.getGovernor()) && predicate.test(d.getDependent()))
                      .collect(Collectors.toList());
              filteredGovernor.putAll(k, filtered);
            });

    dependents
        .keySet()
        .stream()
        .filter(predicate)
        .forEach(
            k -> {
              final Dependency d = dependents.get(k);
              if (predicate.test(d.getGovernor()) && predicate.test(d.getDependent())) {
                filteredDependent.put(k, d);
              }
            });

    partOfSpeech
        .asMap()
        .entrySet()
        .forEach(
            entry -> {
              final List<WordToken> filtered =
                  entry.getValue().stream().filter(predicate).collect(Collectors.toList());
              filteredPartOfSpeech.putAll(entry.getKey(), filtered);
            });

    return new DependencyGraph(
        filteredEdges, filteredDependent, filteredGovernor, filteredPartOfSpeech);
  }

  /**
   * Adds the dependency.
   *
   * @param dependency the dependency
   */
  private void addDependency(Dependency dependency) {
    if ((dependency.getDependencyType() == null
            || !"ROOT".equalsIgnoreCase(dependency.getDependencyType()))
        && dependency.getGovernor() != null
        && dependency.getDependent() != null) {
      addEdge(dependency);

    } else if ("ROOT".equalsIgnoreCase(dependency.getDependencyType())) {
      dependents.put(dependency.getDependent(), dependency);
    }
  }

  /**
   * Gets the words in te graph.
   *
   * @return the words
   */
  public Set<WordToken> getWords() {
    return Collections.unmodifiableSet(edges.keySet());
  }

  /**
   * Shortest path between from and to, limited by maxDistance.
   *
   * @param from the from
   * @param to the to
   * @param maxDistance the max distance
   * @return the list
   */
  public List<WordToken> shortestPath(
      Collection<WordToken> from, Collection<WordToken> to, int maxDistance) {
    if (from.isEmpty() || to.isEmpty() || maxDistance <= -1) {
      return Collections.emptyList();
    }

    final Set<WordToken> visited = new HashSet<>();
    final PriorityQueue<WordDistance> queue = new PriorityQueue<>();
    from.stream()
        .forEach(
            t -> {
              queue.add(new WordDistance(t));
              visited.add(t);
            });

    while (!queue.isEmpty()) {
      final WordDistance wd = queue.poll();
      LOGGER.debug("{}", wd);

      if (to.contains(wd.getWord())) {
        return wd.getWords();
      }

      if (wd.getDistance() < maxDistance) {
        final Set<WordToken> nextWords =
            edges
                .get(wd.getWord())
                .stream()
                .map(w -> w.getOther(wd.getWord()))
                .collect(Collectors.toSet());
        nextWords.removeAll(visited);
        nextWords
            .stream()
            .forEach(
                t -> {
                  queue.add(new WordDistance(t, wd));
                  visited.add(t);
                });
      }
    }

    return Collections.emptyList();
  }

  /**
   * Build a dependency graph from a JCAS which has already been processed through a dependency
   * grammar.
   *
   * <p>Thus the JCAS as Dependency annotations.
   *
   * @param jCas the jCAS to process.
   * @return the dependency graph (non-null)
   */
  public static DependencyGraph build(final JCas jCas) {
    final DependencyGraph graph = new DependencyGraph();

    JCasUtil.select(jCas, Dependency.class).stream().forEach(graph::addDependency);

    return graph;
  }

  /**
   * Build a dependency graph from a JCAS which has already been processed through a dependency
   * grammar, but limit to a subset of the jcas (covered by annotation).
   *
   * <p>Thus the JCAS as Dependency annotations.
   *
   * @param jCas the jCAS to process.
   * @param annnotation the annnotation
   * @return the dependency graph (non-null)
   */
  public static DependencyGraph build(final JCas jCas, AnnotationFS annnotation) {
    final DependencyGraph graph = new DependencyGraph();

    JCasUtil.selectCovered(jCas, Dependency.class, annnotation)
        .stream()
        .forEach(graph::addDependency);

    return graph;
  }

  /**
   * Traverse the graph looking
   *
   * @param distance the distance
   * @param start the start
   * @param predicate the predicate - use this to act on the graph (eg collect information) and
   *     return false to stop or true to continue.
   */
  public void traverse(int distance, Collection<Dependency> start, TraversePredicate predicate) {
    if (distance <= 0) {
      return;
    }

    final ImmutableStack<WordToken> history = new ImmutableStack<>();

    for (final Dependency d : start) {
      if (predicate.test(d, null, d.getDependent(), history)) {
        ImmutableStack<WordToken> stack = history.push(d.getDependent());
        traverse(distance, d.getDependent(), stack, predicate);
      }
    }
  }

  /**
   * Traverse the graph from token.
   *
   * @param distance the distance
   * @param token the token
   * @param history the history
   * @param predicate the predicate
   */
  private void traverse(
      int distance,
      WordToken token,
      ImmutableStack<WordToken> history,
      TraversePredicate predicate) {
    final int newDistance = distance - 1;

    if (newDistance <= 0) {
      return;
    }

    for (final Edge e : edges.get(token)) {
      final WordToken other = e.getOther(token);

      if (!history.contains(other) && predicate.test(e.getDependency(), token, other, history)) {
        final ImmutableStack<WordToken> stack = history.push(other);
        traverse(newDistance, other, stack, predicate);
      }
    }
  }

  /** A functional interface to implement */
  @FunctionalInterface
  public interface TraversePredicate {

    /**
     * Test if should follow this dependencies.
     *
     * @param dependency the dependency
     * @param from the from word
     * @param to the to word
     * @param history the history (all the word tokens up to from)
     * @return true, if successful
     */
    boolean test(
        Dependency dependency, WordToken from, WordToken to, ImmutableStack<WordToken> history);
  }

  /**
   * Check for "isomorphic" matches in this graph to the given {@link DependencyTree}.
   *
   * <p>By isomorphic here we must have the same graph structure and all the nodes and edges must
   * match according the the definition of match for node and edge. Which is that a edge must have
   * exactly the same type and a node must have the same root type (e.g. NN will also match NNP) and
   * may additionally specify a regular expression for the content to match.
   *
   * <p>Multiple matches may occur.
   *
   * @param dependencyTree to search for
   * @return a collection of found {@link DependencyMatch}es
   */
  public Collection<DependencyMatch> match(DependencyTree dependencyTree) {
    if (!isTreeSmaller(dependencyTree)) {
      return Collections.emptySet();
    }

    DependencyNode root = dependencyTree.getRoot();
    List<WordToken> rootCandidates =
        partOfSpeech
            .get(root.getType())
            .stream()
            .filter(root::matches)
            .collect(Collectors.toList());

    if (rootCandidates.isEmpty()) {
      return Collections.emptySet();
    }

    List<BiMap<DependencyNode, WordToken>> matched = new ArrayList<>();
    for (WordToken rootCandidate : rootCandidates) {

      List<BiMap<DependencyNode, WordToken>> total = new ArrayList<>();
      BiMap<DependencyNode, WordToken> nodeToToken = HashBiMap.create();
      List<DependencyTree> treeMatch = Arrays.asList(dependencyTree);
      List<WordToken> twMatch = Arrays.asList(rootCandidate);

      if (matchTree(total, treeMatch, twMatch, nodeToToken)) {
        matched.addAll(total);
      }
    }

    return matched.stream().map(DependencyMatch::new).collect(Collectors.toList());
  }

  private boolean isTreeSmaller(DependencyTree dependencyTree) {
    return dependencyTree.size() < partOfSpeech.size();
  }

  private boolean matchTree(
      List<BiMap<DependencyNode, WordToken>> total,
      List<DependencyTree> treeMatchs,
      List<WordToken> wtMatchs,
      BiMap<DependencyNode, WordToken> nodeToTokens) {

    List<DependencyTree> treeMatch = new ArrayList<>(treeMatchs);
    List<WordToken> wtMatch = new ArrayList<>(wtMatchs);
    BiMap<DependencyNode, WordToken> nodeToToken = HashBiMap.create(nodeToTokens);

    boolean failure = false;
    boolean success = true;
    while (!treeMatch.isEmpty()) {
      DependencyTree tree = treeMatch.remove(0);
      DependencyNode root = tree.getRoot();
      WordToken candidate = wtMatch.remove(0);

      if (!root.matches(candidate)) {
        return failure;
      }

      if (nodeToToken.containsKey(root) || nodeToToken.containsValue(candidate)) {
        if (!nodeToToken.get(root).equals(candidate)) {
          return failure;
        }
      } else {
        nodeToToken.put(root, candidate);
      }

      List<DependencyEdge> children = tree.getDependencies();
      Set<Dependency> dependants = governors.get(candidate);

      if (children.size() > dependants.size()) {
        return failure;
      }

      for (DependencyEdge edge : children) {
        List<WordToken> candidates = new ArrayList<>();
        addDependantsToCandidates(dependants, edge, candidates);

        DependencyTree childTree = edge.getTree();
        DependencyNode childNode = childTree.getRoot();

        boolean flag = false;
        boolean terminate = false;
        for (WordToken childCandidate : candidates) {
          if (nodeToToken.containsKey(childNode) || nodeToToken.containsValue(childCandidate)) {
            if (nodeToToken.inverse().getOrDefault(childCandidate, DUMMY_NODE).equals(childNode)) {
              terminate = true;
              break;
            } else {
              continue;
            }
          }
          List<DependencyTree> treeMatchTemp =
              ImmutableList.<DependencyTree>builder()
                  .addAll(treeMatch)
                  .add(tree)
                  .add(childTree)
                  .build();

          List<WordToken> wtMatchTemp =
              ImmutableList.<WordToken>builder()
                  .addAll(wtMatch)
                  .add(candidate)
                  .add(childCandidate)
                  .build();

          flag |= matchTree(total, treeMatchTemp, wtMatchTemp, nodeToToken);
        }

        if (terminate) {
          continue;
        }
        if (flag) {
          return success;
        }

        return failure;
      }
    }

    total.add(nodeToToken);
    return success;
  }

  private void addDependantsToCandidates(
      Set<Dependency> dependants, DependencyEdge edge, List<WordToken> candidates) {
    for (Dependency dependency : dependants) {
      if (edge.matches(dependency)) {
        candidates.add(dependency.getDependent());
      }
    }
  }

  /**
   * Get the head node for the given annotation.
   *
   * <p>This is the highest word token in the dependency graph covered by the annotation.
   *
   * @param annotation
   * @return the head node
   */
  public Optional<WordToken> getHeadNode(Entity annotation) {
    List<WordToken> covered = JCasUtil.selectCovered(WordToken.class, annotation);
    return getHeadNode(covered);
  }

  /**
   * Get the head node from the given set of WordTokens.
   *
   * <p>This is the highest word token in the dependency graph covered by the annotation, assumes
   * they are connected.
   *
   * @param annotation
   * @return the head node
   */
  public Optional<WordToken> getHeadNode(List<WordToken> covered) {
    if (covered.isEmpty()) {
      return Optional.empty();
    }
    if (covered.size() == 1) {
      return Optional.of(covered.get(0));
    }
    WordToken head = covered.get(0);
    Dependency d = dependents.get(head);
    while (d != null && !head.equals(d.getGovernor()) && covered.contains(d.getGovernor())) {
      head = d.getGovernor();
    }
    return Optional.of(head);
  }

  /**
   * Get the minimal tree containing the given word tokens.
   *
   * <p>The tokens must be from the same dependency tree (i.e. be in the same sentence) or an {@link
   * IllegalArgumentException} will be thrown.
   *
   * @param tokens to contain
   * @return the minimal subtree
   */
  public DependencyTree minimalTree(Collection<WordToken> tokens) {
    final Set<WordToken> common = getCommonParents(tokens);
    if (common.isEmpty()) {
      throw new IllegalArgumentException(
          "Given tokens not from the same dependency tree: " + tokens.toString());
    }

    Set<Dependency> dependencies = new HashSet<>();
    Deque<WordToken> deque = new ArrayDeque<>(tokens);
    while (!deque.isEmpty()) {
      WordToken current = deque.removeLast();
      Dependency dependency = dependents.get(current);

      if (dependency != null
          && dependencies.add(dependency)
          && !common.contains(dependency.getDependent())) {
        deque.push(dependency.getGovernor());
      }
    }
    return DependencyTreeBuilder.buildFromDependencies(dependencies);
  }

  private Set<WordToken> getCommonParents(Collection<WordToken> tokens) {
    final Iterator<Set<WordToken>> iterator =
        tokens
            .stream()
            .map(
                t -> {
                  final Set<WordToken> path = new HashSet<>();
                  WordToken wt = t;
                  do {
                    path.add(wt);
                    wt = dependents.get(wt).getGovernor();

                  } while (!path.contains(wt));

                  return path;
                })
            .iterator();

    final Set<WordToken> common = new HashSet<>();

    if (iterator.hasNext()) {
      common.addAll(iterator.next());
      while (iterator.hasNext()) {
        common.retainAll(new HashSet<>(iterator.next()));
      }
    }
    return common;
  }
}
