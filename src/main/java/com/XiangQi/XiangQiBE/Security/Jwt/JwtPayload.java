package com.XiangQi.XiangQiBE.Security.Jwt;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import com.XiangQi.XiangQiBE.utils.JsonUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtPayload {
    private String tokenID;
    private String jwtToken;

    public Map<String, Object> toMap() {
        var result = new HashMap<String, Object>();
        
        result.put("tokenID", tokenID);
        result.put("jwtToken", jwtToken);

        return result;
    }

    public static JwtPayload createPayload(Map<String, String> payload) {
        var result = new JwtPayload();

        result.tokenID = (String) payload.get("tokenID");
        result.jwtToken = (String) payload.get("jwtToken");

        return result;
    }

    public static JwtPayload createPayload(String payload64Encoded) {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        return createPayload(JsonUtils.getJsonAsMap(new String(decoder.decode(payload64Encoded))));
    }
}
