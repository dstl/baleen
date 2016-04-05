package uk.gov.dstl.baleen.collectionreaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringJoiner;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.MethodNotSupportedException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;
import uk.gov.dstl.baleen.uima.IContentExtractor;

/**
 * Connects to a specified mail server, treating each message and/or it's attachments as documents.
 * 
 * Optionally, messages can be deleted or marked as read after it has been retrieved.
 * Be aware that this happens at the point the message is retrieved, and as such if the pipeline
 * fails at a later point then the message will still have been deleted/marked read.
 */
public class EmailReader extends BaleenCollectionReader {
	/**
	 * The mail server URL
	 * 
	 * @baleen.config localhost
	 */
	public static final String PARAM_SERVER = "server";
	@ConfigurationParameter(name = PARAM_SERVER, defaultValue = "localhost")
	private String server;
	
	/**
	 * The mail server port
	 * 
	 * @baleen.config 110
	 */
	public static final String PARAM_PORT = "port";
	@ConfigurationParameter(name = PARAM_PORT, defaultValue = "110")
	private Integer port;
	
	/**
	 * The e-mail address (user) to check
	 * 
	 * @baleen.config baleen@localhost
	 */
	public static final String PARAM_USER = "username";
	@ConfigurationParameter(name = PARAM_USER, defaultValue = "baleen@localhost")
	private String user;
	
	/**
	 * The password for the account
	 * 
	 * @baleen.config
	 */
	public static final String PARAM_PASS = "password";
	@ConfigurationParameter(name = PARAM_PASS, defaultValue = "")
	private String pass;
	
	/**
	 * Connection protocol (e.g. pop3 or imap)
	 * 
	 * Must be a valid JavaMail protocol - pop3 and imap are supported by default,
	 * other protocols will require the relevant libraries to be on the classpath.
	 * 
	 * @baleen.config pop3
	 */
	public static final String PARAM_PROTOCOL = "protocol";
	@ConfigurationParameter(name = PARAM_PROTOCOL, defaultValue = "pop3")
	private String protocol;
	
	/**
	 * The name of the inbox on the server which we will monitor
	 * 
	 * @baleen.config INBOX
	 */
	public static final String PARAM_INBOX = "inbox";
	@ConfigurationParameter(name = PARAM_INBOX, defaultValue = "INBOX")
	private String inbox;

	/**
	 * How minimum wait in seconds between checking for new messages
	 * 
	 * @baleen.config 120
	 */
	public static final String PARAM_WAIT = "wait";
	@ConfigurationParameter(name = PARAM_WAIT, defaultValue = "120")
	private Integer wait;
	
	/**
	 * Should a message be deleted after it has been processed
	 * 
	 * @baleen.config false
	 */
	public static final String PARAM_DELETE_EMAIL = "deleteEmails";
	@ConfigurationParameter(name = PARAM_DELETE_EMAIL, defaultValue = "false")
	private Boolean deleteEmailsAfterProcessing;
	
	/**
	 * Should an attachment be deleted after it has been processed
	 * 
	 * @baleen.config false
	 */
	public static final String PARAM_DELETE_ATTACHMENT = "deleteAttachments";
	@ConfigurationParameter(name = PARAM_DELETE_ATTACHMENT, defaultValue = "false")
	private Boolean deleteAttachmentsAfterProcessing;
	
	/**
	 * The folder in which to save attachments
	 * 
	 * @baleen.config <i>Current directory</i>
	 */
	public static final String PARAM_FOLDER = "attachmentFolder";
	@ConfigurationParameter(name = PARAM_FOLDER, defaultValue = "")
	private String folder;
	
	/**
	 * Choose whether to process just message content, just attachments, or both.
	 * 
	 * Valid options are: content, attachments, both
	 * 
	 * @baleen.config both
	 */
	public static final String PARAM_PROCESS = "process";
	@ConfigurationParameter(name = PARAM_PROCESS, defaultValue = BOTH)
	private String process;
	
