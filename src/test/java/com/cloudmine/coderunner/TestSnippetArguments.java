package com.cloudmine.coderunner;

import com.cloudmine.api.CMObject;
import com.cloudmine.api.SimpleCMObject;
import com.cloudmine.api.rest.response.CMObjectResponse;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        argumentMap.put(SnippetArguments.PARAMS_KEY, "{\"param1\":\"aString\", \"subObject\":{ \"number\":42, \"boolean\":true }, \"anotherString\": \"strange\" }");
        SnippetArguments args = new SnippetArguments(new SnippetResponseConfiguration(), argumentMap);
        SimpleCMObject params = args.getParamsAsSimpleCMObject();
        assertEquals("aString", params.getString("param1"));
        assertEquals("strange", params.getString("anotherString"));
        SimpleCMObject subObject = params.getSimpleCMObject("subObject");
        assertEquals(Integer.valueOf(42), subObject.getInteger("number"));
        assertEquals(Boolean.TRUE, subObject.getBoolean("boolean"));
    }

    @Test
    public void testVersion2() {
        String requestBody = "{\"objectId45435\":{\"topLevelKey\":\"stringValue\",\"intKey\":42,\"nested\":{\"key\":\"value\",\"deeper\":{\"nested\":true},\"boolean\":true},\"boolean\":true,\"__id__\":\"objectId45435\",\"__access__\":[]}}";
        String successValues = "{\"2fe27a08db6e864f89eadd8907e704ac\":{\"key\":\"value\"},\"F15A2768499F4952BC735576C5F1421A\":{\"__id__\":\"F15A2768499F4952BC735576C5F1421A\",\"kitId\":\"60473\",\"qrValue\":\"0ElkSY9\"},\"CBED2A8E601D42E3A80CB830257AFB17\":{\"__id__\":\"CBED2A8E601D42E3A80CB830257AFB17\",\"kitId\":\"61838\",\"qrValue\":\"1KvfSM6\"}}";
        String responseBody = "{\"success\":" + successValues + ",\"errors\":{},\"count\":10668,\"__id__\":\"body\",\"__access__\":[]}";
        String params = "{\"aString\":\"just\",\"bool\":true,\"int\":42";
        String sessionToken = "bWFyY0BjbG91ZG1pbmUubWU6cGFzc3dvcmQK";
        String json = "{\"request\":{\"body\":" + requestBody + ",\"method\":\"POST\",\"content-type\":\"application/json\"},\"response\":{\"body\":" + responseBody + "},\"session\":{\"api_key\":\"64eda7d937be4d02b2cda117a4ad44e3\",\"app_id\":\"f5dc4d84d9c5400e9286352a6a072b5f\", \"session_token\": \"" + sessionToken + "\"},\"params\":" + params + "},\"config\":{\"async\":false,\"timeout\":30.0,\"version\":2,\"type\":\"post\"}}";
        SnippetArguments arguments = new SnippetArguments(new SnippetResponseConfiguration(), json);
        assertEquals(requestBody, arguments.getRequestDataTransportableRepresentation());

        assertEquals(responseBody, arguments.getDataTransportableRepresentation());
        assertEquals("{}",arguments.getErrorsTransportableRepresentation());
        assertEquals(successValues, arguments.getSuccessTransportableRepresentation());
        assertTrue(arguments.getParamsTransportableRepresentation().startsWith(params));
        assertEquals(sessionToken, arguments.getSessionToken().getSessionToken());
        Map<String, CMObject> successObjects = arguments.getSuccessDataObjects();
        assertEquals(3, successObjects.size());
        CMObjectResponse response = arguments.getAsResponse(CMObjectResponse.class);
        assertEquals(3, response.getObjects().size());
        for(Map.Entry<String, CMObject> successObject : successObjects.entrySet()) {
            assertEquals(successObject.getValue().getObjectId(), response.getCMObject(successObject.getKey()).getObjectId());
        }

    }
}
