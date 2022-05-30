package com.XiangQi.XiangQiBE;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.XiangQi.XiangQiBE.Components.Board;
import com.XiangQi.XiangQiBE.Components.Board.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import lombok.extern.log4j.Log4j2;

@SpringBootTest
@Log4j2
public class BoardTest {
  @Autowired
  Board board;

  @Test
  void TestFirstMoves() {
    var time = System.nanoTime();
    board.setBoard(    
    "rheakaehr" +
    "000000000" +
    "0c00000c0" +
    "p0p0p0p0p" +
    "000000000" +
    "000000000" +
    "P0P0P0P0P" +
    "0C00000C0" +
    "000000000" +
    "RHEAKAEHR");
    log.info(System.nanoTime() - time);
    // Wrong format
    time = System.nanoTime();
    assertEquals(false, board.IsMoveValid("00-2"));

    assertEquals(true, board.IsMoveValid("00r02"));
    assertEquals(true, board.IsMoveValid("00r01"));
    assertEquals(false, board.IsMoveValid("00r03"));
    assertEquals(false, board.IsMoveValid("00r00"));
    assertEquals(false, board.IsMoveValid("00r10"));

    assertEquals(true, board.IsMoveValid("09R08"));
    assertEquals(true, board.IsMoveValid("09R07"));
    assertEquals(false, board.IsMoveValid("09R06"));
    assertEquals(false, board.IsMoveValid("09R09"));
    assertEquals(false, board.IsMoveValid("09R19"));

    assertEquals(true, board.IsMoveValid("12c62"));
    assertEquals(true, board.IsMoveValid("12c19"));
    assertEquals(false, board.IsMoveValid("12c01"));
    assertEquals(false, board.IsMoveValid("12c10"));

    assertEquals(true, board.IsMoveValid("77C70"));
    assertEquals(false, board.IsMoveValid("77C72"));
    assertEquals(true, board.IsMoveValid("77C73"));
    log.info(System.nanoTime() - time);
  }

  @Test
  void CheckedTestRed1() {
    board.setBoard(    
    "000C0k000" +
    "000000000" +
    "000000000" +
    "000000000" +
    "000000000" +
    "000000000" +
    "R00p00000" +
    "000000000" +
    "0000A000r" +
    "r00pK0000");

    assertEquals(true, board.IsMoveValid("30C39"));
    assertEquals(false, board.IsMoveValid("30C31"));
    assertEquals(false, board.IsMoveValid("30C32"));
    assertEquals(false, board.IsMoveValid("30C33"));
    assertEquals(false, board.IsMoveValid("30C34"));


    // King face king
    assertEquals(false, board.IsMoveValid("49K59"));
    // Rook project pawn
    assertEquals(false, board.IsMoveValid("49K39"));
    // Rook row 8 check K
    assertEquals(false, board.IsMoveValid("49K48"));

    assertEquals(false, board.IsMoveValid("06R09"));
    assertEquals(false, board.IsMoveValid("06R36"));

    assertEquals(true, board.IsMoveValid("48A39"));
    assertEquals(false, board.IsMoveValid("48A37"));
    assertEquals(false, board.IsMoveValid("48A59"));

    assertEquals(true, (
      "00000k000" +
      "000000000" +
      "000000000" +
      "000000000" +
      "000000000" +
      "000000000" +
      "R00p00000" +
      "000000000" +
      "0000A000r" +
      "r00CK0000").equals(board.UpdateBoard("30C39")));

    board.setBoard(    
      "000C0k000" +
      "000000000" +
      "000000000" +
      "000000000" +
      "000000000" +
      "000000000" +
      "R00p00000" +
      "000000000" +
      "0000A000r" +
      "r00pK0000");
    assertEquals(true, (
      "000C0k000" +
      "000000000" +
      "000000000" +
      "000000000" +
      "000000000" +
      "000000000" +
      "R00p00000" +
      "000000000" +
      "00000000r" +
      "r00AK0000").equals(board.UpdateBoard("48A39")));
  }

