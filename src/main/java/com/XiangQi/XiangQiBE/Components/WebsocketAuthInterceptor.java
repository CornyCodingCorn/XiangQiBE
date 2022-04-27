package com.XiangQi.XiangQiBE.Components;

import com.XiangQi.XiangQiBE.Models.Lobby;
import com.XiangQi.XiangQiBE.Repositories.LobbyRepo;
import com.XiangQi.XiangQiBE.Security.Jwt.JwtUtils;
import com.XiangQi.XiangQiBE.Services.LobbyService;
import com.XiangQi.XiangQiBE.Services.LobbyService.LobbyException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class WebsocketAuthInterceptor implements ChannelInterceptor {
    public class WebsocketAuthError extends Error {
        public WebsocketAuthError(String message) {super(message);}
    }

    @Autowired
    private LobbyService lobbyService;
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        var jwtStr = (String) accessor.getHeader("${xiangqibe.app.jwt-header}");

        if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            var destination = accessor.getDestination();
            if (destination.isBlank()) throw new WebsocketAuthError("The destination can't be blank");

            var movePattern = "/lobbies/+[a-zA-Z0-9-]+";
            if (destination.matches(movePattern)) {
                String[] arr = destination.split("/");
                String lobbyID = "";

                // Get requested subscribe lobbyID
                for (int i = 0; i < arr.length; i++) {
                    if (!arr[i].isBlank()) {
                        lobbyID = arr[i + 1];
                        break;
                    }
                }

                // Get player lobby
                String player = jwtUtils.getUserNameFromJwtToken(jwtStr);
                Lobby lobby = null;
                try {
                    lobby = lobbyService.getPlayerLobby(player);
                } catch (LobbyException e) {
                    throw new WebsocketAuthError("Player haven't join any lobby");
                }

                // Check if the lobby that the player in is the same as the requested lobby
                if (!lobby.getId().equals(lobbyID)) {
                    throw new WebsocketAuthError("Player doesn't have authority in this lobby");
                }
            } else {
                throw new WebsocketAuthError("The destination doesn't exist");
            }
        }

        return message;
    }
    
}
