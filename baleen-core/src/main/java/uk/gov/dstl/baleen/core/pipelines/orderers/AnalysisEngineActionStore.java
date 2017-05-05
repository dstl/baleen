//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.pipelines.orderers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A singleton class to hold action information for all annotators currently within Baleen.
 * 
 * This is not the best way to do it, but we're constrained by UIMA and the fact that we're
 * unable to get from the AnalysisEngine we're given in the pipeline back to BaleenAnnotator
 * where this information is defined. So we need an intermediary to store the information,
 * which is this class.
 */
public class AnalysisEngineActionStore {
	private static final AnalysisEngineActionStore INSTANCE = new AnalysisEngineActionStore();
	
	private Map<String, AnalysisEngineAction> store = new HashMap<>();
	
	private AnalysisEngineActionStore(){
		// Private constructor
	}
	
	/**
	 * Get the singleton instance of this class
	 */
	public static AnalysisEngineActionStore getInstance(){
		return INSTANCE;
	}
	
	/**
	 * Add an entry into the store, with the UUID of the annotator
	 * as the key, and the action as the value
	 */
	public void add(String uuid, AnalysisEngineAction action){
		store.put(uuid, action);
	}
	
	/**
	 * Remove an entry from the store, with the given UUID
	 */
	public AnalysisEngineAction remove(String uuid){
		return store.remove(uuid);
	}
	
	/**
	 * Get the action from the store, with the give UUID.
	 * If the store doesn't contain the key, then a new
	 * empty AnalysisEngineAction is returned.
	 */
	public AnalysisEngineAction get(String uuid){
		if(store.containsKey(uuid))
			return store.get(uuid);
		
		return new AnalysisEngineAction(Collections.emptySet(), Collections.emptySet());	
	}
}