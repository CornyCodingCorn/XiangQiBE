package com.XiangQi.XiangQiBE.Components;

import com.XiangQi.XiangQiBE.utils.Vector2;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Component
public class Piece {
  @AllArgsConstructor
  public enum PieceType {
    King("k"),
    Advisor("a"),
    Elephant("e"),
    Rook("r"),
    Cannon("c"),
    Horse("h"),
    Pawn("p"),
    Empty("0");
  
    @Getter
    @Setter
    private String value;
  }

  PieceType type = PieceType.Empty;
	boolean isRed = false;
	Vector2 location = Vector2.create(0, 0);

	public boolean isSameColor(String piece, boolean isRed) {
    String charString = "";
    charString = piece;

		if (charString == PieceType.Empty.getValue()) 
      return false;

		if (isRed) {
			return charString.toUpperCase() == charString;
		} else {
			return charString.toLowerCase() == charString;
		}
  }
	//public isSameColor(board: string, isRed: boolean, x: number,	y: number,): boolean;
	public boolean isSameColor(String info, boolean isRed, int x, int y) {
		String charString = "";
		if (x != 0 || y != 0) 
      charString = info;
		else 
      charString = getPiece(info, x, y);

		if (charString == PieceType.Empty.getValue()) 
      return false;

		if (isRed) {
			return charString.toUpperCase() == charString;
		} else {
			return charString.toLowerCase() == charString;
		}
	}

	public static String getPiece(String board, int x, int y) {
		return String.valueOf(board.charAt(x + y * Board.BOARD_COL));
	}

	public static PieceType getPieceType(String board, int x, int y)  {
		String charString = String.valueOf(board.charAt(x + y * Board.BOARD_COL)).toLowerCase();
		return PieceType.valueOf(charString);
	}

	public static boolean isPieceRed(String board, int x, int y) {
		String charString = getPiece(board, x, y);
		return charString == charString.toUpperCase();
	}

	//public static getPieceObject(board: string, x: number, y: number): Piece 
	public static Piece getPieceObject(String board, int index) {
		int posX = index % Board.BOARD_COL;
		int posY = (int)Math.floor(index / Board.BOARD_COL);

		Piece result = new Piece();
		result.location.x = posX;
		result.location.y = posY;
		result.type = getPieceType(board, posX, posY);
		result.isRed = isPieceRed(board, posX, posY);

		return result;
	}

	public Piece getPieceObject(String board, int x, int y) {
		int posX = x;
		int posY = y;

		Piece result = new Piece();
		result.location.x = posX;
		result.location.y = posY;
		result.type = getPieceType(board, posX, posY);
		result.isRed = isPieceRed(board, posX, posY);

		return result;
	}

	public boolean isPosValid(int x, int y) {
		return (
			x >= 0 && x < Board.BOARD_COL && y >= 0 && y < Board.BOARD_ROW
		);
	}

	public String generatePos(
		String board,
		int x,
		int y,
		boolean isRed
	) {
		String result = "";
		String piece = getPiece(board, x, y);
		if (isPosValid(x, y) && !isSameColor(piece, isRed)) {
			result = "${x}${y}${piece}/";
		}

		return result;
	}

	public String generatePos(
		String board,
		int x,
		int y,
		boolean isRed,
		boolean forceReturn
	) {
		String result = "";
		String piece = getPiece(board, x, y);
		if (
			isPosValid(x, y) &&
			(!isSameColor(piece, isRed) || forceReturn)
		) {
			result = "${x}${y}${piece}/";
		}

		return result;
	}

	// Generate move until hit invalid or another piece
	public String generateGenericMove(
		String board,
		int x,
		int y,
		boolean isRed,
		int deltaX,
		int deltaY,
		boolean allowKill
	) {
		boolean isValid = true;
		boolean anotherPiece = false;
		String result = "";

		do {
			x += deltaX;
			y += deltaY;

			isValid = isPosValid(x, y);
			if (isValid) {
				String str = generatePos(board, x, y, isRed);
				anotherPiece = str == "" || String.valueOf(str.charAt(2)) != PieceType.Empty.getValue();
				if (String.valueOf(str.charAt(2)) != PieceType.Empty.getValue()) {
					if (allowKill) result += str;
				} else {
					result += str;
				}
			}
		} while (isValid && !anotherPiece);

		return result;
	}

  public String generateMove(String board, int x, int y, boolean isRed) {
    return "";
  }
}
