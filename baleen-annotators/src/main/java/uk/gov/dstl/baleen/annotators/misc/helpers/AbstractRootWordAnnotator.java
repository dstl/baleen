//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc.helpers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.BaleenTextAwareAnnotator;
import uk.gov.dstl.baleen.uima.data.TextBlock;

/**
 * Abstract annotator that identifies a root word indicative of an entity,
 * and then identifies modifiers/descriptive words prior to that root word
 * to build up a complete entity.
 * 
 * For example, it might identify 'car' as a vehicle, and then 'red' as a modifier
 * to produce an entity 'red car'.
 */
public abstract class AbstractRootWordAnnotator<T extends Entity> extends BaleenTextAwareAnnotator{
	protected static final Set<Class<? extends Annotation>> requiredInputs = ImmutableSet.of(Sentence.class, WordToken.class);
	
	public static final List<String> STOPWORDS = Arrays.asList("and", "or");
	
	@Override
	protected void doProcessTextBlock(TextBlock textBlock) throws AnalysisEngineProcessException {
		Collection<Sentence> sentences = textBlock.select(Sentence.class);
		
		if(sentences.isEmpty()){
			//No sentences, use whole text block
			processSentence(textBlock.getJCas(), textBlock.select(WordToken.class));
		}else{
			//Process each sentence in turn
			for(Sentence s : sentences){
				processSentence(textBlock.getJCas(), JCasUtil.selectCovered(WordToken.class, s));
			}
		}
		
	}
	
	protected void processSentence(JCas jCas, Collection<WordToken> wordTokens){
		WordToken wtPrevDesc = null;
		
		for(WordToken wt : wordTokens){
			String word = wt.getCoveredText().toLowerCase();
			
			String entityType = isEntity(word);
			if(entityType != null){
				Entity e = createEntity(jCas);
				
				if(wtPrevDesc == null){
					e.setBegin(wt.getBegin());
				}else{
					e.setBegin(wtPrevDesc.getBegin());
				}
				
				e.setEnd(wt.getEnd());
				e.setSubType(entityType);
				
				addToJCasIndex(e);
				wtPrevDesc = null;
			}else if(isDescriptiveWord(word)){
				if(wtPrevDesc == null)
					wtPrevDesc = wt;
			}else if(!isStopWord(word)){
				wtPrevDesc = null;
			}
		}
	}
	
	protected boolean isStopWord(String word){
		return STOPWORDS.contains(word);
	}
	
	protected abstract String isEntity(String word);
	
	protected abstract boolean isDescriptiveWord(String word);
	
	protected abstract T createEntity(JCas jCas);
}