//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.web.servlets;

import java.util.Arrays;
import java.util.Collections;

import uk.gov.dstl.baleen.core.web.security.WebPermission;
import uk.gov.dstl.baleen.cpe.PipelineCpeBuilder;

/**
 * List all collection readers (inheriting from BaleenCollectionReader) on the class path
 * 
 * 
 */
public class CollectionReadersServlet extends AbstractComponentApiServlet{
	private static final long serialVersionUID = 1L;
	private static final String ROLES = "collectionreaders";
	
	public static final String READER_CLASS = "uk.gov.dstl.baleen.uima.BaleenCollectionReader";

	/**
	 * Constructor
	 */
	public CollectionReadersServlet(){
		super(READER_CLASS,
				PipelineCpeBuilder.READER_DEFAULT_PACKAGE,
				Collections.emptyList(),
				Arrays.asList(".*\\.internals", ".*\\.helpers", "uk.gov.dstl.baleen.uima(\\..*)?"),
				CollectionReadersServlet.class);
	}
	
	@Override
	public WebPermission[] getPermissions() {
		return new WebPermission[] { new WebPermission("Access Collection Readers", ROLES) };
	}
}
