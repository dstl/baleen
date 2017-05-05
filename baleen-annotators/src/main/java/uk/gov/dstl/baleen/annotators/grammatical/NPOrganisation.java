//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.grammatical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.atteo.evo.inflector.English;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.common.Organisation;
import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Find noun phrases that contain a word that might indicate it's an organisation,
 * such as 'council' or 'forces'.
 */
public class NPOrganisation extends BaleenAnnotator {
	private static final List<String> ORGANISATION_KEYWORDS = Arrays.asList(
			"council", "government", "coalition", "agency", "community", "regime", "service", "group", "unit",
			"faculty", "division", "department", "institute", "university", "committee", "company", "organisation", "coalition", "tribe",
			"brotherhood","sisterhood",
			"force", "police", "army", "navy",
			"rebels", "insurgents", "militants",
			"united nations", "nato"
		);
	
	private List<String> organisationInclPlurals;
	
	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		organisationInclPlurals = new ArrayList<>(ORGANISATION_KEYWORDS.size() * 2);
		
		for(String org : ORGANISATION_KEYWORDS){
			organisationInclPlurals.add(org);
			organisationInclPlurals.add(English.plural(org));
		}
	}
	
	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		for(PhraseChunk chunk : JCasUtil.select(jCas, PhraseChunk.class)){
			if(!"NP".equals(chunk.getChunkType()))
				continue;
			
			processNP(jCas, chunk);
		}
	}
	
	/**
	 * Process a noun phrase to find entities
	 */
	public void processNP(JCas jCas, PhraseChunk chunk){
		String coveredText = chunk.getCoveredText().toLowerCase();
		
		for(String org : organisationInclPlurals){
			if(coveredText.contains(org)){
				//Check that word isn't part of another word
				Pattern p = Pattern.compile("\\b"+org+"\\b", Pattern.CASE_INSENSITIVE);

				Matcher m = p.matcher(coveredText);
				if(m.find()){
					Organisation o = new Organisation(jCas);
					o.setBegin(chunk.getBegin());
					o.setEnd(chunk.getEnd());
					addToJCasIndex(o);
					
					return;	//Phrase chunk shouldn't contain more than one organisation
				}
			}
		}
	}

	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(ImmutableSet.of(PhraseChunk.class), ImmutableSet.of(Organisation.class));
	}
}