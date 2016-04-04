//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.collectionreaders;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FilenameUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.exceptions.InvalidParameterException;
import uk.gov.dstl.baleen.resources.SharedDocumentCheckerResource;
import uk.gov.dstl.baleen.resources.SharedDocumentStatusResource;
import uk.gov.dstl.baleen.resources.documentchecker.DocumentExistanceStatus;
import uk.gov.dstl.baleen.uima.BaleenCollectionReader;
import uk.gov.dstl.baleen.uima.IContentExtractor;

/**
 * Inspect a folder for unprocessed files, and process them through the pipeline.
 * Currently, the list of previously processed files is held in memory and so will be lost if the server is restarted.
 * This can be avoided by using the MoveSourceConsumer (for example), and removing the files after processing
 *
 * @baleen.javadoc
 */
public class FolderReader extends BaleenCollectionReader implements DocumentExistanceStatus {

	/**
	 * Map document location to modified date
	 */
	private Map<String, Long> documents = new ConcurrentHashMap<String, Long>();
	
	/**
	 * Document status resource
	 * 
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedDocumentStatusResource
	 */
	public static final String KEY_DOC_STATUS = "documentstatus";
	@ExternalResource(key = KEY_DOC_STATUS)
	private SharedDocumentStatusResource docStatus;


	/**
	 * Document checker resource
	 * 
	 * @baleen.resource uk.gov.dstl.baleen.resources.SharedDocumentStatusResource
	 */
	public static final String KEY_DOC_CHECKER = "documentchecker";
	@ExternalResource(key = KEY_DOC_CHECKER)
	private SharedDocumentCheckerResource docChecker;
	
	/**
	 * A list of folders to watch
	 * 
	 * @baleen.config <i>Current directory</i>
	 */
	public static final String PARAM_FOLDERS = "folders";
	@ConfigurationParameter(name = PARAM_FOLDERS, defaultValue = {})
	private String[] folders;

	/**
	 * Should files be reprocessed when modified?
	 * 
	 * @baleen.config false
	 */
	public static final String PARAM_REPROCESS_ON_MODIFY = "reprocess";
	@ConfigurationParameter(name = PARAM_REPROCESS_ON_MODIFY, defaultValue="false")
	private boolean reprocessOnModify = false;

	/**
	 * Should folders be processed recursively (i.e. should we watch subfolders too)?
	 * 
	 * @baleen.config true
	 */
	public static final String PARAM_RECURSIVE = "recursive";
	@ConfigurationParameter(name = PARAM_RECURSIVE, defaultValue="true")
	private boolean recursive = true;

	/**
	 * Should content be reingested at start-up
	 * 
	 * @baleen.config true
	 */
	public static final String PARAM_REINGEST = "reingest";
	@ConfigurationParameter(name = PARAM_REINGEST, defaultValue="true")
	private boolean reingest = true;
	
	/**
	 * The content extractor to use to extract content from files
	 * 
	 * @baleen.config TikaContentExtractor
	 */
	public static final String PARAM_CONTENT_EXTRACTOR = "contentExtractor";
	@ConfigurationParameter(name = PARAM_CONTENT_EXTRACTOR, defaultValue="TikaContentExtractor")
	private String contentExtractor = "TikaContentExtractor";

	/**
	 * A list of file extensions that are allowed by the folder reader.
	 * If not specified, then all file extensions are accepted.
	 * Extensions are assumed to be case insensitive.
	 * 
	 * @baleen.config
	 */
	public static final String PARAM_ALLOWED_EXTENSIONS = "allowedExtensions";
	@ConfigurationParameter(name = PARAM_ALLOWED_EXTENSIONS, defaultValue = {})
	private String[] allowedExtensions;
	
	private List<String> allowedExtensionsSet = new ArrayList<>();
	
	private WatchService watcher;
	private Map<WatchKey, Path> watchKeys = new HashMap<>();
	private List<Path> queue = new ArrayList<>();

	private IContentExtractor extractor;

	@Override
	public void doInitialize(UimaContext context) throws ResourceInitializationException {
		if(folders == null || folders.length == 0){
			folders = new String[1];
			folders[0] = System.getProperty("user.dir");
		}
		
		for(String extension : allowedExtensions){
			allowedExtensionsSet.add(extension.toLowerCase());
		}

		try{
			extractor = getContentExtractor(contentExtractor);
		}catch(InvalidParameterException ipe){
			throw new ResourceInitializationException(ipe);
		}
		extractor.initialize(context, getConfigParameters(context));

		try{
			watcher = FileSystems.getDefault().newWatchService();
		}catch(IOException ioe){
			throw new ResourceInitializationException(ioe);
		}

		if (!reingest && null==docStatus) {
			getMonitor().warn("No doc status resource, setting reingest to true");
			reingest=true;
		}
		if (!reingest) {
			docChecker.register(this);
			checkExistingDocuments();
		}
		registerFolders();
	}

