package com.cloudmine.coderunner;

import com.cloudmine.api.CMObject;
import com.cloudmine.api.exceptions.CloudMineException;
import com.cloudmine.api.exceptions.ConversionException;
import com.cloudmine.api.rest.JsonUtilities;
import com.cloudmine.api.rest.response.ResponseBase;
import com.cloudmine.api.rest.response.SuccessErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * <br>
 * Copyright CloudMine LLC. All rights reserved<br>
 * See LICENSE file included with SDK for details.
 */
public class SnippetArguments {
    private static final Logger LOG = LoggerFactory.getLogger(SnippetArguments.class);
    public static final String DATA_KEY = "data";
    public static final String PARAMS_KEY = "params";
    private SnippetResponseConfiguration responseConfiguration;
    private Map<String, String> arguments;

    public SnippetArguments(SnippetResponseConfiguration responseConfiguration, Map<String, String> arguments) {
        this.responseConfiguration = responseConfiguration;
        this.arguments = arguments;
    }

    /**
     * Get the configuration object for this snippet call
     * @return
     */
    public SnippetResponseConfiguration getResponseConfiguration() {
        return responseConfiguration;
    }

    /**
     * Provides direct access to the arguments passed in. Use {@link #DATA_KEY} and {@link #PARAMS_KEY} to access the contents
     * @return a Map containing the arguments that were passed in
     */
    public Map<String, String> getArguments() {
        return arguments;
    }

    /**
     * Get the data that was returned by the original call. If there was none, an empty string is returned
     * @return
     */
    public String getDataTransportableRepresentation() {
        String data = arguments.get(DATA_KEY);
        return data == null ?
                "" :
                data;
    }

    /**
     * Get the parameters that were passed into this snippet call. If there were none, an empty string is returned
     * @return
     */
    public String getParamsTransportableRepresentation() {
        String params = arguments.get(PARAMS_KEY);
        return params == null ?
                "" :
                params;
    }

    /**
     * Get the success part of the data
     * @return
     */
    public String getSuccessTransportableRepresentation() {
        Map<String, String> dataMap = JsonUtilities.jsonMapToKeyMap(getDataTransportableRepresentation());
        String success = dataMap.get(SuccessErrorResponse.SUCCESS);
        return success == null ?
                "" :
                success;

    }

    /**
     * Get the errors
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
        if(successResponse == null)
            throw new ConversionException("No success response returned");
        return CMObject.convertTransportableCollectionToObjectMap(successResponse);
    }

    /**
     * Get the snippet arguments as a response. <br>
     * This is useful if you always know that this snippet is going to be called during a specific type of request;
     * for example, if you know it will always be called during an object fetch request, you can pass in {@link com.cloudmine.api.rest.response.CMObjectResponse}
     * If you request a different Response then would normally be used
     * for this data, this call may fail. It will also fail on File related response classes. If the call fails,
     * null will be returned. The response code will always be 200, no matter the actual server response
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
