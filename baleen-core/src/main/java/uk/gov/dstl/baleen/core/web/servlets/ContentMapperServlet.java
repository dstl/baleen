//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.net.MediaType;

import uk.gov.dstl.baleen.core.utils.BaleenDefaults;
import uk.gov.dstl.baleen.core.web.security.WebPermission;

/**
 * List all content mappers (inheriting from ContentMapper) on the class path
 */
public class ContentMapperServlet extends AbstractComponentApiServlet {
	private static final long serialVersionUID = 1L;
	private static final String ROLES = "contentmanipulators";
	
	public static final String CONTENT_MAPPER_CLASS = "uk.gov.dstl.baleen.contentmappers.helpers.ContentMapper";

	/**
	 * Constructor
	 */
	public ContentMapperServlet(){
		super(CONTENT_MAPPER_CLASS,
				BaleenDefaults.DEFAULT_CONTENT_MAPPER_PACKAGE,
				Collections.emptyList(),
				Arrays.asList(".*\\.internals", ".*\\.helpers", "uk.gov.dstl.baleen.uima(\\..*)?"),
				ContentMapperServlet.class);
	}
	
	@Override
	public WebPermission[] getPermissions() {
		return new WebPermission[] { new WebPermission("Access Content Mappers", ROLES) };
	}
	
	/* 
	 * No requesting of parameters, so always respond with list of available Content Manipulators
	 */
	@Override
	protected void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (getComponents().isPresent()) {
			respond(resp, MediaType.create("text", "x-yaml"), getComponents().get());
		}else{
			respondWithError(resp, 503, "Unable to load content mapper class");
		}
	}
}
