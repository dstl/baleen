//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.internals.UrlRegex;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.TestEntity;
import uk.gov.dstl.baleen.types.common.Url;

/**
 * 
 */
public class UrlRegexTest extends AbstractAnnotatorTest {
	
	public UrlRegexTest() {
		super(UrlRegex.class);
	}

	@Test
	public void test() throws Exception{
		
		jCas.setDocumentText("Dstl's website is http://www.dstl.gov.uk/. An example FTP directory is ftp://foo.example.com/this/is/a/path.txt. Here's a secure URL https://www.example.com/index.php?test=true . Some naughty person hasn't specified a schema here... www.example.com/path/to/page.html.");
		processJCas();

		assertAnnotations(4, Url.class, 
				new TestEntity<>(0, "http://www.dstl.gov.uk/"),
				new TestEntity<>(1, "ftp://foo.example.com/this/is/a/path.txt"),
				new TestEntity<>(2, "https://www.example.com/index.php?test=true"),
				new TestEntity<>(3, "www.example.com/path/to/page.html"));
		
	}

}
