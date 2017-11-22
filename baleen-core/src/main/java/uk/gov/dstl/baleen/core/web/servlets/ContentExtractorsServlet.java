//Dstl (c) Crown Copyright 2017
//Modified by NCA (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import uk.gov.dstl.baleen.core.utils.BaleenDefaults;
import uk.gov.dstl.baleen.core.web.security.WebPermission;

import java.util.Arrays;
import java.util.Collections;

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
				BaleenDefaults.DEFAULT_CONTENT_EXTRACTOR_PACKAGE,
				Collections.emptyList(),
				Arrays.asList(".*\\.internals", ".*\\.helpers", "uk.gov.dstl.baleen.uima(\\..*)?"),
				ContentExtractorsServlet.class);
	}
	
	@Override
	public WebPermission[] getPermissions() {
		return new WebPermission[] { new WebPermission("Access Content Extractors", ROLES) };
	}
}
