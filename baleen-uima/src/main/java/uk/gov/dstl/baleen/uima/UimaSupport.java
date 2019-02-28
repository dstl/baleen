// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.uima;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.tcas.DocumentAnnotation;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import uk.gov.dstl.baleen.core.history.BaleenHistory;
import uk.gov.dstl.baleen.core.history.DocumentHistory;
import uk.gov.dstl.baleen.core.history.HistoryEvent;
import uk.gov.dstl.baleen.core.history.HistoryEvents;
import uk.gov.dstl.baleen.core.history.Recordable;
import uk.gov.dstl.baleen.core.history.noop.NoopBaleenHistory;
import uk.gov.dstl.baleen.types.Base;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.utils.UimaUtils;

/**
 * A support class for Uima within Baleen.
 *
 * <p>This is used to keep common functions for manipulating Baleen entities and annotations
 * together to avoid code duplication. It provides helpers to accessing and creating entities, and
 * standardises history.
 *
 * <p>Users of Baleen do not need to create this object, as it is typically access through
 * BaleenAnnotator, BaleenConsumer, etc and will be preconfigured.
 */
public class UimaSupport {

  private final UimaMonitor monitor;
  private final String referrer;
  private final boolean mergeDistinctEntities;
  private final BaleenHistory history;
  private final String pipelineName;

  /**
   * New instance.
   *
   * @param pipelineName the name of the pipeline
   * @param clazz the clazz to owning this support
   * @param history the history to store to (if null will use the Noop history)
   * @param monitor the monitor instance, used for logging.
   * @param mergeDistinctEntities when merging should we merge entities when they have distinct
   *     reference targets
   */
  public UimaSupport(
      String pipelineName,
      Class<?> clazz,
      BaleenHistory history,
      UimaMonitor monitor,
      boolean mergeDistinctEntities) {
    this.pipelineName = pipelineName;
    this.history = history != null ? history : NoopBaleenHistory.getInstance();
    referrer = UimaUtils.makePipelineSpecificName(pipelineName, clazz);
    this.monitor = monitor;
    this.mergeDistinctEntities = mergeDistinctEntities;
  }

  /**
   * Get the name of the pipeline to which this belongs.
   *
   * @return pipeline name
   */
  public String getPipelineName() {
    return pipelineName;
  }

  /**
   * Add an annotation to the JCas index, notifying UimaMonitor of the fact we have done so
   *
   * @param annotations Annotation(s) to add
   */
  public void add(Annotation... annotations) {
    add(Arrays.asList(annotations));
  }

  /**
   * Add an annotation to the JCas index, notifying UimaMonitor of the fact we have done so
   *
   * @param annotations Annotation(s) to add
   */
  public void add(Collection<? extends Annotation> annotations) {
    for (Annotation annot : annotations) {
      annot.addToIndexes();
      monitor.entityAdded(annot.getType().getName());

      if (annot instanceof Entity) {
        Entity entity = (Entity) annot;

        // Add in a value if it doesn't have one
        if (Strings.isNullOrEmpty(entity.getValue())) {
          entity.setValue(annot.getCoveredText());
        }

        addToHistory(annot.getCAS(), HistoryEvents.createAdded((Recordable) annot, referrer));
      }
    }
  }

  /**
   * Remove an annotation to the JCas index, notifying UimaMonitor of the fact we have done so.
   *
   * <p>Relations that refer to the given annotation will also be removed.
   *
   * @param annotations Annotation(s) to remove
   */
  public void remove(Collection<? extends Annotation> annotations) {
    for (Annotation annot : annotations) {

      if (annot instanceof Recordable) {
        try {
          addToHistory(
              annot.getCAS().getJCas(), HistoryEvents.createAdded((Recordable) annot, referrer));
        } catch (CASException e) {
          monitor.error("Unable to add to history on remove", e);
        }
      }

      if (annot instanceof Entity) {
        for (Relation r : getRelations((Entity) annot)) {
          monitor.entityRemoved(r.getType().getName());
          r.removeFromIndexes();
        }
      }

      monitor.entityRemoved(annot.getType().getName());

      annot.removeFromIndexes();
    }
  }

  /**
   * Remove an annotation to the JCas index, notifying UimaMonitor of the fact we have done so.
   *
   * <p>Relations that refer to the given annotation will also be removed.
   *
   * @param annotations Annotation(s) to remove
   */
  public void remove(Annotation... annotations) {
    remove(Arrays.asList(annotations));
  }

  /**
   * Add a new annotation, which is merged from the old annotations, removing the old annotations.
   *
   * @param newAnnotation The annotation which is to be added to the document as the merged result
   *     of the old annotations
   * @param annotations Annotation(s) which have been merged and should be removed
   */
  public void mergeWithNew(Annotation newAnnotation, Annotation... annotations) {
    mergeWithNew(newAnnotation, Arrays.asList(annotations));
  }

  /**
   * Add a new annotation, which is merged from the old annotations, removing the old annotations.
   *
   * @param newAnnotation The annotation which is to be added to the document as the merged result
   *     of the old annotations
   * @param annotations Annotation(s) which have been merged and should be removed
   */
  public void mergeWithNew(Annotation newAnnotation, Collection<? extends Annotation> annotations) {
    add(newAnnotation);
    mergeWithExisting(newAnnotation, annotations);
  }

