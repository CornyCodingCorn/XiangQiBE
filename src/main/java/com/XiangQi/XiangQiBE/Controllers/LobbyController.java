package com.XiangQi.XiangQiBE.Controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import com.XiangQi.XiangQiBE.Configurations.SessionAttrs;
import com.XiangQi.XiangQiBE.Models.Lobby;
import com.XiangQi.XiangQiBE.Models.LobbyMessage;
import com.XiangQi.XiangQiBE.Models.ResponseObject;
import com.XiangQi.XiangQiBE.Services.LobbyService;
import com.XiangQi.XiangQiBE.Services.ServerMessageService;
import com.XiangQi.XiangQiBE.Services.LobbyService.LobbyException;
import com.XiangQi.XiangQiBE.dto.LobbyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/lobbies")
public class LobbyController {
    @Autowired
    private LobbyService lobbyService;
    @Autowired
    private ServerMessageService serverMessageService;

    @PostMapping
    public ResponseEntity<ResponseObject<LobbyDto>> createLobby(
            @SessionAttribute(name = SessionAttrs.Username) String username, @RequestParam(name = "private", required = false) boolean isPrivate) {
        try {
            Lobby lobby = lobbyService.Create(username, isPrivate);

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
    public ResponseEntity<ResponseObject<LobbyDto>> joinLobby(
            @SessionAttribute(name = SessionAttrs.Username) String username,
            @RequestParam("id") String lobbyID) {
        try {
            Lobby lobby = lobbyService.Join(lobbyID, username);

            return ResponseObject.Response(HttpStatus.OK, "Joined room " + lobbyID,
                    new LobbyDto(lobby));
        } catch (Exception e) {
            return ResponseObject.Response(HttpStatus.FORBIDDEN, e.getMessage(), null);
        }
    }

    @PutMapping("/ready")
    public ResponseEntity<ResponseObject<LobbyDto>> ready(
            @SessionAttribute(name = SessionAttrs.Username) String username) {
        try {
            Lobby lobby = lobbyService.Ready(username);

            return ResponseObject.Response(HttpStatus.OK,
                    "Player " + username + " readied in lobby " + lobby.getId(),
                    new LobbyDto(lobby));
        } catch (Exception e) {
            return ResponseObject.Response(HttpStatus.FORBIDDEN, e.getMessage(), null);
        }
    }

    @MessageMapping("/lobbies/{id}")
    public void sendLobbyMessage(Message<LobbyMessage> message,
            @AuthenticationPrincipal Principal principal) {
        ResponseObject<LobbyMessage> resObj = null;
        try {
            String player = principal.getName();
            if (!message.getPayload().getPlayer().equals(player)) {
                throw lobbyService.new LobbyException("",
                        "Player can only send message with their name!");
            }

            var exception = lobbyService.new LobbyException("",
                    "The message of type " + message.getPayload().getType()
                            + " doesn't belong to types that allow to send by client");
            switch (message.getPayload().getType()) {
                case PLAY_AGAIN:
                    lobbyService.PlayAgain(player);
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
                case END:
                    lobbyService.Concede(player);
                    break;
                case UNDO_REQUEST:
                    lobbyService.AskForUndo(player);
                    break;
                case UNDO_REPLY:
                    lobbyService.ReplyToUndo(player, message.getPayload());
                    break;
                case CHANGE_SETTING:
                    lobbyService.ChangeSetting(player, message.getPayload());
                    break;
                default:
                    throw exception;
            }

            resObj = new ResponseObject<LobbyMessage>(HttpStatus.OK, "Message accepted",
                    message.getPayload());
        } catch (LobbyException e) {
            resObj = new ResponseObject<LobbyMessage>(HttpStatus.FORBIDDEN, e.getMessage(),
                    message.getPayload());
        } catch (Exception e) {
            resObj = new ResponseObject<LobbyMessage>(HttpStatus.BAD_REQUEST, e.getMessage(),
                    message.getPayload());
        } finally {
            if (resObj != null) {
                serverMessageService.SendResponse(principal.getName(), resObj);
            }
        }
    }
}
