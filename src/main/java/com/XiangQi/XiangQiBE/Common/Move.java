package com.XiangQi.XiangQiBE.Common;

public class Move {
  public static final int POS_MASK    = 0x000FF;
  public static final int POS_X_MASK  = 0x0000F;
  public static final int POS_Y_MASK  = 0x000F0;
  public static final int IS_RED_MASK = 0x00F00;
  public static final int PIECE_MASK  = 0xFF000;

  public static final int PIECE_SHIFT = 12;
  public static final int X_SHIFT = 0;
  public static final int Y_SHIFT = 4;
  public static final int RED_SHIFT = 8;

  int value;

  // 00000000 0000 0000 0000
  // piece    red  y    x

  public static Move Create(int x, int y, char piece) {
    return new Move(x, y, piece);
  }

  private Move(int x, int y, char piece) {
    value = (x << X_SHIFT) | (y << Y_SHIFT) | ((Character.isUpperCase(piece) ? 1 : 0) << RED_SHIFT) | (Character.toLowerCase(piece) << PIECE_SHIFT);
  }

  public boolean ComparePosTo(Move move) {
    return (move.value & POS_MASK) == (value & POS_MASK);
  }

  public int getX() {
    return (value & POS_X_MASK) >> X_SHIFT;
  }
  public int getY() {
    return (value & POS_Y_MASK) >> Y_SHIFT;
  }
  public char getPiece() {
    return (char)((value & PIECE_MASK) >> PIECE_SHIFT);
  }
  public char getPieceCaseSensitive() {
    return isRed() ? Character.toUpperCase(getPiece()) : getPiece();
  }
  // Ignore cases
  public void setPiece(char piece) {
    // Flush value to 0 with mask then | it
    value = (value & ~PIECE_MASK) | (Character.toLowerCase(piece) << PIECE_SHIFT);
  }
  public boolean isRed() {
    return (value & IS_RED_MASK) != 0;
  }
  public void setRed(boolean isRed) {
    value = (value & ~IS_RED_MASK) | ((isRed ? 1 : 0) << RED_SHIFT);
  }

  public boolean CompareTo(Move move) {
    return move.value == value;
  }
}
