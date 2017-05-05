//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import java.util.Arrays;

import uk.gov.dstl.baleen.core.utils.BaleenDefaults;
import uk.gov.dstl.baleen.core.web.security.WebPermission;

/**
 * List all annotators (inheriting from BaleenAnnotator) on the class path
 * 
 * 
 */
public class AnnotatorsServlet extends AbstractComponentApiServlet{
	private static final long serialVersionUID = 1L;
	private static final String ROLES = "annotators";
	
	public static final String ANNOTATOR_CLASS = "uk.gov.dstl.baleen.uima.BaleenAnnotator";
	public static final String CONSUMER_CLASS = "uk.gov.dstl.baleen.uima.BaleenConsumer";

	/**
	 * Constructor
	 */
	public AnnotatorsServlet(){
		super(ANNOTATOR_CLASS,
				BaleenDefaults.DEFAULT_ANNOTATOR_PACKAGE,
				Arrays.asList(CONSUMER_CLASS, "uk.gov.dstl.baleen.common.structure.TextBlocks"),
				Arrays.asList(".*\\.internals", ".*\\.helpers", "uk.gov.dstl.baleen.uima(\\..*)?"),
				AnnotatorsServlet.class);
	}
	
	@Override
	public WebPermission[] getPermissions() {
		return new WebPermission[] { new WebPermission("Access Annotators", ROLES) };
	}
}