	/**
	 * The content extractor to use to extract content from messages or attachments
	 * 
	 * @baleen.config TikaContentExtractor
	 */
	public static final String PARAM_CONTENT_EXTRACTOR = "contentExtractor";
	@ConfigurationParameter(name = PARAM_CONTENT_EXTRACTOR, defaultValue="TikaContentExtractor")
	private String contentExtractor = "TikaContentExtractor";

	public static final String BOTH = "both";
	public static final String CONTENT = "content";
	public static final String ATTACHMENTS = "attachments";
	
	private IContentExtractor extractor;
	
	private Long lastCheck = 0L;
	
	private Authenticator authenticator;
	private Session session;
	private Store store;
	private Folder inboxFolder;
	
	private List<Message> messageQueue = new ArrayList<>();
	private List<File> attachmentQueue = new ArrayList<>();
	
	private Set<String> alreadyProcessed = new HashSet<>();
		
	@Override
	protected void doInitialize(UimaContext context) throws ResourceInitializationException {
		validateParams();
		
		try{
			extractor = getContentExtractor(contentExtractor);
		}catch(InvalidParameterException ipe){
			throw new ResourceInitializationException(ipe);
		}
		extractor.initialize(context, getConfigParameters(context));
		
		authenticator = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication(){
				return new PasswordAuthentication(user, pass);
			}
		};
		
		Properties prop = new Properties();
		prop.put("mail.store.protocol", protocol);
		prop.put("mail.host", server);
		prop.put("mail."+protocol+".port", port);
		prop.put("mail.user", user);
		
		session = Session.getInstance(prop, authenticator);
		try {
			store = session.getStore();
		} catch (NoSuchProviderException e) {
			throw new ResourceInitializationException(e);
		}
		
