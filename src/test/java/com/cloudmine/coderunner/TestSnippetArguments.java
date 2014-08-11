package com.cloudmine.coderunner;

import com.cloudmine.api.CMObject;
import com.cloudmine.api.SimpleCMObject;
import com.cloudmine.api.rest.response.CMObjectResponse;
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
        argumentMap.put(SnippetArguments.DATA_KEY, data);
        argumentMap.put(SnippetArguments.PARAMS_KEY, params);

        return new SnippetArguments(null, argumentMap);
    }

    @Test
    public void testGetResponse() {
        SnippetArguments arguments = createSnippetArguments();
        CMObjectResponse response = arguments.getAsResponse(CMObjectResponse.class);
        assertNotNull(response);
        SimpleCMObject object = (SimpleCMObject) response.getCMObject("objectId45435");
        assertEquals("stringValue", object.getString("topLevelKey"));
    }

    @Test
    public void testGetParams() {
        Map<String, String> argumentMap = new HashMap<String, String>();
        argumentMap.put(SnippetArguments.PARAMS_KEY + "[obj]", "{\"param1\":\"aString\", \"subObject\":{ \"number\":42, \"boolean\":true }, \"anotherString\": \"strange\" }");
        SnippetArguments args = new SnippetArguments(new SnippetResponseConfiguration(), argumentMap);
        SimpleCMObject params = args.getParamsAsSimpleCMObject("obj");
        assertEquals("aString", params.getString("param1"));
        assertEquals("strange", params.getString("anotherString"));
        SimpleCMObject subObject = params.getSimpleCMObject("subObject");
        assertEquals(Integer.valueOf(42), subObject.getInteger("number"));
        assertEquals(Boolean.TRUE, subObject.getBoolean("boolean"));
    }
}
