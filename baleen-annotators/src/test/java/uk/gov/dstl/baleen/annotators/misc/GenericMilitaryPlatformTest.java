//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.misc.GenericMilitaryPlatform;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.types.TestMilitaryPlatform;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.military.MilitaryPlatform;

public class GenericMilitaryPlatformTest extends AbstractAnnotatorTest {
	public GenericMilitaryPlatformTest(){
		super(GenericMilitaryPlatform.class);
	}
	
	@Test
	public void testSingleVehicle() throws UIMAException{
		jCas.setDocumentText("A fighter jet was scrambled.");
		createSentences(jCas);
		createWordTokens(jCas);
		
		processJCas();
		
		assertAnnotations(1, MilitaryPlatform.class, new TestMilitaryPlatform(0, "fighter jet", "AIR"));
	}
	
	@Test
	public void testMultipleVehicle() throws UIMAException{
		jCas.setDocumentText("The aircraft carrier carried a number of attack helicopters");
		createSentences(jCas);
		createWordTokens(jCas);
		
		processJCas();
		
		assertAnnotations(2, MilitaryPlatform.class,
				new TestMilitaryPlatform(0, "aircraft carrier", "NAVAL"),
				new TestMilitaryPlatform(1, "attack helicopters", "AIR"));
	}
	
	@Test
	public void testPlural() throws UIMAException{
		jCas.setDocumentText("There were four tanks.");
		createSentences(jCas);
		createWordTokens(jCas);
		
		processJCas();
		
		assertAnnotations(1, MilitaryPlatform.class, new TestMilitaryPlatform(0, "tanks", "GROUND"));
	}
	
	
	@Test
	public void testMultipleSentences() throws UIMAException{
		jCas.setDocumentText("The armoured vehicle was hidden in the forest. The UAV couldn't see it.");
		createSentences(jCas);
		createWordTokens(jCas);
		
		processJCas();
		
		assertAnnotations(2, MilitaryPlatform.class,
				new TestMilitaryPlatform(0, "armoured vehicle", "GROUND"),
				new TestMilitaryPlatform(1, "UAV", "AIR"));
	}
	
	private void createSentences(JCas jCas){
		Pattern p = Pattern.compile("[^ ].*?\\.");
		Matcher m = p.matcher(jCas.getDocumentText());
		while(m.find()){
			new Sentence(jCas, m.start(), m.end()).addToIndexes();
		}
	}
	
	private void createWordTokens(JCas jCas){
		Pattern p = Pattern.compile("[A-Za-z]+");
		Matcher m = p.matcher(jCas.getDocumentText());
		while(m.find()){
			new WordToken(jCas, m.start(), m.end()).addToIndexes();
		}
	}
}