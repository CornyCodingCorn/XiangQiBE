package com.XiangQi.XiangQiBE.Components;

import java.util.LinkedList;
import java.util.Vector;
import com.XiangQi.XiangQiBE.Common.Move;
import com.XiangQi.XiangQiBE.Common.PieceMove;
import com.XiangQi.XiangQiBE.Components.Piece.PieceType;
import com.XiangQi.XiangQiBE.utils.StringUtils;
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

	public Vector<Piece> horses = new Vector<>();
	public Vector<Piece> canons = new Vector<>();
	public Vector<Piece> advisors = new Vector<>();
	public Vector<Piece> elephants = new Vector<>();
	public Vector<Piece> pawns = new Vector<>();
	public Vector<Piece> rooks = new Vector<>();

	public Piece blackKing = new Piece();
	public Piece redKing = new Piece();

	protected String _board = null;

	public void setBoard(String _board) {
		this._board = _board;
		this._findAllPieces();
	}

	public String getBoard() {
		return _board;
	}

	public boolean IsMoveValid(String moveStr) {
		PieceMove moveObj = null;
		try {
			moveObj = PieceMove.Parse(moveStr);
		}
		catch (Exception e) {
			return false;
		} 
		catch (Error e) {
			return false;
		}

		// If crash here that mean that it's a server's error
		int x = moveObj.newX;
		int y = moveObj.newY;
		// Empty piece can't move : | dunno what else to say.
		if (PieceType.EMPTY.compareIgnoreCase(moveObj.piece)) {
			return false;
		}

		LinkedList<Move> possibleMoves = generateMove(moveObj.oldX, moveObj.oldY, moveObj.isRed());
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

		// Update pieces
		var piece = getPieceAt(moveObj.oldX, moveObj.oldY, false);
		piece.location.x = moveObj.newX;
		piece.location.y = moveObj.newY;

		// This is a really dumb way to do this, why did I do this?
		_board = StringUtils.replaceCharAt(_board, PieceType.EMPTY.getValue(),
				moveObj.oldX + moveObj.oldY * BOARD_COL);
		_board = StringUtils.replaceCharAt(_board, move.charAt(2),
				moveObj.newX + moveObj.newY * BOARD_COL);

		return _board;
	}

	public Result CheckResult(boolean isRedTurn) {
		// Get other player king and check for move if have more than one move then Continue
		Piece otherKing = isRedTurn ? blackKing : redKing;
		LinkedList<Move> moves = generateMove(otherKing.location.x, otherKing.location.y, !isRedTurn);

		// If move is not empty then continue
		if (!moves.isEmpty()) {
			return Result.CONTINUE;
		}

		Board otherSide = getInfoOfOneSide(otherKing.isRed);
		Vector<Piece> otherPieces = new Vector<Piece>();
		otherPieces.addAll(otherSide.rooks);
		otherPieces.addAll(otherSide.advisors);
		otherPieces.addAll(otherSide.canons);
		otherPieces.addAll(otherSide.elephants);
		otherPieces.addAll(otherSide.horses);
		otherPieces.addAll(otherSide.pawns);

		for (var otherPiece : otherPieces) {
			LinkedList<Move> possibleMoves = generateMove(otherPiece.location.x, otherPiece.location.y, otherPiece.isRed);
			if (!possibleMoves.isEmpty()) {
				// If have one piece that is movable on next turn then continue
				return Result.CONTINUE;
			}
		}

		// If no piece is movable on next turn then if the king is checked then Win otherwise Draw
		// Get info of the side of the player of this turn then check if the king of other player is
		// checked. 
		if (isKingChecked(getInfoOfOneSide(isRedTurn), otherKing.isRed, null, false)) {
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
		PieceType piece = PieceType.valueOfIgnoreCase(this._board.charAt(x + y * BOARD_COL));
		if (removeIt) this._board = StringUtils.replaceCharAt(this._board, PieceType.EMPTY.getValue(), x + y * BOARD_COL);

		Vector<Piece> pieceList = null;
		switch (piece) {
			case ROOK:
				pieceList = rooks;
				break;
			case HORSE:
				pieceList = horses;
				break;
			case ELEPHANT:
				pieceList = elephants;
				break;
			case ADVISOR:
				pieceList = advisors;
				break;
			case KING:
				Piece king = null;
				if (this.blackKing.location.isEqual(Vector2.create(x, y))) {
					king = this.blackKing;
				} else {
					king = this.redKing;
				}
				return king;
			case PAWN:
				pieceList = pawns;
				break;
			case CANON:
				pieceList = canons;
				break;
			case EMPTY:
				break;
		}
		
		if (pieceList != null) {
			for (int i = 0; i < pieceList.size(); i++) {
				var iPiece = pieceList.get(i);
				if (iPiece.location.isEqual(Vector2.create(x, y))) {
					if (removeIt) pieceList.remove(i);
					return iPiece;
				}
			}
		}

		return null;
	}

	public Board getInfoOfOneSide(boolean isRed) {
		Board board = new Board();

		board.horses = this._extractPieceByType(this.horses, isRed);
		board.rooks = this._extractPieceByType(this.rooks, isRed);
		board.pawns = this._extractPieceByType(this.pawns, isRed);
		board.canons = this._extractPieceByType(this.canons, isRed);
		board.advisors = this._extractPieceByType(this.advisors, isRed);
		board.elephants = this._extractPieceByType(this.elephants, isRed);

		board.blackKing = this.blackKing;
		board.redKing = this.redKing;

		board._board = this._board;

		return board;
	}

	private Vector<Piece> _extractPieceByType(Vector<Piece> pieces, boolean isRed) {
		Vector<Piece> result = new Vector<>();

		for (Piece value : pieces) {
			if (value.isRed == isRed)
				result.add(value);
		}

		return result;
	}

	private void _findAllPieces() {
		this._removeAllPieces();

		for (int i = 0; i < this._board.length(); i++) {
			final char pieceChar = this._board.charAt(i);
			PieceType type = PieceType.valueOfIgnoreCase(pieceChar);

			// Ignore empty and same color
			if (type == PieceType.EMPTY)
				continue;

			switch (type) {
				case CANON:
					this.canons.add(Piece.getPieceObject(this._board, i, Canon.class));
					break;
				case HORSE:
					this.horses.add(Piece.getPieceObject(this._board, i, Horse.class));
					break;
				case ROOK:
					this.rooks.add(Piece.getPieceObject(this._board, i, Rook.class));
					break;
				case PAWN:
					this.pawns.add(Piece.getPieceObject(this._board, i, Pawn.class));
					break;
				case ADVISOR:
					this.advisors.add(Piece.getPieceObject(this._board, i, Advisor.class));
					break;
				case ELEPHANT:
					this.elephants.add(Piece.getPieceObject(this._board, i, Elephant.class));
					break;
				case KING:
					var piece = Piece.getPieceObject(this._board, i, King.class);
					if (Character.isLowerCase(pieceChar)) {
						this.blackKing = piece;
					} else {
						this.redKing = piece;
					}
					break;
				default:
					break;
			}
		}
	}

	private void _removeAllPieces() {
		this.horses.clear();
		this.canons.clear();
		this.advisors.clear();
		this.elephants.clear();
		this.pawns.clear();
		this.rooks.clear();

		this.blackKing = new Piece();
		this.redKing = new Piece();
	}

	public LinkedList<Move> generateRawMove(PieceType type, int x, int y, boolean isRed, String board) {
		// let func: generateMoveFunc | null = null;
		String boardPos;
		if (board == null)
			boardPos = getBoard();
		else
			boardPos = board;

		LinkedList<Move> result = new LinkedList<>();

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
				return null;
		}

		return result;
		// return func ? func(board || this.getInstance().getBoard(), x, y, isRed) : "";
	}

	public LinkedList<Move> generateMove(int x, int y, boolean isRed) {
		PieceType type = Piece.getPieceType(_board, x, y);

		Board board = getInfoOfOneSide(!isRed);
		board.getPieceAt(x, y, true);

		LinkedList<Move> rawMove = generateRawMove(type, x, y, isRed, null);

		// Filter out moves
		for (var it = rawMove.iterator(); it.hasNext();) {
			var move = it.next();
			move.setPiece(type.getValue());
			move.setRed(isRed);

			if (isKingChecked(board, isRed, move, isRed)) {
				it.remove();
			}
		}

		return rawMove;
	}

	/**
	 * If return something other than "" then the king is checked
	 * 
	 * @param board The board object created from the normal board but remove all the allies;
	 * @param isRed Whose king we are checking
	 * @param fillPiece This piece fill get filled in if defined, the board will get changed then
	 *        restored to normal
	 * @returns
	 */
	public boolean isKingChecked(Board board, boolean isRed, Move fillInMove, boolean fillInRed) {
		/*
		 * - Check the king in vertical line. - Check the other horses, canons, pawns, rooks - Advisors
		 * and elephant can't attack the king anyway.
		 */

		// Enemies
		String boardStr = board.getBoard();
		int fillX = -1;
		int fillY = -1;

		Piece king = isRed ? board.blackKing : board.redKing;
		Vector<Piece> enemies = new Vector<>();
		enemies.addAll(board.canons);
		enemies.addAll(board.horses);
		enemies.addAll(board.pawns);
		enemies.addAll(board.rooks);
		Piece ourKing = !isRed ? board.blackKing : board.redKing;
		// Has to do this because we can't really change the value of the king location.
		int ourKingX = ourKing.location.x;
		int ourKingY = ourKing.location.y;

		if (fillInMove != null) {
			// If asked to fill in then it will fill in the boardStr.
			fillX = fillInMove.getX();
			fillY = fillInMove.getY();
			char cPiece = fillInMove.getPieceCaseSensitive();

			boardStr = StringUtils.replaceCharAt(boardStr, cPiece,
					fillX + fillY * BOARD_COL);

			if (fillInMove.isRed() == isRed && fillInMove.getPiece() == PieceType.KING.getValue()) {
				// Change the local king location, don't need to change other king loc because that can't happen
				ourKingX = fillX;
				ourKingY = fillY;
			}
		}

		// Check if the king is seeing each other :))
		if (ourKingX == king.location.x) {
			int startIdx = Math.min(ourKingY, king.location.y) + 1;
			int endIdx = Math.max(ourKingY, king.location.y) - 1;

			boolean blocked = false;
			for (int i = startIdx; i < endIdx; i++) {
				PieceType piece = Piece.getPieceType(board.getBoard(), king.location.x, i);
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
			// If the enemy has the same x and y as the fill in piece then it mean that the enemy is going
			// to be killed
			if (value.location.x == fillX && value.location.y == fillY)
				continue;

			LinkedList<Move> moves =
					generateRawMove(value.type, value.location.x, value.location.y, value.isRed, boardStr);

			for (var move : moves) {
				if (PieceType.KING.getValue() == move.getPiece())
					return true;
			}
		}

		return false;
	}
}
