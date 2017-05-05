//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.common.Nationality;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.BaleenTextAwareAnnotator;
import uk.gov.dstl.baleen.uima.data.TextBlock;

/**
 * Merge Nationality entities into an adjacent following entity of any time.
 * For instance [British] [Prime Minister Theresa May] would become
 * [British Prime Minister Theresa May].
 */
public class MergeNationalityIntoEntity extends BaleenTextAwareAnnotator {

	@Override
	protected void doProcessTextBlock(TextBlock block) throws AnalysisEngineProcessException {
		Collection<Nationality> nationalities = block.select(Nationality.class);
		if(nationalities.isEmpty())
			return;
		
		List<Entity> entities = block.select(Entity.class).stream().filter(e -> !e.getClass().equals(Nationality.class)).collect(Collectors.toList());
		for(Nationality n : nationalities){
			mergeEntities(block, n, entities);
		}
	}
	
	private void mergeEntities(TextBlock block, Nationality n, List<Entity> entities){
		for(Entity e : entities){
			if(e.getBegin() < n.getEnd())
				continue;
			
			String between = block.getDocumentText().substring(n.getEnd(), e.getBegin());
			if(between.trim().isEmpty()){
				e.setBegin(n.getBegin());	
				e.setValue(e.getCoveredText());
				mergeWithExisting(e, n);
				
				return;
			}
		}
	}
	
	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(ImmutableSet.of(Nationality.class, Entity.class), Collections.emptySet());
	}

}