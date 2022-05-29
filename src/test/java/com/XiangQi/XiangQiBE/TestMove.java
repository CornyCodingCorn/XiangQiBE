package com.XiangQi.XiangQiBE;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.XiangQi.XiangQiBE.Common.Move;
import com.XiangQi.XiangQiBE.Components.Piece.PieceType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestMove {
  @Test
  void Test1() {
    int x = 0;
    int y = 2;
    char piece = PieceType.ROOK.getValue();

    var move = Move.Create(x, y, piece);
    assertEquals(x, move.getX());
    assertEquals(y, move.getY());
    assertEquals(piece, move.getPiece());
    assertEquals(false, move.isRed());
  }

  @Test
  void Test2() {
    int x = 5;
    int y = 2;
    char piece = PieceType.ADVISOR.getValue();

    var move = Move.Create(x, y, Character.toUpperCase(piece));
    assertEquals(x, move.getX());
    assertEquals(y, move.getY());
    assertEquals(piece, move.getPiece());
    assertEquals(true, move.isRed());
  }

  @Test
  void Test3() {
    int x = 7;
    int y = 9;
    char piece = PieceType.EMPTY.getValue();

    var move = Move.Create(x, y, Character.toUpperCase(piece));
    assertEquals(x, move.getX());
    assertEquals(y, move.getY());
    assertEquals(piece, move.getPiece());
    assertEquals(false, move.isRed());
  }

  @Test
  void Test4() {
    int x = 9;
    int y = 1;
    char piece = PieceType.KING.getValue();

    var move = Move.Create(x, y, piece);
    assertEquals(x, move.getX());
    assertEquals(y, move.getY());
    assertEquals(piece, move.getPiece());
    assertEquals(false, move.isRed());
  }

  @Test
  void Test5() {
    int x = 0;
    int y = 0;
    char piece = PieceType.CANON.getValue();

    var move = Move.Create(x, y, piece);
    assertEquals(x, move.getX());
    assertEquals(y, move.getY());
    assertEquals(piece, move.getPiece());
    assertEquals(false, move.isRed());
  }
}
