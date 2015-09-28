//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.annotators;

import org.junit.Test;

import uk.gov.dstl.baleen.annotators.regex.IpV4;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;
import uk.gov.dstl.baleen.annotators.testing.types.TestCommsIdentifier;
import uk.gov.dstl.baleen.types.common.CommsIdentifier;

/**
 * Tests to ensure IP V4 regex annotator works as expected, ignoring incorrect octets and extracts multiple IPs in any document.
 * 
 *  , Rich Brantingham
 * 
 */
public class IpV4AddressRegexTest extends AbstractAnnotatorTest {

	private static final String IPV4ADDRESS = "ipv4address";

	public IpV4AddressRegexTest() {
		super(IpV4.class);
	}
	
	@Test
	public void testSingleIp() throws Exception{
	
		// Create a document containing just 1 IP
		jCas.setDocumentText("This is a fake IP address made up for the unit test: 123.123.123.123");
		processJCas();

		assertAnnotations(1, CommsIdentifier.class, 
				new TestCommsIdentifier(0, "123.123.123.123", IPV4ADDRESS));
	}
	
	@Test
	public void testMultipleIpsInDocument() throws Exception{
		// Create a document containing a couple of IPs
		jCas.setDocumentText("This document contains several IP addresses. There is this one 123.123.123.123, but also the localhost 127.0.0.1 and this 192.168.0.1 - all should match.");
		processJCas();

		assertAnnotations(3, CommsIdentifier.class, 
				new TestCommsIdentifier(0, "123.123.123.123", IPV4ADDRESS),
				new TestCommsIdentifier(1, "127.0.0.1", IPV4ADDRESS),
				new TestCommsIdentifier(2, "192.168.0.1", IPV4ADDRESS)
				);	

	}
	
	@Test
	public void testWithTrailingSlash() throws Exception{
		jCas.setDocumentText("An IP with a trailing slash IP mask 125.125.125.125/25");
		processJCas();

		assertAnnotations(1, CommsIdentifier.class, 
				new TestCommsIdentifier(0, "125.125.125.125", IPV4ADDRESS)
		);	
	
	}

	@Test
	public void testIgnoreIncorrectOctet() throws Exception{

		jCas.setDocumentText("An IP with a trailing slash IP mask 600.123.123.123");
		processJCas();

		assertAnnotations(0, CommsIdentifier.class);	
			
	
	}
	
	
	
}
