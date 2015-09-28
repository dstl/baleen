//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class SharedFileResourceTest {
	@Test
	public void testReadFile() throws Exception{
		assertEquals("This is my test file.\n\t\nIt has lots of whitespace in it!",
				SharedFileResource.readFile(new File(getClass().getResource("test.txt").toURI())));
		
		try{
			SharedFileResource.readFile(new File("missing.txt"));
			fail("Expected an exception");
		}catch(IOException ioe){
			// Test passed, we expected an error
		}
	}
	
	@Test
	public void testReadFileLines() throws Exception{
		String[] lines = SharedFileResource.readFileLines(new File(getClass().getResource("test.txt").toURI()));
		assertEquals(3, lines.length);
		assertEquals("This is my test file.", lines[0]);
		assertEquals("", lines[1]);
		assertEquals("It has lots of whitespace in it!", lines[2]);
		
		//Test missing file
		try{
			SharedFileResource.readFileLines(new File("missing.txt"));
			fail("Expected an exception");
		}catch(IOException ioe){
			// Test passed, we expected an error
		}
	}
}
