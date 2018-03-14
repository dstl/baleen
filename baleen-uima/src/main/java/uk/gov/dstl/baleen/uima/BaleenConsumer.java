// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima;

import java.util.Collections;

import org.apache.uima.jcas.tcas.DocumentAnnotation;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;

/**
 * Extends the {@link uk.gov.dstl.baleen.uima.BaleenAnnotator} class, to differentiate between
 * consumers and annotators where necessary, but without requiring the duplication of code.
 */
public abstract class BaleenConsumer extends BaleenAnnotator {
  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(DocumentAnnotation.class, Metadata.class, Relation.class, Entity.class),
        Collections.emptySet());
  }
}
