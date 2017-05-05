//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmanipulators;

import static org.junit.Assert.assertEquals;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

public class NewLineToNewParagraphTest {

	private NewLineToNewParagraph m;

	@Before
	public void before() {
		m = new NewLineToNewParagraph();

	}

	@Test
	public void testNoSplit() {
		Document doc = Jsoup.parseBodyFragment("<p>This is some text</p>");
		m.manipulate(doc);

		assertEquals(doc.body().select("p").size(), 1);
	}

	@Test
	public void testOneSplitInP() {
		Document doc = Jsoup.parseBodyFragment("<p>This is <br /> some text</p>");
		m.manipulate(doc);

		assertEquals(doc.body().select("p").size(), 2);
	}

	@Test
	public void testTwoSplitInP() {
		Document doc = Jsoup.parseBodyFragment("<p>This <br /> is some<br /> text</p>");
		m.manipulate(doc);

		assertEquals(doc.body().select("p").size(), 3);
	}

	@Test
	public void testSplitInLi() {
		Document doc = Jsoup.parseBodyFragment("<li>This is  <br />some text</li>");
		m.manipulate(doc);

		assertEquals(doc.body().select("li > p").size(), 2);
	}
}