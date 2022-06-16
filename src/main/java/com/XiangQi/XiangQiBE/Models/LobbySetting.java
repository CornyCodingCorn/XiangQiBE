package com.XiangQi.XiangQiBE.Models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LobbySetting {
  private int minPerTurn;
  private int totalMin;
  private boolean isVsBot;
  private boolean isPrivate;

  public LobbySetting(int minPerTurn, int totalMin, boolean isVsBot, boolean isPrivate) {
    this.minPerTurn = minPerTurn;
    this.totalMin = totalMin;
    this.isVsBot = isVsBot;
    this.isPrivate = isPrivate;
  }
}
