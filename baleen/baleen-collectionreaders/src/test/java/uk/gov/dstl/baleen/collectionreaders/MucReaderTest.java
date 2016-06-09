package uk.gov.dstl.baleen.collectionreaders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;

public class MucReaderTest {

	private static final String MUC = "DEV-MUC3-0001 (NOSC)\n\n" +
			"SAN SALVADOR, 3 JAN 90 -- [REPORT] [ARMED FORCES PRESS COMMITTEE,\n" +
			"COPREFA] [TEXT] THE ARCE BATTALION COMMAND HAS REPORTED THAT ABOUT 50\n" +
			"PEASANTS OF VARIOUS AGES HAVE BEEN KIDNAPPED BY TERRORISTS OF THE\n" +
			"FARABUNDO MARTI NATIONAL LIBERATION FRONT [FMLN] IN SAN MIGUEL\n" +
			"DEPARTMENT.  ACCORDING TO THAT GARRISON, THE MASS KIDNAPPING TOOK PLACE ON\n" +
			"30 DECEMBER IN SAN LUIS DE LA REINA.  THE SOURCE ADDED THAT THE TERRORISTS\n" +
			"FORCED THE INDIVIDUALS, WHO WERE TAKEN TO AN UNKNOWN LOCATION, OUT OF\n" +
			"THEIR RESIDENCES, PRESUMABLY TO INCORPORATE THEM AGAINST THEIR WILL INTO\n" +
			"CLANDESTINE GROUPS.";

	private static Path tmpDir;
	JCas jCas;
	

	@BeforeClass
	public static void beforeClass() throws IOException {
		tmpDir = Files.createTempDirectory("muctest");
		Files.write(tmpDir.resolve("file"), MUC.getBytes(StandardCharsets.UTF_8));

	}

	@AfterClass
	public static void afterClass() {
		tmpDir.toFile().delete();
	}
	
	@Before
	public void beforeTest() throws UIMAException {
		if(jCas == null){
			jCas = JCasFactory.createJCas();
		}else{
			jCas.reset();
		}
	}
	
	@Test
	public void testNoFiles() throws UIMAException, IOException {
		Path emptyTmpDir = Files.createTempDirectory("muctest");
		
		try{
			BaleenCollectionReader bcr = (BaleenCollectionReader) CollectionReaderFactory.createReader(MucReader.class, MucReader.KEY_PATH, emptyTmpDir.toAbsolutePath().toString());
			bcr.initialize();
			
			fail("Expected exception not thrown");
		}catch(ResourceInitializationException be){
			//Do nothing, expected exception
			assertEquals(BaleenException.class, be.getCause().getClass());
		}
		
		emptyTmpDir.toFile().delete();
	}
	
	@Test
	public void testKeyFile() throws UIMAException, IOException {
		Path keyTmpDir = Files.createTempDirectory("muctest");
		Files.write(keyTmpDir.resolve("key-test"), MUC.getBytes(StandardCharsets.UTF_8));
		
		try{
			BaleenCollectionReader bcr = (BaleenCollectionReader) CollectionReaderFactory.createReader(MucReader.class, MucReader.KEY_PATH, keyTmpDir.toAbsolutePath().toString());
			bcr.initialize();
			
			fail("Expected exception not thrown");
		}catch(ResourceInitializationException be){
			//Do nothing, expected exception
			assertEquals(BaleenException.class, be.getCause().getClass());
		}
		
		keyTmpDir.toFile().delete();
	}
	
	@Test
	public void test() throws UIMAException, IOException {
		BaleenCollectionReader bcr = (BaleenCollectionReader) CollectionReaderFactory.createReader(MucReader.class, MucReader.KEY_PATH, tmpDir.toAbsolutePath().toString());

		assertTrue(bcr.doHasNext());

		bcr.getNext(jCas.getCas());

		assertEquals("DEV-MUC3-0001 (NOSC)", getSource(jCas));

		String s = "THE ARCE BATTALION COMMAND HAS REPORTED THAT ABOUT 50 " +
				"PEASANTS OF VARIOUS AGES HAVE BEEN KIDNAPPED BY TERRORISTS OF THE " +
				"FARABUNDO MARTI NATIONAL LIBERATION FRONT IN SAN MIGUEL " +
				"DEPARTMENT.  ACCORDING TO THAT GARRISON, THE MASS KIDNAPPING TOOK PLACE ON " +
				"30 DECEMBER IN SAN LUIS DE LA REINA.  THE SOURCE ADDED THAT THE TERRORISTS " +
				"FORCED THE INDIVIDUALS, WHO WERE TAKEN TO AN UNKNOWN LOCATION, OUT OF " +
				"THEIR RESIDENCES, PRESUMABLY TO INCORPORATE THEM AGAINST THEIR WILL INTO " +
				"CLANDESTINE GROUPS.";
		s = s.toLowerCase();
		assertEquals(s, jCas.getDocumentText());

		assertFalse(bcr.doHasNext());

		bcr.close();
	}

	private String getSource(JCas jCas){
		DocumentAnnotation doc = (DocumentAnnotation) jCas.getDocumentAnnotationFs();
		return doc.getSourceUri();
	}
}
