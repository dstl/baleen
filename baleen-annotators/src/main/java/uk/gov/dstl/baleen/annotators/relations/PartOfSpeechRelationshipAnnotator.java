// Copyright (c) Committed Software 2018, opensource@committed.io
package uk.gov.dstl.baleen.annotators.relations;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.annotators.relations.helpers.AbstractTypedRelationshipAnnotator;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;

/**
 * Extract relations by Part Of Speech regular expression.
 *
 * <p>The 'regular expression' follows all the normal rules of regular expressions except that all
 * the words should be replaced by part of speech tags. A list of tags and their definition is
 * below. Sentences are processed separately and this annotator requires that the Part Of Speech
 * tags have already been added (say by the OpenNLP annotator).
 *
 * <p>The source, target and value capturing groups should be configured to match the pattern. For
 * example,
 *
 * <pre>
 * (NNP).*(VBD).*(NNP)
 * </pre>
 *
 * Will match any sentence containing a proper noun followed by a past tense verb and then another
 * proper noun, with anything in between. In this example we map group 1 to the source, group 2 to
 * the value and group 3 to the target. It is required that the source and target also align with
 * extracted entities to be able to create the relation.
 *
 * <p>A type, subType to assign can be supplied.
 *
 * <p>An additional W tag is also added to match any word, regardless of the part of speech and (by
 * default but optionally) higher level tags such as NN will also match their more specific tags
 * such as NNP and NNS.
 *
 * <p>
 *
 * <table summary="">
 * <tr>
 * <th align="left">Tag</th>
 * <th align="left">Description</th>
 * <th align="left">Example</th>
 * </tr>
 * <tr>
 * <td><code>CC  </code></td>
 * <td>coordinating conjunction</td>
 * <td>and</td>
 * </tr>
 * <tr>
 * <td><code>CD  </code></td>
 * <td>cardinal number</td>
 * <td>1, third</td>
 * </tr>
 * <tr>
 * <td><code>DT  </code></td>
 * <td>determiner</td>
 * <td>the</td>
 * </tr>
 * <tr>
 * <td><code>EX  </code></td>
 * <td>existential there</td>
 * <td>there is</td>
 * </tr>
 * <tr>
 * <td><code>FW  </code></td>
 * <td>foreign word</td>
 * <td>d’hoevre</td>
 * </tr>
 * <tr>
 * <td><code>IN  </code></td>
 * <td>preposition, subordinating conjunction</td>
 * <td>in, of, like</td>
 * </tr>
 * <tr>
 * <td><code>IN/t</code></td>
 * <td>hat that as subordinator</td>
 * <td>that</td>
 * </tr>
 * <tr>
 * <td><code>JJ  </code></td>
 * <td>adjective</td>
 * <td>green</td>
 * </tr>
 * <tr>
 * <td><code>JJR </code></td>
 * <td>adjective, comparative</td>
 * <td>greener</td>
 * </tr>
 * <tr>
 * <td><code>JJS </code></td>
 * <td>adjective, superlative</td>
 * <td>greenest</td>
 * </tr>
 * <tr>
 * <td><code>LS  </code></td>
 * <td>list marker</td>
 * <td>1)</td>
 * </tr>
 * <tr>
 * <td><code>MD  </code></td>
 * <td>modal</td>
 * <td>could, will</td>
 * </tr>
 * <tr>
 * <td><code>NN  </code></td>
 * <td>noun, singular or mass</td>
 * <td>table</td>
 * </tr>
 * <tr>
 * <td><code>NNS </code></td>
 * <td>noun plural</td>
 * <td>tables</td>
 * </tr>
 * <tr>
 * <td><code>NNP  </code></td>
 * <td>proper noun, singular</td>
 * <td>John</td>
 * </tr>
 * <tr>
 * <td><code>NNPS </code></td>
 * <td>proper noun, plural</td>
 * <td>Vikings</td>
 * </tr>
 * <tr>
 * <td><code>PDT </code></td>
 * <td>predeterminer</td>
 * <td>both the boys</td>
 * </tr>
 * <tr>
 * <td><code>POS </code></td>
 * <td>possessive ending</td>
 * <td>friend’s</td>
 * </tr>
 * <tr>
 * <td><code>PP  </code></td>
 * <td>personal pronoun</td>
 * <td>I, he, it</td>
 * </tr>
 * <tr>
 * <td><code>PP$ </code></td>
 * <td>possessive pronoun</td>
 * <td>my, his</td>
 * </tr>
 * <tr>
 * <td><code>RB  </code></td>
 * <td>adverb</td>
 * <td>however, usually</td>
 * </tr>
 * <tr>
 * <td><code>RBR </code></td>
 * <td>adverb, comparative</td>
 * <td>better</td>
 * </tr>
 * <tr>
 * <td><code>RBS </code></td>
 * <td>adverb, superlative</td>
 * <td>best</td>
 * </tr>
 * <tr>
 * <td><code>RP  </code></td>
 * <td>particle</td>
 * <td>give up</td>
 * </tr>
 * <tr>
 * <td><code>SYM </code></td>
 * <td>Symbol</td>
 * <td>/ [ = *</td>
 * </tr>
 * <tr>
 * <td><code>TO  </code></td>
 * <td>infinitive</td>
 * <td>‘to’ togo</td>
 * </tr>
 * <tr>
 * <td><code>UH  </code></td>
 * <td>interjection</td>
 * <td>uhhuhhuhh</td>
 * </tr>
 * <tr>
 * <td><code>VB  </code></td>
 * <td>verb be, base form</td>
 * <td>be</td>
 * </tr>
 * <tr>
 * <td><code>VBD </code></td>
 * <td>verb be, past tense</td>
 * <td>was, were</td>
 * </tr>
 * <tr>
 * <td><code>VBG </code></td>
 * <td>verb be, gerund/present participle</td>
 * <td>being</td>
 * </tr>
 * <tr>
 * <td><code>VBN </code></td>
 * <td>verb be, past participle</td>
 * <td>been</td>
 * </tr>
 * <tr>
 * <td><code>VBP </code></td>
 * <td>verb be, sing. present, non-3d</td>
 * <td>am, are</td>
 * </tr>
 * <tr>
 * <td><code>VBZ </code></td>
 * <td>verb be, 3rd person sing. present</td>
 * <td>is</td>
 * </tr>
 * <tr>
 * <td><code>VH  </code></td>
 * <td>verb have, base form</td>
 * <td>have</td>
 * </tr>
 * <tr>
 * <td><code>VHD </code></td>
 * <td>verb have, past tense</td>
 * <td>had</td>
 * </tr>
 * <tr>
 * <td><code>VHG </code></td>
 * <td>verb have, gerund/present participle</td>
 * <td>having</td>
 * </tr>
 * <tr>
 * <td><code>VHN </code></td>
 * <td>verb have, past participle</td>
 * <td>had</td>
 * </tr>
 * <tr>
 * <td><code>VHP </code></td>
 * <td>verb have, sing. present, non-3d</td>
 * <td>have</td>
 * </tr>
 * <tr>
 * <td><code>VHZ </code></td>
 * <td>verb have, 3rd person sing. present</td>
 * <td>has</td>
 * </tr>
 * <tr>
 * <td><code>VV  </code></td>
 * <td>verb, base form</td>
 * <td>take</td>
 * </tr>
 * <tr>
 * <td><code>VVD </code></td>
 * <td>verb, past tense</td>
 * <td>took</td>
 * </tr>
 * <tr>
 * <td><code>VVG </code></td>
 * <td>verb, gerund/present participle</td>
 * <td>taking</td>
 * </tr>
 * <tr>
 * <td><code>VVN </code></td>
 * <td>verb, past participle</td>
 * <td>taken</td>
 * </tr>
 * <tr>
 * <td><code>VVP </code></td>
 * <td>verb, sing. present, non-3d</td>
 * <td>take</td>
 * </tr>
 * <tr>
 * <td><code>VVZ </code></td>
 * <td>verb, 3rd person sing. present</td>
 * <td>takes</td>
 * </tr>
 * <tr>
 * <td><code>WDT </code></td>
 * <td>wh-determiner</td>
 * <td>which</td>
 * </tr>
 * <tr>
 * <td><code>WP  </code></td>
 * <td>wh-pronoun</td>
 * <td>who, what</td>
 * </tr>
 * <tr>
 * <td><code>WP$ </code></td>
 * <td>possessive wh-pronoun</td>
 * <td>whose</td>
 * </tr>
 * <tr>
 * <td><code>WRB </code></td>
 * <td>wh-abverb</td>
 * <td>where, when</td>
 * </tr>
 * <tr>
 * <td><code>W </code></td>
 * <td>any word</td>
 * <td>regardless of part of speech</td>
 * </tr>
 * </table>
 *
 * @baleen.javadoc
 */
