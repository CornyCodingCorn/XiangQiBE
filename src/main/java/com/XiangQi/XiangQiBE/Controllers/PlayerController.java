package com.XiangQi.XiangQiBE.Controllers;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.XiangQi.XiangQiBE.Models.Player;
import com.XiangQi.XiangQiBE.Models.ResponseObject;
import com.XiangQi.XiangQiBE.Services.PlayerService;
import com.XiangQi.XiangQiBE.dto.PlayerDto;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/api/player")
@AllArgsConstructor
public class PlayerController {
    PlayerService playerService;
    
    @PutMapping("/profile")
    public ResponseEntity<ResponseObject<PlayerDto>> changePlayerProfile(@RequestParam(name = "username") String username, @RequestParam(name = "profile", required =  true) Integer index) {
        try {
            var player = playerService.changeProfile(username, index);
            return ResponseObject.Response(HttpStatus.OK, "Change profile successful", new PlayerDto(player));
        } catch(Exception e) {
            return ResponseObject.Response(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }

    @GetMapping()
    public ResponseEntity<ResponseObject<PlayerDto>> getPlayerInfo(@RequestParam(name = "username", required = true) String username) {
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
}
