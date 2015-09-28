//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.web.servlets;

import java.util.Arrays;
import java.util.Collections;

import uk.gov.dstl.baleen.core.web.security.WebPermission;

/**
 * List all content extractors (inheriting from BaleenContentExtractor) on the class path
 * 
 * 
 */
public class ContentExtractorsServlet extends AbstractComponentApiServlet{
	private static final long serialVersionUID = 1L;
	private static final String ROLES = "contentextractors";
	
	public static final String EXTRACTOR_CLASS = "uk.gov.dstl.baleen.uima.BaleenContentExtractor";

	/**
	 * Constructor
	 */
	public ContentExtractorsServlet(){
		super(EXTRACTOR_CLASS,
				"uk.gov.dstl.baleen.contentextractors",
				Collections.emptyList(),
				Arrays.asList(".*\\.internals", ".*\\.helpers"),
				ContentExtractorsServlet.class);
	}
	
	@Override
	public WebPermission[] getPermissions() {
		return new WebPermission[] { new WebPermission("Access Content Extractors", ROLES) };
	}
}
