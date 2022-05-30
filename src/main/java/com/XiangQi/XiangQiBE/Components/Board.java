package com.XiangQi.XiangQiBE.Components;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import com.XiangQi.XiangQiBE.Common.Move;
import com.XiangQi.XiangQiBE.Common.PieceMove;
import com.XiangQi.XiangQiBE.Components.Piece.PieceType;
import com.XiangQi.XiangQiBE.utils.Vector2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Component
public class Board {
	@AllArgsConstructor
	public enum Result {
		CONTINUE(0), RED_WIN(1), BLACK_WIN(2), DRAW(3);

		@Getter
		@Setter
		private int value;
	}

	@Autowired
	private Advisor advisor;
	@Autowired
	private Canon canon;
	@Autowired
	private Elephant elephant;
	@Autowired
	private Horse horse;
	@Autowired
	private King king;
	@Autowired
	private Pawn pawn;
	@Autowired
	private Rook rook;

	public static final int BOARD_COL = 9;
	public static final int BOARD_ROW = 10;

	public Vector<Piece> redPieces = new Vector<>(16);
	public Vector<Piece> blackPieces = new Vector<>(16);
	public Piece redKing = null;
	public Piece blackKing = null;
	protected char[] _board = null;

	public void setBoard(char[] board) {
		_board = board.clone();
		this._findAllPieces();
	}

	public void setBoard(String board) {
		this._board = board.toCharArray();
		this._findAllPieces();
	}

	public String getBoardAsString() {
		return String.valueOf(_board);
	}

	public char[] getBoard() {
		return _board;
	}

	public boolean IsMoveValid(String moveStr) {
		PieceMove moveObj = null;
		try {
			moveObj = PieceMove.Parse(moveStr);
		} catch (Exception e) {
			return false;
		} catch (Error e) {
			return false;
		}

		// If crash here that mean that it's a server's error
		int x = moveObj.newX;
		int y = moveObj.newY;
		// Empty piece can't move : | dunno what else to say.
		if (PieceType.EMPTY.compareIgnoreCase(moveObj.piece)) {
			return false;
		}

		LinkedList<Move> possibleMoves = generateMove(moveObj.oldX, moveObj.oldY);
		if (possibleMoves.isEmpty()) {
			return false;
		}

		for (var move : possibleMoves) {
			if (move.getX() == x && move.getY() == y) {
				return true;
			}
		}

		return false;
	}

	public String UpdateBoard(String move) {
		// Should be able to parse without problem due to the method that will be called before this
		// it also need to parse and if it failed then this function won't be called
		var moveObj = PieceMove.Parse(move);

		// Remove the piece that is in the same pos;
		getPieceAt(moveObj.newX, moveObj.newY, false);

		// Update pieces
		setPieceAt(moveObj.oldX, moveObj.oldY, moveObj.newX, moveObj.newY);

		// This is a really dumb way to do this, why did I do this?
		// _board = StringUtils.replaceCharAt(_board, PieceType.EMPTY.getValue(),
		// moveObj.oldX + moveObj.oldY * BOARD_COL);
		// _board = StringUtils.replaceCharAt(_board, move.charAt(2),
		// moveObj.newX + moveObj.newY * BOARD_COL);


		return String.valueOf(_board);
	}

