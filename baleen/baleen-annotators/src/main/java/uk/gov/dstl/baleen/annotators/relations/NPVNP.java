package uk.gov.dstl.baleen.annotators.relations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Identifies relationships that have the NP-V(W*P)?-NP pattern,
 * where NP is a Noun Phrase, V is a Verb, W is any word, and P is a preposition.
 * 
 * TODO: Are we covering all acceptable phrases here, or should we use full parse tags
 */
public class NPVNP extends BaleenAnnotator {

	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		List<PhraseChunk> nounPhrases = JCasUtil.select(jCas, PhraseChunk.class).stream()
				.filter(c -> "NP".equals(c.getChunkType()))
				.collect(Collectors.toList());
		
		for(int i = 0; i < nounPhrases.size() - 1; i++){
			PhraseChunk np = nounPhrases.get(i);
			PhraseChunk next = nounPhrases.get(i + 1);
			
			processNounPhrase(jCas, np, next);
		}
	}

	private void processNounPhrase(JCas jCas, PhraseChunk current, PhraseChunk next){
		List<PhraseChunk> middle = JCasUtil.selectBetween(jCas, PhraseChunk.class, current, next);
		
		if(middle.isEmpty() || !"VP".equals(middle.get(0).getChunkType()))
			return;
		
		if(middle.size() == 1 || "PP".equals(middle.get(middle.size() - 1).getChunkType())){
			Entity e1 = new Entity(jCas, current.getBegin(), current.getEnd());
			Entity e2 = new Entity(jCas, next.getBegin(), next.getEnd());
			
			List<Entity> source = new ArrayList<>();
			List<Entity> target = new ArrayList<>();
			
			List<Entity> e1Entities = JCasUtil.selectCovered(jCas, Entity.class, e1);
			findMatchingEntities(e1Entities, e1, source);
			
			List<Entity> e2Entities = JCasUtil.selectCovered(jCas, Entity.class, e2);
			findMatchingEntities(e2Entities, e2, target);
			
			createRelations(jCas, source, target, jCas.getDocumentText().substring(current.getEnd(), next.getBegin()).trim());
		}
	}
	
	private void createRelations(JCas jCas, List<Entity> source, List<Entity> target, String text){
		for(Entity eSource : source){
			for(Entity eTarget : target){
				Relation relation = new Relation(jCas);
				relation.setBegin(eSource.getBegin());
				relation.setEnd(eTarget.getEnd());
				relation.setValue(text);
				relation.setSource(eSource);
				relation.setTarget(eTarget);
				relation.setRelationshipType("unknown");
				
				addToJCasIndex(relation);
			}
		}
	}
	
	private void findMatchingEntities(List<Entity> entities, Entity entity, List<Entity> addResultsTo){
		//If entities already exist for these spans, reuse them.
		//If no exact entity is found, also add the generic entity to capture the full extent
		
		boolean exactFound = false;
		
		if(!entities.isEmpty()){
			for(Entity e : entities){
				if(e.getBegin() == entity.getBegin() && e.getEnd() == entity.getEnd()){
					addResultsTo.add(e);
					exactFound = true;
				}else if(e.getBegin() >= entity.getBegin() && e.getEnd() <= entity.getEnd()){
					addResultsTo.add(e);
				}
			}
		}
		
		if(!exactFound || entities.isEmpty()){
			addToJCasIndex(entity);
			addResultsTo.add(entity);
		}
	}
}
