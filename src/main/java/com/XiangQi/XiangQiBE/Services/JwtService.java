package com.XiangQi.XiangQiBE.Services;

import com.XiangQi.XiangQiBE.Models.Token;
import com.XiangQi.XiangQiBE.Repositories.TokenRepo;
import com.XiangQi.XiangQiBE.Security.Jwt.JwtPayload;
import com.XiangQi.XiangQiBE.Security.Jwt.JwtUtils;
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
        var pair = generateJwtPair(username, tokenDoc.getId());

        tokenDoc.setToken(pair.getSecond());
        tokenRepo.save(tokenDoc);

        ResponseCookie cookie = createCookie(pair.getSecond());
        return new JwtPair(pair.getFirst(), cookie);
    }

    public JwtPair refreshJwtToken(String jwtToken, String refreshToken) throws JWTVerificationException, RuntimeException {
        DecodedJWT refreshJwt = jwtUtils.decodeRefreshToken(refreshToken);
        JwtPayload payload = JwtPayload.createPayload(refreshJwt.getPayload());

        if (!jwtUtils.getJwtEncoder().matches(jwtToken, payload.getJwtToken())) {
            throw new JWTVerificationException("Jwt token mismatch");
        }

        String username = refreshJwt.getSubject();
        var tokenDoc = tokenRepo.findById(payload.getTokenID()).orElseThrow(() -> new JWTVerificationException("Refresh token doesn't exist in database") );

        var pair = generateJwtPair(username, tokenDoc.getId());
        
        tokenDoc.setToken(pair.getSecond());
        tokenRepo.save(tokenDoc);

        ResponseCookie cookie = createCookie(pair.getSecond());
        return new JwtPair(pair.getFirst(), cookie);
    }

    public void logout(String refreshToken) throws JWTVerificationException {
        DecodedJWT refreshJwt = jwtUtils.decodeRefreshToken(refreshToken);
        JwtPayload payload = JwtPayload.createPayload(refreshJwt.getPayload());
        tokenRepo.deleteById(payload.getTokenID());
    }

    private ResponseCookie createCookie(String jwt) {
        return ResponseCookie.from(jwtUtils.getJwtCookie(), jwt).path("/api").maxAge(24 * 60 * 60).sameSite("Lax").build();
    }

    private Pair<String, String> generateJwtPair(String username, String tokenID) {
        String jwt = jwtUtils.generateToken(username);
        String jwtRefresh = jwtUtils.generateRefreshToken(username, tokenID, jwtUtils.getJwtEncoder().encode(jwt));

        return Pair.of(jwt, jwtRefresh);
    }
}
