package com.XiangQi.XiangQiBE.Components;

import java.util.LinkedList;
import com.XiangQi.XiangQiBE.Common.Move;
import org.springframework.stereotype.Component;

@Component
public class Canon extends Piece {
	public LinkedList<Move> generateMove(char[] board, int x, int y, boolean isRed) {
		LinkedList<Move> result = new LinkedList<>();

		result.addAll(_generateInDirection(board, x, y, isRed, -1, 0));
		result.addAll(_generateInDirection(board, x, y, isRed, +1, 0));
		result.addAll(_generateInDirection(board, x, y, isRed, 0, -1));
		result.addAll(_generateInDirection(board, x, y, isRed, 0, +1));

		return result;
	}

	private LinkedList<Move> _generateInDirection(char[] board, int x, int y, boolean isRed, int deltaX,
			int deltaY) {
		LinkedList<Move> result = new LinkedList<>();
		result.addAll(generateGenericMove(board, x, y, isRed, deltaX, deltaY, false));

		Move last = null;
		if (!result.isEmpty()) {
			last = result.getLast();
			x = last.getX();
			y = last.getY();
		}
		x += deltaX;
		y += deltaY;

		if (isPosValid(x, y)) {
			// If the next pos is valid that mean it was stopped by another piece.
			LinkedList<Move> jump = generateGenericMove(board, x, y, isRed, deltaX, deltaY, true);

			if (jump.isEmpty())
				return result;

			last = jump.getLast();

			// If the last piece of the returned result is a
			// piece then add the last one to the result of this function.
			if (last.getPiece() != PieceType.EMPTY.getValue()) {
				result.add(last);
			}
		}

		return result;
	}
}
