package com.XiangQi.XiangQiBE.Services;

import com.XiangQi.XiangQiBE.Models.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ServerMessageService {
  public static final String WS_USER_BROKER = "/users";

  @Autowired
  private SimpMessagingTemplate simpMessagingTemplate;
  
  public void SendResponse(String username, ResponseObject<?> resObj) {
    simpMessagingTemplate.convertAndSend(WS_USER_BROKER + "/" + username, resObj);
  }
}
