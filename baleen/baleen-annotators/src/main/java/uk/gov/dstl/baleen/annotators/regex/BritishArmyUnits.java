//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.regex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.common.Organisation;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;
import uk.gov.dstl.baleen.uima.ComparableSpan;


/**
 * Find British Army Units using Regex
 * 
 * <p>A series of Regex are used to find British Army units (e.g. Platoons, Companies) and then they are aggregated based on hierarchy where possible.</p>
 * 
 * 
 */
public class BritishArmyUnits extends BaleenAnnotator {
	private final Pattern section = Pattern.compile("\\b\\d+ Sect\\b");
	private final Pattern platoon = Pattern.compile("\\b\\d+ Pl\\b");
	private final Pattern company = Pattern.compile("\\b[A-Z] Coy\\b");
	
	private static final int HIERARCHY_SECTION = 1;
	private static final int HIERARCHY_PLATOON = 2;
	private static final int HIERARCHY_COMPANY = 3;
	
	@Override
	public void doProcess(JCas aJCas) throws AnalysisEngineProcessException {
		
		String documentText = aJCas.getDocumentText();
	
		// 1. Find all sections, platoons, companies, etc.

		List<ComparableSpan> sectionSpans = ComparableSpan.buildSpans(documentText, section);
		List<ComparableSpan> platoonSpans = ComparableSpan.buildSpans(documentText, platoon);
		List<ComparableSpan> companySpans = ComparableSpan.buildSpans(documentText, company);
		
		Map<Integer, List<ComparableSpan>> hierarchySpans = new HashMap<>();
	
		hierarchySpans.put(HIERARCHY_SECTION, sectionSpans);
		hierarchySpans.put(HIERARCHY_PLATOON, platoonSpans);
		hierarchySpans.put(HIERARCHY_COMPANY, companySpans);
	
		
		// 2. Merge when spans are separated by a space or a comma, and the second span is higher in the hierarchy
		SortedSet<Integer> hierarchyLevels = new TreeSet<>(hierarchySpans.keySet());
		for(Integer level = hierarchyLevels.first(); level < hierarchyLevels.last(); level++){
			compareHierarchy(aJCas, hierarchySpans, level);
		}
		
		// 3. Add spans to JCas as organisations
		for(ComparableSpan span : hierarchySpans.get(hierarchyLevels.last())){
			Organisation org = new Organisation(aJCas);
			
			org.setConfidence(1.0);
			
			org.setBegin(span.getStart());
			org.setEnd(span.getEnd());
			org.setValue(span.getValue());

			addToJCasIndex(org);
		}
	}
	
	private void compareHierarchy(JCas aJCas, Map<Integer, List<ComparableSpan>> hierarchySpans, int level) {
		
		List<ComparableSpan> newSpans = hierarchySpans.get(level + 1);
		if(newSpans == null)
			newSpans = new ArrayList<>();
		
		for(ComparableSpan s1 : hierarchySpans.get(level)){
			ComparableSpan s = s1;
			for(ComparableSpan s2 : hierarchySpans.get(level + 1)){
				ComparableSpan t = mergeSpansIfPossible(s1, s2, aJCas);
				if(t != null){
					s = t;
					newSpans.remove(s2);
					break;
				}
			}
			
			newSpans.add(s);
		}
		
		hierarchySpans.put(level + 1, newSpans);
	}
	
	private ComparableSpan mergeSpansIfPossible(ComparableSpan s1, ComparableSpan s2, JCas jCas){
		if(s1.getStart() < s2.getEnd()) {
			String text = jCas.getDocumentText().substring(s1.getStart(), s2.getEnd());
			if(text.equals(s1.getValue() + " " + s2.getValue()) || text.equals(s1.getValue() + "," + s2.getValue()) || text.equals(s1.getValue() + ", " + s2.getValue())){
				return new ComparableSpan(s1.getStart(), s2.getEnd(), text);
			}
		}
		
		return null;
	}
}
