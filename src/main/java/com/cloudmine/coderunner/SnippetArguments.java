package com.cloudmine.coderunner;

import com.cloudmine.api.CMObject;
import com.cloudmine.api.CMSessionToken;
import com.cloudmine.api.SimpleCMObject;
import com.cloudmine.api.exceptions.CloudMineException;
import com.cloudmine.api.exceptions.ConversionException;
import com.cloudmine.api.rest.JsonUtilities;
import com.cloudmine.api.rest.TransportableString;
import com.cloudmine.api.rest.response.ResponseBase;
import com.cloudmine.api.rest.response.SuccessErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Map;

/**
 * <br>
 * Copyright CloudMine LLC. All rights reserved<br>
 * See LICENSE file included with SDK for details.
 */
public class SnippetArguments {
    private static final Logger LOG = LoggerFactory.getLogger(SnippetArguments.class);
    public static final String DATA_KEY = "data";
    public static final String PARAMS_KEY = "params[params]";
    public static final String SESSION_TOKEN_KEY = "session_token";
    public static final String REQUEST_KEY = "request";
    public static final int SESSION_TOKEN_VALID_TIME = 1209600000;
    private SnippetResponseConfiguration responseConfiguration;
    private Map<String, String> arguments;

    private SimpleCMObject inputJson;

    public SnippetArguments(SnippetResponseConfiguration responseConfiguration, Map<String, String> arguments) {
        this.responseConfiguration = responseConfiguration;
        this.arguments = arguments;
    }

    public SnippetArguments(SnippetResponseConfiguration responseConfiguration, String inputJson) {
        this.inputJson = new SimpleCMObject(new TransportableString(inputJson));
        this.responseConfiguration = responseConfiguration;
        arguments = null;
    }

    /**
     * Get the configuration object for this snippet call
     *
     * @return
     */
    public SnippetResponseConfiguration getResponseConfiguration() {
        return responseConfiguration;
    }

    /**
     * Provides direct access to the arguments passed in. Use {@link #DATA_KEY} and {@link #PARAMS_KEY} to access the contents
     *
     * @return a Map containing the arguments that were passed in
     */
    public Map<String, String> getArguments() {
        return arguments;
    }

    /**
     * Get the data that was returned by the original call. If there was none, an empty string is returned
     *
     * @return
     */
    public String getDataTransportableRepresentation() {
        if(isVersionTwo()) {
            try {
            SimpleCMObject response = inputJson.getSimpleCMObject("response");
            return response.asUnkeyedObject();
            }catch(Exception e) {
                return "";
            }
        }
        String data = arguments.get(DATA_KEY);
        return data == null ?
                "" :
                data;
    }

    private boolean isVersionTwo() {
        return arguments == null;
    }

    /**
     * Get the parameters that were passed into this snippet call. If there were none, an empty string is returned
     *
     * @return
     */
    public String getParamsTransportableRepresentation() {
        if(isVersionTwo()) {
            try {
                return inputJson.getSimpleCMObject("params").asUnkeyedObject();
            }catch(Exception e){
                return "";
            }
        }
        String params = arguments.get(PARAMS_KEY);
        return params == null ?
                "" :
                params;
    }


    public SimpleCMObject getParamsAsSimpleCMObject() {
        try {
            return new SimpleCMObject(new TransportableString(getParamsTransportableRepresentation()));
        }catch (ConversionException ce) {
            SimpleCMObject cmObject = new SimpleCMObject();
            cmObject.add("errors", "Conversion exception");
            return cmObject;
        }
    }

    /**
     * Attempts to return the session token. If there isn't one, {@link CMSessionToken.FAILED} is returned
     *
     * @return
     */
    public CMSessionToken getSessionToken() {
        if(isVersionTwo()) {
            try {
                return new CMSessionToken(inputJson.getSimpleCMObject("session").getString("session_token", CMSessionToken.INVALID_TOKEN), new Date(System.currentTimeMillis() + SESSION_TOKEN_VALID_TIME));
            }catch (Exception e) {
                return CMSessionToken.FAILED;
            }
        }
        String sessionTokenJson = arguments.get(SESSION_TOKEN_KEY);

        try {
            return new CMSessionToken(sessionTokenJson);
        } catch (ConversionException ce) {
            return CMSessionToken.FAILED;
        }
    }

