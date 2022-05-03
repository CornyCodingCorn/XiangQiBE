package com.XiangQi.XiangQiBE.Security.Jwt;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.Base64.Decoder;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import com.XiangQi.XiangQiBE.Configurations.ApplicationProperties;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Getter
public class JwtUtils {
  private final String jwtSecret;
  private final String jwtRefreshSecret;
  private final String salt;

  @Value("${xiangqibe.app.jwt-header}")
  private String jwtHeader;
  @Value("${xiangqibe.app.jwt-refresh-expiration-ms}")
  private int jwtRefreshExpirationMs;
  @Value("${xiangqibe.app.jwt-expiration-ms}")
  private int jwtExpirationMs;
  @Value("${xiangqibe.app.jwt-cookie-name}")
  private String jwtCookie;
  @Value("${xiangqibe.app.jwt-issuer}")
  private String issuer;

  private final Algorithm algorithm;
  private final Algorithm refreshAlgorithm;
  private final JWTVerifier verifier;
  private final JWTVerifier refreshVerifier;
  private final BCryptPasswordEncoder jwtEncoder;

  public JwtUtils(@Value("${xiangqibe.app.jwt-secret}") String jwtSecret,
      @Value("{${xiangqibe.app.jwt-refresh-secret}}") String jwtRefreshSecret,
      @Value("${xiangqibe.app.jwt-salt}") String salt) {
    this.jwtSecret = jwtSecret;
    this.jwtRefreshSecret = jwtRefreshSecret;
    this.salt = salt;

    algorithm = Algorithm.HMAC256(jwtSecret);
    verifier = JWT.require(algorithm).withIssuer(issuer).build();

    refreshAlgorithm = Algorithm.HMAC512(jwtRefreshSecret);
    refreshVerifier = JWT.require(refreshAlgorithm).withIssuer(issuer).build();

    Decoder decoder = (Decoder) Base64.getDecoder();

    SecureRandom secureRandom = new SecureRandom(decoder.decode(salt));
    jwtEncoder = new BCryptPasswordEncoder(4, secureRandom);
  }

  public String DecodePart(String encodedPart) {
    Base64.Decoder decoder = Base64.getUrlDecoder();
    return new String(decoder.decode(encodedPart));
  }

  public String getJwtFromCookies(HttpServletRequest request) {
    Cookie cookie = WebUtils.getCookie(request, jwtCookie);
    if (cookie != null) {
      return cookie.getValue();
    } else {
      return null;
    }
  }

  public String getJwtFromHeader(HttpServletRequest request) {
    return request.getHeader(jwtHeader);
  }

  public String getJwtFromParam(HttpServletRequest request) {
    return request.getParameter(jwtHeader.toLowerCase());
  }

  public String getUserNameFromJwtToken(String token) throws JWTVerificationException {
    var decodedJwt = decodeJwtToken(token);
    if (decodedJwt == null) {
      return "";
    }
    return decodedJwt.getSubject();
  }

  public DecodedJWT decodeJwtToken(String token) throws JWTVerificationException {
    return verifier.verify(token);
  }

  public DecodedJWT decodeRefreshToken(String token) throws JWTVerificationException {
    return refreshVerifier.verify(token);
  }

  public boolean validateJwtToken(String authToken) {
    try {
      verifier.verify(authToken);
      return true;
    } catch (JWTVerificationException e) {
      return false;
    }
  }

  public String generateToken(String username) {
    try {
      String token = JWT.create().withSubject(username).withIssuedAt(new Date())
          .withExpiresAt(new Date(new Date().getTime() + jwtExpirationMs)).withIssuer(issuer)
          .sign(algorithm);

      return token;
    } catch (JWTCreationException exception) {
      // Invalid Signing configuration / Couldn't convert Claims.
      return "";
    }
  }

  public String generateRefreshToken(String username, String tokenID, String jwtToken) {
    try {
      JwtPayload payload = new JwtPayload();
      payload.setTokenID(tokenID);
      payload.setJwtToken(jwtToken);

      String token =
          JWT.create().withSubject(username).withPayload(payload.toMap()).withIssuedAt(new Date())
              .withExpiresAt(new Date(new Date().getTime() + jwtRefreshExpirationMs))
              .withIssuer(issuer).sign(refreshAlgorithm);

      return token;
    } catch (JWTCreationException exception) {
      // Invalid Signing configuration / Couldn't convert Claims.
      return "";
    }
  }
}
