package com.XiangQi.XiangQiBE.Components;

import java.util.LinkedList;
import com.XiangQi.XiangQiBE.Common.Move;
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

	public Move generatePos(
		String board,
		int x,
		int y,
		boolean isRed
	) {
		Move move = null;
		char piece = getPiece(board, x, y);
		if (isPosValid(x, y) && !isSameColor(piece, isRed)) {
			move = Move.Create(x, y, piece);
		}

		return move;
	}

	// Generate move until hit invalid or another piece
	public LinkedList<Move> generateGenericMove(
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
		LinkedList<Move> moves = new LinkedList<Move>();

		do {
			x += deltaX;
			y += deltaY;

			isValid = isPosValid(x, y);
			if (isValid) {
				var move = generatePos(board, x, y, isRed);
				
				if (move == null)
					break;

				anotherPiece = move.getPiece() != PieceType.EMPTY.value;
				if (anotherPiece) {
					if (allowKill) moves.add(move);
				} else {
					moves.add(move);
				}
			}
		} while (isValid && !anotherPiece);

		return moves;
	}

  public LinkedList<Move> generateMove(String board, int x, int y, boolean isRed) {
    return null;
  }
}
