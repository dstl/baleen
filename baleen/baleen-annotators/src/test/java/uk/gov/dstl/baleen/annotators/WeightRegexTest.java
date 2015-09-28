//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.Weight;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.types.TestQuantity;
import uk.gov.dstl.baleen.types.common.Quantity;

/**
 * 
 */
public class WeightRegexTest extends AbstractAnnotatorTest{
	
	private static final String WEIGHT = "weight";

	public WeightRegexTest() {
		super(Weight.class);
	}

	@Test
	public void testKG() throws Exception{
		
		jCas.setDocumentText("400kg of Cannabis was found, adding to the 2,000 kg that had been retrieved previously.");
		processJCas();
		
		assertAnnotations(2, Quantity.class, 
				new TestQuantity(0, "400kg", 400, "kg", 400.0, "kg", WEIGHT),
				new TestQuantity(1, "2,000 kg", 2000, "kg", 2000.0, "kg", WEIGHT)
		);

	}
	
	@Test
	public void testG() throws Exception{
	
		jCas.setDocumentText("Mix in 30 g of yellow powder with 2,170g of red powder.");
		processJCas();
		
		assertAnnotations(2, Quantity.class, 
				new TestQuantity(0, "30 g", 30, "g", 0.03, "kg", WEIGHT),
				new TestQuantity(1, "2,170g", 2170, "g", 2.17, "kg", WEIGHT)
		);

	}
	
	@Test
	public void testMG() throws Exception{
		
		jCas.setDocumentText("47 milligrams of powder is the correct amount.");
		processJCas();

		assertAnnotations(1, Quantity.class, 
				new TestQuantity(0, "47 milligrams", 47.0, "mg", 0.000047, "kg", WEIGHT)
		);

	}
	
	@Test
	public void testTonne() throws Exception{
		
		jCas.setDocumentText("3.7 tonnes of explosive is enough to make a very big bang.");
		processJCas();
		
		assertAnnotations(1, Quantity.class, 
				new TestQuantity(0, "3.7 tonnes", 3.7, "tonne", 3700, "kg", WEIGHT)
		);

	}
	
	@Test
	public void testTon() throws Exception{
		jCas.setDocumentText("3.7 tons of explosive is enough to make a very big bang.");
		processJCas();
		
		assertAnnotations(1, Quantity.class, 
				new TestQuantity(0, "3.7 tons", 3.7, "long ton", 3.7*uk.gov.dstl.baleen.annotators.regex.Weight.LONG_TON_TO_KG, "kg", WEIGHT)
		);
		

	}
	
	@Test
	public void testLbs() throws Exception{
		
		jCas.setDocumentText("According to 3 sources, 4lb of explosive was carried across the border.");
		processJCas();
		
		assertAnnotations(1, Quantity.class, 
				new TestQuantity(0, "4lb", 4, "lb", 4*uk.gov.dstl.baleen.annotators.regex.Weight.POUNDS_TO_KG, "kg", WEIGHT)
		);

	}
	
	@Test
	public void testStones() throws Exception{
		
		jCas.setDocumentText("The brief case weighed 2 stone.");
		processJCas();
		
		assertAnnotations(1, Quantity.class, 
				new TestQuantity(0, "2 stone", 2, "st", 2*uk.gov.dstl.baleen.annotators.regex.Weight.STONE_TO_KG, "kg", WEIGHT)
		);
		
	}
	
	@Test
	public void testOunces() throws Exception{
		
		jCas.setDocumentText("Add 4oz of sugar to the mix.");
		processJCas();
		
		assertAnnotations(1, Quantity.class, 
				new TestQuantity(0, "4oz", 4, "oz", 4*uk.gov.dstl.baleen.annotators.regex.Weight.OUNCES_TO_KG, "kg", WEIGHT)
		);
	
	}

	@Test
	public void testMultiplier() throws Exception{
		
		jCas.setDocumentText("The ship weight 4.3 million tonnes.");
		processJCas();
		
		assertAnnotations(1, Quantity.class, 
				new TestQuantity(0, "4.3 million tonnes", 4300000, "tonne", 4300000000L, "kg", WEIGHT)
		);
	}
	
	@Test
	public void testPunctuation() throws Exception{
	
		jCas.setDocumentText("3,700.3kg is a valid weight; 40.kg isn't.");
		processJCas();		
		
		assertAnnotations(1, Quantity.class, 
				new TestQuantity(0, "3,700.3kg", 3700.3, "kg", 3700.3, "kg", WEIGHT)
		);

	}
}
