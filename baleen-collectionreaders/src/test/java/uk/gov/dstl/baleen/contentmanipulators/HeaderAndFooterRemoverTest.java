//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.contentmanipulators;

import static org.junit.Assert.assertTrue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

public class HeaderAndFooterRemoverTest {

	private HeaderAndFooterRemover m;

	@Before
	public void before() {
		m = new HeaderAndFooterRemover();
	}

	@Test
	public void test() {
		Document doc = Jsoup.parseBodyFragment(
				"<header>this</header><header></header><p>This is some text</p><footer>other</footer>");
		m.manipulate(doc);

		assertTrue(doc.body().select("header,footer").isEmpty());
	}

}