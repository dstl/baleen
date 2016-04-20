//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Clean up the punctuation in entities
 * 
 * <p>Cleans punctuation by doing the following:</p>
 * <ul>
 * <li>Removing any characters from the start or end of a string that aren't alphanumeric, brackets or currency symbols</li>
 * <li>Counting brackets and if there is a mismatch, and the offending bracket
 * appears at the start or end of the string, then it is removed</li>
 * <li>Removing any entities that are empty following the above actions</li>
 * </ul>
 * This annotator handles the value and the covered text of the entity, and will
 * address them separately if required.
 * 
 * 
 */
public class CleanPunctuation extends BaleenAnnotator {
	private static final String ALLOWED_CHARACTERS_START_END = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789()£$€-";

	@Override
	public void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		Collection<Entity> annotations = JCasUtil.select(jCas, Entity.class);
		Collection<Entity> toRemove = new ArrayList<>();
		
		for (Entity e : annotations) {
			if (!e.getCoveredText().isEmpty()) {
				cleanCoveredText(e);

				if (e.getCoveredText().isEmpty()) {
					toRemove.add(e);
					continue;
				}
			}

			if (e.getValue() != null) {
				e.setValue(cleanValue(e.getValue()));
			}
		}
		
		removeFromJCasIndex(toRemove);
	}

	private void cleanCoveredText(Entity e) {
		int begin = e.getBegin();
		int end = e.getEnd();
		
		// Remove punctuation from the coverText

		String coverText = e.getCoveredText();
		String startStripped = removeStartPunctuation(coverText);
		String bothStripped = removeEndPunctuation(startStripped);

		begin  = begin + (coverText.length() - startStripped.length());
		end = begin + bothStripped.length();

		// Remove excessive brackets

		int openingBrackets = StringUtils.countMatches(bothStripped, "(");
		int closingBrackets = StringUtils.countMatches(bothStripped, ")");

		if (openingBrackets > closingBrackets) {
			int diff = openingBrackets - closingBrackets;
			for (int i = 0; i < diff; i++) {
				if (bothStripped.charAt(i) == '(') {
					begin += 1;
				} else {
					break;
				}
			}
		} else if (openingBrackets < closingBrackets) {
			int diff = closingBrackets - openingBrackets;
			for (int i = bothStripped.length() - 1; i > coverText.length() - 1
					- diff; i--) {
				if (bothStripped.charAt(i) == ')') {
					end -= 1;
				} else {
					break;
				}
			}
		}
		
		e.setBegin(begin);
		e.setEnd(end);
	}

	private String removeStartPunctuation(String s){
		String ret = s;
		
		while(ret.length() > 0 && !ALLOWED_CHARACTERS_START_END.contains(ret.substring(0, 1))){
			ret = ret.substring(1);
		}
		
		return ret;
	}
	
	private String removeEndPunctuation(String s){
		String ret = s;
		
		while(ret.length() > 0 && !ALLOWED_CHARACTERS_START_END.contains(ret.substring(ret.length() - 1))){
			ret = ret.substring(0, ret.length() - 1);
		}
		
		return ret;
	}
	
	private String cleanValue(String inputValue) {
		// Remove puncations from the value

		String value = removeEndPunctuation(removeStartPunctuation(inputValue));

		// Remove excess brackets

		int openingBrackets = StringUtils.countMatches(value, "(");
		int closingBrackets = StringUtils.countMatches(value, ")");

		if (openingBrackets > closingBrackets) {
			int diff = openingBrackets - closingBrackets;
			for (int i = 0; i < diff; i++) {
				if (value.startsWith("(")) {
					value = value.substring(1);
				} else {
					break;
				}
			}
		} else if (openingBrackets < closingBrackets) {
			int diff = closingBrackets - openingBrackets;
			for (int i = 0; i < diff; i++) {
				if (value.endsWith(")")) {
					value = value.substring(0, value.length() - 1);
				} else {
					break;
				}
			}
		}

		return value;
	}
}
