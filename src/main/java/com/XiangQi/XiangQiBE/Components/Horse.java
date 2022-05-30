package com.XiangQi.XiangQiBE.Components;

import java.util.LinkedList;
import com.XiangQi.XiangQiBE.Common.Move;
import com.XiangQi.XiangQiBE.utils.LinkedListUtils;
import org.springframework.stereotype.Component;

@Component
public class Horse extends Piece {
	public LinkedList<Move> generateMove(char[] board, int x, int y, boolean isRed) {
		LinkedList<Move> result = new LinkedList<>();

		if (isValid(board, x, y + 1)) {
			LinkedListUtils.AddIfNotNull(result, generatePos(board, x - 1, y + 2, isRed));
			LinkedListUtils.AddIfNotNull(result, generatePos(board, x + 1, y + 2, isRed));
		}

		if (isValid(board, x, y - 1)) {
			LinkedListUtils.AddIfNotNull(result, generatePos(board, x - 1, y - 2, isRed));
			LinkedListUtils.AddIfNotNull(result, generatePos(board, x + 1, y - 2, isRed));
		}

		if (isValid(board, x + 1, y)) {
			LinkedListUtils.AddIfNotNull(result, generatePos(board, x + 2, y - 1, isRed));
			LinkedListUtils.AddIfNotNull(result, generatePos(board, x + 2, y + 1, isRed));
		}

		if (isValid(board, x - 1, y)) {
			LinkedListUtils.AddIfNotNull(result, generatePos(board, x - 2, y - 1, isRed));
			LinkedListUtils.AddIfNotNull(result, generatePos(board, x - 2, y + 1, isRed));
		}

		return result;
	}

	private boolean isValid(char[] board, int x, int y) {
		return PieceType.EMPTY.compareIgnoreCase(getPiece(board, x, y));
	}
}
