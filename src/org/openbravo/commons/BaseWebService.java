package org.openbravo.commons;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.openbravo.service.json.JsonConstants;
import org.openbravo.service.web.WebService;

/**
 * A class which gives default (i.e. empty) implementation for methods in
 * {@link WebService} interface. The primary intend of this class is that web
 * service authors do not have to override all four methods in
 * {@link WebService} interface other than which specific HTTP method(s) they
 * want to implement.
 * 
 * Default implementation of all methods in this class sends error response to
 * the clients. Supported HTTP methods are GET, POST, PUT and DELETE
 * 
 * @see JsonConstants
 * @see HttpServletResponse#SC_NOT_IMPLEMENTED
 * 
 * @author Nobody
 *
 */
public class BaseWebService implements WebService {
	private static Logger log = Logger.getLogger(BaseWebService.class);
	private static final String errorMessage = "HTTP Method not implemented";

	@Override
	public void doGet(String path, HttpServletRequest request, HttpServletResponse response) throws Exception {
		sendErrorResponse(response, HttpServletResponse.SC_NOT_IMPLEMENTED, -1, errorMessage);
	}

	@Override
	public void doPost(String path, HttpServletRequest request, HttpServletResponse response) throws Exception {
		sendErrorResponse(response, HttpServletResponse.SC_NOT_IMPLEMENTED, -1, errorMessage);
	}

	@Override
	public void doDelete(String path, HttpServletRequest request, HttpServletResponse response) throws Exception {
		sendErrorResponse(response, HttpServletResponse.SC_NOT_IMPLEMENTED, -1, errorMessage);
	}

	@Override
	public void doPut(String path, HttpServletRequest request, HttpServletResponse response) throws Exception {
		sendErrorResponse(response, HttpServletResponse.SC_NOT_IMPLEMENTED, -1, errorMessage);
	}

	/**
	 * A helper method to read request body as String
	 * 
	 * @param request HttpRequest
	 * @return request body as {{@link String}
	 * @throws IOException on IO Error
	 */
	protected String getRequestContent(HttpServletRequest request) throws IOException {
		final BufferedReader reader = request.getReader();
		if (reader == null) {
			return "";
		}
		String line;
		final StringBuilder sb = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			if (sb.length() > 0) {
				sb.append("\n");
			}
			sb.append(line);
		}
		return sb.toString();
	}

	/**
	 * A helper method which constructs the error JSONObject and delegates task of
	 * sending that error object to the client to
	 * {@link DefaultWebService#writeResult(HttpServletResponse, String)} method.
	 * 
	 * @param response                      {@link HttpServletResponse} object
	 * @param httpRequestResponseStatusCode HTTP Request Status code (i.e. 200, 400
	 *                                      etc..)
	 * @param validationStatusCode          Openbravo specific validation status
	 *                                      code (i.e. -1, -4 etc..)
	 * @param message                       Actual error message which gets sent to
	 *                                      the consumer of the API
	 * @see ResponseBuilder
	 */
	protected void sendErrorResponse(final HttpServletResponse response, final int httpRequestResponseStatusCode,
			final int validationStatusCode, final String message) {

		response.setStatus(httpRequestResponseStatusCode);

		final String finalResponse = new ResponseBuilder()
				.withStatus(validationStatusCode)
				.withError(message)
				.buildAsString();
		try {
			writeResult(response, finalResponse);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * Helper method which writes the {@link String} response using
	 * {@link HttpServletResponse} object and sets necessary header and other
	 * information.
	 * 
	 * @implNote This method should only be when you want send output at once.
	 * 			 Because this method closes the {@link Writer} once it has written the response. 
	 * 			 So, for custom implementation consider overriding this method in your class. 
	 * 
	 * @param response {@link HttpServletResponse} object
	 * @param result   is a {@link String} which has the whole response
	 * @throws IOException if anything goes wrong in writing the response
	 */
	protected void writeResult(HttpServletResponse response, String result) throws IOException {
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Content-Type", "application/json;charset=UTF-8");

		try (final Writer w = response.getWriter()) {
			w.write(result);
		}
	}
}
