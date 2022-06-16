package com.XiangQi.XiangQiBE.utils;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonUtils {
    public static HashMap<String, String> getJsonAsMap(String json) throws RuntimeException {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<Map<String,String>> typeRef = new TypeReference<Map<String,String>>() {};
            HashMap<String, String> result = (HashMap<String, String>) mapper.readValue(json, typeRef);

            return result;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Couldnt parse json:" + json, e);
        }
    }

    public static String getJsonString(Object object) {
        try
        {
            ObjectWriter oWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = oWriter.writeValueAsString(object);

            return json;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Couldnt parse object:" + object, e);
        }
    }
}
