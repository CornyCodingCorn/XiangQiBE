package com.XiangQi.XiangQiBE.Components.Board;

import org.springframework.stereotype.Component;

@Component
public class Rook extends Piece {
	public String generateMove(String board, int x, int y, boolean isRed) {
		String result = "";

		result += generateGenericMove(board, x, y, isRed, -1, 0, true);
		result += generateGenericMove(board, x, y, isRed, 1, 0, true);
		result += generateGenericMove(board, x, y, isRed, 0, -1, true);
		result += generateGenericMove(board, x, y, isRed, 0, 1, true);

		return result;
	}
}
