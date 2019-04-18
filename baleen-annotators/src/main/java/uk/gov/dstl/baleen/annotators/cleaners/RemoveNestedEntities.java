// Dstl (c) Crown Copyright 2017
// Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.maltparser.core.helper.HashSet;

import uk.gov.dstl.baleen.annotators.cleaners.helpers.AbstractNestedEntities;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.core.utils.ReflectionUtils;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;

/**
 * Remove entities which are contained within other entities of the same type
 *
 * <p>All potential merges area made for all types (bearing those set in the exclusion configuration
 * parameter). Information may be lost by the removal of entities. Some kind of merging of entities
 * might be a better option.
 *
 * @baleen.javadoc
 */
public class RemoveNestedEntities extends AbstractNestedEntities<Entity> {

  /**
   * A list of types to exclude when removing nested entities.
   *
   * @baleen.config
   */
  public static final String PARAM_EXCLUDED_TYPES = "excludedTypes";

  @ConfigurationParameter(
      name = PARAM_EXCLUDED_TYPES,
      defaultValue = {})
  private Set<String> excluded;

  Set<Class<? extends Annotation>> classTypes = new HashSet<>();

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    JCas jCas;
    try {
      jCas = JCasFactory.createJCas(TypeSystemSingleton.getTypeSystemDescriptionInstance());
    } catch (UIMAException e) {
      throw new ResourceInitializationException(e);
    }
    for (String type : excluded) {
      try {
        classTypes.add(TypeUtils.getEntityClass(type, jCas));
      } catch (BaleenException e) {
        getMonitor().error("Couldn't parse type {} - type will not be excluded", type, e);
      }
    }
  }

  @Override
  protected Collection<List<Entity>> compileEntities(JCas jCas) {
    Map<String, List<Entity>> annotations = new HashMap<>();

    FSIterator<Annotation> iter = jCas.getAnnotationIndex(Entity.type).iterator();
    while (iter.hasNext()) {
      Entity e = (Entity) iter.next();
      String type = e.getType().getName();

      if (!excluded.contains(type)) {
        List<Entity> typeAnnotations = annotations.computeIfAbsent(type, k -> new ArrayList<>());
        typeAnnotations.add(e);
      }
    }

    return annotations.values();
  }

  @Override
  protected boolean shouldMerge(Entity keep, Entity remove) {
    // Merge everything
    return true;
  }

  @Override
  public void doDestroy() {
    excluded = null;
  }

  @Override
  public AnalysisEngineAction getAction() {
    Set<Class<? extends Entity>> types = ReflectionUtils.getSubTypes(Entity.class);
    types.removeAll(classTypes);

    Set<Class<? extends Annotation>> annotations = new HashSet<>();
    annotations.addAll(types);

    return new AnalysisEngineAction(annotations, Collections.emptySet());
  }
}
