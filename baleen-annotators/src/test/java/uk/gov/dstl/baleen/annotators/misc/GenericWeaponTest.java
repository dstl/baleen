//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.misc.GenericWeapon;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.types.TestWeapon;
import uk.gov.dstl.baleen.types.language.Sentence;
import uk.gov.dstl.baleen.types.language.WordToken;
import uk.gov.dstl.baleen.types.military.Weapon;

public class GenericWeaponTest extends AbstractAnnotatorTest {
	public GenericWeaponTest(){
		super(GenericWeapon.class);
	}
	
	@Test
	public void testSingleWeapon() throws UIMAException{
		jCas.setDocumentText("Natalie had a tactical assault rifle hidden in her cupboard.");
		createSentences(jCas);
		createWordTokens(jCas);
		
		processJCas();
		
		assertAnnotations(1, Weapon.class, new TestWeapon(0, "tactical assault rifle", "FIREARM"));
	}
	
	@Test
	public void testMultipleWeapon() throws UIMAException{
		jCas.setDocumentText("Jose had a combat knife and chemical weapons.");
		createSentences(jCas);
		createWordTokens(jCas);
		
		processJCas();
		
		assertAnnotations(2, Weapon.class,
				new TestWeapon(0, "combat knife", "BLADED"),
				new TestWeapon(1, "chemical weapons", "OTHER"));
	}
	
	@Test
	public void testPlural() throws UIMAException{
		jCas.setDocumentText("He was found with six flamethrowers and 47 bullets.");
		createSentences(jCas);
		createWordTokens(jCas);
		
		processJCas();
		
		assertAnnotations(2, Weapon.class,
				new TestWeapon(0, "flamethrowers", "OTHER"),
				new TestWeapon(1, "bullets", "AMMUNITION"));
	}
	
	@Test
	public void testNoDescriptor() throws UIMAException{
		jCas.setDocumentText("Sam owned a gun.");
		createSentences(jCas);
		createWordTokens(jCas);
		
		processJCas();
		
		assertAnnotations(1, Weapon.class, new TestWeapon(0, "gun", "FIREARM"));
	}
	
	@Test
	public void testMultipleSentences() throws UIMAException{
		jCas.setDocumentText("It was combat. Rifles everywhere. A silenced pistol in his hand.");
		createSentences(jCas);
		createWordTokens(jCas);
		
		processJCas();
		
		assertAnnotations(2, Weapon.class, new TestWeapon(0, "Rifles", "FIREARM"), new TestWeapon(1, "silenced pistol", "FIREARM"));
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