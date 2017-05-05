//Dstl (c) Crown Copyright 2017
package uk.gov.dstl.baleen.testing.servlets;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A helper class to allow simpler testing of servlets.
 *
 * Users of this class should:
 *
 * 1. Determine the size of the response (and then use this value in the
 * responseBufferSize constructor).
 *
 * 2. Add parameters
 *
 * 3. Call the servlet with the doGet method. This will be routined to the
 * corresponding do method on the servlet.
 *
 * 4. Use the getResponse* methods to examine the output.
 *
 * Note that since this does not run in an HTTP server the getResponse* values
 * will be whatever was set in the servlet. If the servlet does not set a status
 * code then no status code will be available here.
 *
 * This class has been implemented to the fidelity required for the current
 * tests, and will need to be enhanced over time.
 *
 * 
 *
 */
public class ServletCaller {

	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final Map<String, String[]> paramMap = new HashMap<>();
	private final Set<String> paramNames = new HashSet<>();
	private final BufferServletOutputStream outputStream;
	private final ObjectMapper mapper;
	private PrintWriter writer;

	/**
	 * New isntance with default response size.
	 *
	 */
	public ServletCaller() {
		this(BufferServletOutputStream.BUFFER_SIZE);
	}

	/**
	 * New instance with a specific buffer size.
	 *
	 * @param responseBufferSize
	 *            the maximum size of response to hold (in bytes).
	 */
	public ServletCaller(int responseBufferSize) {
		mapper = new ObjectMapper();

		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);

		when(request.getParameterMap()).thenReturn(paramMap);
		when(request.getParameterNames()).thenReturn(Collections.enumeration(paramNames));

		outputStream = new BufferServletOutputStream(responseBufferSize);
		writer = new PrintWriter(outputStream);
		try {
			doReturn(writer).when(response).getWriter();
			doReturn(outputStream).when(response).getOutputStream();
		} catch (IOException e) {
			// Never happens
		}

	}

	/**
	 * Set the request uri.
	 *
	 * @param requestUri
	 *            (as to be returend from request.getRequestURI())
	 */
	public void setRequestUri(String requestUri) {
		doReturn(requestUri).when(request).getRequestURI();
	}

	/**
	 * Set the servlet path.
	 *
	 * @param servletPath
	 *            (as to be returend from request.getServletPath())
	 */
	public void setServletPath(String servletPath) {
		doReturn(servletPath).when(request).getServletPath();
	}

	/**
	 * Set the context path.
	 *
	 * @param contextPath
	 *            (as to be returend from request.getContextPath())
	 */
	public void setContextPath(String contextPath) {
		doReturn(contextPath).when(request).getContextPath();
	}

	/**
	 * Add a new parameter to the request.
	 *
	 * @param key
	 *            the parameter name
	 * @param values
	 *            the values of the parameter
	 */
	public void addParameter(String key, String... values) {
		if (values != null && values.length > 0) {
			when(request.getParameter(key)).thenReturn(values[0]);
		}
		when(request.getParameterValues(key)).thenReturn(values);
		paramMap.put(key, values);
		paramNames.add(key);
	}

	/**
	 * Get the representation of the request object.
	 *
	 * This is not a complete implementation.
	 *
	 * @return
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * Get the representation of the response.
	 *
	 * This is not a complete implementation.
	 *
	 *
	 * @return
	 */
	public HttpServletResponse getResponse() {
		return response;
	}

	/**
	 * Simulate a GET request on this servlet.
	 *
	 * @param servlet
	 *            the servlet instance to call
	 * @return the response representation (will be non-null)
	 * @throws ServletException
	 *             as per HttpServlet.service
	 * @throws IOException
	 *             as per HttpServlet.service
	 */
	public HttpServletResponse doGet(HttpServlet servlet) throws Exception {
		return doMethod("GET", servlet);
	}

	/**
	 * Simulate a DELETE request on this servlet.
	 *
	 * @param servlet
	 *            the servlet instance to call
	 * @return the response representation (will be non-null)
	 * @throws ServletException
	 *             as per HttpServlet.service
	 * @throws IOException
	 *             as per HttpServlet.service
	 */
	public HttpServletResponse doDelete(HttpServlet servlet) throws Exception {
		return doMethod("DELETE", servlet);
	}

	/**
	 * Simulate a POST request on this servlet.
	 *
	 * @param servlet
	 *            the servlet instance to call
	 * @return the response representation (will be non-null)
	 * @throws ServletException
	 *             as per HttpServlet.service
	 * @throws IOException
	 *             as per HttpServlet.service
	 */
	public HttpServletResponse doPost(HttpServlet servlet) throws Exception {
		return doMethod("POST", servlet);
	}

	/**
	 * Simulate a PUT request on this servlet.
	 *
	 * @param servlet
	 *            the servlet instance to call
	 * @return the response representation (will be non-null)
	 * @throws ServletException
	 *             as per HttpServlet.service
	 * @throws IOException
	 *             as per HttpServlet.service
	 */
	public HttpServletResponse doPut(HttpServlet servlet) throws Exception {
		return doMethod("PUT", servlet);
	}

	private HttpServletResponse doMethod(String method, HttpServlet servlet) throws Exception {
		when(request.getMethod()).thenReturn(method);
		servlet.service(request, response);
		writer.flush();
		return response;
	}

	/**
	 * The written body of the response.
	 *
	 * Should only be called after calling doGet (or similar).
	 *
	 * @return the body as a UTF-8 string
	 */
	public String getResponseBody() {
		return outputStream.getAsString();
	}

	/**
	 * Get the response as a object by parsing JSON.
	 *
	 * @param clazz
	 * @return object an object instance or null
	 */
	public <T> T getJSONResponse(Class<T> clazz) {
		try {
			return mapper.readValue(getResponseBody(), clazz);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * The content type of the response.
	 *
	 * @return the content type (if set by the servlet call, or null if not)
	 */
	public String getResponseType() {
		ArgumentCaptor<String> t = ArgumentCaptor.forClass(String.class);
		verify(response).setContentType(t.capture());
		return t.getValue();
	}

	/**
	 * The status code of the response
	 *
	 * @return the status code (if set by the servlet call, or null if not)
	 */
	public Integer getResponseStatus() {

		ArgumentCaptor<Integer> t = ArgumentCaptor.forClass(Integer.class);
		verify(response).setStatus(t.capture());
		return t.getValue();
	}

	/**
	 * Gets the status code as set by sendError
	 *
	 * @return the status code.
	 */
	public Integer getSentError() {
		try {
			ArgumentCaptor<Integer> c = ArgumentCaptor.forClass(Integer.class);
			ArgumentCaptor<String> m = ArgumentCaptor.forClass(String.class);
			verify(response).sendError(c.capture(), m.capture());
			return c.getValue();
		} catch (IOException e) {
			return null;
		}

	}
}
