package uk.gov.dstl.baleen.resources;

import static org.junit.Assert.assertEquals;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.junit.Test;

import com.google.common.collect.Maps;

import uk.gov.dstl.baleen.resources.documentchecker.DocumentExistanceStatus;

public class SharedDocumentCheckerResourceTest {
	private static class Dummy implements DocumentExistanceStatus {
		public String removedUri;
		@Override
		public void documentRemoved(String uri) {
			removedUri=uri;			
		}
	}
		
	@Test
	public void testNonExistantFile() throws Exception{
		String uri="/xxxxx/xxxxx/xxxxx/xxxxx/xxxxx/xxxxx/xxxxx";
		assertEquals(checkUri(uri), uri);
	}
	
	@Test
	public void testFileExists() throws Exception{
		String uri=getClass().getResource("/uk/gov/dstl/baleen/resources/countries/countries.json").toString();
		assertEquals(checkUri(uri), null);
	}
	
	private String checkUri(String uri) throws ResourceInitializationException {
		int sleep=50;
		int checks=10;
		Dummy cleaner = new Dummy();
		SharedDocumentCheckerResource checker=new SharedDocumentCheckerResource();
		CustomResourceSpecifier_impl specifier = new CustomResourceSpecifier_impl();
		checker.initialize(specifier, Maps.newHashMap());
		checker.register(cleaner);
		checker.check(uri);
		for(int i=0; i<checks; ++i) {
			if (null!=cleaner.removedUri) {
				break;
			} else {
				try {
					Thread.sleep(sleep);
				} catch(Exception e) {
				}
			}
		}
		return cleaner.removedUri;
	}
}
