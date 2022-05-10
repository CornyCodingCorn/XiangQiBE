package com.XiangQi.XiangQiBE.utils;

public class PieceMove {
  public int oldX;
  public int oldY;
  public String piece;
  public int newX;
  public int newY;

  public static PieceMove Parse(String moveStr) {
    var result = new PieceMove();

    result.oldX = Integer.parseInt("" + moveStr.charAt(0));
    result.oldY = Integer.parseInt("" + moveStr.charAt(1));
    result.piece = "" + moveStr.charAt(2);
    result.newX = Integer.parseInt("" + moveStr.charAt(3));
    result.newY = Integer.parseInt("" + moveStr.charAt(4));

    return result;
  }
}
