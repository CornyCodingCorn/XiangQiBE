package com.XiangQi.XiangQiBE.Components;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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
  private static final int DISCONNECT_DURATION = 5000;
  private HashMap<String, String> wsSessionMap = new HashMap<>();
  private HashMap<String, Timer> wsDisconnectTimer = new HashMap<>();
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
    var timer = wsDisconnectTimer.get(sessionID);
    if (timer != null) {
      timer.cancel();
      wsDisconnectTimer.remove(sessionID);
    }

    log.info("Player " + username + " connect with sessionID: " + sessionID);
  }

  public void removeSession(String sessionID) {
    var timer = new Timer();
    wsDisconnectTimer.put(sessionID, timer);
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        String player = wsSessionMap.get(sessionID);
        log.info("Player " + player + " disconnect with sessionID: " + sessionID);
    
        if (player == null)
          return;
        try {
          lobbyService.Quit(player);
        } catch (Exception e) {
          // Don't really care if the player have a lobby or not just call quit then remove
        } finally {
          wsSessionMap.remove(sessionID);
        }
      }
    }, DISCONNECT_DURATION);
  }
}


@AllArgsConstructor
class OnDisconnectListener implements WebsocketEvent {
  PlayerManager playerManager;

  @Override
  public void invoke(StompHeaderAccessor accessor) {
    playerManager.removeSession(accessor.getSessionId());
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
