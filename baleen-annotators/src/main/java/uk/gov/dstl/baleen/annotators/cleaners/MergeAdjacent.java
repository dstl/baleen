// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;

/**
 * Merge adjacent entities of the same type.
 *
 * <p>This can be useful if a gazetteer contains name parts rather than full names, and you want to
 * combine those into single entities.
 *
 * @baleen.javadoc
 */
public class MergeAdjacent extends BaleenAnnotator {
  /**
   * A regular expression that sets what is allowed between entities for them to be considered
   * adjacent.
   *
   * @baleen.config \h*
   */
  public static final String PARAM_SEPARATOR = "separator";

  @ConfigurationParameter(name = PARAM_SEPARATOR, defaultValue = "\\h*")
  String separator;

  Pattern separatorPattern;

  /**
   * A list of the allowed entity types. Entities will still only be compared with entities of the
   * same type, but only entity types on this list (excluding subtypes) will be checked.
   *
   * <p>This may be ignored by some annotators which inherit from this class.
   *
   * @baleen.config
   */
  public static final String PARAM_TYPE = "types";

  @ConfigurationParameter(
    name = PARAM_TYPE,
    defaultValue = {}
  )
  String[] types;

  List<Class<? extends Entity>> classTypes = new ArrayList<>();

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    separatorPattern = Pattern.compile(separator);

    JCas jCas;
    try {
      jCas = JCasFactory.createJCas(TypeSystemSingleton.getTypeSystemDescriptionInstance());
    } catch (UIMAException e) {
      throw new ResourceInitializationException(e);
    }
    for (String type : types) {
      try {
        classTypes.add(TypeUtils.getEntityClass(type, jCas));
      } catch (BaleenException e) {
        getMonitor().error("Couldn't parse type - type will not be included", e);
      }
    }
    if (classTypes.isEmpty()) {
      getMonitor().warn("No valid types specified, no merging of entities will take place");
    }
  }

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
    for (Class<? extends Entity> klass : classTypes) {
      processType(jCas, klass);
    }
  }

  private void processType(JCas jCas, Class<? extends Entity> type) {
    List<List<Entity>> mergeables = findAllEntitiesToMerge(jCas, type);

    for (List<Entity> mergeable : mergeables) {
      int begin = mergeable.get(0).getBegin();
      int end = mergeable.get(mergeable.size() - 1).getEnd();

      Double lowestConfidence =
          mergeable
              .stream()
              .min(
                  new Comparator<Entity>() {
                    @Override
                    public int compare(Entity e1, Entity e2) {
                      return Double.compare(e1.getConfidence(), e2.getConfidence());
                    }
                  })
              .get()
              .getConfidence();

      // Build a new annotation
      try {
        Entity merged = type.getConstructor(JCas.class).newInstance(new Object[] {jCas});

        merged.setBegin(begin);
        merged.setEnd(end);

        merged.setValue(jCas.getDocumentText().substring(begin, end));
        merged.setConfidence(lowestConfidence);

        if (mergeAdditionalProperties(merged, type, mergeable)) {
          mergeWithNew(merged, mergeable);
        } else {
          merged = null;
        }
      } catch (Exception e) {
        getMonitor().error("Unable to merge entities", e);
      }
    }
  }

  private List<List<Entity>> findAllEntitiesToMerge(JCas jCas, Class<? extends Entity> type) {
    List<List<Entity>> mergeables = new LinkedList<List<Entity>>();
    Map<Entity, List<Entity>> toMerge = new HashMap<>();

    // Create a mapping of annotations to join together
    List<Entity> entities = filterEntities(JCasUtil.select(jCas, type), type);
    for (Entity current : entities) {
      List<Entity> following =
          filterEntities(JCasUtil.selectFollowing(jCas, type, current, 1), type);
      if (following.isEmpty()) continue;

      Entity next = following.get(0);

      String between = jCas.getDocumentText().substring(current.getEnd(), next.getBegin());

      // Check that the entities are only separated by whitespace,
      if (separatorPattern.matcher(between).matches() && shouldMerge(current, next)) {

        List<Entity> list;
        if (toMerge.containsKey(current)) {
          list = toMerge.get(current);
        } else {
          list = new LinkedList<>();
          list.add(current);
          toMerge.put(current, list);
          mergeables.add(list);
        }
        list.add(next);
        toMerge.put(next, list);
      }
    }
    return mergeables;
  }

  private List<Entity> filterEntities(
      Collection<? extends Entity> entities, Class<? extends Entity> type) {
    // A better way to do this would be with .collect(),
    // but there's a bug with the version of JDK we have installed on the Jenkins server that won't
    // allow that at the moment
    List<Entity> ret = new ArrayList<>();

    entities.stream().filter(e -> e.getClass().equals(type)).forEach(e -> ret.add(e));

    return ret;
  }

  /** Returns true if e1 should be merged with e2 */
  public boolean shouldMerge(Entity e1, Entity e2) {
    return true;
  }

  /**
   * Merge additional properties from originalEntities into merged.
   *
   * <p>If this method returns false, then none of the entities will be merged
   */
  public boolean mergeAdditionalProperties(
      Entity merged, Class<? extends Entity> type, List<Entity> originalEntities) {
    // Do nothing here - this is intended to be overridden if additional merging is required
    return true;
  }

  @Override
  public AnalysisEngineAction getAction() {
    Set<Class<? extends Annotation>> annotatorTypes = new HashSet<>();
    annotatorTypes.addAll(classTypes);

    return new AnalysisEngineAction(annotatorTypes, annotatorTypes);
  }
}
