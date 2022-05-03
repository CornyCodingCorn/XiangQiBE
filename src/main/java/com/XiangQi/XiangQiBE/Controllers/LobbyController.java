package com.XiangQi.XiangQiBE.Controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.XiangQi.XiangQiBE.Models.Lobby;
import com.XiangQi.XiangQiBE.Models.LobbyMessage;
import com.XiangQi.XiangQiBE.Models.ResponseObject;
import com.XiangQi.XiangQiBE.Security.Jwt.JwtUtils;
import com.XiangQi.XiangQiBE.Services.LobbyService;
import com.XiangQi.XiangQiBE.Services.LobbyService.LobbyException;
import com.XiangQi.XiangQiBE.dto.LobbyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/lobbies")
public class LobbyController {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private LobbyService lobbyService;

    @PostMapping
    public ResponseEntity<ResponseObject<LobbyDto>> createLobby(@RequestHeader(name = "${xiangqibe.app.jwt-header}") String jwtToken) {
        try {
            String username = jwtUtils.getUserNameFromJwtToken(jwtToken);
            Lobby lobby = lobbyService.Create(username);

            return ResponseObject.Response(HttpStatus.OK, "Room created", new LobbyDto(lobby));
        } catch (Exception e) {
            return ResponseObject.Response(HttpStatus.FORBIDDEN, e.getMessage(), null);
        }
    }

    @GetMapping
    public ResponseEntity<ResponseObject<List<LobbyDto>>> getAllLobbies() {
        List<Lobby> lobbies = lobbyService.GetAll();
        List<LobbyDto> lobbiesDto = new ArrayList<LobbyDto>();

        for (var lobby : lobbies) {
            lobbiesDto.add(new LobbyDto(lobby));
        }

        return ResponseObject.Response(HttpStatus.OK, "Room created", lobbiesDto);
    }

    @PutMapping
    public ResponseEntity<ResponseObject<LobbyDto>> joinLobby(@RequestHeader(name = "${xiangqibe.app.jwt-header}") String jwtToken, @RequestParam("id") String lobbyID) {
        try {
            String username = jwtUtils.getUserNameFromJwtToken(jwtToken);
            Lobby lobby = lobbyService.Join(lobbyID, username);

            return ResponseObject.Response(HttpStatus.OK, "Joined room " + lobbyID, new LobbyDto(lobby));
        }
        catch(Exception e) {
            return ResponseObject.Response(HttpStatus.FORBIDDEN, e.getMessage(), null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<LobbyDto>> ready() {

        return null;
    }

    @MessageMapping("/lobbies/moves")
    public ResponseEntity<ResponseObject<Object>> movePiece(@Header(name = "${xiangqibe.app.jwt-header= bearer}") String jwt, Message<String> message) {
        try {
            String player = jwtUtils.getUserNameFromJwtToken(jwt);
            lobbyService.Move(player, message.getPayload());

            return ResponseObject.Response(HttpStatus.OK, "Move accepted", message.getPayload());
        } catch (LobbyException e) {
            return ResponseObject.Response(HttpStatus.FORBIDDEN, e.getMessage(), message.getPayload());
        } catch (Exception e) {
            return ResponseObject.Response(HttpStatus.OK, e.getMessage(), message.getPayload());
        }
    }

    @MessageMapping("/lobbies")
    public ResponseEntity<ResponseObject<Object>> sendLobbyMessage(@Header(name = "${xiangqibe.app.jwt-header= bearer}") String jwt, Message<LobbyMessage> message) {
        try {
            String player = jwtUtils.getUserNameFromJwtToken(jwt);
            switch(message.getPayload().getType()) {
                case JOIN:
                lobbyService.Create(player);
                break;
                case DISCONNECT:
                lobbyService.Quit(player);
                break;
                case CHANGE_READY:
                lobbyService.Ready(player);
                break;
                case MOVE:
                lobbyService.Move(player, message.getPayload().getData());
                break;
            }

            return ResponseObject.Response(HttpStatus.OK, "Message accepted", message.getPayload());
        } catch (LobbyException e) {
            return ResponseObject.Response(HttpStatus.FORBIDDEN, e.getMessage(), message.getPayload());
        } catch (Exception e) {
            return ResponseObject.Response(HttpStatus.OK, e.getMessage(), message.getPayload());
        }
    }
}
