package uk.gov.dstl.baleen.collectionreaders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.io.IOUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.common.io.Files;
import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;

public class EmailReaderTest {
	@Rule
	public final GreenMailRule greenMail = new GreenMailRule(ServerSetupTest.SMTP_POP3_IMAP);
	
	JCas jCas;
	
	private static final String FIRST_LINE = "This is the first line";
	
	//TODO: Improve test coverage (e.g. duplicate attachments)
	
	@Before
	public void beforeTest() throws UIMAException{
		greenMail.setUser("to@localhost.com", "to@localhost.com", "password");
		
		if(jCas == null){
			jCas = JCasFactory.createJCas();
		}else{
			jCas.reset();
		}
	}
	
	
	@Test
	public void testPopNoMessages() throws Exception{
		BaleenCollectionReader bcr = (BaleenCollectionReader) CollectionReaderFactory.createReader(EmailReader.class,
				EmailReader.PARAM_PROTOCOL, "pop3",
				EmailReader.PARAM_WAIT, 5,
				EmailReader.PARAM_SERVER, greenMail.getPop3().getBindTo(),
				EmailReader.PARAM_PORT, greenMail.getPop3().getPort(),
				EmailReader.PARAM_USER, "to@localhost.com",
				EmailReader.PARAM_PASS, "password");

		bcr.initialize();

		assertFalse(bcr.doHasNext());
		
		bcr.close();
	}
	
	@Test
	public void testPopContent() throws Exception{
		String subject1 = GreenMailUtil.random();
		String body1 = GreenMailUtil.random();
		
		GreenMailUtil.sendTextEmailTest("to@localhost.com", "from@localhost.com", subject1, body1);
		
		String subject2 = GreenMailUtil.random();
		String body2 = GreenMailUtil.random();
		
		GreenMailUtil.sendAttachmentEmail("to@localhost.com", "from@localhost.com", subject2, body2, IOUtils.toByteArray(getClass().getResourceAsStream("lineReader.txt")), "text/plain", "lineReader.txt", "Test File", ServerSetupTest.SMTP);
				
		BaleenCollectionReader bcr = (BaleenCollectionReader) CollectionReaderFactory.createReader(EmailReader.class,
				EmailReader.PARAM_PROTOCOL, "pop3",
				EmailReader.PARAM_WAIT, 5,
				EmailReader.PARAM_SERVER, greenMail.getPop3().getBindTo(),
				EmailReader.PARAM_PORT, greenMail.getPop3().getPort(),
				EmailReader.PARAM_USER, "to@localhost.com",
				EmailReader.PARAM_PASS, "password",
				EmailReader.PARAM_PROCESS, "content");

		bcr.initialize();
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		assertTrue(jCas.getDocumentText().startsWith(body1));
		
		for(Metadata md : JCasUtil.select(jCas, Metadata.class)){
			System.err.println(md.getKey() + ": " + md.getValue());
		}
		
		jCas.reset();
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		assertTrue(jCas.getDocumentText().startsWith(body2));
		
		jCas.reset();
		
		assertFalse(bcr.doHasNext());
		
		bcr.close();
	}
	
