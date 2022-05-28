package com.XiangQi.XiangQiBE.Common;

public class Move {
  public static final int POS_MASK = 0xFF;
  public static final int POS_X_MASK = 0x0F;
  public static final int POS_Y_MASK = 0xF0;
  public static final int PIECE_MASK = 0xFF00;
  int value;

  public static Move Create(String moveStr) {
    int x = moveStr.charAt(0);
    int y = moveStr.charAt(1);
    char c = moveStr.charAt(2);

    return new Move(x, y, c);
  }
  public static Move Create(int x, int y, char piece) {
    return new Move(x, y, piece);
  }

  private Move(int x, int y, char piece) {
    value = x | (y << 4) | (piece << 8);
  }

  public boolean ComparePosTo(Move move) {
    return (move.value & POS_MASK) == (value & POS_MASK);
  }

  public int getX() {
    return value & POS_X_MASK;
  }
  public int getY() {
    return (value & POS_Y_MASK) >> 4;
  }
  public char getPiece() {
    return (char)((value & PIECE_MASK) >> 8);
  }

  public boolean CompareTo(Move move) {
    return move.value == value;
  }
}