    /**
     * Get the success part of the data
     *
     * @return
     */
    public String getSuccessTransportableRepresentation() {
        Map<String, String> dataMap = JsonUtilities.jsonMapToKeyMap(getDataTransportableRepresentation());
        String success = dataMap.get(SuccessErrorResponse.SUCCESS);
        return success == null ?
                "" :
                success;

    }

    public String getRequestDataTransportableRepresentation() {
        if(isVersionTwo()) {
            try {
                SimpleCMObject request = inputJson.getSimpleCMObject(REQUEST_KEY);
                Object requestData = request.getSimpleCMObject("body");
                return requestData.toString();
            }catch(Exception e) {
                return "";
            }
        }
        String input = arguments.get(REQUEST_KEY);
        return input == null ?
                "" :
                input;
    }

    /**
     * Get the errors
     *
     * @return
     */
    public String getErrorsTransportableRepresentation() {
        Map<String, String> dataMap = JsonUtilities.jsonMapToKeyMap(getDataTransportableRepresentation());
        String errors = dataMap.get(SuccessErrorResponse.ERRORS);
        return errors == null ?
                "" :
                errors;
    }

    public Map<String, CMObject> getSuccessDataObjects() throws ConversionException {
        Map<String, String> jsonMap = JsonUtilities.jsonMapToKeyMap(getDataTransportableRepresentation());
        String successResponse = jsonMap.get(SuccessErrorResponse.SUCCESS);
        if (successResponse == null)
            throw new ConversionException("No success response returned");
        return CMObject.convertTransportableCollectionToObjectMap(successResponse);
    }

    /**
     * Version 2 API only
     * @return
     */
    public SimpleCMObject getRequest() {
        return inputJson.getSimpleCMObject("request");
    }

    public SimpleCMObject getConfig() {
        return inputJson.getSimpleCMObject("config");
    }

    public SimpleCMObject getSession() {
        return inputJson.getSimpleCMObject("session");
    }

    public String getRequestMethod() {
        return getRequest().getString("method");
    }

    public String getRequestContentType() {
        return getRequest().getString("content-type");
    }

    public String getSessionTokenString() {
        return getSessionToken().getSessionToken();
    }

    public String getUserId() {
        return getSession().getString("user_id");
    }

    public String getApiKey() {
        return getSession().getString("api_key");

    }

    public String getAppId() {
        return getSession().getString("app_id");
    }

    /**
     * Get the snippet arguments as a response. <br>
     * This is useful if you always know that this snippet is going to be called during a specific type of request;
     * for example, if you know it will always be called during an object fetch request, you can pass in {@link com.cloudmine.api.rest.response.CMObjectResponse}
     * If you request a different Response then would normally be used
     * for this data, this call may fail. It will also fail on File related response classes. If the call fails,
     * null will be returned. The response code will always be 200, no matter the actual server response
     *
     * @param responseType The class of the response to construct
     * @param <T>
     * @return
     */
    public <T extends ResponseBase> T getAsResponse(Class<T> responseType) {
        try {
            return responseType.getConstructor(String.class, int.class).newInstance(getDataTransportableRepresentation(), 200);
        } catch (NoSuchMethodException e) {
            LOG.error("Exception thrown", e);
        } catch (InvocationTargetException e) {
            LOG.error("Exception thrown", e);
        } catch (InstantiationException e) {
            LOG.error("Exception thrown", e);
        } catch (IllegalAccessException e) {
            LOG.error("Exception thrown", e);
        } catch (CloudMineException e) {
            LOG.error("Construction failed", e);
        }
        return null;
    }

}