public class PartOfSpeechRelationshipAnnotator extends AbstractTypedRelationshipAnnotator {

  private static final String ID_PATTERN = "\\d+";

  private static final Set<String> IGNORE_TOKENS =
      ImmutableSet.of(".", ",", "/", "[", "]", "=", "*");
  private static final Set<String> MAP_POS = ImmutableSet.of("SYM");

  private static final Pattern GROUPER = Pattern.compile("(\\w+)");

  private static final Pattern TAGS =
      Pattern.compile(
          "(W|CC|CD|DT|EX|FW|IN|JJ|JJR|JJS|LS|MD|NN|NNS|NNP|NNPS|PDT|POS|PRP|PRP$|RB|RBR|RBS|RP|SYM|TO|UH|VB|VBD|VBG|VBN|VBP|VBZ|WDT|WP|WP$|WRB)");

  /**
   * Expand tags to include all forms, NN also match NNP and NNS.
   *
   * @baleen.config false
   */
  public static final String PARAM_EXPAND_TAGS = "expandTags";

  @ConfigurationParameter(name = PARAM_EXPAND_TAGS, defaultValue = "true")
  private boolean expandTags = true;

  /**
   * Which group in the regular expression should be used as the source?
   *
   * @baleen.config 1
   */
  public static final String PARAM_SOURCE_GROUP = "sourceGroup";

