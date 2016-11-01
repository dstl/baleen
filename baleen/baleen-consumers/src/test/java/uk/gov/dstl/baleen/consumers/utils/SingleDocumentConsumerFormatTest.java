package uk.gov.dstl.baleen.consumers.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import uk.gov.dstl.baleen.types.metadata.Metadata;

public class SingleDocumentConsumerFormatTest {
	@Test
	public void testCreateMetadata() throws UIMAException{
		JCas jCas = JCasFactory.createJCas();
		
		Metadata m1 = new Metadata(jCas);
		m1.setKey("en.hello");
		m1.setValue("Hello World");
		
		Metadata m2 = new Metadata(jCas);
		m2.setKey("fr.hello");
		m2.setValue("Bonjour le monde");
		
		Metadata m3 = new Metadata(jCas);
		m3.setKey("foo");
		m3.setValue("bar");
		
		Collection<Metadata> m = new ArrayList<>();
		m.add(m1);
		m.add(m2);
		m.add(m3);
		
		Map<String, Object> metadata = SingleDocumentConsumerFormat.createMetadataMap(m);
		
		assertEquals(3, metadata.size());
		assertTrue(metadata.containsKey("en_hello"));
		assertFalse(metadata.containsKey("en.hello"));
		assertEquals("Hello World", metadata.get("en_hello"));
		assertTrue(metadata.containsKey("fr_hello"));
		assertFalse(metadata.containsKey("fr.hello"));
		assertEquals("Bonjour le monde", metadata.get("fr_hello"));
		assertTrue(metadata.containsKey("foo"));
		assertEquals("bar", metadata.get("foo"));
	}
}
