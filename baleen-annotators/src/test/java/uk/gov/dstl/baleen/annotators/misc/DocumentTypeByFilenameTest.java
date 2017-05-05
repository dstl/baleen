//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.annotators.misc;

import static org.junit.Assert.assertEquals;

import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.junit.Test;

import uk.gov.dstl.baleen.annotators.misc.DocumentTypeByFilename;
import uk.gov.dstl.baleen.annotators.testing.AbstractAnnotatorTest;

public class DocumentTypeByFilenameTest extends AbstractAnnotatorTest {

	public DocumentTypeByFilenameTest() {
		super(DocumentTypeByFilename.class);
	}

	@Test
	public void testDefault() throws Exception {
		DocumentAnnotation da = getDocumentAnnotation();
		da.setSourceUri("20170127-Test_Document.docx");

		processJCas();

		assertEquals("docx", da.getDocType());
	}
	
	@Test
	public void testPrefix() throws Exception {
		DocumentAnnotation da = getDocumentAnnotation();
		da.setSourceUri("20170127-Test_Document.docx");

		processJCas(DocumentTypeByFilename.PARAM_PREFIX, "filetype_");

		assertEquals("filetype_docx", da.getDocType());
	}
	
	@Test
	public void testPattern() throws Exception {
		DocumentAnnotation da = getDocumentAnnotation();
		da.setSourceUri("20170127-Test_Document.docx");

		processJCas(DocumentTypeByFilename.PARAM_PATTERN, "(\\d{4}).*");

		assertEquals("2017", da.getDocType());
	}
	
	@Test
	public void testPatternNoMatch() throws Exception {
		DocumentAnnotation da = getDocumentAnnotation();
		da.setSourceUri("20170127-Test_Document.docx");

		processJCas(DocumentTypeByFilename.PARAM_PATTERN, "([a-z]{2}).*", DocumentTypeByFilename.PARAM_DEFAULT, "unknown");

		assertEquals("unknown", da.getDocType());
	}
	
	@Test
	public void testPatternCaseSensitiveFalse() throws Exception {
		DocumentAnnotation da = getDocumentAnnotation();
		da.setSourceUri("20170127-Test_Document.docx");

		processJCas(DocumentTypeByFilename.PARAM_PATTERN, "\\d{8}-([a-z]).*", DocumentTypeByFilename.PARAM_DEFAULT, "unknown");

		assertEquals("t", da.getDocType());
	}
	
	@Test
	
	public void testPatternCaseSensitiveTrue() throws Exception {
		DocumentAnnotation da = getDocumentAnnotation();
		da.setSourceUri("20170127-Test_Document.docx");

		processJCas(DocumentTypeByFilename.PARAM_PATTERN, "\\d{8}-([a-z]).*", DocumentTypeByFilename.PARAM_DEFAULT, "unknown", DocumentTypeByFilename.PARAM_CASE_SENSITIVE, true);

		assertEquals("unknown", da.getDocType());
	}
	
	@Test
	public void testGroup() throws Exception {
		DocumentAnnotation da = getDocumentAnnotation();
		da.setSourceUri("20170127-Test_Document.docx");

		processJCas(DocumentTypeByFilename.PARAM_PATTERN, "(\\d{4})(\\d{2})(\\d{2}).*", DocumentTypeByFilename.PARAM_GROUP, 2);

		assertEquals("01", da.getDocType());
	}
	
	@Test
	public void testLowerCase() throws Exception {
		DocumentAnnotation da = getDocumentAnnotation();
		da.setSourceUri("20170127-Test_Document.docx");

		processJCas(DocumentTypeByFilename.PARAM_PATTERN, "\\d{8}-([a-z]).*", DocumentTypeByFilename.PARAM_LOWER_CASE, false);

		assertEquals("T", da.getDocType());
	}
}
