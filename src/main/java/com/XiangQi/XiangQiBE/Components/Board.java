package com.XiangQi.XiangQiBE.Components;

import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Component
public class Board {
  @AllArgsConstructor
  public enum Result {
    CONTINUE(0),
    RED_WIN(1),
    BLACK_WIN(2),
    DRAW(3);

    @Getter
    @Setter
    private int value;
  }

  public boolean IsMoveValid(String board, String move) {
    return true;
  }

  public String UpdateBoard(String board, String move) {
    return board;
  }

  public Result CheckResult(String board, boolean isRedTurn) {
    //Check the result of the board game.
    return Result.CONTINUE;
  }
}
