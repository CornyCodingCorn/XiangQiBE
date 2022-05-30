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
	public boolean isSameColor(char[] info, boolean isRed, int x, int y) {
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

	public static char getPiece(char[] board, int x, int y) {
		if (!isPosValid(x, y))
			return ' ';
		return board[x + y * Board.BOARD_COL];
	}

	public static PieceType getPieceType(char[] board, int x, int y)  {
		return PieceType.valueOfIgnoreCase(getPieceChar(board, x, y));
	}

	public static char getPieceChar(char[] board, int x, int y) {
		return board[x + y * Board.BOARD_COL];
	}
	public static char setPieceChar(char[] board, int x, int y, char piece) {
		return board[x + y * Board.BOARD_COL] = piece;
	}

	public static boolean isPieceRed(char[] board, int x, int y) {
		char c = getPiece(board, x, y);
		return Character.isUpperCase(c);
	}

	public static boolean isPieceRed(char piece) {
		return Character.isUpperCase(piece);
	}

	//public static getPieceObject(board: string, x: number, y: number): Piece 
	// This implementation is so dumb
	public static Piece getPieceObject(char[] board, int index) {
		int posX = index % Board.BOARD_COL;
		int posY = (int)Math.floor(index / Board.BOARD_COL);
		PieceType type = getPieceType(board, posX, posY);

		try {
			Piece result = new Piece();
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

	public Piece getPieceObject(char[] board, int x, int y) {
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
		char[] board,
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
		char[] board,
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

  public LinkedList<Move> generateMove(char[] board, int x, int y, boolean isRed) {
    return null;
  }
}
