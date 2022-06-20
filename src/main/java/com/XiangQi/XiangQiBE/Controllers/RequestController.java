package com.XiangQi.XiangQiBE.Controllers;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.XiangQi.XiangQiBE.Models.Request;
import com.XiangQi.XiangQiBE.Models.ResponseObject;
import com.XiangQi.XiangQiBE.Services.RequestService;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("api/request")
@AllArgsConstructor
public class RequestController {
    RequestService requestService;

    @PostMapping("/change-password")
    public ResponseEntity<ResponseObject<Object>> requestChangePassword(
        @RequestParam(name = "username", required = true) String username
        ) {

        try {
            requestService.SendRequest(username, Request.Type.CHANGE_PASSWORD);
            return ResponseObject.Response(HttpStatus.OK, "Email sent.", null);
        } catch (Exception e) {
            return ResponseObject.Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<ResponseObject<Object>> resolveChangePassword(
        @RequestParam(name = "token", required = true) String token,
        @RequestParam(name = "password", required = true) String password
        ) {

        try {
            requestService.ResolveRequest(token, new String[] {password});
            return ResponseObject.Response(HttpStatus.OK, "Email verified", null);
        } catch (Exception e) {
            return ResponseObject.Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }

    @PutMapping("/verify-email")
    public ResponseEntity<ResponseObject<Object>> resolveVerifyEmail(
        @RequestParam(name = "token", required = true) String token
        ) {

        try {
            requestService.ResolveRequest(token, new String[0]);
            return ResponseObject.Response(HttpStatus.OK, "Email verified", null);
        } catch (Exception e) {
            return ResponseObject.Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }
}