  @Test
  void CheckedTestBack1() {
    board.setBoard(    
    "000Cka000" +
    "00000P000" +
    "000000000" +
    "000000000" +
    "00e000000" +
    ///////////
    "000000000" +
    "0000Rp00c" +
    "000000000" +
    "00000000r" +
    "0000K0000");

    assertEquals(false, board.IsMoveValid("24e46"));
    assertEquals(true , board.IsMoveValid("24e42"));

    assertEquals(true , board.IsMoveValid("56p46"));
    assertEquals(false, board.IsMoveValid("56p57"));
    assertEquals(false, board.IsMoveValid("56p66"));

    assertEquals(true , board.IsMoveValid("86c46"));
    assertEquals(false, board.IsMoveValid("86c76"));
    assertEquals(false, board.IsMoveValid("86c66"));

    assertEquals(true , board.IsMoveValid("50a41"));
    assertEquals(false, board.IsMoveValid("50a61"));

    assertEquals(false, board.IsMoveValid("40k41"));
    assertEquals(false, board.IsMoveValid("40k50"));
    assertEquals(true , board.IsMoveValid("40k30"));
  }

  @Test
  void CheckedTestBack2() {
    board.setBoard(    
    "0000k0000" +
    "0000r0000" +
    "00000H000" +
    "000000000" +
    "000000000" +
    ///////////
    "000000000" +
    "0000C0000" +
    "000000000" +
    "00000000r" +
    "000K00000");

    assertEquals(true , board.IsMoveValid("41r51"));
    assertEquals(false, board.IsMoveValid("41r46"));
    assertEquals(false, board.IsMoveValid("41r45"));
    assertEquals(false, board.IsMoveValid("41r44"));

    assertEquals(true , board.IsMoveValid("40k50"));
    assertEquals(false , board.IsMoveValid("40k30"));
    assertEquals(false , board.IsMoveValid("40k41"));
  }

  @Test
  void TestResultSet() {
    TestResult(
    "0000k0000" +
    "00000P00R" +
    "000000000" +
    "000000000" +
    "c00000000" +
    ///////////
    "000000000" +
    "000000000" +
    "000000000" +
    "000000000" +
    "000K00000", Result.CONTINUE, true);

    TestResult(
      "0000k0000" +
      "00000P00R" +
      "000000000" +
      "000000000" +
      "000000000" +
      ///////////
      "000000000" +
      "000000000" +
      "000000000" +
      "000000000" +
      "000K00000", Result.DRAW, true);

    TestResult(
      "0000k0000" +
      "r0000P00R" +
      "000H00000" +
      "000000000" +
      "000000000" +
      ///////////
      "000000000" +
      "000000000" +
      "000000000" +
      "000000000" +
      "000K00000", Result.CONTINUE, true);

    TestResult(
      "0000k0000" +
      "00000P00R" +
      "000H00000" +
      "000000000" +
      "000000000" +
      ///////////
      "000000000" +
      "000000000" +
      "000000000" +
      "000000000" +
      "000K00000", Result.CONTINUE, true);

    TestResult(
      "0000k0000" +
      "00000P00R" +
      "000H00000" +
      "000000000" +
      "000000000" +
      ///////////
      "000000000" +
      "000000000" +
      "000000000" +
      "000C00000" +
      "000K00000", Result.RED_WIN, true);

    TestResult(
      "0000k0000" +
      "000000000" +
      "000000000" +
      "000000000" +
      "000000000" +
      ///////////
      "0R0000000" +
      "000000000" +
      "00H000000" +
      "0000A0000" +
      "cc00KA000", Result.BLACK_WIN, false);
  }

  void TestResult(String boardStr, Result result, boolean isRedTurn) {
    board.setBoard(boardStr);
    assertEquals(result, board.CheckResult(isRedTurn));
  }
}