	@Test
	public void testPopAttachments() throws Exception{
		File folder = Files.createTempDir();
		
		String subject1 = GreenMailUtil.random();
		String body1 = GreenMailUtil.random();
		
		GreenMailUtil.sendTextEmailTest("to@localhost.com", "from@localhost.com", subject1, body1);
		
		String subject2 = GreenMailUtil.random();
		String body2 = GreenMailUtil.random();
		
		GreenMailUtil.sendAttachmentEmail("to@localhost.com", "from@localhost.com", subject2, body2, IOUtils.toByteArray(getClass().getResourceAsStream("lineReader.txt")), "text/plain", "lineReader.txt", "Test File", ServerSetupTest.SMTP);
				
		BaleenCollectionReader bcr = (BaleenCollectionReader) CollectionReaderFactory.createReader(EmailReader.class,
				EmailReader.PARAM_PROTOCOL, "pop3",
				EmailReader.PARAM_WAIT, 5,
				EmailReader.PARAM_SERVER, greenMail.getPop3().getBindTo(),
				EmailReader.PARAM_PORT, greenMail.getPop3().getPort(),
				EmailReader.PARAM_USER, "to@localhost.com",
				EmailReader.PARAM_PASS, "password",
				EmailReader.PARAM_PROCESS, "attachments",
				EmailReader.PARAM_FOLDER, folder.getPath());

		bcr.initialize();
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		assertTrue(jCas.getDocumentText().startsWith(FIRST_LINE));
		assertEquals(1, folder.list().length);
		
		jCas.reset();
		
		assertFalse(bcr.doHasNext());
		
		bcr.close();
		
		FileUtils.deleteDirectory(folder);
	}
	
	@Test
	public void testPopBoth() throws Exception{
		File folder = Files.createTempDir();
		
		String subject1 = GreenMailUtil.random();
		String body1 = GreenMailUtil.random();
		
		GreenMailUtil.sendTextEmailTest("to@localhost.com", "from@localhost.com", subject1, body1);
		
		String subject2 = GreenMailUtil.random();
		String body2 = GreenMailUtil.random();
		
		GreenMailUtil.sendAttachmentEmail("to@localhost.com", "from@localhost.com", subject2, body2, IOUtils.toByteArray(getClass().getResourceAsStream("lineReader.txt")), "text/plain", "lineReader.txt", "Test File", ServerSetupTest.SMTP);
				
		BaleenCollectionReader bcr = (BaleenCollectionReader) CollectionReaderFactory.createReader(EmailReader.class,
				EmailReader.PARAM_PROTOCOL, "pop3",
				EmailReader.PARAM_WAIT, 5,
				EmailReader.PARAM_SERVER, greenMail.getPop3().getBindTo(),
				EmailReader.PARAM_PORT, greenMail.getPop3().getPort(),
				EmailReader.PARAM_USER, "to@localhost.com",
				EmailReader.PARAM_PASS, "password",
				EmailReader.PARAM_PROCESS, "both",
				EmailReader.PARAM_FOLDER, folder.getPath());

		bcr.initialize();
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		assertTrue(jCas.getDocumentText().startsWith(body1));
		
		jCas.reset();
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		assertTrue(jCas.getDocumentText().startsWith(body2));
		
		jCas.reset();

		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		assertTrue(jCas.getDocumentText().startsWith(FIRST_LINE));
		assertEquals(1, folder.list().length);
		
		jCas.reset();
		
		assertFalse(bcr.doHasNext());
		
		bcr.close();
		
		FileUtils.deleteDirectory(folder);
	}
	
	@Test
	public void testPopDeleteMessages() throws Exception{
		String subject1 = GreenMailUtil.random();
		String body1 = GreenMailUtil.random();
		
		GreenMailUtil.sendTextEmailTest("to@localhost.com", "from@localhost.com", subject1, body1);
		
		String subject2 = GreenMailUtil.random();
		String body2 = GreenMailUtil.random();
		
		GreenMailUtil.sendAttachmentEmail("to@localhost.com", "from@localhost.com", subject2, body2, IOUtils.toByteArray(getClass().getResourceAsStream("lineReader.txt")), "text/plain", "lineReader.txt", "Test File", ServerSetupTest.SMTP);
				
		BaleenCollectionReader bcr = (BaleenCollectionReader) CollectionReaderFactory.createReader(EmailReader.class,
				EmailReader.PARAM_PROTOCOL, "pop3",
				EmailReader.PARAM_WAIT, 5,
				EmailReader.PARAM_SERVER, greenMail.getPop3().getBindTo(),
				EmailReader.PARAM_PORT, greenMail.getPop3().getPort(),
				EmailReader.PARAM_USER, "to@localhost.com",
				EmailReader.PARAM_PASS, "password",
				EmailReader.PARAM_PROCESS, "content",
				EmailReader.PARAM_DELETE_EMAIL, true);

		bcr.initialize();
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		assertTrue(jCas.getDocumentText().startsWith(body1));
		
		jCas.reset();
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		assertTrue(jCas.getDocumentText().startsWith(body2));
		
		jCas.reset();
		
		assertFalse(bcr.doHasNext());
		
		bcr.close();
		
		//Check that there are no messages on the server
		bcr = (BaleenCollectionReader) CollectionReaderFactory.createReader(EmailReader.class,
				EmailReader.PARAM_PROTOCOL, "pop3",
				EmailReader.PARAM_WAIT, 5,
				EmailReader.PARAM_SERVER, greenMail.getPop3().getBindTo(),
				EmailReader.PARAM_PORT, greenMail.getPop3().getPort(),
				EmailReader.PARAM_USER, "to@localhost.com",
				EmailReader.PARAM_PASS, "password");
		
		bcr.initialize();
		assertFalse(bcr.doHasNext());
		bcr.close();
	}
	
