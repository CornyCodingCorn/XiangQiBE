package com.XiangQi.XiangQiBE.utils;

public class Vector2 {
  public int x = 0;
  public int y = 0;

  private Vector2(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public static Vector2 create(int x, int y) {
    return new Vector2(x, y);
  }

  public boolean isEqual(Vector2 other) {
    return this.x == other.x && this.y == other.y;
  }

  public void equal(Vector2 other) {
    this.x = other.x;
    this.y = other.y;
  }
}
