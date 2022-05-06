package com.XiangQi.XiangQiBE.Components;

import org.springframework.stereotype.Component;

@Component
public class Board {
  public boolean IsMoveValid(String board, String move) {
    return true;
  }

  public String UpdateBoard(String board, String move) {
    return board;
  }
}