	@Test
	public void testPopDeleteAttachments() throws Exception{
		File folder = Files.createTempDir();
		
		String subject1 = GreenMailUtil.random();
		String body1 = GreenMailUtil.random();
		
		GreenMailUtil.sendTextEmailTest("to@localhost.com", "from@localhost.com", subject1, body1);
		
		String subject2 = GreenMailUtil.random();
		String body2 = GreenMailUtil.random();
		
		GreenMailUtil.sendAttachmentEmail("to@localhost.com", "from@localhost.com", subject2, body2, IOUtils.toByteArray(getClass().getResourceAsStream("lineReader.txt")), "text/plain", "lineReader.txt", "Test File", ServerSetupTest.SMTP);
				
		BaleenCollectionReader bcr = (BaleenCollectionReader) CollectionReaderFactory.createReader(EmailReader.class,
				EmailReader.PARAM_PROTOCOL, "pop3",
				EmailReader.PARAM_WAIT, 5,
				EmailReader.PARAM_SERVER, greenMail.getPop3().getBindTo(),
				EmailReader.PARAM_PORT, greenMail.getPop3().getPort(),
				EmailReader.PARAM_USER, "to@localhost.com",
				EmailReader.PARAM_PASS, "password",
				EmailReader.PARAM_PROCESS, "attachments",
				EmailReader.PARAM_FOLDER, folder.getPath(),
				EmailReader.PARAM_DELETE_ATTACHMENT, true);

		bcr.initialize();
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		assertTrue(jCas.getDocumentText().startsWith(FIRST_LINE));
		assertEquals(0, folder.list().length);
		
		jCas.reset();
		
		assertFalse(bcr.doHasNext());
		
		bcr.close();
		
		FileUtils.deleteDirectory(folder);
	}
	
	@Test
	public void testPopLongWait() throws Exception{
		String subject = GreenMailUtil.random();
		String body = GreenMailUtil.random();
		
		String subject2 = GreenMailUtil.random();
		String body2 = GreenMailUtil.random();
		
		GreenMailUtil.sendTextEmailTest("to@localhost.com", "from@localhost.com", subject, body);
		GreenMailUtil.sendTextEmailTest("to@localhost.com", "from@localhost.com", subject2, body2);
		
		BaleenCollectionReader bcr = (BaleenCollectionReader) CollectionReaderFactory.createReader(EmailReader.class,
				EmailReader.PARAM_PROTOCOL, "pop3",
				EmailReader.PARAM_WAIT, 15,
				EmailReader.PARAM_SERVER, greenMail.getPop3().getBindTo(),
				EmailReader.PARAM_PORT, greenMail.getPop3().getPort(),
				EmailReader.PARAM_USER, "to@localhost.com",
				EmailReader.PARAM_PASS, "password",
				EmailReader.PARAM_PROCESS, "content");

		bcr.initialize();
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		jCas.reset();
		
		Thread.sleep(20000);
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		jCas.reset();
		
		assertFalse(bcr.doHasNext());
		
		bcr.close();
	}
	
