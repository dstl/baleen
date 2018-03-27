// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.relations;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections.ComparatorUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.relations.helpers.AbstractTypedRelationshipAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.utils.TypeSystemSingleton;
import uk.gov.dstl.baleen.uima.utils.TypeUtils;

/**
 * Extract relations by regular expression.
 * <p>
 * The regular expression should include a source and target using <code>(:Type:)<code/>. The
 * source, target and value capturing groups should be configured to match the pattern. For example,
 *
 * <pre>
 * (:Person:)\\s+(?:visit\\w*|went)\\s+(:Location:)
 * </pre>
 *
 * <p>
 * A type, subType to assign can be supplied.
 *
 * @baleen.javadoc
 */
public class RegExRelationshipAnnotator extends AbstractTypedRelationshipAnnotator {

  private static final String SEPARATOR = ":";

  private static final String ID_PATTERN = "\\d+";

  private static final Pattern GROUPER = Pattern.compile(":(\\w+?):");

  /**
   * Is the regular expression case sensitive?
   *
   * @baleen.config false
   */
  public static final String PARAM_CASE_SENSITIVE = "caseSensitive";

  @ConfigurationParameter(name = PARAM_CASE_SENSITIVE, defaultValue = "false")
  private boolean caseSensitive = false;

  /**
   * Which group in the regular expression should be used as the source?
   *
   * @baleen.config 1
   */
  public static final String PARAM_SOURCE_GROUP = "sourceGroup";

  @ConfigurationParameter(name = PARAM_SOURCE_GROUP, defaultValue = "1")
  private int sourceGroup = 1;

  /**
   * Which group in the regular expression should be used as the value? defaults to the full match.
   *
   * @baleen.config 0
   */
  public static final String PARAM_VALUE_GROUP = "valueGroup";

  @ConfigurationParameter(name = PARAM_VALUE_GROUP, defaultValue = "0")
  private int valueGroup = 0;

  /**
   * Which group in the regular expression should be used as the target?
   *
   * @baleen.config 2
   */
  public static final String PARAM_TARGET_GROUP = "targetGroup";

  @ConfigurationParameter(name = PARAM_TARGET_GROUP, defaultValue = "2")
  private int targetGroup = 2;

  /**
   * The regular expression to search for
   *
   * @baleen.config
   */
  public static final String PARAM_PATTERN = "pattern";

  @ConfigurationParameter(name = PARAM_PATTERN, defaultValue = "")
  private String pattern;

  private Pattern p = null;

  private Class<? extends Entity> sourceType;
  private Class<? extends Entity> targetType;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);

    try {

      Matcher groups = GROUPER.matcher(pattern);
      groups.find();
      String sourceString = groups.group(1);
      int sourceOffset = groups.end(1);
      groups.find();
      String targetString = groups.group(1);
      int targetOffset = groups.end(1);

      JCas tempJcas =
          JCasFactory.createJCas(TypeSystemSingleton.getTypeSystemDescriptionInstance());

      sourceType = TypeUtils.getEntityClass(sourceString, tempJcas);
      targetType = TypeUtils.getEntityClass(targetString, tempJcas);

      String ammended =
          new StringBuilder(pattern)
              .insert(targetOffset, ID_PATTERN)
              .insert(sourceOffset, ID_PATTERN)
              .toString();
      if (caseSensitive) {
        p = Pattern.compile(ammended);
      } else {
        p = Pattern.compile(ammended, Pattern.CASE_INSENSITIVE);
      }
      getMonitor().debug("The regular expression is \"{}\"", p.pattern());

    } catch (UIMAException | BaleenException e) {
      throw new ResourceInitializationException(e);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void extract(JCas jCas) throws AnalysisEngineProcessException {

    Map<String, Entity> idMap = new HashMap<>();

    JCasUtil.select(jCas, sourceType).forEach(e -> idMap.put(toEntityIdentifier(e, sourceType), e));
    if (!targetType.equals(sourceType)) {
      JCasUtil.select(jCas, targetType)
          .forEach(e -> idMap.put(toEntityIdentifier(e, targetType), e));
    }

    StringBuilder builder = new StringBuilder();
    String documentText = jCas.getDocumentText();

    List<Entry<String, Entity>> sorted =
        (List<Entry<String, Entity>>)
            idMap
                .entrySet()
                .stream()
                .sorted(
                    Entry.comparingByValue(
                        ComparatorUtils.reversedComparator(Comparator.comparing(Entity::getBegin))))
                .collect(Collectors.toList());

    int processedTo = documentText.length();
    for (Entry<String, Entity> entry : sorted) {
      String id = entry.getKey();
      Entity entity = entry.getValue();
      int entityBegin = entity.getBegin();
      int entityEnd = entity.getEnd();
      if (entityEnd > processedTo) {
        continue; // Ignore overlapping entities
      }

      builder.insert(0, documentText.substring(entityEnd, processedTo));
      builder.insert(0, id);
      processedTo = entityBegin;
    }

    builder.insert(0, documentText.substring(0, processedTo));

    String substitutedText = builder.toString();

    Matcher m = p.matcher(substitutedText);
    while (m.find()) {
      String sourceString = m.group(sourceGroup);
      String targetString = m.group(targetGroup);

      Entity source = idMap.get(sourceString);
      Entity target = idMap.get(targetString);

      String sourceValue = Optional.ofNullable(source.getValue()).orElse("");
      String targetValue = Optional.ofNullable(target.getValue()).orElse("");

      String value =
          Optional.ofNullable(m.group(valueGroup))
              .map(s -> s.replace(sourceString, sourceValue).replace(targetString, targetValue))
              .orElse("");

      int begin = Math.min(source.getBegin(), target.getBegin());
      int end = Math.max(source.getEnd(), target.getEnd());

      addToJCasIndex(createRelation(jCas, source, target, begin, end, value));
    }
  }

  private String toEntityIdentifier(Entity entity, Class<? extends Entity> type) {
    StringBuilder builder = new StringBuilder();
    builder.append(SEPARATOR);
    builder.append(type.getSimpleName());
    builder.append(entity.getInternalId());
    builder.append(SEPARATOR);
    return builder.toString();
  }

  @Override
  public void doDestroy() {
    pattern = null;
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(ImmutableSet.of(Entity.class), ImmutableSet.of(Relation.class));
  }
}
