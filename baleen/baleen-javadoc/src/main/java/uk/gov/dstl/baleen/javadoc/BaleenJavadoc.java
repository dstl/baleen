//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.javadoc;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * Provide a class level summary of Baleen configuration parameters and external resources
 * 
 * 
 */
public class BaleenJavadoc extends AbstractBaleenTaglet {
	public static final String NAME = "baleen.javadoc";
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public boolean inType() {
		return true;
	}

	@Override
	public String toString(Tag[] tags) {
		if(tags.length == 0)
			return null;
		
		ClassDoc classDoc = (ClassDoc) tags[0].holder();
		
		return processExternalResources(classDoc) + processConfigurationParameters(classDoc);
	}

	protected static String processExternalResources(ClassDoc classDoc){
		String cpText = "<dt><b>Configuration Parameters:</b></dt><dd><table><tr style=\"text-align: left\"><th>Parameter</th><th>Description</th><th>Default Value(s)</th></tr>";
		Map<String, String> rows = new TreeMap<>();

		for(FieldDoc field : getFields(classDoc)){
			Entry<String, String> entry = createParameterRow(field);
			if(entry != null){
				rows.put(entry.getKey(), entry.getValue());
			}
		}
		
		if(rows.isEmpty()){
			cpText = "";
		}else{
			for(String s : rows.values()){
				cpText += s;
			}
		}
		
		cpText += "</table></dd>";
		return cpText;
	}
	
	protected static String processConfigurationParameters(ClassDoc classDoc){
		String erText = "<dt><b>External Resources:</b></dt><dd><ul>";
		
		List<String> resources = new ArrayList<>();
		for(FieldDoc field : getFields(classDoc)){
			resources.addAll(createResourceItem(field));
		}
		
		if(resources.isEmpty()){
			return null;
		}else{
			for(String s : resources){
				erText += wrapWithTag("li", s, null);
			}
		}
		
		erText += "</ul></dd>";
		return erText;
	}
	
	private static Entry<String, String> createParameterRow(FieldDoc field){
		Tag[] tags = field.tags("@"+ConfigurationParameters.NAME);
		if(tags.length == 0){
			return null;
		}
		
		String name = wrapWithTag("td", field.constantValue(), "padding-right: 20px");
		String desc = wrapWithTag("td", field.commentText(), "padding-right: 20px");
		
		String defaultValues = "";
		for(Tag tag : tags){
			defaultValues += tag.text() + "<br />";
		}
		String values = wrapWithTag("td", defaultValues, null);
		
		String row = wrapWithTag("tr", name + desc + values, null);

		return new AbstractMap.SimpleEntry<String, String>(field.constantValue().toString(), row);
	}
	
	private static List<String> createResourceItem(FieldDoc field){
		Tag[] tags = field.tags("@"+ExternalResources.NAME);
		if(tags.length == 0){
			return Collections.emptyList();
		}
		
		List<String> ret = new ArrayList<>();
		
		String pkg = field.containingPackage().name();
		int levels = pkg.length() - pkg.replaceAll("\\.", "").length() + 1;
		
		String linkLevels = "";
		for(int i = 0; i < levels; i++){
			linkLevels += "../";
		}
		
		for(Tag tag : tags){
			ret.add("<a href=\""+linkLevels+tag.text().replaceAll("\\.", "/")+".html\">" + tag.text() + "</a> (key = " + field.constantValue() + ")");
		}
		
		return ret;
	}
	
	protected static String wrapWithTag(String tag, Object content, String style){
		return "<" + tag + (style != null ? " style=\"" + style + "\"" : "") +">"
				+ content +
				"</" + tag + ">";
	}
	
	/**
	 * Register the Taglet
	 */
	public static void register(Map<String, Taglet> tagletMap){
		if(tagletMap.containsKey(NAME)){
			tagletMap.remove(NAME);
		}
		
		tagletMap.put(NAME, new BaleenJavadoc());
	}

}
