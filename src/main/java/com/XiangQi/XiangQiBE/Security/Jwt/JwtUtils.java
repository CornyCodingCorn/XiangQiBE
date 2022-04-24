package com.XiangQi.XiangQiBE.Security.Jwt;

import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import com.XiangQi.XiangQiBE.Security.PlayerDetail;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
  private final String jwtSecret = "bezKoderSecretKey";

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

  public String getJwtFromCookies(HttpServletRequest request) {
    Cookie cookie = WebUtils.getCookie(request, jwtCookie);
    if (cookie != null) {
      return cookie.getValue();
    } else {
      return null;
    }
  }

  public ResponseCookie generateJwtCookie(PlayerDetail userPrincipal) {
    String jwt = generateTokenFromUsername(userPrincipal.getUsername());
    ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt).path("/api").maxAge(24 * 60 * 60).build();
    return cookie;
  }

  public ResponseCookie getCleanJwtCookie() {
    ResponseCookie cookie = ResponseCookie.from(jwtCookie, null).path("/api").build();
    return cookie;
  }

  public String getUserNameFromJwtToken(String token) {
    var decodedJwt = decodeJwtToken(token);
    if (decodedJwt == null) {
      return "";
    }
    return decodedJwt.getSubject();
  }

  public DecodedJWT decodeJwtToken(String token) {
    try {
      return verifier.verify(token);
    } catch (JWTVerificationException e) {
      return null;
    }
  }

  public boolean validateJwtToken(String authToken) {
    try {
      verifier.verify(authToken);
      return true;
    } catch (JWTVerificationException e) {
      return false;
    }
  }
  
  public String generateTokenFromUsername(String username) {
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
}
