package com.XiangQi.XiangQiBE.Components;

import org.springframework.stereotype.Component;

@Component
public class Horse extends Piece {
  public String generateMove(String board, int x, int y, boolean isRed) {
		String result = "";

		if (isValid(board, x, y + 1)) {
			result += generatePos(board, x - 1, y + 2, isRed);
			result += generatePos(board, x + 1, y + 2, isRed);
		}
	
		if (isValid(board, x, y - 1)) {
			result += generatePos(board, x - 1, y - 2, isRed);
			result += generatePos(board, x + 1, y - 2, isRed);
		}
	
		if (isValid(board, x + 1, y)) {
			result += generatePos(board, x + 2, y - 1, isRed);
			result += generatePos(board, x + 2, y + 1, isRed);
		}
	
		if (isValid(board, x - 1, y)) {
			result += generatePos(board, x - 2, y - 1, isRed);
			result += generatePos(board, x - 2, y + 1, isRed);
		}
	
		return result;
	}
	
	private boolean isValid(String board, int x, int y) {
		String charString = getPiece(board, x, y);
		return charString == PieceType.Empty.getValue();
	}
}
