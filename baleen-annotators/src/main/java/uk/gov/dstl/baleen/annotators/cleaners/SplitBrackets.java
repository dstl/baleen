//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Identify entities that include brackets, and split them into separate coreferenced entities
 */
public class SplitBrackets extends BaleenAnnotator {

	private static final Pattern ENDS_WITH_BRACKET = Pattern.compile("\\s*\\(([^\\(\\)]*?)\\)$");
	
	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		Collection<Entity> entities = JCasUtil.select(jCas, Entity.class);
		
		for(Entity e : entities){
			String text = e.getCoveredText();
			
			ReferenceTarget rt = e.getReferent();
			if(rt == null){
				rt = new ReferenceTarget(jCas);
				e.setReferent(rt);
			}
			
			Matcher m = ENDS_WITH_BRACKET.matcher(text);
			while(m.find()){
				//Split bracket off
				Entity eBracket = null;
				try {
					eBracket = e.getClass().getConstructor(JCas.class).newInstance(jCas);
					eBracket.setBegin(e.getBegin() + m.start(1));
					eBracket.setEnd(e.getBegin() + m.end(1));
					
					eBracket.setReferent(rt);
					
					addToJCasIndex(eBracket);
				} catch (Exception ex) {
					getMonitor().error("Unable to create new entity of class {}", e.getClass().getName(), ex);
				}
				
				text = text.substring(0, m.start());
				m = ENDS_WITH_BRACKET.matcher(text);
			}
			
			if(text.length() != e.getCoveredText().length()){
				e.setEnd(e.getBegin() + text.length());
				e.setValue(e.getCoveredText());
			}
		}
	}
	
	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(ImmutableSet.of(Entity.class), ImmutableSet.of(Entity.class));
	}
}