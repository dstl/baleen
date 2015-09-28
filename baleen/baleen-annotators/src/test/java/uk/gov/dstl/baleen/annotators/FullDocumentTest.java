//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import static org.junit.Assert.fail;

import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.misc.FullDocument;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.types.semantic.Relation;

/** Tests for {@link FullDocument}
 * 
 */
public class FullDocumentTest extends AbstractAnnotatorTest {
	
	private static final String TYPE = "type";
	private static final String NAME = "Edward Smith";

	public FullDocumentTest() {
		super(FullDocument.class);
	}

	@Test
	public void testPerson() throws Exception{
		jCas.setDocumentText(NAME);
		
		processJCas(TYPE, "uk.gov.dstl.baleen.types.common.Person");
		
		assertAnnotations(1, Person.class);
	}
	
	@Test
	public void testDefault() throws Exception{
		jCas.setDocumentText(NAME);
		processJCas();
		
		assertAnnotations(1, Entity.class);
	}
	
	@Test
	public void testNoText() throws Exception{
		processJCas();
		
		assertAnnotations(0, Entity.class);
	}
	
	@Test
	public void testShortType() throws Exception{
		jCas.setDocumentText(NAME);
		processJCas(TYPE, "Person");
		
		assertAnnotations(1, Person.class);
	}

	@Test
	public void testNullType() throws Exception{
		jCas.setDocumentText(NAME);
		processJCas(TYPE, null);
		
		assertAnnotations(1, Entity.class);
	}
	
	@Test
	public void testBadTypes() throws Exception{
		try{
			processJCas(TYPE, "this.is.not.a.type");
			
			fail("Expected exception not thrown");
		}catch(ResourceInitializationException e){
			// Expected exception
		}
		
		try{
			processJCas(TYPE, FullDocument.class.getName());

			fail("Expected exception not thrown");
		}catch(ResourceInitializationException e){
			// Expected exception
		}
		
		try{
			processJCas(TYPE, Relation.class.getName());
			
			fail("Expected exception not thrown");
		}catch(ResourceInitializationException e){
			// Expected exception
		}
	}
}
