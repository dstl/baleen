package uk.gov.dstl.baleen.annotators.triage;

import com.google.common.collect.ImmutableSet;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.cas.FSArray;
import uk.gov.dstl.baleen.annotators.triage.impl.AbstractSentenceRankingSummarisation;
import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.resources.SharedStopwordResource;
import uk.gov.dstl.baleen.resources.utils.StopwordUtils;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordLemma;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.metadata.Metadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TokenFrequencySummarisation extends AbstractSentenceRankingSummarisation {

    /**
     * Connection to Stopwords Resource
     *
     * @baleen.resource uk.gov.dstl.baleen.resources.SharedStopwordResource
     */
    public static final String KEY_STOPWORDS = "stopwords";

    @ExternalResource(key = KEY_STOPWORDS)
    protected SharedStopwordResource stopwordResource;

    @Override
    protected Map<Sentence, Double> scoreSentences(Collection<Sentence> sentences) {
        Map<String, Integer> tokenFrequency = new HashMap<>();
        Map<Sentence, Double> sentenceScores = new HashMap<>();

        //Loop over collection first time to count tokens and assign a frequency
        for(Sentence sentence : sentences){
            Collection<WordToken> tokens = JCasUtil.selectCovered(WordToken.class, sentence);
            for(WordToken token : tokens){
                //Ignore punctuation, just numbers, etc.
                if(!token.getCoveredText().toLowerCase().matches("[a-z][-a-z0-9]*"))
                    continue;

                FSArray arr = token.getLemmas();
                if(arr == null || arr.size() == 0){
                    tokenFrequency.merge(token.getCoveredText().toLowerCase(), 1, (v1, v2) -> v1 + v2); //TODO: We should probably revert to stemming rather than just using the word?
                }else{
                    tokenFrequency.merge(token.getLemmas(0).getLemmaForm(), 1, (v1, v2) -> v1 + v2);
                }
            }
        }

        //Loop over collection second time to score sentences, ignoring stop words
        for(Sentence sentence : sentences){
            int score = 0;

            Collection<WordToken> tokens = JCasUtil.selectCovered(WordToken.class, sentence);   //TODO: This could be more efficient if we save value above?
            for(WordToken token : tokens){
                if(StopwordUtils.isStopWord(token.getCoveredText(), stopwordResource.getStopwords(), false))
                    continue;

                FSArray arr = token.getLemmas();
                if(arr == null || arr.size() == 0){
                    score += tokenFrequency.getOrDefault(token.getCoveredText().toLowerCase(), 0);
                }else{
                    score += tokenFrequency.getOrDefault(token.getLemmas(0).getLemmaForm(), 0);
                }
            }

            sentenceScores.put(sentence, (double) score);
        }

        return sentenceScores;
    }

    @Override
    public AnalysisEngineAction getAction() {
        return new AnalysisEngineAction(ImmutableSet.of(Sentence.class, WordToken.class, WordLemma.class), ImmutableSet.of(Metadata.class));
    }
}
