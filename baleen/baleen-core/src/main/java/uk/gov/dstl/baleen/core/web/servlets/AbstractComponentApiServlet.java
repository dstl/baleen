//Dstl (c) Crown Copyright 2015
package uk.gov.dstl.baleen.core.web.servlets;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.reflections.Reflections;
import org.slf4j.LoggerFactory;

import com.google.common.net.MediaType;

/**
 * Abstract class for listing components based on their super type (for example,
 * BaleenAnnotator). Returns a YAML formatted list of components, which could in
 * theory be copied into a pipeline configuration. In practice though, the order
 * may not be appropriate and some components will require configuration.
 *
 * 
 */
public class AbstractComponentApiServlet extends AbstractApiServlet {
	private static final long serialVersionUID = 1L;

	private transient Optional<String> components = null;

	private final Class<?> clazz;

	private final String componentClass;

	private final String componentPackage;

	private final transient List<String> excludePackage;

	private final transient List<String> excludeClass;

	/**
	 * Constructor
	 *
	 * @param componentClass
	 *            The name of the supertype for which we are listing subclasses
	 * @param componentPackage
	 *            The default package name, which will be stripped from class
	 *            names
	 * @param excludeClass
	 *            A list of classes of which subclasses will be excluded from
	 *            the listing
	 * @param excludePackage
	 *            A list of regular expressions against which packages will be
	 *            tested and, if matched, excluded from the listing
	 * @param clazz
	 *            The subclass of AbstractComponentApiServlet, for creating a
	 *            logger
	 */
	public AbstractComponentApiServlet(String componentClass, String componentPackage, List<String> excludeClass,
			List<String> excludePackage, Class<?> clazz) {
		super(LoggerFactory.getLogger(clazz), clazz);
		this.componentClass = componentClass;
		this.componentPackage = componentPackage;
		this.excludeClass = excludeClass;
		this.excludePackage = excludePackage;

		this.clazz = clazz;
	}

	private void calculateComponents() {
		Class<?> componentClazz = null;
		try {
			componentClazz = Class.forName(componentClass);
		} catch (ClassNotFoundException e) {
			LoggerFactory.getLogger(clazz).warn(
					"Unable to find component class - annotator listing will not be available", e);
		}

		List<Class<?>> excludeClazz = new ArrayList<>();
		for (String c : excludeClass) {
			try {
				Class<?> cl = Class.forName(c);
				excludeClazz.add(cl);
			} catch (ClassNotFoundException e) {
				LoggerFactory.getLogger(clazz).warn("Unable to find component class - component will not be excluded",
						e);
			}
		}

		if (componentClazz == null) {
			components = Optional.empty();
		} else {
			Reflections reflections = new Reflections();
			List<String> componentsList = classesToFilteredList(reflections.getSubTypesOf(componentClazz),
					componentPackage, excludeClazz, excludePackage);

			StringBuilder componentBuilder = new StringBuilder();
			for (String s : componentsList) {
				componentBuilder.append("- " + s + "\n");
			}

			components = Optional.of(componentBuilder.toString());
		}
	}

	private List<String> classesToFilteredList(Set<?> components, String componentPackage, List<Class<?>> excludeClass,
			List<String> excludePackage) {
		List<String> ret = new ArrayList<>();
		for (Object o : components) {

			try {
				Class<?> c = (Class<?>) o;

				String s = c.getName();
				String p = c.getPackage().getName();

				if (excludeByPackage(p, excludePackage) || excludeByClass(c, excludeClass) || isAbstract(c)) {
					continue;
				}

				if (s.startsWith(componentPackage)) {
					s = s.substring(componentPackage.length());
					s = StringUtils.strip(s, ".");
				}
				
				ret.add(s);
			} catch (ClassCastException cce) {
				LoggerFactory.getLogger(clazz).warn("Unable to cast to class", cce);
			}
		}

		Collections.sort(ret);

		return ret;
	}

	private static boolean isAbstract(Class<?> clazz) {		
		return Modifier.isAbstract(clazz.getModifiers());
	}

	/**
	 * Compare a package with a list of packages to determine whether the
	 * package should be excluded or not.
	 *
	 * @param pkg
	 *            The package to test
	 * @param excludePkg
	 *            A list of RegEx patterns that describe the set of packages to
	 *            test against
	 * @return True if the package should be excluded, false otherwise
	 */
	public static boolean excludeByPackage(String pkg, List<String> excludePkg) {
		for (String ep : excludePkg) {
			if (pkg.matches(ep)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Compare a package with a list of classes to determine whether the class
	 * should be excluded or not. Classes in the list, or that inherit from a
	 * class in the list, will be excluded.
	 *
	 * @param clazz
	 *            The class to test
	 * @param excludeClazz
	 *            A list of classes to test against
	 * @return True if the class should be excluded, false otherwise
	 */
	public static boolean excludeByClass(Class<?> clazz, List<Class<?>> excludeClazz) {
		for (Class<?> ec : excludeClazz) {
			if (ec.isAssignableFrom(clazz)) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (!getComponents().isPresent()) {
			respondWithError(resp, 503, "Unable to load annotator class");
			return;
		}
		respond(resp, MediaType.create("text", "x-yaml"), getComponents().get());
	}

	protected Optional<String> getComponents() {
		if (components == null) {
			calculateComponents();
		}
		return components;
	}
}
