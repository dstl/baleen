//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.web.servlets;

import java.util.Arrays;
import java.util.Collections;

import uk.gov.dstl.baleen.core.web.security.WebPermission;
import uk.gov.dstl.baleen.cpe.CpeBuilder;

/**
 * List all consumers (inheriting from BaleenConsumer) on the class path
 * 
 * 
 */
public class ConsumersServlet extends AbstractComponentApiServlet{
	private static final long serialVersionUID = 1L;
	private static final String ROLES = "consumers";
	
	public static final String CONSUMER_CLASS = "uk.gov.dstl.baleen.uima.BaleenConsumer";

	/**
	 * Constructor
	 */
	public ConsumersServlet(){
		super(CONSUMER_CLASS,
				CpeBuilder.CONSUMER_DEFAULT_PACKAGE,
				Collections.emptyList(),
				Arrays.asList(".*\\.internals", ".*\\.helpers"),
				ConsumersServlet.class);
	}
	
	@Override
	public WebPermission[] getPermissions() {
		return new WebPermission[] { new WebPermission("Access Consumers", ROLES) };
	}
}
