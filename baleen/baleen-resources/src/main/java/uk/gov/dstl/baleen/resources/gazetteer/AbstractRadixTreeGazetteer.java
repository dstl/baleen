//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.resources.gazetteer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.uima.resource.Resource;

import uk.gov.dstl.baleen.exceptions.BaleenException;

import com.google.common.collect.Iterables;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.RadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;

/**
 * An abstract class to implement a gazetteer using a RadixTree as the backend.
 * 
 * Developers should provide a reloadValues() method, and override any existing methods they wish to extend.
 * 
 * The following configuration parameters are expected/allowed:
 * <ul>
 * <li><b>caseSensitive</b> - Should we do comparisons case sensitively; defaults to false</li>
 * </ul>
 * 
 * 
 * @param <T> The type used for the ID of terms
 */
public abstract class AbstractRadixTreeGazetteer<T> implements IGazetteer {
	public static final String CONFIG_CASE_SENSITIVE = "caseSensitive";
	public static final Boolean DEFAULT_CASE_SENSITIVE = false;

	protected RadixTree<T> terms = new ConcurrentRadixTree<>(new DefaultCharArrayNodeFactory());
	protected boolean caseSensitive = DEFAULT_CASE_SENSITIVE;
	
	@Override
	public void init(Resource connection, Map<String, Object> config) throws BaleenException {
		caseSensitive = false;
		if (config.containsKey(CONFIG_CASE_SENSITIVE) && "true".equalsIgnoreCase(config.get(CONFIG_CASE_SENSITIVE).toString())) {
			caseSensitive = true;
		}

		reloadValues();
	}
	
	@Override
	public String[] getValues() {
		CharSequence[] keys = Iterables.toArray(terms.getKeysStartingWith(""), CharSequence.class);
		String[] ret = new String[keys.length];

		int i = 0;
		for (CharSequence cs : keys) {
			ret[i] = cs.toString();
			i++;
		}

		return ret;
	}

	@Override
	public boolean hasValue(String key) {
		if (terms == null) {
			return false;
		}

		return terms.getValueForExactKey(caseSensitive ? key : key.toLowerCase()) != null;
	}
	
	@Override
	public String[] getAliases(String key) {
		T id = terms.getValueForExactKey(key);
		if(id == null){
			return new String[0];
		}else{
			List<String> ret = new ArrayList<>();
			
			Iterable<CharSequence> vals = terms.getKeysStartingWith("");
			for(CharSequence val : vals){
				if(val.equals(key)){
					continue;
				}
				
				T aliasId = terms.getValueForExactKey(val);
				if(id.equals(aliasId)){
					ret.add(val.toString());
				}
			}
			
			return ret.toArray(new String[0]);
		}
	}
	
	@Override
	public Map<String, Object> getAdditionalData(String key) {
		return Collections.emptyMap();
	}

	@Override
	public void destroy() {
		terms = null;
	}

}
