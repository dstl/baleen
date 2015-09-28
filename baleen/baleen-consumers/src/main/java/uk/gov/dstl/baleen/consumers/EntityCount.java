//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.consumers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;

import uk.gov.dstl.baleen.types.semantic.Entity;
import uk.gov.dstl.baleen.uima.BaleenConsumer;

/**
 * Create a TSV file listing the files processed and how many entities were found in each file
 * 
 * 
 * @baleen.javadoc
 */
public class EntityCount extends BaleenConsumer {

	/**
	 * The file to write results to
	 * 
	 * @baleen.config entityCount.tsv
	 */
	public static final String PARAM_OUTPUT_FILE = "outputFile";
	@ConfigurationParameter(name = PARAM_OUTPUT_FILE, defaultValue="entityCount.tsv")
	private String outputFile = "entityCount.tsv";
	
	private File output;

	@Override
	public void doInitialize(UimaContext aContext) throws ResourceInitializationException {
		output = new File(outputFile);
		
		try{
			if(!output.exists()){
				output.createNewFile();
			}
			
			if(!output.canWrite()){
				throw new IOException("Can not write to output file");
			}
		}catch(IOException ioe){
			throw new ResourceInitializationException(ioe);
		}
	}

	@Override
	public void doProcess(JCas jCas) throws AnalysisEngineProcessException {
		DocumentAnnotation da = getDocumentAnnotation(jCas);
		
		try( PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(output, true))) ) {
			int count = JCasUtil.select(jCas, Entity.class).size();
			pw.println(da.getSourceUri()+"\t"+count);
		} catch (IOException e) {
			getMonitor().warn("Unable to write to output", e);
		}
	}

	@Override
	public void doDestroy() {
		output = null;
	}

}
