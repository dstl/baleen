//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.grammatical;

import java.util.Arrays;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.language.PhraseChunk;
import uk.gov.dstl.baleen.uima.BaleenAnnotator;

/**
 * Look for noun phrases (NP) that start with a known title, e.g. Mr, and annotate these as people
 * 
 * 
 */
public class NPTitleEntity extends BaleenAnnotator {
	public static final List<String> TITLES = Arrays.asList("mr", "mrs", "ms", "miss", "master", 	//Common titles
		"rev", "reverend", "fr", "father", "pope", "brother", "sister", "friar", "abbess", "abbott", "cardinal", "vicar",		//Religious titles (Christian)
		"mullah", "imam", "ayatollah", "druid", "lama", "buddha", "rabbi", "rebbe",		//Religious titles (non-Christian)
		"dr", "doctor", "prof", "professor",	//Educational titles
		"pres", "president", "governor", "senator", "ambassador", "mayor", "envoy", "prime minister", "minister", "councillor", "representative", "speaker", "mp", "emir", "chief", "sultan", "wali", "sheikh", "shaykh", 	//Political titles
		"pvt", "cpl", "corporal", "sgt", "sergeant", "capt", "captain", "maj", "cmdr", "commander", "lt", "lieutenant", "lt col", "lieutenant colonel", "col", "colonel", "gen", "adm", "admiral", "cdre", "commodore",	"officer",	//Military titles
		"hrh", "his royal highness", "his majesty", "her royal highness", "her majesty", "king", "queen", "prince", "princess", "emperor", "empress", "tsar", "tsarina", "tsaritsa", "shah",		//Royal titles
		"sir", "dame", "lord", "lady", "baron", "baroness", "count", "countess", "duke", "duchess", "earl", "viscount", "marquis", "marquess", "grand duke", "grand duchess", "archduke", "archduchess"	//Nobility titles
	);
	
	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		for(PhraseChunk chunk : JCasUtil.select(jCas, PhraseChunk.class)){
			if(!"NP".equals(chunk.getChunkType()))
				continue;
			
			String coveredText = chunk.getCoveredText().toLowerCase();
			
			for(String title : TITLES){
				if(coveredText.startsWith(title) && coveredText.length() > title.length() && coveredText.substring(title.length(), title.length() + 1).matches("[^a-z0-9]")){
					Person p = new Person(jCas);
					p.setBegin(chunk.getBegin());
					p.setEnd(chunk.getEnd());
					p.setTitle(chunk.getCoveredText().substring(0, title.length()));
					addToJCasIndex(p);
					break;
				}
			}
		}
	}
	
	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(ImmutableSet.of(PhraseChunk.class), ImmutableSet.of(Person.class));
	}

}
