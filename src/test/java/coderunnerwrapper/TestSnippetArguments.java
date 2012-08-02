package com.cloudmine.coderunnerwrapper;

import com.cloudmine.api.CMObject;
import com.cloudmine.api.SimpleCMObject;
import com.cloudmine.api.rest.response.CMObjectResponse;
import com.cloudmine.coderunner.SnippetArguments;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * <br>
 * Copyright CloudMine LLC. All rights reserved<br>
 * See LICENSE file included with SDK for details.
 */
public class TestSnippetArguments {

    @Test
    public void testGetObjects() {
        SnippetArguments arguments = createSnippetArguments();
        Map<String, CMObject> dataMap = arguments.getSuccessDataObjects();

        CMObject object = dataMap.get("objectId45435");
        assertNotNull(object);
    }

    private SnippetArguments createSnippetArguments() {
        String data = "{\"success\":{\"objectId45435\":{\"topLevelKey\":\"stringValue\",\"intKey\":42,\"nested\":{\"key\":\"value\",\"deeper\":{\"nested\":true},\"boolean\":true},\"boolean\":true}},\"errors\":{}}";
        String params = null;
        Map<String, String> argumentMap = new HashMap<String, String>();
        argumentMap.put("data", data);
        argumentMap.put("params", params);

        return new SnippetArguments(null, argumentMap);
    }

    @Test
    public void testGetResponse() {
        SnippetArguments arguments = createSnippetArguments();
        CMObjectResponse response = arguments.getAsResponse(CMObjectResponse.class);
        assertNotNull(response);
        SimpleCMObject object = (SimpleCMObject)response.getCMObject("objectId45435");
        assertEquals("stringValue", object.getString("topLevelKey"));
    }
}
