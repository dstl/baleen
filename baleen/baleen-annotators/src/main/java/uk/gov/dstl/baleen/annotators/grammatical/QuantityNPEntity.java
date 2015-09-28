//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.grammatical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.common.Quantity;
import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Annotate generic entities by looking for unmarked up NPs after '[[Quantity]] of',
 * and create relationships between entities.
 * 
 * <p>We find quantities that are followed by the word of, and then check to see if the following POS is a NP.
 * If it is, and it is unannotated, we can annotate it as an entity (although we can't determine the type of entity).</p>
 * <p>For example, in the phrase '7kg of blue powder was found hidden in the building', we should be able to identify blue powder as an entity.</p>
 * <p>A relationship is created between the quantity annotation and the entity annotation (or an existing entity annotation if one exists).</p>
 * <p>This annotator should be run towards the end of the pipeline, once language features and quantities have been annotated.</p>
 * 
 * 
 */
public class QuantityNPEntity extends BaleenAnnotator {
	private Pattern ofPattern = Pattern.compile("([\\h]*of[\\h]*).*", Pattern.CASE_INSENSITIVE);
	
	@Override
	public void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		Collection<PhraseChunk> phraseChunks = JCasUtil.select(jCas, PhraseChunk.class);
		Map<Integer, PhraseChunk> nounPhrases = new HashMap<>();
		
		for(PhraseChunk pc : phraseChunks){
			if("NP".equals(pc.getChunkType())){
				nounPhrases.put(pc.getBegin(), pc);
			}
		}
		Collection<Quantity> quantities = JCasUtil.select(jCas, Quantity.class);
		
		String text = jCas.getDocumentText();
		
		for(Quantity q : quantities){
			String followingText = text.substring(q.getEnd());
			Matcher m = ofPattern.matcher(followingText);
			
			if(m.matches()){
				int start = q.getEnd() + m.end(1);
				if(nounPhrases.containsKey(start)){
					PhraseChunk pc = nounPhrases.get(start);
					
					processNounPhrase(jCas, q, pc, text);
				}
			}
		}
	}

	private void processNounPhrase(JCas jCas, Quantity q, PhraseChunk pc, String text){
		List<Entity> coveredEntities = new ArrayList<>(JCasUtil.selectCovered(jCas, Entity.class, pc));
		if(coveredEntities.isEmpty()){
			Entity e = new Entity(jCas);
			e.setConfidence(pc.getConfidence());
			
			e.setBegin(pc.getBegin());
			e.setEnd(pc.getEnd());
			e.setValue(text.substring(pc.getBegin(), pc.getEnd()));
			
			addToJCasIndex(e);
			coveredEntities.add(e);
		}
		
		for(Entity e : coveredEntities){
			Relation r = new Relation(jCas);
			r.setBegin(q.getBegin());
			r.setEnd(e.getEnd());
			
			r.setConfidence(1.0);
			r.setSource(q);
			r.setTarget(e);
			
			r.setRelationshipType("QUANTITY");
			
			addToJCasIndex(r);
		}
	}
}
