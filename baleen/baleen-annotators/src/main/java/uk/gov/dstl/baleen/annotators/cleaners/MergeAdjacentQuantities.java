//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.cleaners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.types.common.Quantity;
import uk.gov.dstl.baleen.types.semantic.Entity;

/**
 * Find adjacent quantities of the same type and merge them
 * 
 * <p>This annotator will find adjacent quantities of the same type and merge them into a single quantity.
 * For example, 7lb 4oz should be annotated as a single entity, not two.</p>
 */
public class MergeAdjacentQuantities extends MergeAdjacent {
	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException{
		separatorPattern = Pattern.compile(separator);
		
		classTypes = new ArrayList<>();
		classTypes.add(Quantity.class);
	}

	@Override
	public boolean shouldMerge(Entity e1, Entity e2){
		if(!(e1 instanceof Quantity && e2 instanceof Quantity)){
			return false;
		}
		
		Quantity q1 = (Quantity) e1;
		Quantity q2 = (Quantity) e2;
		
		return q1.getNormalizedQuantity() >= q2.getNormalizedQuantity() && StringUtils.equals(q1.getSubType(), q2.getSubType()) && StringUtils.equals(q1.getNormalizedUnit(), q2.getNormalizedUnit());
	}
	
	@Override
	public boolean mergeAdditionalProperties(Entity merged, Class<? extends Entity> type, List<Entity> originalEntities){
		if(type != Quantity.class || merged.getClass() != type)
			return false;
		
		Quantity qMerged = (Quantity) merged;
		Double normalizedQuantity = 0.0;

		Set<String> units = new HashSet<>();
		
		for(Entity e : originalEntities){
			if(e.getClass() != type)
				continue;
			
			Quantity q = (Quantity) e;
			
			if(StringUtils.isNotBlank(q.getUnit())){
				boolean newUnit = units.add(q.getUnit());
				if(!newUnit)
					return false;
			}
			
			normalizedQuantity += q.getNormalizedQuantity();
			
			setNormalizedUnit(qMerged, q.getNormalizedUnit());
			setSubType(qMerged, q.getSubType());
		}
		
		qMerged.setNormalizedQuantity(normalizedQuantity);
		
		return true;
	}
	
	private void setNormalizedUnit(Quantity qMerged, String unit){
		if(StringUtils.isBlank(qMerged.getNormalizedUnit())){
			qMerged.setNormalizedUnit(unit);
		}
	}
	
	private void setSubType(Quantity qMerged, String type){
		if(StringUtils.isBlank(qMerged.getSubType())){
			qMerged.setSubType(type);
		}
	}
}
