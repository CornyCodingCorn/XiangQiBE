package com.XiangQi.XiangQiBE.Events;

import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

public interface WebsocketEvent {
  public void invoke(StompHeaderAccessor accessor);
}
