package com.XiangQi.XiangQiBE.Components;

import org.springframework.stereotype.Component;

@Component
public class Canon extends Piece {
	public String generateMove(String board, int x, int y, boolean isRed) {
		String result = "";

		result += _generateInDirection(board, x, y, isRed, -1, 0);
		result += _generateInDirection(board, x, y, isRed, +1, 0);
		result += _generateInDirection(board, x, y, isRed, 0, -1);
		result += _generateInDirection(board, x, y, isRed, 0, +1);

		return result;
	}

	private String _generateInDirection(String board, int x, int y, boolean isRed, int deltaX,
			int deltaY) {
		String result = "";
		result += generateGenericMove(board, x, y, isRed, deltaX, deltaY, false);

		String[] arr = result.split("/");
		String last = arr.length <= 0 ? null : arr[arr.length - 1];

		if (last != null && !last.equals("")) {
			x = Integer.parseInt(String.valueOf(last.charAt(0)));
			y = Integer.parseInt(String.valueOf(last.charAt(1)));
		}
		x += deltaX;
		y += deltaY;

		if (isPosValid(x, y)) {
			// If the next pos is valid that mean it was stopped by another piece.
			String jump = generateGenericMove(board, x, y, isRed, deltaX, deltaY, true);

			if (jump.equals(""))
				return result;

			arr = jump.substring(0, jump.length() - 1).split("/");
			last = arr[arr.length - 1];

			// If the last piece of the returned result is a
			// piece then add the last one to the result of this function.
			if (last.length() >= 3 && !String.valueOf(last.charAt(2)).equals(PieceType.Empty.getValue())) {
				result += last + "/";
			}
		}

		return result;
	}
}