	private void registerFolders() {
		for(String folder : folders){
			try{
				Path p = Paths.get(folder);
				p = p.toRealPath();

				if(recursive){
					Files.walkFileTree(p, new SimpleFileVisitor<Path>(){
						@Override
						public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attr) throws IOException{
							registerDirectory(dir);
							return FileVisitResult.CONTINUE;
						}
					});
				}else{
					registerDirectory(p);
				}
				addFilesFromDir(p.toFile());
			}catch(IOException ioe){
				getMonitor().warn("Could not find or register folder '{}' or it's subfolders - folder will be skipped", folder,ioe);
			}
		}		
	}

	private void registerDirectory(Path path) throws IOException{
		WatchKey key;
		if(reprocessOnModify){
			key = path.register(watcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
		}else{
			key = path.register(watcher, ENTRY_CREATE, ENTRY_DELETE);
		}

		watchKeys.put(key, path);
		
		getMonitor().counter("directories").inc();
	}

	private void addFilesFromDir(File dir){
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if(!files[i].isDirectory()){
				addFile(files[i].toPath());
				getMonitor().counter("files").inc();
			}else if (recursive) {
				addFilesFromDir(files[i]);
			}
		}
	}

	@Override
	public void doGetNext(JCas jCas) throws IOException, CollectionException {
		if(queue.isEmpty()){
			getMonitor().error("No documents on the queue - this method should not have been called");
			throw new CollectionException();
		}
		Path path = queue.remove(0);

		try(
			InputStream is = new FileInputStream(path.toFile());
		){
			extractor.processStream(is, path.toString(), jCas);
		}		
	}

	@Override
	public void doClose() throws IOException {
		if(watcher != null) {
			watcher.close();
			watcher = null;
		}
		
		watchKeys.clear();
		queue.clear();

		if(extractor != null) {
			extractor.destroy();
			extractor = null;
		}
		
		if (!reingest) {
			docChecker.unregister(this);
			documents.clear();
		}
	}

	/**
	 * Every time doHasNext() is called, check the WatchService for new events and add all new events to the queue.
	 * Then return true if there are files on the queue, or false otherwise.
	 *
	 * If the event indicates that a file has been deleted, ensure it is removed from the queue.
	 */
	@Override
	public boolean doHasNext() throws IOException, CollectionException {
		WatchKey key;
		while((key = watcher.poll()) != null && !isShuttingDown()){
			for(WatchEvent<?> event : key.pollEvents()){
				processEvent(key, event);
				getMonitor().meter("events").mark();				
			}

			key.reset();
		}

		return !queue.isEmpty();
	}

	private void processEvent(WatchKey key, WatchEvent<?> event) {
		@SuppressWarnings("unchecked")
		WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;

		if(event.kind() == OVERFLOW){
			getMonitor().warn("OVERFLOW event received - some files may be missing from the queue");
		}else if(event.kind() == ENTRY_DELETE){
			getMonitor().debug("ENTRY_DELETE event received - file '{}' will be removed from queue", pathEvent.context());

			try{
				Path dir = watchKeys.get(key);
				if(dir != null){
					Path resolved = dir.resolve(pathEvent.context());
					boolean wasDirectory = false;
					
					for (Map.Entry<WatchKey, Path> watch : watchKeys.entrySet()) {
						if (watch.getValue().equals(resolved)) {
							getMonitor().debug("Removing dir {} from watch list", resolved);
							watchKeys.remove(watch.getKey());
							getMonitor().counter("directories").dec();
							wasDirectory=true;
							break;
						}
					}
					if (!wasDirectory) {
						queue.remove(resolved);
						docChecker.check(resolved.toString());
					}
				}else{
					getMonitor().warn("WatchKey not found - file '{}' will not be removed from the queue", pathEvent.context());
				}
			}catch(Exception ioe){
				getMonitor().warn("An error occurred - file '{}' will not be removed from the queue", pathEvent.context(), ioe);
			}
			
			queue.remove(pathEvent.context());
		}else {
			getMonitor().debug(event.kind().name() + " event received - file '{}' will be added to the queue", pathEvent.context());
			try{
				Path dir = watchKeys.get(key);
				if(dir != null){
					Path resolved = dir.resolve(pathEvent.context());
					if (resolved.toFile().isDirectory()) {
						getMonitor().debug("Adding dir {} to watch list", resolved);
						registerDirectory(resolved);
						addFilesFromDir(resolved.toFile());
					} else {
						addFile(resolved);
					}
				}else{
					getMonitor().warn("WatchKey not found - file '{}' will not be added to the queue", pathEvent.context());
				}
			}catch(Exception ioe){
				getMonitor().warn("An error occurred - file '{}' will not be added to the queue", pathEvent.context(), ioe);
			}
		}
	}
	
	private void addFile(Path path){
		if(allowedExtensionsSet.isEmpty() || allowedExtensionsSet.contains(FilenameUtils.getExtension(path.toString().toLowerCase()))){
			/**
			 * Add file to queue if..
			 * a. We always reingest OR
			 * b. Its a new file OR
			 * c. Its modification time is newer than that stored in DB
			 */
			boolean canAdd=reingest ||
					       !documents.containsKey(path.toString()) ||
					       documents.get(path.toString())<path.toFile().lastModified();
			
			if (canAdd) {
				queue.add(path);
			} else {
				getMonitor().info("Not re-ingesting {}", path);
			}
		}
	}

	@Override
	public void documentRemoved(String uri) {
		documents.remove(uri);
		docStatus.removeDocumentDetails(uri);
	}
	
	private void checkExistingDocuments() {
		documents.putAll(docStatus.getExistingDocumentDetails());
		for (Map.Entry<String, Long> entry : documents.entrySet()) {
			docChecker.check(entry.getKey());
		}
	}
}
