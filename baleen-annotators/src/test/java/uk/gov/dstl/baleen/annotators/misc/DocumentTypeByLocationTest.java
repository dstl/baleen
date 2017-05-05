//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;

import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.misc.DocumentTypeByLocation;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;

import com.google.common.io.Files;

/**
 * 
 */
public class DocumentTypeByLocationTest extends AbstractAnnotatorTest {

	private static final String BASE_DIRECTORY = "baseDirectory";
	private static File tmp;
	private static File topDir;
	private static File childDir;
	private static File parentDir;

	public DocumentTypeByLocationTest() {
		super(DocumentTypeByLocation.class);
	}

	@BeforeClass
	public static void setUp() throws IOException {
		topDir = Files.createTempDir();
		parentDir = new File(topDir, "parent");
		parentDir.mkdir();
		childDir = new File(parentDir, "child");
		childDir.mkdir();
		tmp = new File(childDir, "file.tmp");
		tmp.createNewFile();
	}

	@AfterClass
	public static void tearDown() {
		if (tmp != null && tmp.exists()) {
			tmp.delete();
		}

		if (childDir != null && childDir.exists()) {
			childDir.delete();
		}

		if (topDir != null && topDir.exists()) {
			topDir.delete();
		}
	}

	@Test
	public void test() throws Exception {

		try {
			DocumentAnnotation da = getDocumentAnnotation();
			da.setSourceUri(tmp.getAbsolutePath());

			processJCas();

			// Remove slash (requried for unix paths)
			String absolutePath = childDir.getAbsolutePath();
			if (absolutePath.startsWith(File.separator)) {
				absolutePath = absolutePath.substring(1);
			}

			assertEquals(absolutePath, da.getDocType());
		} finally {
			tmp.delete();
		}
	}

	@Test
	public void testBaseDirectory() throws Exception {

		DocumentAnnotation da = getDocumentAnnotation();
		da.setSourceUri(tmp.getAbsolutePath());

		processJCas(BASE_DIRECTORY, childDir.getAbsolutePath());

		assertEquals("", da.getDocType());
	}

	@Test
	public void testBaseDirectoryOneLayers() throws Exception {

		DocumentAnnotation da = getDocumentAnnotation();
		da.setSourceUri(tmp.getAbsolutePath());

		processJCas(BASE_DIRECTORY, parentDir.getAbsolutePath());

		String relative = tmp.getAbsolutePath().substring(
				parentDir.getAbsolutePath().length() + 1,
				tmp.getAbsolutePath().length() - tmp.getName().length() - 1);

		assertEquals(relative, da.getDocType());

	}

	@Test
	public void testBadParameters() throws Exception {

		DocumentAnnotation da = getDocumentAnnotation();
		da.setSourceUri(tmp.getAbsolutePath());

		processJCas(BASE_DIRECTORY, null);

		String relative = tmp.getAbsolutePath().substring(
				parentDir.getAbsolutePath().length() + 1,
				tmp.getAbsolutePath().length() - tmp.getName().length() - 1);

		assertNotEquals(relative, da.getDocType());

		processJCas(BASE_DIRECTORY, "/not/the/path");
		assertNotEquals(relative, da.getDocType());

	}

	@Test
	public void testBaseDirectoryTwoLayers() throws Exception {

		DocumentAnnotation da = getDocumentAnnotation();
		da.setSourceUri(tmp.getAbsolutePath());

		processJCas(BASE_DIRECTORY, topDir.getAbsolutePath());

		String relative = tmp.getAbsolutePath().substring(
				topDir.getAbsolutePath().length() + 1,
				tmp.getAbsolutePath().length() - tmp.getName().length() - 1);

		assertEquals(relative, da.getDocType());

	}

}
