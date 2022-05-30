package com.XiangQi.XiangQiBE.Components;

import java.util.LinkedList;
import com.XiangQi.XiangQiBE.Common.Move;
import com.XiangQi.XiangQiBE.utils.LinkedListUtils;
import org.springframework.stereotype.Component;

@Component
public class Pawn extends Piece {
	public LinkedList<Move> generateMove(char[] board, int x, int y, boolean isRed) {
		LinkedList<Move> result = new LinkedList<>();
		boolean crossedTheRiver = isRed ? y < 5 : y > 4;

		if (crossedTheRiver) {
			LinkedListUtils.AddIfNotNull(result, generatePos(board, x + 1, y, isRed));
			LinkedListUtils.AddIfNotNull(result, generatePos(board, x - 1, y, isRed));
		}

		if (isRed) {
			LinkedListUtils.AddIfNotNull(result, generatePos(board, x, y - 1, isRed));
		} else {
			LinkedListUtils.AddIfNotNull(result, generatePos(board, x, y + 1, isRed));
		}

		return result;
	}
}
