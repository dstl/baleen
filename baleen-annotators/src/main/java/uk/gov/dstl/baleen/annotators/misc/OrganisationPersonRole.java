//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;

import com.google.common.collect.ImmutableSet;

import uk.gov.dstl.baleen.core.pipelines.orderers.AnalysisEngineAction;
import uk.gov.dstl.baleen.types.common.Organisation;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Relation;
import uk.gov.dstl.baleen.uima.BaleenTextAwareAnnotator;
import uk.gov.dstl.baleen.uima.data.TextBlock;

/**
 * Identify organisations with an adjacent (or nested) person and create
 * a relationship of type ROLE between the two (from person to organisation).
 * 
 * Allows common roles to appear between the entities, such as spokesperson.
 * 
 * If no Organisation-Person match is found, it will also check that the role
 * hasn't been mis-extracted and included in the organisation. If it has, then a
 * new Person entity will be created and a relationship between the two made.
 * 
 * @baleen.javadoc
 */
public class OrganisationPersonRole extends BaleenTextAwareAnnotator {

	private static final List<String> ROLES = Arrays.asList("'s",
			"spokesperson", "spokesman", "spokeswoman",
			"chair", "chairperson", "chairman", "chairwoman", "chair person", "chair man", "chair woman",
			"secretary", "secretary general",
			"leader", "chief executive", "ceo", "c.e.o.", "boss", "president",
			"commander", "officer",
			"adviser", "senior adviser", "advisor", "senior advisor",
			"minister", "member");
	
	@Override
	protected void doProcessTextBlock(TextBlock block) throws AnalysisEngineProcessException {
		Collection<Person> people = block.select(Person.class);
		
		for(Organisation org : block.select(Organisation.class)){
			processOrganisation(block, org, people);
		}
	}

	private void processOrganisation(TextBlock block, Organisation org, Collection<Person> people){
		if(findRole(block, org, people))
			return;
		
		if(findAdjacent(block, org))
			return;
		
		findNested(block, org);
	}
	
	private boolean findRole(TextBlock block, Organisation org, Collection<Person> people){
		for(Person p : people){
			if(p.getBegin() >= org.getEnd()){
				String between = block.getDocumentText().substring(org.getEnd(), p.getBegin()).trim().toLowerCase();
				if(between.isEmpty() || ROLES.contains(between) || ROLES.contains("'s " + between) || ROLES.contains("' " + between)){
					Relation r = new Relation(block.getJCas(), org.getBegin(), p.getEnd());
					r.setRelationshipType("ROLE");
					r.setSource(p);
					r.setTarget(org);
					addToJCasIndex(r);
					
					return true;
				}
			}else if(p.getBegin() > org.getBegin() && p.getBegin() <= org.getEnd() && p.getEnd() >= org.getEnd()){
				//Adjust boundary of organisation
				org.setEnd(p.getBegin());
				while(org.getCoveredText().endsWith(" ") || org.getCoveredText().endsWith("'s") || org.getCoveredText().endsWith("'"))
					org.setEnd(org.getEnd() - 1);
				
				Relation r = new Relation(block.getJCas(), org.getBegin(), p.getEnd());
				r.setRelationshipType("ROLE");
				r.setSource(p);
				r.setTarget(org);
				addToJCasIndex(r);
				
				return true;
			}
		}
		
		return false;
	}
	
	private boolean findAdjacent(TextBlock block, Organisation org){
		String longestMatch = "";
		String subsequentText = block.getCoveredText().substring(block.toBlockOffset(org.getEnd()));
		
		for(String role : ROLES){
			if("'s".equals(role) || role.length() <= longestMatch.length())
				continue;
			
			Pattern p = Pattern.compile("('|'s)?\\s?"+Pattern.quote(role)+".*", Pattern.CASE_INSENSITIVE);
			if(p.matcher(subsequentText).matches()){
				longestMatch = role;
			}
		}
		
		if(!longestMatch.isEmpty()){
			Integer start = org.getEnd() + subsequentText.toLowerCase().indexOf(longestMatch);
			Person pers = new Person(block.getJCas(), start, start + longestMatch.length());
			
			Relation r = new Relation(block.getJCas(), org.getBegin(), pers.getEnd());
			r.setSource(org);
			r.setTarget(pers);
			r.setRelationshipType("ROLE");
			addToJCasIndex(pers, r);
			
			return true;
		}
		
		return false;
	}
	
	private boolean findNested(TextBlock block, Organisation org){
		//Didn't find a person, check that there's not an implied person/role nested in the organisation (e.g. bad extraction)
		String longestMatch = "";
		for(String role : ROLES){
			if("'s".equals(role) || role.length() <= longestMatch.length())
				continue;
			
			Pattern p = Pattern.compile(".*\\b"+Pattern.quote(role), Pattern.CASE_INSENSITIVE);
			if(p.matcher(org.getCoveredText()).matches()){
				longestMatch = role;
			}
		}
		
		if(!longestMatch.isEmpty()){
			Person pers = new Person(block.getJCas(), org.getEnd() - longestMatch.length(), org.getEnd());
			
			org.setEnd(org.getEnd() - longestMatch.length());
			while(org.getCoveredText().endsWith(" ") || org.getCoveredText().endsWith("'s") || org.getCoveredText().endsWith("'"))
				org.setEnd(org.getEnd() - 1);
			
			Relation r = new Relation(block.getJCas(), org.getBegin(), pers.getEnd());
			r.setSource(org);
			r.setTarget(pers);
			r.setRelationshipType("ROLE");
			addToJCasIndex(pers, r);
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public AnalysisEngineAction getAction() {
		return new AnalysisEngineAction(ImmutableSet.of(Organisation.class, Person.class), ImmutableSet.of(Relation.class, Person.class));
	}

}