	@Test
	public void testPopWait() throws Exception{
		BaleenCollectionReader bcr = (BaleenCollectionReader) CollectionReaderFactory.createReader(EmailReader.class,
				EmailReader.PARAM_PROTOCOL, "pop3",
				EmailReader.PARAM_WAIT, 5,
				EmailReader.PARAM_SERVER, greenMail.getPop3().getBindTo(),
				EmailReader.PARAM_PORT, greenMail.getPop3().getPort(),
				EmailReader.PARAM_USER, "to@localhost.com",
				EmailReader.PARAM_PASS, "password",
				EmailReader.PARAM_PROCESS, "content");

		bcr.initialize();
		
		
		assertFalse(bcr.doHasNext());
		GreenMailUtil.sendTextEmailTest("to@localhost.com", "from@localhost.com", GreenMailUtil.random(), GreenMailUtil.random());
		assertFalse(bcr.doHasNext());	//Should be a 5 second delay before it returns true
		
		Thread.sleep(5000);
		
		assertTrue(bcr.doHasNext());
		
		bcr.close();
	}
	
	@Test
	public void testPopMetadata() throws Exception{		
		String subject1 = GreenMailUtil.random();
		String body1 = GreenMailUtil.random();
		
		String subject2 = GreenMailUtil.random();
		String body2 = GreenMailUtil.random();
		
		GreenMailUtil.sendAttachmentEmail("to@localhost.com", "from@localhost.com", subject1, body1, IOUtils.toByteArray(getClass().getResourceAsStream("lineReader.txt")), "text/plain", "lineReader.txt", "Test File", ServerSetupTest.SMTP);
		
		MimeMessage ccMessage = GreenMailUtil.createTextEmail("to@localhost.com", "from@localhost.com", subject2, body2, ServerSetupTest.SMTP);
		ccMessage.addRecipients(RecipientType.CC, "cc@localhost.com");
		GreenMailUtil.sendMimeMessage(ccMessage);
				
		BaleenCollectionReader bcr = (BaleenCollectionReader) CollectionReaderFactory.createReader(EmailReader.class,
				EmailReader.PARAM_PROTOCOL, "pop3",
				EmailReader.PARAM_WAIT, 5,
				EmailReader.PARAM_SERVER, greenMail.getPop3().getBindTo(),
				EmailReader.PARAM_PORT, greenMail.getPop3().getPort(),
				EmailReader.PARAM_USER, "to@localhost.com",
				EmailReader.PARAM_PASS, "password",
				EmailReader.PARAM_PROCESS, "content");

		bcr.initialize();
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		Map<String, String> metadata = new HashMap<>();
		for(Metadata md : JCasUtil.select(jCas, Metadata.class)){
			metadata.put(md.getKey(), md.getValue());
		}
		
		assertEquals(subject1, metadata.get("subject"));
		assertEquals("lineReader.txt", metadata.get("attachment"));
		assertEquals("from@localhost.com", metadata.get("sender"));
		assertEquals("to@localhost.com", metadata.get("toRecipient"));
		
		assertNotNull(metadata.get("Content-Type"));
		assertNull(metadata.get("attachmentSaveLocation"));
		
		jCas.reset();
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		metadata = new HashMap<>();
		for(Metadata md : JCasUtil.select(jCas, Metadata.class)){
			metadata.put(md.getKey(), md.getValue());
		}
		
		assertEquals(subject2, metadata.get("subject"));
		assertEquals("from@localhost.com", metadata.get("sender"));
		assertEquals("to@localhost.com", metadata.get("toRecipient"));
		assertEquals("cc@localhost.com", metadata.get("ccRecipient"));
		
		jCas.reset();
		
		assertFalse(bcr.doHasNext());
		
		bcr.close();
	}
	
