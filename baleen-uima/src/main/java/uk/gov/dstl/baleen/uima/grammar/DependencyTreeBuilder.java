// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.uima.grammar;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.fit.util.JCasUtil;

import uk.gov.dstl.baleen.types.language.Dependency;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;

/** Build a {@link DependencyTree} for the given {@link Sentence}. */
public class DependencyTreeBuilder {

  /**
   * Build a {@link DependencyTree} for the given sentence.
   *
   * @param s the sentence to build a {@link DependencyTree} for
   * @return the build {@link DependencyTree} or null if no root node found
   */
  public static DependencyTree buildFromSentence(Sentence s) {
    return buildFromDependencies(JCasUtil.selectCovered(Dependency.class, s));
  }

  public static DependencyTree buildFromDependencies(Collection<Dependency> dependencies) {
    Map<WordToken, DependencyTree> map = new HashMap<>();
    for (Dependency d : dependencies) {
      WordToken dependent = d.getDependent();
      DependencyNode dependencyNode =
          new DependencyNode(
              Long.toString(dependent.getInternalId()),
              dependent.getPartOfSpeech(),
              dependent.getCoveredText());
      DependencyTree dependencyTree = new DependencyTree(dependencyNode);
      map.put(dependent, dependencyTree);
    }
    DependencyTree root = null;
    for (Dependency d : dependencies) {
      WordToken govenor = d.getGovernor();
      WordToken dependent = d.getDependent();
      if (govenor == dependent) {
        root = map.get(dependent);
      } else {
        DependencyTree parent = map.get(govenor);
        DependencyTree child = map.get(dependent);
        if (parent != null) {
          parent.addDependency(d.getDependencyType(), child);
        } else {
          root = map.get(dependent);
        }
      }
    }

    return root;
  }
}
