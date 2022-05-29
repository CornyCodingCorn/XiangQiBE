package com.XiangQi.XiangQiBE.Components;

import java.util.LinkedList;
import com.XiangQi.XiangQiBE.Common.Move;
import com.XiangQi.XiangQiBE.utils.LinkedListUtils;
import org.springframework.stereotype.Component;

@Component
public class Advisor extends Piece {
	public LinkedList<Move> generateMove(String board, int x, int y, boolean isRed) {
		LinkedList<Move> result = new LinkedList<>();

		if ((!isRed || y - 1 > 6) && x - 1 > 2)
			LinkedListUtils.AddIfNotNull(result, generatePos(board, x - 1, y - 1, isRed));
		if ((!isRed || y - 1 > 6) && x + 1 < 6)
			LinkedListUtils.AddIfNotNull(result, generatePos(board, x + 1, y - 1, isRed));
		if ((isRed || y + 1 < 3) && x - 1 > 2)
			LinkedListUtils.AddIfNotNull(result, generatePos(board, x - 1, y + 1, isRed));
		if ((isRed || y + 1 < 3) && x + 1 < 6)
			LinkedListUtils.AddIfNotNull(result, generatePos(board, x + 1, y + 1, isRed));

		return result;
	}
}
