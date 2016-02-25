//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators.cleaners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.cleaners.MergeAdjacentQuantities;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.Annotations;
import uk.gov.dstl.baleen.types.common.Quantity;

/**
 * 
 */
public class MergeAdjacentQuantitiesTest extends AbstractAnnotatorTest {


	public MergeAdjacentQuantitiesTest() {
		super(MergeAdjacentQuantities.class);
	}

	@Test
	public void testSingleType() throws Exception{

		jCas.setDocumentText("The package weighed 4st 7oz. There was an additional 3lbs found lying near by.");

		Annotations.createWeightQuantity(jCas, 20, 23, "4st", 4, "st", 25.4012);
		Annotations.createWeightQuantity(jCas, 24, 27, "7oz", 7, "oz", 0.198447);
		Quantity q3 = Annotations.createWeightQuantity(jCas, 53, 57, "3lbs", 3, "lb", 1.36078);

		processJCas();

		assertEquals(2, JCasUtil.select(jCas, Quantity.class).size());

		Quantity q = JCasUtil.selectByIndex(jCas, Quantity.class, 0);
		assertEquals("4st 7oz", q.getCoveredText());
		assertEquals("4st 7oz", q.getValue());
		assertNull(q.getUnit());
		assertEquals(0.0, q.getQuantity(), 0.0);
		assertEquals("kg", q.getNormalizedUnit());
		assertEquals(25.599647, q.getNormalizedQuantity(), 0.0);
		assertEquals("weight", q.getSubType());

		assertEquals(q3, JCasUtil.selectByIndex(jCas, Quantity.class, 1));
	}

	
	
	@Test
	public void testMixedType() throws Exception{
		jCas.setDocumentText("The following unit makes no sense; 4st 8km");

		Annotations.createWeightQuantity(jCas, 35, 38, "4st", 4, "st", 25.4012);
		Annotations.createDistanceQuantity(jCas, 39, 42, "8km", 8, "km", 8000);

		processJCas();

		assertEquals(2, JCasUtil.select(jCas, Quantity.class).size());
	}

	@Test
	public void testDecreasingSizes() throws Exception{
		jCas.setDocumentText("The following unit makes no sense; 2ft 8yd");

		Annotations.createDistanceQuantity(jCas,35, 38, "4ft", 4, "ft", 1.3);
		Annotations.createDistanceQuantity(jCas,39, 42, "8yd", 8, "yd", 8);

		processJCas();

		assertEquals(2, JCasUtil.select(jCas, Quantity.class).size());
	}

	@Test
	public void testSameUnit() throws Exception{
		jCas.setDocumentText("The packages weighed: 4kg 2.5kg 14.2kg");

		Annotations.createWeightQuantity(jCas,22, 25, "4kg", 4, "kg", 4);
		Annotations.createWeightQuantity(jCas,26, 31, "2.5kg", 2.5, "kg", 2.5);
		Annotations.createWeightQuantity(jCas,32, 38, "14.2kg", 14.2, "kg", 14.2);

		processJCas();

		assertEquals(3, JCasUtil.select(jCas, Quantity.class).size());
	}

	@Test
	public void testList() throws Exception{
		jCas.setDocumentText("The packages weighed: 4kg, 2.5kg, 14.2kg");

		Annotations.createWeightQuantity(jCas,22, 25, "4kg", 4, "kg", 4);
		Annotations.createWeightQuantity(jCas,27, 32, "2.5kg", 2.5, "kg", 2.5);
		Annotations.createWeightQuantity(jCas,34, 40, "14.2kg", 14.2, "kg", 14.2);

		processJCas();

		assertEquals(3, JCasUtil.select(jCas, Quantity.class).size());
	}
	
	@Test
	public void testMissingNormalizedUnit() throws Exception{
		jCas.setDocumentText("The package was 4st 8oz");

		Quantity q = Annotations.createWeightQuantity(jCas, 16, 19, "4st", 4, "st", 25.4012);
		q.setNormalizedUnit(null);
		
		Annotations.createWeightQuantity(jCas, 20, 23, "80z", 8, "oz", 0.2268);

		processJCas();

		assertEquals(2, JCasUtil.select(jCas, Quantity.class).size());
	}
}
