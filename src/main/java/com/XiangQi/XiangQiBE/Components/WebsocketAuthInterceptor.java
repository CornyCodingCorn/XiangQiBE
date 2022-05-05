package com.XiangQi.XiangQiBE.Components;

import java.util.ArrayList;
import java.util.List;
import com.XiangQi.XiangQiBE.Configurations.SessionAttrs;
import com.XiangQi.XiangQiBE.Events.WebsocketEvent;
import com.XiangQi.XiangQiBE.Models.Lobby;
import com.XiangQi.XiangQiBE.Repositories.LobbyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import lombok.Getter;

@Component
public class WebsocketAuthInterceptor implements ChannelInterceptor {
    public class WebsocketAuthError extends Error {
        public WebsocketAuthError(String message) {
            super(message);
        }
    }

    @Autowired
    private LobbyRepo lobbyRepo;
    @Getter
    private List<WebsocketEvent> onConnect = new ArrayList<WebsocketEvent>();
    @Getter
    private List<WebsocketEvent> onDisconnect = new ArrayList<WebsocketEvent>();

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        String player = "";
        String destination = accessor.getDestination();;

        // Check if player is authenticated, then set the header for player name
        var attribute = accessor.getSessionAttributes();
        if (accessor.isMutable()) {
            player = (String) attribute.get(SessionAttrs.Username);
            accessor.setHeader("username", player);
        }

        switch (accessor.getCommand()) {
            case CONNECT:
                // Set attribute for the rest of the session.
                attribute.put(SessionAttrs.Username, accessor.getUser().getName());
                accessor.setSessionAttributes(attribute);

                for (var eventListener : onConnect) {
                    eventListener.invoke(accessor);
                }
                break;
            case SUBSCRIBE:
                if (destination.isBlank()) {
                    throw new WebsocketAuthError("The subscribing to des can't be blank");
                }

                // Only if player is subscribing to a specific lobby message broker
                var moveDesPattern = "/lobbies/+[a-zA-Z0-9-]+";
                if (destination.matches(moveDesPattern)) {
                    String[] arr = destination.split("/");
                    String lobbyID = arr[2];
                    Error error = new WebsocketAuthError(
                            "Can't subscribe to a lobby that you didn't join");

                    // If player didn't join any lobby or request to subscribe to another lobby
                    Lobby lobby = lobbyRepo.findByPlayer(player).orElseThrow(() -> error);
                    if (!lobby.getId().equals(lobbyID)) {
                        throw error;
                    }
                }
                break;
            case DISCONNECT:
                for (var eventListener : onDisconnect) {
                    eventListener.invoke(accessor);
                }
                break;
            default:
                break;
        }

        return message;
    }
}