	// Check after the player execute the move what is the result if the board.
	public Result CheckResult(boolean isRedTurn) {
		// Get other player king and check for move if have more than one move then Continue
		Piece otherKing = _getKing(!isRedTurn);
		LinkedList<Move> moves = generateMove(otherKing.location.x, otherKing.location.y);

		// If move is not empty then continue
		if (!moves.isEmpty()) {
			return Result.CONTINUE;
		}

		Vector<Piece> enemiesPieces = _getPieces(!isRedTurn);
		for (var piece : enemiesPieces) {
			LinkedList<Move> possibleMoves = generateMove(piece.location.x, piece.location.y);
			if (!possibleMoves.isEmpty()) {
				// If have one piece that is movable on next turn then continue
				return Result.CONTINUE;
			}
		}

		// If no piece is movable on next turn then if the king is checked then Win otherwise Draw
		// Get info of the side of the player of this turn then check if the king of other player is
		// checked.
		if (isKingChecked(!isRedTurn, null)) {
			return isRedTurn ? Result.RED_WIN : Result.BLACK_WIN;
		}

		return Result.DRAW;
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param removeIt if true then will remove the found piece unless it's a king
	 * @return
	 */
	public Piece getPieceAt(int x, int y, boolean removeIt) {
		char piece = Piece.getPieceChar(_board, x, y);
		boolean isRed = Character.isUpperCase(piece);

		if (removeIt)
			this._board[x + y * BOARD_COL] = PieceType.EMPTY.getValue();

		
		if (PieceType.KING.compareIgnoreCase(piece)) {
			return _getKing(isRed);
		}

		Vector<Piece> pieceList = isRed ? redPieces : blackPieces;
		for (int i = 0; i < pieceList.size(); i++) {
			var iPiece = pieceList.get(i);
			if (iPiece.location.isEqual(Vector2.create(x, y))) {
				if (removeIt) {
					pieceList.remove(i);
				}
				return iPiece;
			}
		}

		return null;
	}
	public void setPieceAt(int x, int y, int newX, int newY) {
		char piece = Piece.getPieceChar(_board, x, y);
		boolean isRed = Character.isUpperCase(piece);
		Vector<Piece> pieceList = isRed ? redPieces : blackPieces;

		if (pieceList != null) {
			for (int i = 0; i < pieceList.size(); i++) {
				var iPiece = pieceList.get(i);
				if (iPiece.location.x == x && iPiece.location.y == y) {
					iPiece.location.x = newX;
					iPiece.location.y = newY;

					Piece.setPieceChar(_board, x, y, PieceType.EMPTY.getValue());
					Piece.setPieceChar(_board, newX, newY, iPiece.isRed ? Character.toUpperCase(iPiece.type.getValue()) : iPiece.type.getValue());
					break;
				}
			}
		}
	}

	private void _findAllPieces() {
		this._removeAllPieces();

		for (int i = 0; i < this._board.length; i++) {
			final char pieceChar = this._board[i];
			if (pieceChar == PieceType.EMPTY.getValue()) continue; 

			final PieceType type = PieceType.valueOfIgnoreCase(pieceChar);
			Piece piece = Piece.getPieceObject(this._board, i);
			if (Character.isUpperCase(pieceChar)) {
				redPieces.add(piece);
				if (type == PieceType.KING) redKing = piece;
			} else {
				blackPieces.add(piece);
				if (type == PieceType.KING) blackKing = piece;
			}
		}
	}

	private void _removeAllPieces() {
		blackPieces.clear();
		redPieces.clear();
		blackKing = null;
		redKing = null;
	}

	private Piece _getKing(boolean isRed) {
		return isRed ? redKing : blackKing;
	}

	private Vector<Piece> _getPieces(boolean isRed) {
		return isRed ? redPieces : blackPieces;
	}

	public LinkedList<Move> generateRawMove(PieceType type, int x, int y, boolean isRed,
			char[] board) {
		// let func: generateMoveFunc | null = null;
		char[] boardPos;
		if (board == null)
			boardPos = getBoard();
		else
			boardPos = board;

		LinkedList<Move> result;

		switch (type) {
			case KING:
				result = king.generateMove(boardPos, x, y, isRed);
				break;
			case ADVISOR:
				result = advisor.generateMove(boardPos, x, y, isRed);
				break;
			case ELEPHANT:
				result = elephant.generateMove(boardPos, x, y, isRed);
				break;
			case HORSE:
				result = horse.generateMove(boardPos, x, y, isRed);
				break;
			case ROOK:
				result = rook.generateMove(boardPos, x, y, isRed);
				break;
			case CANON:
				result = canon.generateMove(boardPos, x, y, isRed);
				break;
			case PAWN:
				result = pawn.generateMove(boardPos, x, y, isRed);
				break;
			default:
				result = new LinkedList<>();
				break;
		}

		return result;
		// return func ? func(board || this.getInstance().getBoard(), x, y, isRed) : "";
	}

	public LinkedList<Move> generateMove(int x, int y) {
		char pieceChar = Piece.getPieceChar(_board, x, y);
		PieceType type = PieceType.valueOfIgnoreCase(pieceChar);
		if (type == PieceType.EMPTY)
			return new LinkedList<>();

		boolean isRed = Character.isUpperCase(pieceChar);
		LinkedList<Move> rawMove = generateRawMove(type, x, y, isRed, null);
		if (rawMove.isEmpty())
			return rawMove;
		PieceMove pieceMove = new PieceMove();
		pieceMove.oldX = x;
		pieceMove.oldY = y;
		pieceMove.piece = pieceChar;

		// Filter out moves
		for (var it = rawMove.iterator(); it.hasNext();) {
			var move = it.next();
			pieceMove.newX = move.getX();
			pieceMove.newY = move.getY();

			if (isKingChecked(isRed, pieceMove)) {
				it.remove();
			} else {
				move.setPiece(type.getValue());
				move.setRed(isRed);
			}
		}

		return rawMove;
	}

	/**
	 * If return something other than "" then the king is checked
	 * 
	 * @param isRed Whose king we are checking
	 * @param move The character is of no importance because the method will only use new xy and old xy
	 * @returns
	 */
	public boolean isKingChecked(boolean isRed, PieceMove move) {
		// This will be now base on the current state of the board and change base in setting without
		// changing the board itself.

		// This is to make sure that we don't have to copy the board over and over again but instead
		// Store the changed value because we already have the mean to restore it in PieceMove
		char[] boardStr = _board;
		int fillX = -1;
		int fillY = -1;
		char replacedPiece = ' ';
		char movePiece = ' ';

		Piece king = _getKing(!isRed);
		Piece ourKing = _getKing(isRed);
		Vector<Piece> enemies = _getPieces(!isRed);
		// Has to do this because we can't really change the value of the king location.
		int ourKingX = ourKing.location.x;
		int ourKingY = ourKing.location.y;

		if (move != null) {
			// If asked to fill in then it will fill in the boardStr.
			fillX = move.newX;
			fillY = move.newY;

			// Store the moving piece.
			movePiece = Piece.getPiece(boardStr, move.oldX, move.oldY);
			Piece.setPieceChar(boardStr, move.oldX, move.oldY, PieceType.EMPTY.getValue());
			// Store the piece that the moving piece will replace
			replacedPiece = Piece.getPiece(boardStr, fillX, fillY);
			Piece.setPieceChar(boardStr, fillX, fillY, move.piece);

			// Only need to check if it is king or not because isRed is calculated from the piece it self
			if (PieceType.KING.compareIgnoreCase(movePiece)) {
				// Change the local king location, don't need to change other king loc because that can't
				// happen
				ourKingX = fillX;
				ourKingY = fillY;
			}
		}

		// To make sure that we restore the board before moving on
		try {
			// Check if the king is seeing each other :))
			if (ourKingX == king.location.x) {
				int startIdx = Math.min(ourKingY, king.location.y) + 1;
				int endIdx = Math.max(ourKingY, king.location.y) - 1;

				boolean blocked = false;
				for (int i = startIdx; i < endIdx; i++) {
					PieceType piece = Piece.getPieceType(boardStr, king.location.x, i);
					if (piece != PieceType.EMPTY) {
						blocked = true;
						break;
					}
				}

				if (!blocked)
					return true;
			}

			for (int i = 0; i < enemies.size(); i++) {
				var value = enemies.elementAt(i);
				// If the enemy has the same x and y as the fill in piece then it mean that the enemy is
				// going
				// to be killed
				if (value.location.x == fillX && value.location.y == fillY)
					continue;

				// Only need to check base on raw move
				LinkedList<Move> enemyMoves =
						generateRawMove(value.type, value.location.x, value.location.y, value.isRed, boardStr);

				for (var enemyMove : enemyMoves) {
					if (PieceType.KING.getValue() == enemyMove.getPiece())
						return true;
				}
			}
		} finally {
			// Restore the _board.
			if (replacedPiece != ' ') {
				Piece.setPieceChar(boardStr, move.oldX, move.oldY, movePiece);
				Piece.setPieceChar(boardStr, fillX, fillY, replacedPiece);
			}
		}

		return false;
	}
}