		try{
			store.connect();
			inboxFolder = store.getFolder(inbox);
			reopenConnection();
		}catch(MessagingException me){
			throw new ResourceInitializationException(me);
		}
	}
	
	@Override
	protected void doGetNext(JCas jCas) throws IOException, CollectionException {
		try{
			reopenConnection();
		}catch(MessagingException me){
			throw new IOException("Unable to reconnect to mail server", me);
		}
		
		try{
			if(CONTENT.equalsIgnoreCase(process)){
				processMessage(jCas, messageQueue.remove(0), Collections.emptyList());
			}else if(ATTACHMENTS.equalsIgnoreCase(process)){
				if(attachmentQueue.isEmpty()){
					attachmentQueue.addAll(saveAttachments(messageQueue.remove(0)));
				}
				
				processAttachment(jCas, attachmentQueue.remove(0));
			}else{	//Both
				if(!attachmentQueue.isEmpty()){
					processAttachment(jCas, attachmentQueue.remove(0));
				}else{
					Message msg = messageQueue.remove(0);
					
					List<File> attachments = saveAttachments(msg);
					attachmentQueue.addAll(attachments);
					
					processMessage(jCas, msg, attachments);
				}
			}
		}catch(MessagingException me){
			throw new IOException("Unable to process message or attachment", me);
		}
	}

	@Override
	protected void doClose() throws IOException {
		try{
			inboxFolder.close(true);
			store.close();
		}catch(MessagingException me){
			throw new IOException(me);
		}
	}

	@Override
	public boolean doHasNext() throws IOException, CollectionException {
		try{
			reopenConnection();
		}catch(MessagingException me){
			throw new IOException("Unable to reconnect to mail server", me);
		}
		
		if(!attachmentQueue.isEmpty() || !messageQueue.isEmpty()){
			return true;
		}
		
		try{
			tryExpunge();
		}catch(MessagingException me){
			throw new IOException("Unable to expunge (delete) messages", me);
		}
		
		if(lastCheck + wait*1000 > System.currentTimeMillis()){
			return false;
		}
		
		lastCheck = System.currentTimeMillis();
		
		try{
			for(Message msg : inboxFolder.getMessages()){
				String uid = generateUniqueId(msg);
				if(!alreadyProcessed.add(uid)){
					continue;
				}
				
				if(!ATTACHMENTS.equalsIgnoreCase(process) || hasAttachments(msg)){
					messageQueue.add(msg);
				}
			}
		}catch(MessagingException me){
			throw new IOException("Unable to check for messages", me);
		}
		
		return !messageQueue.isEmpty();
	}
	
	private String generateUniqueId(Message msg) throws MessagingException{
		String sentDate = "NOSD";
		String receivedDate = "NORD";
		
		if(msg.getSentDate() != null){
			sentDate = String.valueOf(msg.getSentDate().toInstant().toEpochMilli());
		}
		if(msg.getReceivedDate() != null){
			receivedDate = String.valueOf(msg.getReceivedDate().toInstant().toEpochMilli());
		}
		
		String sender = getAddress(msg.getFrom()[0]);
		
		return joinStrings(msg.getSubject(), sender, sentDate, receivedDate);
	}
	
	private String joinStrings(String... strings){
		StringJoiner sj = new StringJoiner("_");
		for(String s : strings){
			if(s != null)
				sj.add(s);
		}
		
		return sj.toString();
	}
	
	private void processMessage(JCas jCas, Message msg, List<File> attachments) throws MessagingException, IOException{
		String content = getContent(msg);
		
		String subject = msg.getSubject();
		String sender = getAddress(msg.getFrom()[0]);
		
		InputStream is = IOUtils.toInputStream(content);
		extractor.processStream(is, "mailto:"+sender + "#" + subject, jCas);
		
		addMetadata(jCas, "sender", sender);
		addMetadata(jCas, "subject", subject);

		addAddressesMetadata(msg.getRecipients(RecipientType.TO), jCas, "toRecipient");
		addAddressesMetadata(msg.getRecipients(RecipientType.CC), jCas, "ccRecipient");
		
		for(String attachment : getAttachments(msg)){	//We don't use the attachments list here, because we want to add the list of attachments to the metadata regardless of whether we've saved them or not
			addMetadata(jCas, "attachment", attachment);
		}
		if(!attachments.isEmpty()){
			for(File attachment : attachments){
				addMetadata(jCas, "attachmentSaveLocation", attachment.getAbsolutePath());
			}
		}
		
		@SuppressWarnings("unchecked")
		Enumeration<Header> headers = msg.getAllHeaders();
		while(headers.hasMoreElements()){
			Header header = headers.nextElement();
			addMetadata(jCas, header.getName(), header.getValue());
		}
		
		//Delete message?
		if(deleteEmailsAfterProcessing){
			try {
				msg.setFlag(Flags.Flag.DELETED, true);
			} catch (MessagingException me) {
				getMonitor().error("Unable to delete message", me);
			}
			
			try{
				alreadyProcessed.remove(generateUniqueId(msg));	//We can save memory by removing messages we've deleted on the server
			} catch (MessagingException me) {
				getMonitor().warn("Unable to re-generate unique ID for message to remove from memory", me);
			}
		}
	}
	
	private void processAttachment(JCas jCas, File attachment) throws IOException{
		try(
			InputStream is = new FileInputStream(attachment);
		){
			extractor.processStream(is, attachment.getAbsolutePath(), jCas);
		}
		
		//Delete attachment?
		if(deleteAttachmentsAfterProcessing){
			try {
				Files.delete(attachment.toPath());
			} catch (IOException ioe) {
				getMonitor().error("Unable to delete attachment", ioe);
			}
		}
	}
	
	private String getContent(Message msg) throws IOException, MessagingException{
		Object messageContentObject = msg.getContent();
		if(messageContentObject instanceof Multipart){
			Multipart multipart = (Multipart) msg.getContent();

			// Loop over the parts of the email
			for(int i = 0; i < multipart.getCount(); i++) {
				// Retrieve the next part
				Part part = multipart.getBodyPart(i);

				if(!Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()) && StringUtils.isBlank(part.getFileName())){
					return part.getContent().toString();
				}
			}
		}else{
			return msg.getContent().toString().trim();
		}
		
		return "";
	}
	
	private Boolean hasAttachments(Message msg) throws MessagingException, IOException{
		if(msg.isMimeType("multipart/mixed")){
			Multipart mp = (Multipart)msg.getContent();
			if(mp.getCount() > 1){
				return true;
			}
		}
		
		return false;
	}
	
	private List<String> getAttachments(Message msg) throws MessagingException, IOException{
		Object messageContentObject = msg.getContent();
		
		List<String> attachments = new ArrayList<>();
		
		if (messageContentObject instanceof Multipart) {
			Multipart multipart = (Multipart) msg.getContent();

			for(int i = 0; i < multipart.getCount(); i++) {
				Part part = multipart.getBodyPart(i);

				if(!Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()) && StringUtils.isBlank(part.getFileName())){
					continue;
				}
				
				attachments.add(part.getFileName());
			}
		}
		
		return attachments;
	}
	
	private List<File> saveAttachments(Message msg) throws MessagingException, IOException{
		Object messageContentObject = msg.getContent();

		List<File> attachmentLocations = new ArrayList<>();
		
		// Determine email type
		if (messageContentObject instanceof Multipart) {
			// Retrieve the Multipart object from the message
			Multipart multipart = (Multipart) msg.getContent();

			// Loop over the parts of the email
			for(int i = 0; i < multipart.getCount(); i++) {
				// Retrieve the next part
				Part part = multipart.getBodyPart(i);

				if(!Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()) && StringUtils.isBlank(part.getFileName())){
					continue;
				}
				String fileName = part.getFileName();
				
				File destFile = new File(folder, fileName);
				
				int append = 0;
				while(destFile.exists()){
					append++;
					destFile = new File(folder, fileName + "." + append);
				}
				if(append != 0){
					getMonitor().info("File with the same name already exists in {} - attachment will be saved as {}", fileName, destFile.getName());
				}

				// Save the file to disk
				writeFileToDisk(destFile, part.getInputStream());
				
				attachmentLocations.add(destFile);
			}

		}
		
		return attachmentLocations;
	}
	
	private void writeFileToDisk(File destFile, InputStream inputStream) throws IOException{
		FileOutputStream output = null;
		try{
			output = new FileOutputStream(destFile);

			byte[] buffer = new byte[4096];
			int byteRead;

			while ((byteRead = inputStream.read(buffer)) != -1) {
				output.write(buffer, 0, byteRead);
			}
		}catch(IOException ex){
			throw new IOException("Unable to save attachment", ex);
		}finally{
			if(inputStream != null){
				try{
					inputStream.close();
				}catch(Exception e){
					getMonitor().debug("Unable to close InputStream, or already closed", e);
				}
			}
			
			if(output != null){
				try{
					output.close();
				}catch(Exception e){
					getMonitor().debug("Unable to close FileOutputStream, or already closed", e);
				}
			}
		}
	}
	
	private void validateParams(){
		if(folder == null || folder.isEmpty()){
			folder = System.getProperty("user.dir");
		}
		
		if(process.isEmpty() || !(BOTH.equalsIgnoreCase(process) || CONTENT.equalsIgnoreCase(process) || ATTACHMENTS.equalsIgnoreCase(process))){
			process = BOTH;
		}
	}
	
	private void addMetadata(JCas jCas, String key, String value){
		Metadata md = new Metadata(jCas);
		md.setKey(key);
		md.setValue(value);
		getSupport().add(md);
	}
	
	private void tryExpunge() throws MessagingException{
		try{
			inboxFolder.expunge();
		}catch(MethodNotSupportedException mnse){
			getMonitor().debug("Expunge method not supported (e.g. POP3) - closing and reopening folder", mnse);
			
			inboxFolder.close(true);
			reopenConnection();
		}
	}
	
	private void reopenConnection() throws MessagingException{
		if(!inboxFolder.isOpen()){
			inboxFolder.open(Folder.READ_WRITE);
		}
	}
	
	private String getAddress(Address addr){
		String address = ((InternetAddress) addr).getPersonal();
		if (address == null) {
			address = ((InternetAddress) addr).getAddress();
		}
		
		return address;
	}
	
	private void addAddressesMetadata(Address[] addresses, JCas jCas, String key){
		if(addresses != null){
			for(Address addr : addresses){
				addMetadata(jCas, key, getAddress(addr));
			}
		}
	}
}
