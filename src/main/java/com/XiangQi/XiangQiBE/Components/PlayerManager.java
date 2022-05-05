package com.XiangQi.XiangQiBE.Components;

import java.util.HashMap;
import com.XiangQi.XiangQiBE.Configurations.SessionAttrs;
import com.XiangQi.XiangQiBE.Events.WebsocketEvent;
import com.XiangQi.XiangQiBE.Services.LobbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class PlayerManager {
  private HashMap<String, String> wsSessionMap = new HashMap<>();
  private LobbyService lobbyService;
  private WebsocketAuthInterceptor wsInterceptor;
  private OnConnectListener onConnectListener = new OnConnectListener(this);
  private OnDisconnectListener onDisconnectListener = new OnDisconnectListener(this);

  @Autowired
  public PlayerManager(LobbyService lobbyService, WebsocketAuthInterceptor wsInterceptor) {
    this.lobbyService = lobbyService;
    this.wsInterceptor = wsInterceptor;

    wsInterceptor.getOnConnect().add(onConnectListener);
    wsInterceptor.getOnDisconnect().add(onDisconnectListener);
  }

  public void addSession(String sessionID, String username) {
    wsSessionMap.put(sessionID, username);
    log.info("Player " + username + " connect with sessionID: " + sessionID);
  }

  public void removeSession(String sessionID) {
    String player = wsSessionMap.get(sessionID);
    if (player == null)
      return;

    try {
      lobbyService.Quit(player);
    } catch (Exception e) {
      // Don't really if the player have a lobby or not just call quit then remove
    } finally {
      wsSessionMap.remove(sessionID);
    }
  }
}


@AllArgsConstructor
class OnDisconnectListener implements WebsocketEvent {
  PlayerManager playerManager;

  @Override
  public void invoke(StompHeaderAccessor accessor) {
    playerManager
        .removeSession(accessor.getSessionId());
  }
}


@AllArgsConstructor
class OnConnectListener implements WebsocketEvent {
  PlayerManager playerManager;

  @Override
  public void invoke(StompHeaderAccessor accessor) {
    playerManager.addSession(accessor.getSessionId(),
        (String) accessor.getSessionAttributes().get(SessionAttrs.Username));
  }
}
