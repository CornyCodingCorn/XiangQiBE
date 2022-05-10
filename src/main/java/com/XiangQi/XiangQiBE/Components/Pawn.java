package com.XiangQi.XiangQiBE.Components;

import org.springframework.stereotype.Component;

@Component
public class Pawn extends Piece {
	public String generateMove(String board, int x, int y, boolean isRed) {
		String result = "";
		boolean crossedTheRiver = (isRed && y < 5) || (!isRed && y > 4);

		if (crossedTheRiver) {
			result += generatePos(board, x + 1, y, isRed);
			result += generatePos(board, x - 1, y, isRed);
		}

		if (isRed) {
			result += generatePos(board, x, y - 1, isRed);
		} else {
			result += generatePos(board, x, y + 1, isRed);
		}

		return result;
	}
}
