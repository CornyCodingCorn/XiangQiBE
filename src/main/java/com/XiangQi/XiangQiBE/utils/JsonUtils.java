package com.XiangQi.XiangQiBE.utils;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
}
