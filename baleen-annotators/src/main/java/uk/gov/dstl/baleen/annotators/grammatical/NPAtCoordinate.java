//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.grammatical;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.types.semantic.Location;
import uk.gov.dstl.baleen.types.semantic.ReferenceTarget;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Identify new locations and coreferences where text has the pattern [NP/Location] at [Coordinate].
 * 
 * For example, in the statement "the former school house at GR 1234 5678", we would identify
 * "the former school house" as a location and a coreference between it and "GR 1234 5678".
 * 
 * If an existing Location exists, that is used and NP are ignored.
 * Otherwise a new Location is created to match the NP.
 * 
 * @baleen.javadoc
 */
public class NPAtCoordinate extends BaleenAnnotator {
	private static final Pattern AT = Pattern.compile("\\sat\\s", Pattern.CASE_INSENSITIVE);
	private static final Pattern IS_AT = Pattern.compile("\\sis\\sat\\s", Pattern.CASE_INSENSITIVE);
	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		for(Coordinate coord : JCasUtil.select(jCas, Coordinate.class)){
			Integer substringStartAt = Math.max(0, coord.getBegin() - 4);
			Integer substringStartIsAt = Math.max(0, coord.getBegin() - 7);
			
			String precedingTextAt = jCas.getDocumentText().substring(substringStartAt, coord.getBegin());
			String precedingTextIsAt = jCas.getDocumentText().substring(substringStartIsAt, coord.getBegin());
			
			final int substringStart;
			if(IS_AT.matcher(precedingTextIsAt).matches()){
				substringStart = substringStartIsAt;
			}else if(AT.matcher(precedingTextAt).matches()){
				substringStart = substringStartAt;
			}else{
				substringStart = -1;
			}
			
			if(substringStart  >= 0){
				//Get NP or Location at this location
				boolean locFound = false;
				
				for(Location l : JCasUtil.select(jCas, Location.class).stream().filter(l -> substringStart == l.getEnd()).collect(Collectors.toList())){
					locFound = true;
					setReferent(jCas, l, coord);
				}
				
				if(locFound)
					continue;
				
				//Get NP and create a Location
				for(PhraseChunk pc : JCasUtil.select(jCas, PhraseChunk.class).stream().filter(pc -> "NP".equalsIgnoreCase(pc.getChunkType())).filter(pc -> substringStart == pc.getEnd()).collect(Collectors.toList())){
					createNewLocation(jCas, pc, coord);
				}
			}
		}
	}

	private void setReferent(JCas jCas, Location l, Coordinate c){
		if(l.getReferent() == null && c.getReferent() == null){
			ReferenceTarget rt = new ReferenceTarget(jCas);
			rt.addToIndexes();
			
			l.setReferent(rt);
			c.setReferent(rt);
		}else if(l.getReferent() != null && c.getReferent() == null){
			c.setReferent(l.getReferent());
		}else if(l.getReferent() == null && c.getReferent() != null){
			l.setReferent(c.getReferent());
		}else{
			//Merge all references
			for(Location lRt : JCasUtil.select(jCas, Location.class).stream().filter(l2 -> l2.getReferent().equals(l.getReferent())).collect(Collectors.toList())){
				lRt.setReferent(c.getReferent());
			}
		}
	}
	
	private void createNewLocation(JCas jCas, PhraseChunk pc, Coordinate c){
		Location l = new Location(jCas, pc.getBegin(), pc.getEnd());
		
		if(c.getReferent() != null){
			l.setReferent(c.getReferent());
		}else{
			ReferenceTarget rt = new ReferenceTarget(jCas);
			rt.addToIndexes();
			
			c.setReferent(rt);
			l.setReferent(rt);
		}
		
		l.addToIndexes();
	}
	
	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(ImmutableSet.of(Coordinate.class, Location.class, PhraseChunk.class), ImmutableSet.of(Location.class));
	}
}