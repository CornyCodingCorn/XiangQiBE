package com.XiangQi.XiangQiBE.Components;

import java.util.LinkedList;
import com.XiangQi.XiangQiBE.Common.Move;
import com.XiangQi.XiangQiBE.utils.LinkedListUtils;
import org.springframework.stereotype.Component;

@Component
public class Elephant extends Piece {
	public LinkedList<Move> generateMove(char[] board, int x, int y, boolean isRed) {
		LinkedList<Move> result = new LinkedList<>();

		if ((!isRed || y - 2 > 4) && getPiece(board, x - 1, y - 1) == PieceType.EMPTY.getValue())
			LinkedListUtils.AddIfNotNull(result, generatePos(board, x - 2, y - 2, isRed));
		if ((!isRed || y - 2 > 4) && getPiece(board, x + 1, y - 1) == PieceType.EMPTY.getValue())
			LinkedListUtils.AddIfNotNull(result, generatePos(board, x + 2, y - 2, isRed));
		if ((isRed || y + 2 < 5)  && getPiece(board, x - 1, y + 1) == PieceType.EMPTY.getValue())
			LinkedListUtils.AddIfNotNull(result, generatePos(board, x - 2, y + 2, isRed));
		if ((isRed || y + 2 < 5)  && getPiece(board, x + 1, y + 1) == PieceType.EMPTY.getValue())
			LinkedListUtils.AddIfNotNull(result, generatePos(board, x + 2, y + 2, isRed));

		return result;
	}
}
