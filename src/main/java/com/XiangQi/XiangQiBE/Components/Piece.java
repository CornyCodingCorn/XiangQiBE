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
    KING('k'),
    ADVISOR('a'),
    ELEPHANT('e'),
    ROOK('r'),
    CANON('c'),
    HORSE('h'),
    PAWN('p'),
    EMPTY('0');
  
    @Getter
    @Setter
    private char value;

		public static PieceType valueOfIgnoreCase(char value) {
			value = Character.toLowerCase(value);
			for (PieceType type : PieceType.values()) {
				if (type.value == value) {
						return type;
				}
			}
			throw new IllegalArgumentException("No piece with type " + value + " found");
		}

		public boolean compareIgnoreCase(char value) {
			return Character.toLowerCase(value) == this.value;
		}
  }

  PieceType type = PieceType.EMPTY;
	boolean isRed = false;
	Vector2 location = Vector2.create(0, 0);

	public boolean isSameColor(char piece, boolean isRed) {
		if (piece == PieceType.EMPTY.value) 
      return false;

		if (isRed) {
			return Character.isUpperCase(piece);
		} else {
			return Character.isLowerCase(piece);
		}
  }
	//public isSameColor(board: string, isRed: boolean, x: number,	y: number,): boolean;
	public boolean isSameColor(String info, boolean isRed, int x, int y) {
		char piece = PieceType.EMPTY.value;
		if (x != 0 || y != 0) 
      return false;
		else 
      piece = getPiece(info, x, y);

		if (piece == PieceType.EMPTY.value) 
      return false;

		if (isRed) {
			return Character.isUpperCase(piece);
		} else {
			return Character.isLowerCase(piece);
		}
	}

	public static char getPiece(String board, int x, int y) {
		if (!isPosValid(x, y))
			return ' ';
		return board.charAt(x + y * Board.BOARD_COL);
	}

	public static PieceType getPieceType(String board, int x, int y)  {
		return PieceType.valueOfIgnoreCase(board.charAt(x + y * Board.BOARD_COL));
	}

	public static boolean isPieceRed(String board, int x, int y) {
		char c = getPiece(board, x, y);
		return Character.isUpperCase(c);
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
		char piece = getPiece(board, x, y);
		if (isPosValid(x, y) && !isSameColor(piece, isRed)) {
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

				anotherPiece = str.charAt(2) != PieceType.EMPTY.value;
				if (anotherPiece) {
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
