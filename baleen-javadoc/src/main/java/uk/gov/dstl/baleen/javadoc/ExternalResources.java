//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.javadoc;

import java.util.Map;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * Adds information about external resources to the Javadoc.
 * 
 * 
 */
public class ExternalResources extends AbstractBaleenTaglet {
	public static final String NAME = "baleen.resource";
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean inField() {
		return true;
	}
	
	@Override
	public String toString(Tag[] tags) {
		if(tags.length == 0)
			return null;
		
		return "<div class=\"block\">This constant holds an external resource key.</div>";
	}
	
	/**
	 * Register the Taglet
	 */
	public static void register(Map<String, Taglet> tagletMap){
		if(tagletMap.containsKey(NAME)){
			tagletMap.remove(NAME);
		}
		
		tagletMap.put(NAME, new ExternalResources());
	}
}
