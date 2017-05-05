//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.utils;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class SourceUtilsTest {
	private File base = new File("my/path");
	
	@Test
	public void testUrl(){
		File fExpected = new File("my/path/www.example.com/test/page.html");
		assertEquals(fExpected, SourceUtils.urlToFile(base, "http://www.example.com/test/page.html"));
		assertEquals(fExpected, SourceUtils.urlToFile(base, "https://www.example.com/test/page.html"));
		assertEquals(fExpected, SourceUtils.urlToFile(base, "ftp://www.example.com/test/page.html"));
		
		assertEquals(new File("my/path/www.example.com/page.html"), SourceUtils.urlToFile(base, "http://www.example.com/page.html"));
		assertEquals(new File("my/path/www.example.com/test/subdir/page.html"), SourceUtils.urlToFile(base, "http://www.example.com/test/subdir/page.html"));
		
		assertEquals(new File("my/path/http_not_url/page.txt"), SourceUtils.urlToFile(base, "http_not_url/page.txt"));
	}
	
	@Test
	public void testNoBase(){
		assertEquals(new File("www.example.com/page.html"), SourceUtils.urlToFile(null, "http://www.example.com/page.html"));
	}
	
	@Test
	public void testNetwork(){
		assertEquals(new File("my/path/network_drive/this/is/my/file.html"), SourceUtils.urlToFile(base, "\\\\network_drive\\this\\is\\my\\file.html"));
	}
	
	@Test
	public void testWindowsFile(){
		assertEquals(new File("my/path/this/is/my/file.html"), SourceUtils.urlToFile(base, "C:\\this\\is\\my\\file.html"));
	}
	
	@Test
	public void testOther(){
		assertEquals(new File("my/path/something_else.txt"), SourceUtils.urlToFile(base, "something_else.txt"));
	}
}
