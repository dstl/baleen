//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.consumers.utils;

import java.io.File;
import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.exceptions.BaleenException;
import uk.gov.dstl.baleen.uima.BaleenConsumer;

/** A base implement for a consumer that writes to the file system.
 * 
 * The file created is derived from the sourceUrl, see SourceUtils for details.
 *  
 * 
 * @baleen.javadoc
 */
public abstract class AbstractBaleenFileConsumer extends BaleenConsumer {

	/**
	 * The root directory to output to.
	 * If not set, then the current directory is used.
	 * 
	 * @baleen.config
	 */
	public static final String PARAM_BASE_PATH = "basePath";
	@ConfigurationParameter(name = PARAM_BASE_PATH, defaultValue = "")
	private String basePathString;

	private File basePath;

	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		super.doInitialize(aContext);

		if (!Strings.isNullOrEmpty(basePathString)) {
			basePath = new File(basePathString);

			if (basePath.exists() && !basePath.isDirectory()) {
				throw new ResourceInitializationException(new IOException("Path already exists and is not a directory"));
			} else if (!basePath.exists() && !basePath.mkdirs()){
				throw new ResourceInitializationException(new IOException("Unable to create directory structure"));
			}
		} else {
			basePath = null;
		}
	}

	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		DocumentAnnotation documentAnnotation =getDocumentAnnotation(jCas); 
		String url = documentAnnotation.getSourceUri();
		if(Strings.isNullOrEmpty(url)) {
			url = ConsumerUtils.getExternalId(documentAnnotation, false);
		}
		
		String extension = getExtension();
		if(!Strings.isNullOrEmpty(extension)) {
			url = url + "." + extension;
		}
		
		File file = SourceUtils.urlToFile(basePath, url);
		
		try {
			getMonitor().debug("Writing {} to {}", url, file.getAbsolutePath());
			writeToFile(jCas, file);
		} catch(Exception e) {
			getMonitor().warn("Failed to write file {}, deleting", file.getAbsolutePath(), e);
			if(!file.delete()){
				getMonitor().warn("Failed to delete file {}", file.getAbsolutePath(), e);	
			}
		}
	}

	protected String getExtension() {
		return "";
	}

	protected abstract void writeToFile(JCas jCas, File file)
			throws BaleenException;

}
