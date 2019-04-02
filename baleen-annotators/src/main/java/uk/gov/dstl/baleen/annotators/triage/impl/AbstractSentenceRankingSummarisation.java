package uk.gov.dstl.baleen.annotators.triage.impl;

import com.google.common.collect.ImmutableSet;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Abstract class for performing extractive summarisation based on scoring sentences.
 * The top X (where X is selected by the user) sentences will be kept and used to form the summary,
 * and can either be returned in document order or ranking order. The summary will be added as Metadata
 * to the document.
 */
public abstract class AbstractSentenceRankingSummarisation extends BaleenAnnotator {

    /**
     * The maximum number of sentences to use in the summary
     *
     * @baleen.config 5
     */
    public static final String PARAM_NUM_SENTENCES = "sentences";

    @ConfigurationParameter(name = PARAM_NUM_SENTENCES, defaultValue = "5")
    protected Integer numSentences;

    /**
     * Should sentences be returned in document order?
     * If false, then they are returned in ranking order.
     *
     * @baleen.config true
     */
    public static final String PARAM_DOCUMENT_ORDER = "documentOrder";

    @ConfigurationParameter(name = PARAM_DOCUMENT_ORDER, defaultValue = "true")
    protected boolean documentOrder;

    /**
     * Metadata key name to use
     *
     * @baleen.config autoSummary
     */
    public static final String PARAM_KEY_NAME = "key";

    @ConfigurationParameter(name = PARAM_KEY_NAME, defaultValue = "autoSummary")
    protected String keyName;

    private static final List<String> END_OF_SENTENCE = Arrays.asList(".", "!", "?");

    @Override
    protected final void doProcess(JCas jCas) {
        Collection<Sentence> sentences = JCasUtil.select(jCas, Sentence.class);
        Map<Sentence, Double> sentenceScores = scoreSentences(sentences);

        List<Sentence> topSentences = sentenceScores.entrySet().stream().sorted((e1, e2) -> -Double.compare(e1.getValue(), e2.getValue()))
                .limit(numSentences)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if(documentOrder)
            topSentences.sort(Comparator.comparingInt(Annotation::getBegin));

        String summary = topSentences.stream().map(s -> s.getCoveredText().trim())
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    String lastChar = s.substring(s.length() - 1);
                    if(!END_OF_SENTENCE.contains(lastChar))
                        return s + ".";

                    return s;
                })
                .collect(Collectors.joining(" "));

        Metadata metadata = new Metadata(jCas);
        metadata.setKey(keyName);
        metadata.setValue(summary);

        addToJCasIndex(metadata);
    }

    protected abstract Map<Sentence, Double> scoreSentences(Collection<Sentence> sentences);

    @Override
    public AnalysisEngineAction getAction() {
        return new AnalysisEngineAction(ImmutableSet.of(Sentence.class), ImmutableSet.of(Metadata.class));
    }
}
