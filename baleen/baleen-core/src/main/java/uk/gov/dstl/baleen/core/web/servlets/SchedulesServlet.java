//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.web.servlets;

import java.util.Arrays;
import java.util.Collections;

import uk.gov.dstl.baleen.core.web.security.WebPermission;
import uk.gov.dstl.baleen.cpe.JobCpeBuilder;

/**
 * List all Schedules (inheriting from BaleenScheduler) on the class path
 * 
 * 
 */
public class SchedulesServlet extends AbstractComponentApiServlet{
	private static final long serialVersionUID = 1L;
	private static final String ROLES = "schedules";
	
	public static final String SCHEDULE_CLASS = "uk.gov.dstl.baleen.uima.BaleenScheduler";

	/**
	 * Constructor
	 */
	public SchedulesServlet(){
		super(SCHEDULE_CLASS,
				JobCpeBuilder.SCHEDULE_DEFAULT_PACKAGE,
				Collections.emptyList(),
				Arrays.asList(".*\\.internals", ".*\\.helpers", "uk.gov.dstl.baleen.uima(\\..*)?"),
				SchedulesServlet.class);
	}
	
	@Override
	public WebPermission[] getPermissions() {
		return new WebPermission[] { new WebPermission("Access Schedules", ROLES) };
	}
}
