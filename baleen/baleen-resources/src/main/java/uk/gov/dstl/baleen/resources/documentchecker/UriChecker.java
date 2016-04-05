package uk.gov.dstl.baleen.resources.documentchecker;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.resources.SharedDocumentCheckerResource;

public abstract class UriChecker implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(UriChecker.class);

	private boolean shuttingDown = false;
	private Thread thread = null;
	private BlockingQueue<String> uris = new LinkedBlockingQueue<String>();
	protected SharedDocumentCheckerResource checker;

	public void initilalize(SharedDocumentCheckerResource checker) {
		this.checker=checker;
	}

	/**
	 * Add a URI to the list needing to be checked.
	 * @param uri
	 */
	public void add(String uri) {
		if (null!=uri && !uri.isEmpty() && !uris.contains(uri) && canCheck(uri)) {
			LOGGER.debug("Add {} to check queue", uri);
			uris.add(uri);
		}
		// If we have added a URI to our check list, then start the processing thread...
		if (!uris.isEmpty() && (null==thread || !thread.isAlive())) {
			thread = new Thread(this);
			thread.start();
		}
	}

	/**
	 * Request that the checker stops.
	 */
	public void shutdown() {
		shuttingDown = true;
		if (null!=thread) { 
			thread.interrupt();
			try {
				if (thread.isAlive()) {
					thread.join();
				}
			} catch (InterruptedException e) {
			}
			thread=null;
		}
	}

	/** Check if this checker is shutting down.
	 * Child classes (which block in say doHasNext()) should check this periodically to see if the checker is ready to close
	 * and they should return.
	 * @return true is shutting down.
	 */
	public boolean isShuttingDown() {
		return shuttingDown;
	}

	@Override
	public void run() {
		while (!shuttingDown) {
			try {
				checkExists(uris.take());
			} catch (InterruptedException e) {
				return;
			}
		}
	}
	
	/**
	 * @param uri
	 * @return true if this checker can check the existence of URI
	 */
	protected abstract boolean canCheck(String uri);

	/**
	 * @param uri to check
	 */
	protected abstract void checkExists(String uri);
}