	@Test
	public void testPopBadProcessConfig() throws Exception{
		File folder = Files.createTempDir();
		
		String subject1 = GreenMailUtil.random();
		String body1 = GreenMailUtil.random();
		
		GreenMailUtil.sendTextEmailTest("to@localhost.com", "from@localhost.com", subject1, body1);
		
		String subject2 = GreenMailUtil.random();
		String body2 = GreenMailUtil.random();
		
		GreenMailUtil.sendAttachmentEmail("to@localhost.com", "from@localhost.com", subject2, body2, IOUtils.toByteArray(getClass().getResourceAsStream("lineReader.txt")), "text/plain", "lineReader.txt", "Test File", ServerSetupTest.SMTP);
				
		BaleenCollectionReader bcr = (BaleenCollectionReader) CollectionReaderFactory.createReader(EmailReader.class,
				EmailReader.PARAM_PROTOCOL, "pop3",
				EmailReader.PARAM_WAIT, 5,
				EmailReader.PARAM_SERVER, greenMail.getPop3().getBindTo(),
				EmailReader.PARAM_PORT, greenMail.getPop3().getPort(),
				EmailReader.PARAM_USER, "to@localhost.com",
				EmailReader.PARAM_PASS, "password",
				EmailReader.PARAM_PROCESS, "NotARealProcess",
				EmailReader.PARAM_FOLDER, folder.getPath());

		bcr.initialize();
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		assertTrue(jCas.getDocumentText().startsWith(body1));
		
		jCas.reset();
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		assertTrue(jCas.getDocumentText().startsWith(body2));
		
		jCas.reset();

		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		assertTrue(jCas.getDocumentText().startsWith(FIRST_LINE));
		assertEquals(1, folder.list().length);
		
		jCas.reset();
		
		assertFalse(bcr.doHasNext());
		
		bcr.close();
		
		FileUtils.deleteDirectory(folder);
	}
	
	@Test
	public void testImapNoMessages() throws Exception{
		BaleenCollectionReader bcr = (BaleenCollectionReader) CollectionReaderFactory.createReader(EmailReader.class,
				EmailReader.PARAM_PROTOCOL, "imap",
				EmailReader.PARAM_WAIT, 5,
				EmailReader.PARAM_SERVER, greenMail.getImap().getBindTo(),
				EmailReader.PARAM_PORT, greenMail.getImap().getPort(),
				EmailReader.PARAM_USER, "to@localhost.com",
				EmailReader.PARAM_PASS, "password");

		bcr.initialize();

		assertFalse(bcr.doHasNext());
		
		bcr.close();
	}
	
	@Test
	public void testImapContent() throws Exception{
		String subject1 = GreenMailUtil.random();
		String body1 = GreenMailUtil.random();
		
		GreenMailUtil.sendTextEmailTest("to@localhost.com", "from@localhost.com", subject1, body1);
		
		String subject2 = GreenMailUtil.random();
		String body2 = GreenMailUtil.random();
		
		GreenMailUtil.sendAttachmentEmail("to@localhost.com", "from@localhost.com", subject2, body2, IOUtils.toByteArray(getClass().getResourceAsStream("lineReader.txt")), "text/plain", "lineReader.txt", "Test File", ServerSetupTest.SMTP);
				
		BaleenCollectionReader bcr = (BaleenCollectionReader) CollectionReaderFactory.createReader(EmailReader.class,
				EmailReader.PARAM_PROTOCOL, "imap",
				EmailReader.PARAM_WAIT, 5,
				EmailReader.PARAM_SERVER, greenMail.getImap().getBindTo(),
				EmailReader.PARAM_PORT, greenMail.getImap().getPort(),
				EmailReader.PARAM_USER, "to@localhost.com",
				EmailReader.PARAM_PASS, "password",
				EmailReader.PARAM_PROCESS, "content");

		bcr.initialize();
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		assertTrue(jCas.getDocumentText().startsWith(body1));
		
		jCas.reset();
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		assertTrue(jCas.getDocumentText().startsWith(body2));
		
		jCas.reset();
		
		assertFalse(bcr.doHasNext());
		
		bcr.close();
	}
	
