//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.Area;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.types.TestQuantity;
import uk.gov.dstl.baleen.types.common.Quantity;

/**
 * 
 */
public class AreaRegexTest extends AbstractAnnotatorTest {

	public AreaRegexTest() {
		super(Area.class);
	}

	@Test
	public void testM2() throws Exception{
		
		jCas.setDocumentText("The field measured 400 square metres (400m^2).");
		processJCas();
		
		assertAnnotations(2, Quantity.class, 
				new TestQuantity(0, "400 square metres", 400, "m^2", 400, "m^2", "area"),
				new TestQuantity(1, "400m^2", 400, "m^2", 400, "m^2", "area")
		);

	}
	
	@Test
	public void testCM2() throws Exception{
	
		jCas.setDocumentText("The table measured 400 square centimetres (400cm^2).");
		processJCas();
		

		assertAnnotations(2, Quantity.class, 
				new TestQuantity(0, "400 square centimetres", 400, "cm^2", 400*Area.CM2_TO_M2, "m^2", "area"),
				new TestQuantity(1, "400cm^2", 400, "cm^2", 400*Area.CM2_TO_M2, "m^2", "area")
		);
	}
	
	@Test
	public void testMM2() throws Exception{
		jCas.setDocumentText("The chip measured 400 square millimetres (400mm^2).");
		processJCas();
		
		assertAnnotations(2, Quantity.class, 
				new TestQuantity(0, "400 square millimetres", 400, "mm^2", 400*Area.MM2_TO_M2, "m^2", "area"),
				new TestQuantity(1, "400mm^2", 400, "mm^2", 400*Area.MM2_TO_M2, "m^2", "area")
		);
		

	}
	
	@Test
	public void testKM2() throws Exception{
	
		jCas.setDocumentText("The region measured 400 square kilometres (400km^2).");
		processJCas();
		
		assertAnnotations(2, Quantity.class, 
				new TestQuantity(0, "400 square kilometres", 400, "km^2", 400*Area.KM2_TO_M2, "m^2", "area"),
				new TestQuantity(1, "400km^2", 400, "km^2", 400*Area.KM2_TO_M2, "m^2", "area")
		);
		

	}
	
	@Test
	public void testMi2() throws Exception{
	
		jCas.setDocumentText("The region measured 400 square miles (400mi^2).");
		processJCas();

		assertAnnotations(2, Quantity.class, 
				new TestQuantity(0, "400 square miles", 400, "mi^2", 400*Area.MI2_TO_M2, "m^2", "area"),
				new TestQuantity(1, "400mi^2", 400, "mi^2", 400*Area.MI2_TO_M2, "m^2", "area")
		);
		
	}

	@Test
	public void testYd2() throws Exception{
	
		jCas.setDocumentText("The field measured 400 square yards (400yd^2).");
		processJCas();
		
		assertAnnotations(2, Quantity.class, 
				new TestQuantity(0, "400 square yards", 400, "yd^2", 400*Area.YD2_TO_M2, "m^2", "area"),
				new TestQuantity(1, "400yd^2", 400, "yd^2", 400*Area.YD2_TO_M2, "m^2", "area")
		);
		

	}
	
	@Test
	public void testFT2() throws Exception{
	
		jCas.setDocumentText("The field measured 400 square feet (400ft^2).");
		processJCas();

		assertAnnotations(2, Quantity.class, 
				new TestQuantity(0, "400 square feet", 400, "ft^2", 400*Area.FT2_TO_M2, "m^2", "area"),
				new TestQuantity(1, "400ft^2", 400, "ft^2", 400*Area.FT2_TO_M2, "m^2", "area")
		);
	}
	
	@Test
	public void testIn2() throws Exception{
	
		jCas.setDocumentText("The table measured 400 square inches (400in^2).");
		processJCas();
		
		assertAnnotations(2, Quantity.class, 
			new TestQuantity(0, "400 square inches", 400, "in^2", 400*Area.IN2_TO_M2, "m^2", "area"),
			new TestQuantity(1, "400in^2", 400, "in^2", 400*Area.IN2_TO_M2, "m^2", "area")
		);
		
	}
	
	@Test
	public void testAcre() throws Exception{
	
		jCas.setDocumentText("The field measured 400 acres.");
		processJCas();

		assertAnnotations(1, Quantity.class, 
			new TestQuantity(0, "400 acres", 400, "acre", 400*Area.ACRE_TO_M2, "m^2", "area")
		);
		
	}
	
	@Test
	public void testHectare() throws Exception{
		
		jCas.setDocumentText("The field measured 400 hectares.");
		processJCas();
		
		assertAnnotations(1, Quantity.class, 
			new TestQuantity(0, "400 hectares", 400, "ha", 400*Area.HECTARE_TO_M2, "m^2", "area")
		);

	}
	
	@Test
	public void testMultiplier() throws Exception{
	
		jCas.setDocumentText("The country had an area of 9.8 million square kms.");
		processJCas();
		
		assertAnnotations(1, Quantity.class, 
				new TestQuantity(0, "9.8 million square kms", 9800000, "km^2", 9.8E12, "m^2", "area")
		);

	}
	
	@Test
	public void testPunctuation() throws Exception{
	
		jCas.setDocumentText("The room had a floorspace of 6.2 square metres.");
		processJCas();
		
		assertAnnotations(1, Quantity.class, 
				new TestQuantity(0, "6.2 square metres", 6.2, "m^2", 6.2, "m^2", "area")
		);
		
	}
}
