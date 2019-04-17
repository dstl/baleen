// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
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
import uk.gov.dstl.baleen.uima.BaleenTextAwareAnnotator;
import uk.gov.dstl.baleen.uima.data.TextBlock;

/**
 * Creates entity annotations for each piece of text that is the same as the covered text.
 *
 * <p>This is useful when a model is used (rather than a regex) and it only finds a subset of the
 * mentions in a document.
 *
 * <p>If an annotation of the same type already exists on the covering text then another is not
 * added.
 *
 * @baleen.javadoc
 */
public class MentionedAgain extends BaleenTextAwareAnnotator {
  /**
   * Should comparisons be done case sensitively?
   *
   * @baleen.config false
   */
  public static final String PARAM_CASE_SENSITIVE = "caseSensitive";

  @ConfigurationParameter(name = PARAM_CASE_SENSITIVE, defaultValue = "true")
  protected boolean caseSensitive;

  @Override
  protected void doProcessTextBlock(TextBlock block) throws AnalysisEngineProcessException {
    // We look through the JCas for the entities, but we only look for matches in this block
    String text = block.getCoveredText();

    Collection<Entity> list = JCasUtil.select(block.getJCas(), Entity.class);

    Set<String> existingSpans = new HashSet<>(list.size());
    Set<String> existingEntities = new HashSet<>();
    Set<Entity> entities = new HashSet<>();

    list.stream()
        .forEach(
            e -> {
              existingSpans.add(e.getBegin() + "//" + e.getEnd() + "//" + e.getType().getName());
              if (existingEntities.add(e.getType().getName() + "//" + e.getValue())) {
                // Only add entities of a new type and value
                entities.add(e);
              }
            });

    for (Entity e : entities) {
      Pattern pattern;
      if (caseSensitive) {
        pattern = Pattern.compile("\\b" + Pattern.quote(e.getCoveredText()) + "\\b");
      } else {
        pattern =
            Pattern.compile(
                "\\b" + Pattern.quote(e.getCoveredText()) + "\\b", Pattern.CASE_INSENSITIVE);
      }

      Matcher matcher = pattern.matcher(text);
      while (matcher.find()) {
        foundMatch(block, matcher, e, existingSpans);
      }
    }
  }

  private void foundMatch(TextBlock block, Matcher matcher, Entity e, Set<String> existingSpans) {
    if (existingSpans.contains(
        matcher.start() + "//" + matcher.end() + "//" + e.getType().getName())) return;

    try {
      Entity newEntity = e.getClass().getConstructor(JCas.class).newInstance(block.getJCas());

      newEntity.setBegin(block.toDocumentOffset(matcher.start()));
      newEntity.setEnd(block.toDocumentOffset(matcher.end()));
      newEntity.setValue(e.getValue());

      ReferenceTarget rt = e.getReferent();
      if (rt == null) {
        rt = new ReferenceTarget(block.getJCas());
        addToJCasIndex(rt);
        e.setReferent(rt);
      }
      newEntity.setReferent(rt);

      addToJCasIndex(newEntity);
    } catch (Exception ex) {
      getMonitor().warn("Unable to create new entitiy", ex);
    }
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(ImmutableSet.of(Entity.class), ImmutableSet.of(Entity.class));
  }
}
