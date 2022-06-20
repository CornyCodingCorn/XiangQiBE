package com.XiangQi.XiangQiBE.Controllers;

import javax.validation.Valid;
import com.XiangQi.XiangQiBE.Configurations.SessionAttrs;
import com.XiangQi.XiangQiBE.Models.Player;
import com.XiangQi.XiangQiBE.Models.Request;
import com.XiangQi.XiangQiBE.Models.ResponseObject;
import com.XiangQi.XiangQiBE.Security.Jwt.JwtUtils;
import com.XiangQi.XiangQiBE.Services.JwtService;
import com.XiangQi.XiangQiBE.Services.PlayerService;
import com.XiangQi.XiangQiBE.Services.RequestService;
import com.XiangQi.XiangQiBE.dto.PlayerDto;
import com.XiangQi.XiangQiBE.dto.PlayerLoginDto;
import com.XiangQi.XiangQiBE.dto.PlayerRegisterDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthorizationController {
    private PlayerService playerService;
    private JwtUtils jwtUtils;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private RequestService requestService;

    @GetMapping()
    public ResponseEntity<ResponseObject<PlayerDto>> getPlayerInfo(@SessionAttribute(name = SessionAttrs.Username) String username) {
        try {
            Player player = playerService.get(username);
            var response = ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject<PlayerDto>(HttpStatus.OK,
                            "User " + player.getUsername() + " login successfully", new PlayerDto(player)));

            return response;
        } catch (Exception e) {
            return ResponseObject.Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject<PlayerDto>> authenticateUser(
            @RequestBody PlayerLoginDto loginRequest) {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            Player player;

            player = playerService.get(loginRequest.getUsername());
            if (!player.isValidated()) throw new Exception("Account not validated!");

            var jwtPair = jwtService.generateJwtToken(player.getUsername());

            var response = ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, jwtPair.getCookie().toString())
                    .header(jwtUtils.getJwtHeader(), jwtPair.getJwt())
                    .body(new ResponseObject<PlayerDto>(HttpStatus.OK,
                            "User " + player.getUsername() + " login successfully", new PlayerDto(player)));

            return response;
        } catch (Exception e) {
            return ResponseObject.Response(HttpStatus.NOT_FOUND, e.getMessage(), null);
        }
    }

    @PostMapping("/register")
    ResponseEntity<ResponseObject<PlayerDto>> register(
            @Valid @RequestBody PlayerRegisterDto registerInfo) {
        try {
            var player = playerService.create(registerInfo.getUsername(),
                    registerInfo.getPassword(), registerInfo.getEmail());

            requestService.SendRequest(player.getUsername(), Request.Type.EMAIL_VERIFY);
            return ResponseObject.Response(HttpStatus.CREATED, "Register successfully",
                    new PlayerDto(player));
        } catch (Exception e) {
            return ResponseObject.Response(HttpStatus.CONFLICT, e.getMessage(), null);
        }
    }

    @PutMapping("/logout")
    ResponseEntity<ResponseObject<Object>> logout(
            @CookieValue(name = "${xiangqibe.app.jwt-cookie-name}") String refreshJwt) {
        try {
            jwtService.logout(refreshJwt);
            return ResponseObject.Response(HttpStatus.OK, "Logged out successfully", null);
        } catch (Exception e) {
            return ResponseObject.Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }

    @PutMapping("/refresh")
    ResponseEntity<ResponseObject<Object>> refresh(
            @CookieValue(name = "${xiangqibe.app.jwt-cookie-name}") String refreshToken,
            @RequestHeader(name = "${xiangqibe.app.jwt-header}") String jwtToken) {
        try {
            var jwtPair = jwtService.refreshJwtToken(jwtToken, refreshToken);

            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, jwtPair.getCookie().toString())
                    .header(jwtUtils.getJwtHeader(), jwtPair.getJwt())
                    .body(new ResponseObject<Object>(HttpStatus.OK, "Refresh token successfully",
                            null));
        } catch (Exception e) {
            return ResponseObject.Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }
}
