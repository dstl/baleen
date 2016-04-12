//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.grammatical;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * This annotator identifies locations by looking for a VBD TO NNP part of speech pattern,
 * where the NNP represents the location.
 * 
 * 
 */
public class TOLocationEntity extends BaleenAnnotator {

	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		List<WordToken> tokens = new ArrayList<>(JCasUtil.select(jCas, WordToken.class));
		
		for(int i = 0; i < tokens.size() - 2; i++){
			if("VBD".equals(tokens.get(i).getPartOfSpeech()) && "TO".equals(tokens.get(i + 1).getPartOfSpeech()) && "NNP".equals(tokens.get(i + 2).getPartOfSpeech())){
				Location l = new Location(jCas);
				l.setBegin(tokens.get(i + 2).getBegin());
				l.setEnd(findNNPEnd(tokens, i + 2));
				addToJCasIndex(l);
			}
		}
	}
	
	private int findNNPEnd(List<WordToken> tokens, int firstToken){
		int end = tokens.get(firstToken).getEnd();
		
		for(int j = firstToken + 1; j < tokens.size(); j++){
			if("NNP".equals(tokens.get(j).getPartOfSpeech())){
				end = tokens.get(j).getEnd();
			} else {
				// Finished sequence of contiguous NNP following VBD, TO,
				// Need to stop now or may encounter another NNP elsewhere
				// in the document that has nothing to do with this location.
				break;
			}
		}
		
		return end;
	}
}