	@Test
	public void testImapAttachments() throws Exception{
		File folder = Files.createTempDir();
		
		String subject1 = GreenMailUtil.random();
		String body1 = GreenMailUtil.random();
		
		GreenMailUtil.sendTextEmailTest("to@localhost.com", "from@localhost.com", subject1, body1);
		
		String subject2 = GreenMailUtil.random();
		String body2 = GreenMailUtil.random();
		
		GreenMailUtil.sendAttachmentEmail("to@localhost.com", "from@localhost.com", subject2, body2, IOUtils.toByteArray(getClass().getResourceAsStream("lineReader.txt")), "text/plain", "lineReader.txt", "Test File", ServerSetupTest.SMTP);
				
		BaleenCollectionReader bcr = (BaleenCollectionReader) CollectionReaderFactory.createReader(EmailReader.class,
				EmailReader.PARAM_PROTOCOL, "imap",
				EmailReader.PARAM_WAIT, 5,
				EmailReader.PARAM_SERVER, greenMail.getImap().getBindTo(),
				EmailReader.PARAM_PORT, greenMail.getImap().getPort(),
				EmailReader.PARAM_USER, "to@localhost.com",
				EmailReader.PARAM_PASS, "password",
				EmailReader.PARAM_PROCESS, "attachments",
				EmailReader.PARAM_FOLDER, folder.getPath());

		bcr.initialize();
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		assertTrue(jCas.getDocumentText().startsWith(FIRST_LINE));
		assertEquals(1, folder.list().length);
		
		jCas.reset();
		
		assertFalse(bcr.doHasNext());
		
		bcr.close();
		
		FileUtils.deleteDirectory(folder);
	}
	
	@Test
	public void testImapBoth() throws Exception{
		File folder = Files.createTempDir();
		
		String subject1 = GreenMailUtil.random();
		String body1 = GreenMailUtil.random();
		
		GreenMailUtil.sendTextEmailTest("to@localhost.com", "from@localhost.com", subject1, body1);
		
		String subject2 = GreenMailUtil.random();
		String body2 = GreenMailUtil.random();
		
		GreenMailUtil.sendAttachmentEmail("to@localhost.com", "from@localhost.com", subject2, body2, IOUtils.toByteArray(getClass().getResourceAsStream("lineReader.txt")), "text/plain", "lineReader.txt", "Test File", ServerSetupTest.SMTP);
				
		BaleenCollectionReader bcr = (BaleenCollectionReader) CollectionReaderFactory.createReader(EmailReader.class,
				EmailReader.PARAM_PROTOCOL, "imap",
				EmailReader.PARAM_WAIT, 5,
				EmailReader.PARAM_SERVER, greenMail.getImap().getBindTo(),
				EmailReader.PARAM_PORT, greenMail.getImap().getPort(),
				EmailReader.PARAM_USER, "to@localhost.com",
				EmailReader.PARAM_PASS, "password",
				EmailReader.PARAM_PROCESS, "both",
				EmailReader.PARAM_FOLDER, folder.getPath());

		bcr.initialize();
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		assertTrue(jCas.getDocumentText().startsWith(body1));
		
		jCas.reset();
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		assertTrue(jCas.getDocumentText().startsWith(body2));
		
		jCas.reset();

		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		assertTrue(jCas.getDocumentText().startsWith(FIRST_LINE));
		assertEquals(1, folder.list().length);
		
		jCas.reset();
		
		assertFalse(bcr.doHasNext());
		
		bcr.close();
		
		FileUtils.deleteDirectory(folder);
	}
	
