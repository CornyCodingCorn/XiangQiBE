package com.XiangQi.XiangQiBE.Components;

import org.springframework.stereotype.Component;

@Component
public class Elephant extends Piece {
	public String generateMove(String board, int x, int y, boolean isRed) {
		String result = "";

		if ((!isRed || y - 2 > 4) && getPiece(board, x - 1, y - 1).equals(PieceType.Empty.getValue()))
			result += generatePos(board, x - 2, y - 2, isRed);
		if ((!isRed || y - 2 > 4) && getPiece(board, x + 1, y - 1).equals(PieceType.Empty.getValue()))
			result += generatePos(board, x + 2, y - 2, isRed);
		if ((isRed || y + 2 < 5) && getPiece(board, x - 1, y + 1).equals(PieceType.Empty.getValue()))
			result += generatePos(board, x - 2, y + 2, isRed);
		if ((isRed || y + 2 < 5) && getPiece(board, x + 1, y + 1).equals(PieceType.Empty.getValue()))
			result += generatePos(board, x + 2, y + 2, isRed);

		return result;
	}
}
