package com.XiangQi.XiangQiBE.Components.Board;

import com.XiangQi.XiangQiBE.utils.Vector2;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Component
public class Piece {
  @AllArgsConstructor
  public enum PieceType {
    KING("k"),
    ADVISOR("a"),
    ELEPHANT("e"),
    ROOK("r"),
    CANON("c"),
    HORSE("h"),
    PAWN("p"),
    EMPTY("0");
  
    @Getter
    @Setter
    private String value;

		public static PieceType fromCharString(String value) {
			for (PieceType type : PieceType.values()) {
				if (type.value.equalsIgnoreCase(value)) {
						return type;
				}
			}
			throw new IllegalArgumentException("No piece with type " + value + " found");
		}
  }

  PieceType type = PieceType.EMPTY;
	boolean isRed = false;
	Vector2 location = Vector2.create(0, 0);

	public boolean isSameColor(String piece, boolean isRed) {
    String charString = "";
    charString = piece;

		if (charString.equals(PieceType.EMPTY.getValue())) 
      return false;

		if (isRed) {
			return charString.toUpperCase().equals(charString);
		} else {
			return charString.toLowerCase().equals(charString);
		}
  }
	//public isSameColor(board: string, isRed: boolean, x: number,	y: number,): boolean;
	public boolean isSameColor(String info, boolean isRed, int x, int y) {
		String charString = "";
		if (x != 0 || y != 0) 
      charString = info;
		else 
      charString = getPiece(info, x, y);

		if (charString.equals(PieceType.EMPTY.getValue())) 
      return false;

		if (isRed) {
			return charString.toUpperCase().equals(charString);
		} else {
			return charString.toLowerCase().equals(charString);
		}
	}

	public static String getPiece(String board, int x, int y) {
		if (!isPosValid(x, y))
			return "";
		return String.valueOf(board.charAt(x + y * Board.BOARD_COL));
	}

	public static PieceType getPieceType(String board, int x, int y)  {
		String charString = String.valueOf(board.charAt(x + y * Board.BOARD_COL)).toLowerCase();
		return PieceType.fromCharString(charString);
	}

	public static boolean isPieceRed(String board, int x, int y) {
		String charString = getPiece(board, x, y);
		return charString.equals(charString.toUpperCase());
	}

	public static boolean isPieceRed(String piece) {
		return piece.equals(piece.toUpperCase());
	}

	//public static getPieceObject(board: string, x: number, y: number): Piece 
	public static <T extends Piece> T getPieceObject(String board, int index, Class<T> pieceClass) {
		int posX = index % Board.BOARD_COL;
		int posY = (int)Math.floor(index / Board.BOARD_COL);
		PieceType type = getPieceType(board, posX, posY);

		try {
			T result = pieceClass.getConstructor().newInstance();
			result.location.x = posX;
			result.location.y = posY;
			result.type = type;
			result.isRed = isPieceRed(board, posX, posY);
	
			return result;
		}
		catch (Exception e) {
			return null;
		}
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

	public static boolean isPosValid(int x, int y) {
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
			result = String.valueOf(x) + String.valueOf(y) + piece + "/";
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
		if (isPosValid(x, y) && (!isSameColor(piece, isRed) || forceReturn)) {
			result = String.valueOf(x) + String.valueOf(y) + piece + "/";
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
				
				if (str.equals(""))
					break;

				anotherPiece = str.equals("") || !String.valueOf(str.charAt(2)).equals(PieceType.EMPTY.getValue());
				if (!String.valueOf(str.charAt(2)).equals(PieceType.EMPTY.getValue())) {
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