  @ConfigurationParameter(name = PARAM_SOURCE_GROUP, defaultValue = "1")
  private int sourceGroup;

  /**
   * Which group in the regular expression should be used as the value? defaults to the full match.
   *
   * @baleen.config 0
   */
  public static final String PARAM_VALUE_GROUP = "valueGroup";

  @ConfigurationParameter(name = PARAM_VALUE_GROUP, defaultValue = "0")
  private int valueGroup;

  /**
   * Which group in the regular expression should be used as the target?
   *
   * @baleen.config 2
   */
  public static final String PARAM_TARGET_GROUP = "targetGroup";

  @ConfigurationParameter(name = PARAM_TARGET_GROUP, defaultValue = "2")
  private int targetGroup;

  /**
   * Stop word, so not convert these words to tags.
   *
   * @baleen.config false
   */
  public static final String PARAM_STOP_WORDS = "stopWords";

  @ConfigurationParameter(name = PARAM_STOP_WORDS, mandatory = false)
  private String[] stopWordStrings = new String[0];

  /**
   * The regular expression to search for
   *
   * @baleen.config
   */
  public static final String PARAM_PATTERN = "pattern";

  @ConfigurationParameter(name = PARAM_PATTERN, defaultValue = "")
  private String pattern;

  private Pattern p = null;

  // Parse the group config parameters into these variables to avoid issues with parameter types

  private Set<String> stopWords;

