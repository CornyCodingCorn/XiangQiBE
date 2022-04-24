package com.XiangQi.XiangQiBE.Services;

import com.XiangQi.XiangQiBE.Models.Token;
import com.XiangQi.XiangQiBE.Repositories.TokenRepo;
import com.XiangQi.XiangQiBE.Security.PlayerDetail;
import com.XiangQi.XiangQiBE.Security.Jwt.JwtPayload;
import com.XiangQi.XiangQiBE.Security.Jwt.JwtUtils;
import com.XiangQi.XiangQiBE.utils.JsonUtils;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Service
@AllArgsConstructor
public class JwtService {
    @Getter
    @AllArgsConstructor
    public class JwtPair {
        private String jwt;
        private ResponseCookie cookie;
    }

    private TokenRepo tokenRepo;
    private JwtUtils jwtUtils;

    public JwtPair generateJwtToken(String username) {
        Token tokenDoc = new Token("");
        // To document _id
        tokenRepo.save(tokenDoc);

        // Create jwt and create refresh jwt from it
        String jwt = jwtUtils.generateToken(username);
        String jwtRefresh = jwtUtils.generateRefreshToken(username, tokenDoc.getId(), jwt);

        tokenDoc.setToken(jwtRefresh);
        tokenRepo.save(tokenDoc);

        ResponseCookie cookie = createCookie(jwtRefresh);
        return new JwtPair(jwt, cookie);
    }

    public JwtPair refreshJwtToken(String jwtToken, String refreshToken) throws JWTVerificationException, RuntimeException {
        DecodedJWT refreshJwt = jwtUtils.decodeJwtToken(refreshToken);
        JwtPayload payload = JwtPayload.createPayload(refreshJwt.getPayload());

        if (payload.getJwtToken().compareTo(jwtToken) != 0) {
            throw new JWTVerificationException("Jwt token mismatch");
        }

        String username = refreshJwt.getSubject();
        var tokenDoc = tokenRepo.findById(payload.getTokenID()).orElseThrow(() -> new JWTVerificationException("Refresh token doesn't exist in database") );

        String jwt = jwtUtils.generateToken(username);
        String jwtRefresh = jwtUtils.generateRefreshToken(username, tokenDoc.getId(), jwt);
        
        tokenDoc.setToken(jwtRefresh);
        tokenRepo.save(tokenDoc);

        ResponseCookie cookie = createCookie(jwtRefresh);
        return new JwtPair(jwt, cookie);
    }

    public void logout(String refreshToken) throws JWTVerificationException {
        DecodedJWT refreshJwt = jwtUtils.decodeJwtToken(refreshToken);
        JwtPayload payload = JwtPayload.createPayload(JsonUtils.getJsonAsMap(refreshJwt.getPayload()));
        tokenRepo.deleteById(payload.getTokenID());
    }

    private ResponseCookie createCookie(String jwt) {
        return ResponseCookie.from(jwtUtils.getJwtCookie(), jwt).path("/api").maxAge(24 * 60 * 60).build();
    }
}
