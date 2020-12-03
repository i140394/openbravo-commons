package org.openbravo.commons;


import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.openbravo.service.json.JsonConstants;

/**
 * Helper class to simplify the construction of JSON REST service response objects.
 * It follows the SmartClient JSON REST response structure.
 * 
 * 
 * <p>Sample Success Response: </p>
 * <pre>
 * 	{
 * 		"status": 0,
 * 		"data": [
 * 			{
 * 				// JSONObject representation of Actual Business Object will be here
 * 			}
 * 		],
 * 		"totalRows": 1
 * 	}
 * </pre>
 * 
 * <p> Sample Error Response: </p>
 * <pre>
 * 	{
 * 		"status": -1,
 * 		"error": {
 * 			// String representation of Actual Error Object will be here
 * 		}
 * 	}
 * </pre>
 * 
 * @author Nobody
 *
 */
public class ResponseBuilder {
	private final Logger log = Logger.getLogger(ResponseBuilder.class);

	// Root JsonObject which holds whole response
	private JSONObject root;
	
	// Keys used in response
	private static final String KEY_DATA = JsonConstants.RESPONSE_DATA;
	private static final String KEY_STATUS = JsonConstants.RESPONSE_STATUS;
	private static final String KEY_TOTAL_ROWS = "totalRows";
	private static final String KEY_ERROR = JsonConstants.RESPONSE_ERROR;
	private static final String KEY_MESSAGE = "message";

	public ResponseBuilder() {
		root = new JSONObject();
	}
	
	/**
	 * @param status represents status field in Openbravo JSON REST response
	 * @return {@link this} Object
	 */
	public ResponseBuilder withStatus(final int status) {
		putInt(KEY_STATUS, status);
		return this;
	}
	
	/**
	 * @param dataObjects represents data part (or list) in Openbravo JSON REST response
	 * @return {@link this} Object
	 * @throws NullPointerException if dataObjects is null
	 */
	public ResponseBuilder withData(final List<? extends Object> dataObjects) {
		Objects.requireNonNull(dataObjects);
		final JSONArray jsonArr = new JSONArray(dataObjects);
		try {
			root.put(KEY_DATA, jsonArr);
		} catch (JSONException e) {
			log.error(e.getMessage());
		}
		return this;
	}
	
	/**
	 * Convenience method for constructing data array from single object.
	 * 
	 * @param object 
	 * 			the object which gets inserted into the single element list
	 * 			and that list will represent data part (i.e list) in Openbravo
	 * 			JSON REST response
	 * @return {@link this} Object
	 */
	public ResponseBuilder withData(final Object object) {
		Objects.requireNonNull(object);
		withData(Arrays.asList(object));
		return this;
	}
	
	/**
	 * @param errorObj Represents the error part in Openbravo JSON REST response
	 * @return {@link this} Object
	 */
	public ResponseBuilder withError(final Object errorObj) {
		Objects.requireNonNull(errorObj);
		try {
			root.put(KEY_ERROR, errorObj);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return this;
	}
	
	/**
	 * Convenience method for constructing {@link JSONObject} from error message
	 * which will be used as error object.
	 * 
	 * @param message
	 * 			the String message which gets wrapped inside a {@link JSONObject}
	 * 			and that object will represent error part in Openbravo
	 * 			JSON REST response
	 * @return
	 */
	public ResponseBuilder withError(final String message) {
		Objects.requireNonNull(message);
		final JSONObject errorObj = new JSONObject();
		try {
			errorObj.put(KEY_MESSAGE, message);
			withError(errorObj);
		} catch (JSONException e) {
			log.error(e.getMessage());
		}
		return this;
	}

	/**
	 * This response field should be omitted when constructing the error response object. 
	 * 
	 * @param noOfRecords Represents totalRows part in Openbravo JSON REST response
	 * @return {@link this} Object
	 */
	public ResponseBuilder withNoRecords(final int noOfRecords) {
		putInt(KEY_TOTAL_ROWS, noOfRecords);
		return this;
	}
	
	/**
	 * Helper method to put {@code int} values into the response object.
	 * Such as status, totalRows etc...
	 * 
	 * It handles its exceptions.
	 * 
	 * @param key represents JSON Key
	 * @param value represents the value of the corresponding JSON Key
	 */
	private void putInt(final String key, final int value) {
		Objects.requireNonNull(key);
		try {
			root.put(key, value);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	/**
	 * This is the method that should be called to get the constructed response object.
	 * 
	 * @return String {@link String} representation of the whole JSON REST response object
	 */
	public String buildAsString() {
		return build().toString();
	}
	
	/**
	 * Wraps the constructed response object with 'response' key.
	 * The wrapper object will be the actual response object that gets sent to the
	 * consumer of the API provided that they are using this class to construct the 
	 * response object.
	 * 
	 * @return response {@link JSONObject} object
	 */
	private JSONObject build() {
		final JSONObject root0 = root;
		final JSONObject response = new JSONObject();
		try {
			response.put(JsonConstants.RESPONSE_RESPONSE, root0);
		} catch (JSONException e) {
			log.error(e.getMessage(), e);
		}
		return response;
	}
}