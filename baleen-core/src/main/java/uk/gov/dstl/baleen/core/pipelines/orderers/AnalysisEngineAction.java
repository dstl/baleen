//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.pipelines.orderers;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.uima.jcas.tcas.Annotation;

/**
 * Holds details of the actions performed by an analysis engine (e.g. an annotator).
 * 
 * Currently, that encompasses only the inputs and outputs of each analysis engine,
 * but this may be expanded in the future.
 */
public class AnalysisEngineAction {
	private Set<Class<? extends Annotation>> inputs = new HashSet<>();
	private Set<Class<? extends Annotation>> outputs = new HashSet<>();

	/**
	 * Create a new AnalysisEngineAction
	 */
	public AnalysisEngineAction(Set<Class<? extends Annotation>> inputs, Set<Class<? extends Annotation>> outputs){
		this.inputs = inputs;
		this.outputs = outputs;
	}
	
	/**
	 * Returns the input annotations defined by this action.
	 * 
	 * An annotation is generally deemed to be an input if it is required 
	 * by an analysis engine.
	 */
	public Set<Class<? extends Annotation>> getInputs(){
		return inputs;
	}
	
	/**
	 * Returns the output annotations defined by this action.
	 * 
	 * An annotation is generally deemed to be an output if it is produced
	 * by an analysis engine. Modification is not considered to be an output.
	 * 
	 * Modification of DocumentAnnotation (i.e. setting the document language)
	 * is considered a special case where it is included as an output.
	 */
	public Set<Class<? extends Annotation>> getOutputs(){
		return outputs;
	}
	
	@Override
	public String toString() {
		StringJoiner sjInputs = new StringJoiner(",");
		StringJoiner sjOutputs = new StringJoiner(",");
		
		for(Class<? extends Annotation> c : inputs){
			sjInputs.add(c.getSimpleName());
		}
		for(Class<? extends Annotation> c : outputs){
			sjOutputs.add(c.getSimpleName());
		}
		
		return "AnalysisEngineAction ("+sjInputs.toString()+") -> ("+sjOutputs.toString()+")";
	}
}