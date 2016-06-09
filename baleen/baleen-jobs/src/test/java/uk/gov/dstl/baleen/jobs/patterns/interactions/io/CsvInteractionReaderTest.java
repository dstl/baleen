package uk.gov.dstl.baleen.jobs.patterns.interactions.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

import net.sf.extjwnl.data.POS;
import uk.gov.dstl.baleen.jobs.interactions.io.CsvInteractionReader;

public class CsvInteractionReaderTest {
	Integer count = 0;
	
	@Test
	public void test() throws Exception{
		File file = new File(getClass().getResource("interactions.csv").toURI());
		CsvInteractionReader reader = new CsvInteractionReader(file.getAbsolutePath());

		count = 0;
		
		reader.read((i, a) -> {
			count++;
			
			assertNotNull(i.getType());
			assertNotNull(i.getSubType());
			assertNotNull(i.getSource());
			assertNotNull(i.getTarget());
			assertNotNull(i.getWord());
			assertEquals(POS.NOUN, i.getWord().getPos());
			
			assertNotNull(a);
			assertFalse(a.isEmpty());
		});
		
		assertEquals(new Integer(3), count);
	}
}
