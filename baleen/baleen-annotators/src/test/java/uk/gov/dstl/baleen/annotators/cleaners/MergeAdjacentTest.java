//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.types.common.Person;
import uk.gov.dstl.baleen.types.geo.Coordinate;
import uk.gov.dstl.baleen.types.semantic.Location;

/**
 * Test the MergeAdjacent Cleaner
 */
public class MergeAdjacentTest extends AbstractAnnotatorTest {


	public MergeAdjacentTest() {
		super(MergeAdjacent.class);
	}

	@Test
	public void testNoTypes() throws Exception{
		jCas.setDocumentText("John Smith was seen at London King's Cross");

		Person p1 = Annotations.createPerson(jCas, 0, 4, "John");
		Person p2 = Annotations.createPerson(jCas, 5, 10, "Smith");
		
		Location l1 = Annotations.createLocation(jCas, 23, 29, "London", null);
		Location l2 = Annotations.createLocation(jCas, 30, 42, "King's Cross", null);
		
		processJCas();

		assertEquals(2, JCasUtil.select(jCas, Person.class).size());
		assertEquals(2, JCasUtil.select(jCas, Location.class).size());

		assertEquals(p1, JCasUtil.selectByIndex(jCas, Person.class, 0));
		assertEquals(p2, JCasUtil.selectByIndex(jCas, Person.class, 1));
		
		assertEquals(l1, JCasUtil.selectByIndex(jCas, Location.class, 0));
		assertEquals(l2, JCasUtil.selectByIndex(jCas, Location.class, 1));
	}
	
	@Test
	public void testSingleTypes() throws Exception{
		jCas.setDocumentText("John Smith was seen at London King's Cross");

		Annotations.createPerson(jCas, 0, 4, "John");
		Annotations.createPerson(jCas, 5, 10, "Smith");
		
		Location l1 = Annotations.createLocation(jCas, 23, 29, "London", null);
		Location l2 = Annotations.createLocation(jCas, 30, 42, "King's Cross", null);
		
		processJCas("types", new String[]{"Person"});

		assertEquals(1, JCasUtil.select(jCas, Person.class).size());
		assertEquals(2, JCasUtil.select(jCas, Location.class).size());

		Person p = JCasUtil.selectByIndex(jCas, Person.class, 0);
		assertEquals("John Smith", p.getCoveredText());
		assertEquals("John Smith", p.getValue());
		
		assertEquals(l1, JCasUtil.selectByIndex(jCas, Location.class, 0));
		assertEquals(l2, JCasUtil.selectByIndex(jCas, Location.class, 1));
	}
	
	@Test
	public void testMultipleTypes() throws Exception{
		jCas.setDocumentText("John Smith was seen at London King's Cross");

		Annotations.createPerson(jCas, 0, 4, "John");
		Annotations.createPerson(jCas, 5, 10, "Smith");
		
		Annotations.createLocation(jCas, 23, 29, "London", null);
		Annotations.createLocation(jCas, 30, 42, "King's Cross", null);
		
		processJCas("types", new String[]{"Person","Location"});

		assertEquals(1, JCasUtil.select(jCas, Person.class).size());
		assertEquals(1, JCasUtil.select(jCas, Location.class).size());

		Person p = JCasUtil.selectByIndex(jCas, Person.class, 0);
		assertEquals("John Smith", p.getCoveredText());
		assertEquals("John Smith", p.getValue());
		
		Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
		assertEquals("London King's Cross", l.getCoveredText());
		assertEquals("London King's Cross", l.getValue());
	}
	
