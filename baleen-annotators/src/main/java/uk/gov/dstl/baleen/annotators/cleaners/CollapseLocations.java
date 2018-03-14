// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Tries to find instances where a single location may have been split into several entities, and
 * collapse them into a single entity.
 *
 * <p>Examples are:
 *
 * <ul>
 *   <li>The Iraq-Syria border
 *   <li>The city of Mosul
 * </ul>
 */
public class CollapseLocations extends BaleenAnnotator {

  private static final String BORDER = "border";

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
    collapseBorders(jCas);
    collapseOf(jCas);
  }

  private void collapseBorders(JCas jCas) {
    for (Location l1 : JCasUtil.select(jCas, Location.class)) {
      List<Location> followingLocs = JCasUtil.selectFollowing(jCas, Location.class, l1, 1);

      if (!followingLocs.isEmpty()) checkBorders(jCas, l1, followingLocs.get(0));
    }
  }

  private void checkBorders(JCas jCas, Location l1, Location l2) {
    String betweenText = jCas.getDocumentText().substring(l1.getEnd(), l2.getBegin());
    String followingText = jCas.getDocumentText().substring(l2.getEnd()).toLowerCase();
    if ("-".equals(betweenText.trim())) {
      Location l;
      if (l2.getCoveredText().toLowerCase().endsWith(BORDER)) {
        l = new Location(jCas, l1.getBegin(), l2.getEnd());
      } else if (followingText.trim().startsWith(BORDER)) {
        l =
            new Location(
                jCas, l1.getBegin(), l2.getEnd() + followingText.indexOf(BORDER) + BORDER.length());
      } else {
        return;
      }

      mergeWithNew(l, l1, l2);
    }
  }

  private void collapseOf(JCas jCas) {
    for (Location l1 : JCasUtil.select(jCas, Location.class)) {
      List<Location> followingLocs = JCasUtil.selectFollowing(jCas, Location.class, l1, 1);

      if (followingLocs.isEmpty()) continue;

      Location l2 = followingLocs.get(0);

      String betweenText = jCas.getDocumentText().substring(l1.getEnd(), l2.getBegin());
      if ("of".equals(betweenText.trim())) {
        l2.setBegin(l1.getBegin());
        l2.setValue(l2.getCoveredText());
        mergeWithExisting(l2, l1);
      }
    }
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(Location.class), ImmutableSet.of(Location.class));
  }
}
