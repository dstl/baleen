//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmanipulators;

import static org.junit.Assert.assertEquals;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;

public class Jsp101HeadingsTest {

	private Jsp101Headings manipulator;

	@Before
	public void before() {
		manipulator = new Jsp101Headings();
	}

	@Test
	public void testSubjectHeading() {
		Document document = Jsoup.parseBodyFragment(
				"<p><b>THIS IS A SUBJECT HEADING</b></p><p>THIS IS A NOT SUBJECT HEADING</p><p>THIS IS not a SUBJECT HEADING</p><p>THIS IS NOT A SUBJECT HEADING EITHER.</p>");

		manipulator.manipulate(document);

		Elements h1s = document.select("h1");
		assertEquals(1, h1s.size());
		assertEquals("THIS IS A SUBJECT HEADING", h1s.first().text());
	}


	@Test
	public void testMainHeading() {
		Document document = Jsoup.parseBodyFragment(
				"<p><b>This is a group heading</b></p><p>This is not a group heading</p><p>This is not a group heading.</p>");

		manipulator.manipulate(document);

		Elements h2s = document.select("h2");
		assertEquals(1, h2s.size());
		assertEquals("This is a group heading", h2s.first().text());
	}

	@Test
	public void testNoneHeading() {
		Document document = Jsoup.parseBodyFragment(
				"<p><b>This is a group heading:</b></p><p>This is not a group heading</p><p>This is not a group heading.</p>");

		manipulator.manipulate(document);

		Elements h2s = document.select("h2");
		assertEquals(0, h2s.size());
	}
}