  @Override
  public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);

    stopWords = new HashSet<>(Arrays.asList(stopWordStrings));

    Matcher groups = GROUPER.matcher(pattern);
    List<Integer> offsets = new ArrayList<>();
    while (groups.find()) {
      if (TAGS.matcher(groups.group(1)).matches()) {
        offsets.add(groups.end(1));
      }
    }

    StringBuilder sb = new StringBuilder(pattern);
    Collections.reverse(offsets);
    for (Integer offset : offsets) {
      sb.insert(offset, ID_PATTERN);
    }

    String ammended = sb.toString().replace("W\\d+", "\\w+?");
    // @formatter:off
    if (expandTags) {
      ammended =
          ammended
              .replaceAll("NN(?=\\\\d)", "NN(?:[SP]|SP)?")
              .replaceAll("VB(?=\\\\d)", "VB[DGNPZ]?")
              .replaceAll("JJ(?=\\\\d)", "JJ[RS]?")
              .replaceAll("RB(?=\\\\d)", "RB[RS]?")
              .replaceAll("WP(?=\\\\d)", "WP[$]?");
    }
    // @formatter:on

    p = Pattern.compile(ammended, Pattern.CASE_INSENSITIVE);
    getMonitor().debug("The regular expression is \"{}\"", p.pattern());
  }

  @Override
  protected void extract(JCas jCas) throws AnalysisEngineProcessException {

    Map<WordToken, List<Entity>> coveredEntities =
        JCasUtil.indexCovering(jCas, WordToken.class, Entity.class);

    Map<Sentence, List<WordToken>> sentences =
        JCasUtil.indexCovered(jCas, Sentence.class, WordToken.class);

    sentences.forEach((s, tokens) -> processSentence(jCas, s, sort(tokens), coveredEntities));
  }

  private List<WordToken> sort(Collection<WordToken> tokens) {
    List<WordToken> list = new ArrayList<>(tokens);
    Collections.sort(list, Comparator.comparing(WordToken::getBegin));
    return list;
  }

  private void processSentence(
      JCas jCas, Sentence s, List<WordToken> wordTokens, Map<WordToken, List<Entity>> entities) {

    Map<String, WordToken> idMap = new HashMap<>();

    StringBuilder builder = new StringBuilder();

    int last = s.getBegin();
    for (WordToken wt : wordTokens) {
      for (; last < wt.getBegin(); last++) {
        builder.append(" ");
      }
      last = wt.getEnd();

      String pos = wt.getPartOfSpeech();
      if (IGNORE_TOKENS.contains(pos)) {
        builder.append(pos);
        continue;
      }

      String coveredText = wt.getCoveredText();
      if (MAP_POS.contains(pos) || stopWords.contains(coveredText)) {
        builder.append(coveredText);
        continue;
      }

      String identifier = toWordTokenIdentifier(wt);
      idMap.put(identifier, wt);
      builder.append(identifier);
    }

    String substitutedText = builder.toString();

    Matcher m = p.matcher(substitutedText);
    while (m.find()) {
      String sourceString = m.group(sourceGroup);
      String targetString = m.group(targetGroup);

      WordToken sourceToken = idMap.get(sourceString);
      WordToken targetToken = idMap.get(targetString);

      Collection<Entity> sources = entities.get(sourceToken);
      Collection<Entity> targets = entities.get(targetToken);

      if (sources.isEmpty() || targets.isEmpty()) {
        continue;
      }

      Entity source = sources.iterator().next();
      Entity target = targets.iterator().next();

      String valueTokens = m.group(valueGroup);
      String[] split = valueTokens.split("\\s");

      String value =
          Arrays.asList(split).stream()
              .map(idMap::get)
              .map(WordToken::getCoveredText)
              .collect(Collectors.joining(" "));

      int begin = Math.min(source.getBegin(), target.getBegin());
      int end = Math.max(source.getEnd(), target.getEnd());

      addToJCasIndex(createRelation(jCas, source, target, begin, end, value));
    }
  }

  private String toWordTokenIdentifier(WordToken entity) {
    StringBuilder builder = new StringBuilder();
    builder.append(entity.getPartOfSpeech());
    builder.append(entity.getInternalId());
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
