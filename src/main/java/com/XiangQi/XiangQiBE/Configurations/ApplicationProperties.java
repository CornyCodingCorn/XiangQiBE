package com.XiangQi.XiangQiBE.Configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "xiangqibe.app.")
public class ApplicationProperties {
    private String jwtSecret;
    private String jwtRefreshSecret;
    private String jwtCookieName;
    private int jwtExpirationMs;
    private int jwtRefreshExpirationMs;
    private String jwtHeader;
    private String jwtIssuer;
    private String jwtSalt;

    private String salt;
}
