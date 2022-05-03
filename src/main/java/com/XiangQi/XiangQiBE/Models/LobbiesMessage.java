package com.XiangQi.XiangQiBE.Models;

import com.XiangQi.XiangQiBE.dto.LobbyDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LobbiesMessage {
  @AllArgsConstructor
  public enum Type {
      CREATE(1),
      REMOVE(2);

      @Getter
      private int value;
  }

  private LobbyDto lobby;
  private Type type;
}
