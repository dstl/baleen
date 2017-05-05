//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.core.web.servlets;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.uima.jcas.tcas.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.dstl.baleen.core.utils.ReflectionUtils;
import uk.gov.dstl.baleen.core.web.security.WebPermission;

/**
 * Return a list of all the UIMA types currently available on the class path
 */
public class TypesServlet extends AbstractApiServlet{
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(TypesServlet.class);
	
	/**
	 * Constructor
	 */
	public TypesServlet(){
		super(LOGGER, TypesServlet.class);
	}
	
	@Override
	protected void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo();
		if(path == null){
			path = "";
		}else if(path.startsWith("/")){
			path = path.substring(1);
		}
		
		switch(path.toLowerCase()){
		case "entities":
			respondWithJson(resp, getTypes("uk.gov.dstl.baleen.types.semantic.Entity"));
			break;
		case "events": 
			respondWithJson(resp, getTypes("uk.gov.dstl.baleen.types.semantic.Event"));
			break;
		case "relations": 
			respondWithJson(resp, getTypes("uk.gov.dstl.baleen.types.semantic.Relation"));
			break;
		case "":
			respondWithJson(resp, getTypes("uk.gov.dstl.baleen.types.BaleenAnnotation"));
			break;
		default:
			respondWithNotFound(resp);
		}
	}
	
	/**
	 * Return a list of types that are sub-types of baseClass.
	 * The baseClass will not be included in the list.
	 */
	@SuppressWarnings("unchecked")
	public List<String> getTypes(String baseClass){
		Class<?> clazz = null;
		try {
			clazz = Class.forName(baseClass);
		} catch (ClassNotFoundException e) {
			LOGGER.error("Unable to find base class - type listing will not be available", e);
			return Collections.emptyList();
		}
		
		Class<? extends Annotation> annotClazz = null;
		if(Annotation.class.isAssignableFrom(clazz)){
			annotClazz = (Class<Annotation>) clazz;
		}else{
			LOGGER.error("Base class is not a subclass of Annotation - type listing will not be available");
			return Collections.emptyList();
		}
		
		return getTypes(annotClazz);
	}
	
	/**
	 * Return a list of types that are sub-types of baseClass.
	 * The baseClass will not be included in the list.
	 */
	public List<String> getTypes(Class<? extends Annotation> baseClass){
		return ReflectionUtils.getInstance().getSubTypesOf(baseClass).stream().map(Class::getName).collect(Collectors.toList());
	}
	
	@Override
	public WebPermission[] getPermissions() {
		return new WebPermission[] { new WebPermission("Access Types", "types") };
	}
}