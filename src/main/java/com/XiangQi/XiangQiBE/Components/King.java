package com.XiangQi.XiangQiBE.Components;

import org.springframework.stereotype.Component;

@Component
public class King extends Piece {
  public String generateMove(String board, int x, int y, boolean isRed) {
		String result = "";
	
		int checkX = x - 1;
		if (checkX > 2) {
			result += generatePos(board, checkX, y, isRed);
		}
		checkX = x + 1;
		if (checkX < 6) {
			result += generatePos(board, checkX, y, isRed);
		}
	
		int checkY = y + 1;
		if (isRed || checkY < 3) {
			result += generatePos(board, x, checkY, isRed);
		}
		checkY = y - 1;
		if (!isRed || checkY > 6) {
			result += generatePos(board, x, checkY, isRed);
		}
	
		return result;
	}
}
