package com.XiangQi.XiangQiBE.Components.Board;

import org.springframework.stereotype.Component;

@Component
public class Advisor extends Piece {
	public String generateMove(String board, int x, int y, boolean isRed) {
		String result = "";

		if ((!isRed || y - 1 > 6) && x - 1 > 2)
			result += generatePos(board, x - 1, y - 1, isRed);
		if ((!isRed || y - 1 > 6) && x + 1 < 6)
			result += generatePos(board, x + 1, y - 1, isRed);
		if ((isRed || y + 1 < 3) && x - 1 > 2)
			result += generatePos(board, x - 1, y + 1, isRed);
		if ((isRed || y + 1 < 3) && x + 1 < 6)
			result += generatePos(board, x + 1, y + 1, isRed);

		return result;
	}
}
