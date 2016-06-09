package uk.gov.dstl.baleen.uima;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Test;

import uk.gov.dstl.baleen.uima.JobSettings;

public class JobSettingsTest {

	private JobSettings settings;
	private JCas jCas;

	@Before
	public void before() throws UIMAException {
		jCas = JCasFactory.createJCas();
		settings = new JobSettings(jCas);

	}

	@Test
	public void testGetWithDefault() {
		assertFalse(settings.get("key").isPresent());
		assertEquals("default", settings.get("key", "default"));
		settings.set("key", "value");
		assertEquals("value", settings.get("key", "default"));
	}

	@Test
	public void testSetGetString() {
		settings.set("key", "value");
		assertEquals("value", settings.get("key").get());
	}

	@Test
	public void testSetGetSetString() {
		settings.set("key", "value");
		assertEquals("value", settings.get("key").get());

		settings.set("key", "2");
		assertEquals("2", settings.get("key").get());
	}

	@Test
	public void testSetGetSetNull() {
		settings.set("key", "value");
		assertEquals("value", settings.get("key").get());

		settings.set("key", null);
		assertFalse(settings.get("key").isPresent());
	}

	@Test
	public void testRemove() {
		settings.remove("key");

		settings.set("key", "value");
		assertEquals("value", settings.get("key").get());

		settings.remove("key");
		assertFalse(settings.get("key").isPresent());
	}

	@Test
	public void testChangeJCas() {
		settings.set("key", "value");
		assertEquals("value", settings.get("key").get());

		JobSettings settings2 = new JobSettings(jCas);
		assertEquals("value", settings2.get("key").get());

	}
}