	@Test
	public void testImapDeleteMessages() throws Exception{
		String subject1 = GreenMailUtil.random();
		String body1 = GreenMailUtil.random();
		
		GreenMailUtil.sendTextEmailTest("to@localhost.com", "from@localhost.com", subject1, body1);
		
		String subject2 = GreenMailUtil.random();
		String body2 = GreenMailUtil.random();
		
		GreenMailUtil.sendAttachmentEmail("to@localhost.com", "from@localhost.com", subject2, body2, IOUtils.toByteArray(getClass().getResourceAsStream("lineReader.txt")), "text/plain", "lineReader.txt", "Test File", ServerSetupTest.SMTP);
				
		BaleenCollectionReader bcr = (BaleenCollectionReader) CollectionReaderFactory.createReader(EmailReader.class,
				EmailReader.PARAM_PROTOCOL, "imap",
				EmailReader.PARAM_WAIT, 5,
				EmailReader.PARAM_SERVER, greenMail.getImap().getBindTo(),
				EmailReader.PARAM_PORT, greenMail.getImap().getPort(),
				EmailReader.PARAM_USER, "to@localhost.com",
				EmailReader.PARAM_PASS, "password",
				EmailReader.PARAM_PROCESS, "content",
				EmailReader.PARAM_DELETE_EMAIL, true);

		bcr.initialize();
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		assertTrue(jCas.getDocumentText().startsWith(body1));
		
		jCas.reset();
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		assertTrue(jCas.getDocumentText().startsWith(body2));
		
		jCas.reset();
		
		assertFalse(bcr.doHasNext());
		
		bcr.close();
		
		//Check that there are no messages on the server
		bcr = (BaleenCollectionReader) CollectionReaderFactory.createReader(EmailReader.class,
				EmailReader.PARAM_PROTOCOL, "imap",
				EmailReader.PARAM_WAIT, 5,
				EmailReader.PARAM_SERVER, greenMail.getImap().getBindTo(),
				EmailReader.PARAM_PORT, greenMail.getImap().getPort(),
				EmailReader.PARAM_USER, "to@localhost.com",
				EmailReader.PARAM_PASS, "password");
		
		bcr.initialize();
		assertFalse(bcr.doHasNext());
		bcr.close();
	}
	
	@Test
	public void testImapDeleteAttachments() throws Exception{
		File folder = Files.createTempDir();
		
		String subject1 = GreenMailUtil.random();
		String body1 = GreenMailUtil.random();
		
		GreenMailUtil.sendTextEmailTest("to@localhost.com", "from@localhost.com", subject1, body1);
		
		String subject2 = GreenMailUtil.random();
		String body2 = GreenMailUtil.random();
		
		GreenMailUtil.sendAttachmentEmail("to@localhost.com", "from@localhost.com", subject2, body2, IOUtils.toByteArray(getClass().getResourceAsStream("lineReader.txt")), "text/plain", "lineReader.txt", "Test File", ServerSetupTest.SMTP);
				
		BaleenCollectionReader bcr = (BaleenCollectionReader) CollectionReaderFactory.createReader(EmailReader.class,
				EmailReader.PARAM_PROTOCOL, "imap",
				EmailReader.PARAM_WAIT, 5,
				EmailReader.PARAM_SERVER, greenMail.getImap().getBindTo(),
				EmailReader.PARAM_PORT, greenMail.getImap().getPort(),
				EmailReader.PARAM_USER, "to@localhost.com",
				EmailReader.PARAM_PASS, "password",
				EmailReader.PARAM_PROCESS, "attachments",
				EmailReader.PARAM_FOLDER, folder.getPath(),
				EmailReader.PARAM_DELETE_ATTACHMENT, true);

		bcr.initialize();
		
		assertTrue(bcr.doHasNext());
		bcr.getNext(jCas);
		
		assertTrue(jCas.getDocumentText().startsWith(FIRST_LINE));
		assertEquals(0, folder.list().length);
		
		jCas.reset();
		
		assertFalse(bcr.doHasNext());
		
		bcr.close();
		
		FileUtils.deleteDirectory(folder);
	}
}