	@Test
	public void testMultipleAdjacentTypes() throws Exception{
		jCas.setDocumentText("John Smith London King's Cross");

		Annotations.createPerson(jCas, 0, 4, "John");
		Annotations.createPerson(jCas, 5, 10, "Smith");
		
		Annotations.createLocation(jCas, 11, 17, "London", null);
		Annotations.createLocation(jCas, 18, 30, "King's Cross", null);
		
		processJCas("types", new String[]{"Person","Location"});

		assertEquals(1, JCasUtil.select(jCas, Person.class).size());
		assertEquals(1, JCasUtil.select(jCas, Location.class).size());

		Person p = JCasUtil.selectByIndex(jCas, Person.class, 0);
		assertEquals("John Smith", p.getCoveredText());
		assertEquals("John Smith", p.getValue());
		
		Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
		assertEquals("London King's Cross", l.getCoveredText());
		assertEquals("London King's Cross", l.getValue());
	}
	
	@Test
	public void testSubTypes() throws Exception{
		jCas.setDocumentText("John Smith was seen at London King's Cross 30N 15E");
		
		Annotations.createLocation(jCas, 23, 29, "London", null);
		Annotations.createLocation(jCas, 30, 42, "King's Cross", null);
		
		Annotations.createCoordinate(jCas, 43, 46, "30N");
		Annotations.createCoordinate(jCas, 47, 50, "15E");
		
		processJCas("types", new String[]{"Location"});

		assertEquals(3, JCasUtil.select(jCas, Location.class).size());	//1 + 2
		assertEquals(2, JCasUtil.select(jCas, Coordinate.class).size());

		Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
		assertEquals("London King's Cross", l.getCoveredText());
		assertEquals("London King's Cross", l.getValue());
		
		Coordinate c1 = JCasUtil.selectByIndex(jCas, Coordinate.class, 0);
		assertEquals("30N", c1.getCoveredText());
		assertEquals("30N", c1.getValue());
		
		Coordinate c2 = JCasUtil.selectByIndex(jCas, Coordinate.class, 1);
		assertEquals("15E", c2.getCoveredText());
		assertEquals("15E", c2.getValue());
	}
	
	@Test
	public void testSubTypesMultiple() throws Exception{
		jCas.setDocumentText("John Smith was seen at London King's Cross 30N 15E");
		
		Annotations.createLocation(jCas, 23, 29, "London", null);
		Annotations.createLocation(jCas, 30, 42, "King's Cross", null);
		
		Annotations.createCoordinate(jCas, 43, 46, "30N");
		Annotations.createCoordinate(jCas, 47, 50, "15E");
		
		processJCas("types", new String[]{"Location", "Coordinate"});

		assertEquals(2, JCasUtil.select(jCas, Location.class).size());	//1 + 1
		assertEquals(1, JCasUtil.select(jCas, Coordinate.class).size());

		Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
		assertEquals("London King's Cross", l.getCoveredText());
		assertEquals("London King's Cross", l.getValue());
		
		Coordinate c = JCasUtil.selectByIndex(jCas, Coordinate.class, 0);
		assertEquals("30N 15E", c.getCoveredText());
		assertEquals("30N 15E", c.getValue());
	}
	
	@Test
	public void testSeparator() throws Exception{
		jCas.setDocumentText("John    Smith was seen at London\tKing's Cross");

		Annotations.createPerson(jCas, 0, 4, "John");
		Annotations.createPerson(jCas, 8, 13, "Smith");
		
		Annotations.createLocation(jCas, 26, 32, "London", null);
		Annotations.createLocation(jCas, 33, 45, "King's Cross", null);
		
		processJCas("types", new String[]{"Person","Location"});

		assertEquals(1, JCasUtil.select(jCas, Person.class).size());
		assertEquals(1, JCasUtil.select(jCas, Location.class).size());

		Person p = JCasUtil.selectByIndex(jCas, Person.class, 0);
		assertEquals("John    Smith", p.getCoveredText());
		assertEquals("John    Smith", p.getValue());
		
		Location l = JCasUtil.selectByIndex(jCas, Location.class, 0);
		assertEquals("London\tKing's Cross", l.getCoveredText());
		assertEquals("London\tKing's Cross", l.getValue());
	}
}
