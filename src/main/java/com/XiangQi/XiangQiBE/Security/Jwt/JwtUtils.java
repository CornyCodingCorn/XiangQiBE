package com.XiangQi.XiangQiBE.Security.Jwt;

import java.util.Base64;
import java.util.Date;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;
import lombok.Getter;

@Component
@Getter
public class JwtUtils {
  private final String jwtSecret = "$2a$12$9k/70Axzg7Q4xj3F7REDPe32ewMLFG6TepQgWBd4ASf38uAQJ7aC6";
  private final String jwtRefreshSecret = "$2a$12$9k/70Axzg7Q4xj3F7REDPe32ewMLFG6TepQgWBd4ASf38uAQJ7aC6";

  @Value("${xiangqibe.app.jwtHeader}")
  private String jwtHeader;
  @Value("${xiangqibe.app.jwtRefreshExpirationMs}")
  private int jwtRefreshExpirationMs;
  @Value("${xiangqibe.app.jwtExpirationMs}")
  private int jwtExpirationMs;
  @Value("${xiangqibe.app.jwtCookieName}")
  private String jwtCookie;
  
  private final String issuer = "auth0";
  private final Algorithm algorithm;
  private final JWTVerifier verifier;

  public JwtUtils() {
    algorithm = Algorithm.HMAC256(jwtSecret);
    verifier= JWT.require(algorithm).withIssuer(issuer).build();
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

  public String getUserNameFromJwtToken(String token) {
    var decodedJwt = decodeJwtToken(token);
    if (decodedJwt == null) {
      return "";
    }
    return decodedJwt.getSubject();
  }

  public DecodedJWT decodeJwtToken(String token) throws JWTVerificationException {
    return verifier.verify(token);
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
      String token = JWT.create()
      .withSubject(username)
      .withIssuedAt(new Date())
      .withExpiresAt(new Date(new Date().getTime() + jwtExpirationMs))
      .withIssuer(issuer)
      .sign(algorithm);

      return token;
    } catch (JWTCreationException exception){
      //Invalid Signing configuration / Couldn't convert Claims.
      return "";
    }
  }
  
  public String generateRefreshToken(String username, String tokenID, String jwtToken) {
    try {
      JwtPayload payload = new JwtPayload();
      payload.setTokenID(tokenID);
      payload.setJwtToken(jwtToken);

      String token = JWT.create()
      .withSubject(username)
      .withPayload(payload.toMap())
      .withIssuedAt(new Date())
      .withExpiresAt(new Date(new Date().getTime() + jwtRefreshExpirationMs))
      .withIssuer(issuer)
      .sign(algorithm);

      return token;
    } catch (JWTCreationException exception){
      //Invalid Signing configuration / Couldn't convert Claims.
      return "";
    }
  }
}
