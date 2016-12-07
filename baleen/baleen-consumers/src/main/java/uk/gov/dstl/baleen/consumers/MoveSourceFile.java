//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;

import com.google.common.base.Strings;

import uk.gov.dstl.baleen.types.metadata.Metadata;
import uk.gov.dstl.baleen.uima.BaleenConsumer;

/**
 * Move the source file of a document to the destination directory, or delete it if destination directory is not set.
 * 
 * Assumes that the Source URI is a file, and will throw an error if we're unable to read it.
 * 
 * @baleen.javadoc
 */
public class MoveSourceFile extends BaleenConsumer {

	/**
	 * The location to move source files to. If not set, then the source file will be deleted.
	 * 
	 * @baleen.config
	 */
	public static final String PARAM_DESTINATION = "destination";
	@ConfigurationParameter(name = PARAM_DESTINATION, defaultValue = "")
	String destination;
	
	/**
	 * If true, then documents will be split into subfolders based on type.
	 * Documents with no type will be placed in the root folder.
	 * 
	 * @baleen.config false
	 */
	public static final String PARAM_SPLIT = "splitByType";
	@ConfigurationParameter(name = PARAM_SPLIT, defaultValue = "false")
	Boolean splitByType;
	
	@Override
	protected void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		DocumentAnnotation da = getDocumentAnnotation(jCas);
		String source = da.getSourceUri();
		
		try{
			File f = new File(source);
			
			if(Strings.isNullOrEmpty(destination)){
				deleteFile(f);
				
				Metadata md = new Metadata(jCas);
				md.setKey("movedDocumentLocation");
				md.setValue("deleted");
				addToJCasIndex(md);
			}else{
				File dest = new File(destination);
				
				if(splitByType){
					String type = da.getDocType();
					if(!Strings.isNullOrEmpty(type)){
						dest = new File(dest, type);
					}
				}
				
				File finalDest = moveFile(f, dest);
				
				Metadata md = new Metadata(jCas);
				md.setKey("movedDocumentLocation");
				md.setValue(finalDest.getPath());
				addToJCasIndex(md);
			}
		}catch(IOException ioe){
			getMonitor().error("Unable to move source file", ioe);
		}
	}
	
	private void deleteFile(File file) throws IOException{
		Files.delete(file.toPath());
	}
	
	private File moveFile(File source, File destination) throws IOException{
		if((!destination.exists() || !destination.isDirectory()) && !destination.mkdirs()){
			throw new IOException("Destination folder '"+destination.getName()+"' does not exist and could not be created");	
		}
		
		if(!source.exists() || source.isDirectory()){
			throw new IOException("File '"+source.getName()+"' does not exist");
		}
		
		File destFile = new File(destination, source.getName());
		
		int append = 0;
		while(destFile.exists()){
			append++;
			destFile = new File(destination, source.getName() + "." + append);
		}
		if(append != 0){
			getMonitor().info("File with the same name already exists in {} - source file will be saved as {}", destination.getName(), destFile.getName());
		}
		
		FileUtils.moveFile(source, destFile);
		return destFile;
	}
}
