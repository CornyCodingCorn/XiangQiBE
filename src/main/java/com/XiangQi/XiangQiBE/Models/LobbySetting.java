package com.XiangQi.XiangQiBE.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LobbySetting {
  private int minPerTurn;
  private int totalMin;
  private boolean isVsBot;
  private boolean isPrivateLobby;
}
