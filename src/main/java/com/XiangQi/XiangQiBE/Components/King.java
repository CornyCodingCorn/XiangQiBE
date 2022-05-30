package com.XiangQi.XiangQiBE.Components;

import java.util.LinkedList;
import com.XiangQi.XiangQiBE.Common.Move;
import com.XiangQi.XiangQiBE.utils.LinkedListUtils;
import org.springframework.stereotype.Component;

@Component
public class King extends Piece {
	public LinkedList<Move> generateMove(char[] board, int x, int y, boolean isRed) {
		LinkedList<Move> result = new LinkedList<>();

		int checkX = x - 1;
		if (checkX > 2) {
			LinkedListUtils.AddIfNotNull(result, generatePos(board, checkX, y, isRed));
		}
		checkX = x + 1;
		if (checkX < 6) {
			LinkedListUtils.AddIfNotNull(result, generatePos(board, checkX, y, isRed));
		}

		int checkY = y + 1;
		if (isRed || checkY < 3) {
			LinkedListUtils.AddIfNotNull(result, generatePos(board, x, checkY, isRed));
		}
		checkY = y - 1;
		if (!isRed || checkY > 6) {
			LinkedListUtils.AddIfNotNull(result, generatePos(board, x, checkY, isRed));
		}

		return result;
	}
}