  /**
   * Merge an existing annotation with old annotations, removing the old annotations.
   *
   * @param existingAnnotation The annotation which exists and is to be left in the document
   *     (merged)
   * @param annotations Annotation(s) which have been merged wiht existingAnnotation and then
   *     removed
   */
  public void mergeWithExisting(Annotation existingAnnotation, Annotation... annotations) {
    mergeWithExisting(existingAnnotation, Arrays.asList(annotations));
  }

  /**
   * Add a new annotation, which is merged from the old annotations, removing the old annotations.
   *
   * @param existingAnnotation The annotation which is to be left in the document (merged)
   * @param annotations Annotation(s) which will be merged with existingAnnotation and then removed
   */
  public void mergeWithExisting(
      Annotation existingAnnotation, Collection<? extends Annotation> annotations) {
    if (annotations == null || annotations.isEmpty()) {
      return;
    }

    if (!(existingAnnotation instanceof Entity)) {
      // If the target is just an annotation then remove everything independently of coreference
      // targets
      // since annotations does not have this configuration.

      mergeWithExistingNoCoref(existingAnnotation, annotations);
      return;
    }

    // If we've got here, then existingAnnotation is an entity and so we need to process it

    Entity existingEntity = (Entity) existingAnnotation;
    for (Annotation a : annotations) {
      if (a instanceof Entity) {
        // If an entity we check if they point to the same reference target
        Entity entity = (Entity) a;

        mergeEntities(entity, existingEntity);
      } else {
        // If an annotation just remove
        mergeWithExistingNoCoref(existingAnnotation, Lists.newArrayList(a));
      }
    }
  }

  /**
   * Merge entity onto targetEntity (assuming they have the same ReferentTarget), updating
   * relationships as required.
   *
   * @return True if merge was successful, false otherwise
   */
  private boolean mergeEntities(Entity entity, Entity targetEntity) {
    ReferenceTarget targetRef = targetEntity.getReferent();
    ReferenceTarget entityRef = entity.getReferent();

    if (mergeDistinctEntities || isSameTarget(targetRef, entityRef)) {
      addMergeToHistory(targetEntity, entity);

      // Update relationship pointers
      for (Relation r : getRelations(entity)) {
        if (r.getSource() == entity) {
          r.setSource(targetEntity);
        }
        if (r.getTarget() == entity) {
          r.setTarget(targetEntity);
        }
      }

      remove(entity);

      return true;
    } else {
      monitor.debug(
          "Not merging objects {} and {} as they have different referents",
          targetEntity.getInternalId(),
          entity.getInternalId());
      return false;
    }
  }

  /**
   * Merge annotations whilst ignoring coreferences. It is recommended that mergeWithExisting(...)
   * is used instead as that will pass off to this method when appropriate
   *
   * @param existingAnnotation The annotation which is to be left in the document (merged)
   * @param annotations Annotation(s) which will be merged with existingAnnotation and then removed
   */
  public void mergeWithExistingNoCoref(
      Annotation existingAnnotation, Collection<? extends Annotation> annotations) {
    for (Annotation a : annotations) {
      addMergeToHistory(existingAnnotation, a);
    }
    remove(annotations);
  }

  private boolean isSameTarget(ReferenceTarget rt1, ReferenceTarget rt2) {
    return Objects.equals(rt1, rt2);
  }

  private void addMergeToHistory(Annotation keep, Annotation removed) {
    if (keep instanceof Recordable && removed instanceof Base) {
      Recordable r = (Recordable) keep;
      Base b = (Base) removed;
      try {
        addToHistory(
            keep.getCAS().getJCas(), HistoryEvents.createMerged(r, referrer, b.getInternalId()));
      } catch (CASException e) {
        monitor.error("Unable to add merge to history", e);
      }
    }
  }

  /**
   * Adds a event to the history for this CAS document.
   *
   * @param cas the target document for the event
   * @param event the event to add
   */
  public void addToHistory(CAS cas, HistoryEvent event) {
    try {
      getDocumentHistory(cas.getJCas()).add(event);
    } catch (CASException e) {
      monitor.error("Unable to add to history on add", e);
    }
  }

  /**
   * Adds a event to the history for this jcas document.
   *
   * @param jCas the target document for the event
   * @param event the event to add
   */
  public void addToHistory(JCas jCas, HistoryEvent event) {
    getDocumentHistory(jCas).add(event);
  }

  /**
   * Get (or create) the history associated with the document.
   *
   * @param jCas the target document
   * @return the history associated with the document
   */
  public DocumentHistory getDocumentHistory(JCas jCas) {
    String documentId = pipelineName + ":" + getDocumentAnnotation(jCas).getHash();
    return history.getHistory(documentId);
  }

  /**
   * Return the document annotation.
   *
   * @param jCas
   * @return the document annotation
   */
  public static DocumentAnnotation getDocumentAnnotation(JCas jCas) {
    return (DocumentAnnotation) jCas.getDocumentAnnotationFs();
  }

  /**
   * Get relations that have a given entity as either the source or the target
   *
   * @param e The given entity
   * @return Collection of relations that refer to the given Entity
   */
  public Collection<Relation> getRelations(Entity e) {
    try {
      JCas jCas = e.getCAS().getJCas();
      Collection<Relation> relations = JCasUtil.select(jCas, Relation.class);

      return relations
          .stream()
          .filter(r -> r.getSource() == e || r.getTarget() == e)
          .collect(Collectors.toList());
    } catch (UIMAException ue) {
      monitor.warn("Unable to get relations from entity", ue);
      return Collections.emptyList();
    }
  }
}
