// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Where an entity is followed by something that may be it's initials in brackets, associate those
 * initials with the entity and extract other occurrences.
 *
 * <p>For the purposes of this annotator, initials are defined as an uppercase string containing
 * only letters that appear in the entity (in the correct order).
 *
 * <p>For example LDN would be accepted as initials for London, but IoP would not be initials for
 * the Institute of Physics as it is not entirely upper case (IOP would be accepted)
 */
public class EntityInitials extends BaleenAnnotator {

  private static final Pattern BRACKETS = Pattern.compile("^\\s*\\((.*?)\\)");

  /**
   * If two entities are thought to be coreferences, but they have different existing reference
   * targets, should we merge them?
   *
   * @baleen.config false
   */
  public static final String PARAM_MERGE_REFERENTS = "mergeReferents";

  @ConfigurationParameter(name = PARAM_MERGE_REFERENTS, defaultValue = "false")
  private boolean mergeReferents = false;

  @Override
  protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
    for (Entity e : JCasUtil.select(jCas, Entity.class)) {
      List<Entity> addedEntities = new ArrayList<>();
      List<Entity> existingEntities = new ArrayList<>();

      String subsequentText = jCas.getDocumentText().substring(e.getEnd());

      Integer offset = e.getEnd();
      Matcher m = BRACKETS.matcher(subsequentText);
      while (m.find()) {
        if (isInitials(e.getCoveredText(), m.group(1))) {
          // Find all instances of entity, including initial instance
          Pattern p = Pattern.compile("\\b" + m.group(1) + "\\b");
          Matcher mInitial = p.matcher(subsequentText); // Initials should only appear after it's
          // been defined
          while (mInitial.find()) {
            processInitialMatch(jCas, e, addedEntities, existingEntities, offset, mInitial);
          }

          // Add entities to JCas and merge references
          addToJCasIndex(addedEntities);
          mergeReferences(jCas, e, existingEntities, addedEntities);
        }

        offset += m.end();
        subsequentText = subsequentText.substring(m.end());
        m = BRACKETS.matcher(subsequentText);
      }
    }
  }

  private void processInitialMatch(
      JCas jCas,
      Entity e,
      List<Entity> addedEntities,
      List<Entity> existingEntities,
      Integer offset,
      Matcher mInitial) {
    List<? extends Entity> existing =
        JCasUtil.selectCovered(
            jCas, e.getClass(), offset + mInitial.start(), offset + mInitial.end());
    if (existing.isEmpty()) {
      Entity eInitials2 = null;
      try {
        eInitials2 = e.getClass().getConstructor(JCas.class).newInstance(jCas);
        eInitials2.setBegin(offset + mInitial.start());
        eInitials2.setEnd(offset + mInitial.end());

        // TODO: Should we copy properties across too?

        addedEntities.add(eInitials2);
      } catch (Exception ex) {
        getMonitor().error("Unable to create new entity of class {}", e.getClass().getName(), ex);
      }
    } else {
      existingEntities.addAll(existing);
    }
  }

  /** Returns true if candidateInitials is a valid set of initials for text. */
  private boolean isInitials(String text, String candidateInitials) {
    if (!candidateInitials.equalsIgnoreCase(candidateInitials)) {
      return false;
    }

    StringJoiner sj = new StringJoiner(".*", ".*", ".*");
    for (int i = 0; i < candidateInitials.length(); i++) {
      sj.add(Pattern.quote(candidateInitials.substring(i, i + 1)));
    }

    return text.toUpperCase().matches(sj.toString());
  }

  /**
   * Merge the references of initials with the original entity
   *
   * @param jCas JCas object for the current document
   * @param e The original entity
   * @param existingEntities List of entities already covered by the found initials
   * @param addedEntities List of new entities created as a result of the initials
   */
  private void mergeReferences(
      JCas jCas, Entity e, List<Entity> existingEntities, List<Entity> addedEntities) {
    ReferenceTarget rt = setReferenceTarget(jCas, e, getReferenceTargets(existingEntities));

    // All new entities should have same RT as initial entity
    for (Entity ent : addedEntities) {
      ent.setReferent(rt);
    }

    // If existing entities don't have an RT, set it - otherwise merge?
    Set<ReferenceTarget> mergeRts = new HashSet<>();
    for (Entity ent : existingEntities) {
      if (ent.getReferent() == null) {
        ent.setReferent(rt);
      } else if (mergeReferents) {
        mergeRts.add(ent.getReferent());
      }
    }

    // Merge all reference targets identified previously
    if (!mergeRts.isEmpty()) {
      for (Entity ent : JCasUtil.select(jCas, Entity.class)) {
        if (ent.getReferent() != null && mergeRts.contains(ent.getReferent())) {
          ent.setReferent(rt);
        }
      }
    }
  }

  private List<ReferenceTarget> getReferenceTargets(List<Entity> entities) {
    List<ReferenceTarget> rts = new ArrayList<>();
    for (Entity ent : entities) {
      if (ent.getReferent() != null) {
        rts.add(ent.getReferent());
      }
    }

    return rts;
  }

  private ReferenceTarget setReferenceTarget(JCas jCas, Entity e, List<ReferenceTarget> rts) {
    ReferenceTarget rt;

    if (rts.size() == 1 && e.getReferent() == null) { // Entity doesn't have RT, but one of the
      // subsequent initials does
      rt = rts.get(0);
      e.setReferent(rt);
    } else if (e.getReferent() != null) { // Entity has RT
      rt = e.getReferent();
    } else { // Entity doesn't have RT, and 0 or several of the subsequent initials do
      rt = new ReferenceTarget(jCas);
      addToJCasIndex(rt);

      e.setReferent(rt);
    }

    return rt;
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(ImmutableSet.of(Entity.class), ImmutableSet.of(Entity.class));
  }
}
