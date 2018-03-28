// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.BaleenAnnotation;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Discards annotations based on duplicate annotation external ids, of a certain type. One
 * annotation of each set of duplicates is kept, the choice of which to keep is arbitrary
 *
 * @param <T> The type of annotation to discard based on id
 */
public abstract class AbstractDiscardWithSameId<T extends BaleenAnnotation>
    extends BaleenAnnotator {

  private final Class<T> clazz;

  /** @param clazz The class of annotation to discard based on id */
  public AbstractDiscardWithSameId(final Class<T> clazz) {
    this.clazz = clazz;
  }

  @Override
  protected void doProcess(final JCas jCas) throws AnalysisEngineProcessException {
    final Multimap<String, T> annotations = MultimapBuilder.hashKeys().arrayListValues().build();

    JCasUtil.select(jCas, clazz).stream().forEach(r -> annotations.put(r.getExternalId(), r));

    final List<T> toDelete = new LinkedList<>();

    annotations
        .asMap()
        .entrySet()
        .stream()
        .map(Map.Entry::getValue)
        .filter(e -> e.size() > 1)
        // Convert to a list of all annotations, BUT skip (drop) one...
        // that means we'll keep that, and all the other duplicates can get deleted
        .flatMap(e -> e.stream().skip(1))
        // through away all the
        .forEach(toDelete::add);

    removeFromJCasIndex(toDelete);
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(ImmutableSet.of(clazz), Collections.emptySet());
  }
}
