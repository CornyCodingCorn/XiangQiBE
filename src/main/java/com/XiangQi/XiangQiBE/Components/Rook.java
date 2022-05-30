package com.XiangQi.XiangQiBE.Components;

import java.util.LinkedList;
import com.XiangQi.XiangQiBE.Common.Move;
import org.springframework.stereotype.Component;

@Component
public class Rook extends Piece {
	public LinkedList<Move> generateMove(char[] board, int x, int y, boolean isRed) {
		LinkedList<Move> result = new LinkedList<>();

		result.addAll(generateGenericMove(board, x, y, isRed, -1, 0, true));
		result.addAll(generateGenericMove(board, x, y, isRed, 1, 0, true));
		result.addAll(generateGenericMove(board, x, y, isRed, 0, -1, true));
		result.addAll(generateGenericMove(board, x, y, isRed, 0, 1, true));

		return result;
	}
}
