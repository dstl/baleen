package uk.gov.dstl.baleen.resources;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import uk.gov.dstl.baleen.resources.documentchecker.DocumentExistanceStatus;
import uk.gov.dstl.baleen.resources.documentchecker.FileSystemChecker;
import uk.gov.dstl.baleen.resources.documentchecker.UriChecker;
import uk.gov.dstl.baleen.uima.BaleenResource;

public class SharedDocumentCheckerResource extends BaleenResource {
	private List<DocumentExistanceStatus> statusListeners = new LinkedList<DocumentExistanceStatus>();
	private List<UriChecker> checkers = new LinkedList<UriChecker>();
    
	@Override
	protected boolean doInitialize(ResourceSpecifier aSpecifier, Map<String, Object> aAdditionalParams) throws ResourceInitializationException {
		UriChecker fs=new FileSystemChecker();
		fs.initilalize(this);
    	checkers.add(fs);
		return true;
	}
	
    /**
     * Register a status listener that will have its 'documentRemoved(uri)'
     * method called when a URI no longer exists.
     * @param cleaner
     */
    public synchronized void register(DocumentExistanceStatus cleaner) {
    	statusListeners.add(cleaner);
    }
    
    /**
     * Un-register a status listener
     * @param cleaner
     */
    public synchronized void unregister(DocumentExistanceStatus cleaner) {
    	statusListeners.remove(cleaner);
    	
    	if (statusListeners.isEmpty()) {
    		for (UriChecker checker : checkers) {
    			checker.shutdown();
    		}
    	}
    }
    
    /**
     * Mark a URI as needing to be checked for existence. The URI
     * is placed on the queue of each checker.
     * @param uri
     */
    public void check(String uri) {
    	for (UriChecker checker : checkers) {
    		checker.add(uri);
    	}
    }

    /**
     * Called by a checker to indicate a URI is no longer valid. This will
     * then call each registered listeners, so that they may remove references
     * to the URI.
     * @param uri
     */
    public synchronized void documentRemoved(String uri) {
    	for (DocumentExistanceStatus cleaner : statusListeners) {
    		cleaner.documentRemoved(uri);
    	}
    }
}
