// Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.language;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.maltparser.concurrent.ConcurrentMaltParserModel;
import org.maltparser.concurrent.ConcurrentMaltParserService;
import org.maltparser.concurrent.graph.ConcurrentDependencyGraph;
import org.maltparser.concurrent.graph.ConcurrentDependencyNode;
import org.maltparser.core.exception.MaltChainedException;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.language.Dependency;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordLemma;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Uses a MaltParser to create a dependency grammar.
 *
 * <p>See http://www.maltparser.org/ for more details of the implementation.
 *
 * <p>The English language model of maltparser is trained on the Penn Treebank corpus, and as such
 * it is is not freely licensed. To avoid this this project contains an English model trained from
 * the English universal dependencies dataset (http://universaldependencies.org/docs/) where the
 * original data is licensed under https://creativecommons.org/licenses/by-sa/4.0/. As such the
 * training data is licensed under the same agreement.
 *
 * <p>The universal dependency model uses their own tags. This annotator converts between the UD and
 * standard tags.
 *
 * <p>The MaltParser appears to be fast, low memory use and stable. As all trained algorithms it
 * will function only as well as its training set. We found the original Penn Treebank to be
 * (subjectively) better than the Universal Dependency model. However if an algorithm requires only
 * dependency distance or an understanding of word linkage the universal dependency model functions
 * well enough.
 *
 * <p>The output of this annotator is Dependency annotations.
 *
 * @baleen.javadoc
 */
public class MaltParser extends BaleenAnnotator {
  private static final String INTJ = "INTJ";
  private static final String PRON = "PRON";
  private static final String PART = "PART";
  private static final String PROPN = "PROPN";
  private static final String ADP = "ADP";
  private static final String X = "X";
  private static final String NOUN = "NOUN";
  private static final String ADV = "ADV";
  private static final String DET = "DET";
  private static final String NUM = "NUM";
  private static final String VERB = "VERB";
  private static final String CONJ = "CONJ";
  private static final String ADJ = "ADJ";
  private static final String SYM = "SYM";
  private static final String PUNCT = "PUNCT";

  private static final Map<String, String> PENN_TO_UNIVERSAL_TAGS = new HashMap<>();

  /**
   * The model file, (.mco), to be loaded into the parser.
   *
   * <p>If no file is provided, then the built in model (trained on Universal Dependency data) will
   * be used.
   *
   * @baleen.config
   */
  public static final String PARAM_FILE_NAME = "model";

  @ConfigurationParameter(name = MaltParser.PARAM_FILE_NAME, defaultValue = "")
  private String modelFilename;

  /**
   * Convert to POS annotations to Universal Dependendency tags before input.
   *
   * <p>This is required if the model is trained on a UD dataset.
   *
   * @baleen.config true
   */
  public static final String PARAM_CONVERT_TO_UD = "udTags";

  @ConfigurationParameter(name = MaltParser.PARAM_CONVERT_TO_UD, defaultValue = "true")
  private Boolean udTags;

  private ConcurrentMaltParserModel model;

  @Override
  public void doInitialize(final UimaContext aContext) throws ResourceInitializationException {
    super.doInitialize(aContext);

    File modelFile = null;
    if (!Strings.isNullOrEmpty(modelFilename)) modelFile = new File(modelFilename);

    if (modelFile == null || !modelFile.exists()) {
      // If the file doesn't exist then we will use try reading from the classpath.

      // Unfortunately Maltparser.doInitialise doesn't seem to like reading it from the Baleen
      // shaded Jar
      // So we copy it our and delete it on exit
      getMonitor().info("Dependency model not provided or does not exist, using built in model");

      InputStream is = getClass().getResourceAsStream("maltparser-universaldependencies-en.mco");
      if (is != null) {
        try {
          modelFile = File.createTempFile("baleen", "maltpaser-model");
          FileUtils.copyInputStreamToFile(is, modelFile);
          modelFile.deleteOnExit();
        } catch (IOException e) {
          getMonitor().error("Unable to copy internal model {}", e);
        }
      }
    }

    try {
      model = ConcurrentMaltParserService.initializeParserModel(modelFile);
    } catch (final MaltChainedException | MalformedURLException e) {
      throw new ResourceInitializationException(e);
    }

    udTags = udTags == null ? true : udTags;
  }

  @Override
  protected void doProcess(final JCas jCas) throws AnalysisEngineProcessException {

    for (final Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {

      final List<WordToken> wordTokens = JCasUtil.selectCovered(jCas, WordToken.class, sentence);

      final String[] tokens = new String[wordTokens.size()];

      int i = 0;
      for (final WordToken wt : wordTokens) {
        final String pos = wt.getPartOfSpeech();
        final String lemma = getLemma(wt);
        final String tag = udTags ? convertPennToUniversal(pos) : pos;
        tokens[i] =
            String.format("%d\t%s\t%s\t%s\t%s\t_", i + 1, wt.getCoveredText(), lemma, tag, pos);
        i++;
      }

      try {
        final ConcurrentDependencyGraph graph = model.parse(tokens);
        for (int j = 0; j < graph.nDependencyNodes(); j++) {
          final ConcurrentDependencyNode node = graph.getDependencyNode(j);

          if (node.hasHead()) {
            final Dependency dep = new Dependency(jCas);
            if (node.getHeadIndex() != 0) {
              dep.setGovernor(wordTokens.get(node.getHeadIndex() - 1));
              final String label = node.getLabel(7);
              dep.setDependencyType(label);
            } else {
              dep.setGovernor(wordTokens.get(node.getIndex() - 1));
              dep.setDependencyType("ROOT");
            }
            dep.setDependent(wordTokens.get(node.getIndex() - 1));
            dep.setBegin(dep.getDependent().getBegin());
            dep.setEnd(dep.getDependent().getEnd());
            addToJCasIndex(dep);
          }
        }

      } catch (final Exception e) {
        throw new AnalysisEngineProcessException(e);
      }
    }
  }

  /**
   * Gets the lemma.
   *
   * @param token the token
   * @return the lemma
   */
  private String getLemma(final WordToken token) {
    final FSArray array = token.getLemmas();
    if (array == null || array.size() == 0) {
      return "_";
    } else {
      return ((WordLemma) array.get(0)).getLemmaForm();
    }
  }

  static {
    // See http://universaldependencies.github.io/docs/tagset-conversion/en-penn-uposf.html
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("#", SYM);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("$", SYM);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("\"", PUNCT);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put(",", PUNCT);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("-LRB-", PUNCT);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("-RRB-", PUNCT);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put(".", PUNCT);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put(":", PUNCT);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("AFX", ADJ);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("CC", CONJ);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("CD", NUM);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("DT", DET);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("EX", ADV);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("FW", X);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("HYPH", PUNCT);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("IN", ADP);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("JJ", ADJ);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("JJR", ADJ);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("JJS", ADJ);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("LS", PUNCT);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("MD", VERB);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("NN", NOUN);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("NNP", PROPN);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("NNPS", PROPN);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("NNS", NOUN);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("PDT", DET);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("POS", PART);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("PRP", PRON);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("PRP$", DET);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("RB", ADV);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("RBR", ADV);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("RBS", ADV);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("RP", PART);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put(SYM, SYM);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("TO", PART);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("UH", INTJ);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("VB", VERB);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("VBD", VERB);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("VBG", VERB);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("VBN", VERB);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("VBP", VERB);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("VBZ", VERB);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("WDT", DET);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("WP", PRON);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("WP$", DET);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("WRB", ADV);
    MaltParser.PENN_TO_UNIVERSAL_TAGS.put("`", PUNCT);
  }

  /**
   * Convert penn to universal.
   *
   * @param tag the tag
   * @return the string
   */
  private String convertPennToUniversal(final String tag) {
    return MaltParser.PENN_TO_UNIVERSAL_TAGS.getOrDefault(tag, tag);
  }

  @Override
  public AnalysisEngineAction getAction() {
    return new AnalysisEngineAction(
        ImmutableSet.of(WordToken.class, Sentence.class, WordLemma.class),
        ImmutableSet.of(Dependency.class));
  }
}
