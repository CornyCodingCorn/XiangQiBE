package com.XiangQi.XiangQiBE.Components;

import java.util.Vector;

import com.XiangQi.XiangQiBE.Components.Piece.PieceType;
import com.XiangQi.XiangQiBE.utils.PieceMove;
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

	public boolean IsMoveValid(String move) {
		var moveObj = PieceMove.Parse(move);
		// Update the full move string to new move string;
		move = "" + moveObj.newX + moveObj.newY;
		boolean isRed = moveObj.piece.equals(moveObj.piece.toUpperCase());

		String[] validMoves = generateMove(moveObj.oldX, moveObj.oldY, isRed).split("/");
		for (String validMove : validMoves) {
			if (validMove.substring(0, 2).equals(move)) {
				return true;
			}
		}

		return false;
	}

	public String UpdateBoard(String move) {
		var moveObj = PieceMove.Parse(move);

		var piece = getPieceAt(moveObj.oldX, moveObj.oldY, false);
		piece.location.x = moveObj.newX;
		piece.location.y = moveObj.newY;

		_board = StringUtils.replaceCharAt(_board, PieceType.Empty.getValue(),
				moveObj.oldX + moveObj.oldY * BOARD_COL);
		_board = StringUtils.replaceCharAt(_board, "" + move.charAt(2),
				moveObj.newX + moveObj.newY * BOARD_COL);

		return _board;
	}

	public Result CheckResult(boolean isRedTurn) {
		// Get other player king and check for move if have more than one move then Continue
		Piece otherKing = isRedTurn ? blackKing : redKing;
		String move = generateMove(otherKing.location.x, otherKing.location.y, !isRedTurn);

		// If move is not empty then continue
		if (!StringUtils.isStringEmpty(move)) {
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
			String possibleMoves = generateMove(otherPiece.location.x, otherPiece.location.y, otherPiece.isRed);
			if (!StringUtils.isStringEmpty(possibleMoves)) {
				// If have one piece that is movable on next turn then continue
				return Result.CONTINUE;
			}
		}

		// If no piece is movable on next turn then if the king is checked then Win otherwise Draw
		// Get info of the side of the player of this turn then check if the king of other player is
		// checked. 
		if (isKingChecked(getInfoOfOneSide(isRedTurn), otherKing.isRed, "")) {
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
		PieceType piece = PieceType.fromCharString("" + this._board.charAt(x + y * BOARD_COL));
		if (removeIt) this._board = StringUtils.replaceCharAt(this._board, PieceType.Empty.getValue(), x + y * BOARD_COL);

		Vector<Piece> pieceList = null;
		switch (piece) {
			case Rook:
				pieceList = rooks;
				break;
			case Horse:
				pieceList = horses;
				break;
			case Elephant:
				pieceList = elephants;
				break;
			case Advisor:
				pieceList = advisors;
				break;
			case King:
				Piece king = null;
				if (this.blackKing.location.isEqual(Vector2.create(x, y))) {
					king = this.blackKing;
				} else {
					king = this.redKing;
				}
				return king;
			case Pawn:
				pieceList = pawns;
				break;
			case Canon:
				pieceList = canons;
				break;
			case Empty:
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
			String charString = String.valueOf(this._board.charAt(i));
			PieceType type = PieceType.fromCharString(charString.toLowerCase());

			// Ignore empty and same color
			if (type == PieceType.Empty)
				continue;

			switch (type) {
				case Canon:
					this.canons.add(Piece.getPieceObject(this._board, i, Canon.class));
					break;
				case Horse:
					this.horses.add(Piece.getPieceObject(this._board, i, Horse.class));
					break;
				case Rook:
					this.rooks.add(Piece.getPieceObject(this._board, i, Rook.class));
					break;
				case Pawn:
					this.pawns.add(Piece.getPieceObject(this._board, i, Pawn.class));
					break;
				case Advisor:
					this.advisors.add(Piece.getPieceObject(this._board, i, Advisor.class));
					break;
				case Elephant:
					this.elephants.add(Piece.getPieceObject(this._board, i, Elephant.class));
					break;
				case King:
					var piece = Piece.getPieceObject(this._board, i, King.class);
					if (charString.toLowerCase().equals(charString)) {
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

	public String generateRawMove(PieceType type, int x, int y, boolean isRed, String board) {
		// let func: generateMoveFunc | null = null;
		String boardPos;
		if (board == null)
			boardPos = getBoard();
		else
			boardPos = board;

		String result = "";

		switch (type) {
			case King:
				result = king.generateMove(boardPos, x, y, isRed);
				break;
			case Advisor:
				result = advisor.generateMove(boardPos, x, y, isRed);
				break;
			case Elephant:
				result = elephant.generateMove(boardPos, x, y, isRed);
				break;
			case Horse:
				result = horse.generateMove(boardPos, x, y, isRed);
				break;
			case Rook:
				result = rook.generateMove(boardPos, x, y, isRed);
				break;
			case Canon:
				result = canon.generateMove(boardPos, x, y, isRed);
				break;
			case Pawn:
				result = pawn.generateMove(boardPos, x, y, isRed);
				break;
			default:
				return "";
		}

		return result;
		// return func ? func(board || this.getInstance().getBoard(), x, y, isRed) : "";
	}

	public String generateMove(int x, int y, boolean isRed) {
		PieceType type = Piece.getPieceType(_board, x, y);

		Board board = getInfoOfOneSide(!isRed);
		board.getPieceAt(x, y, true);

		String rawMove = generateRawMove(type, x, y, isRed, null);
		String[] arr = rawMove.split("/");
		String result = "";

		for (String value : arr) {
			if (value.equals(""))
				break;

			String fillInStr =
					value.substring(0, 2) + (isRed ? type.getValue().toUpperCase() : type.getValue());

			if (!isKingChecked(board, isRed, fillInStr)) {
				result += value + "/";
			}
		}

		return result;
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
	public boolean isKingChecked(Board board, boolean isRed, String fillPiece) {
		/*
		 * - Check the king in vertical line. - Check the other horses, canons, pawns, rooks - Advisors
		 * and elephant can't attack the king anyway.
		 */

		// Enemies
		String boardStr = board.getBoard();
		int fillX = -1;
		int fillY = -1;
		if (!StringUtils.isStringEmpty(fillPiece)) {
			// If asked to fill in then it will fill in the boardStr.
			fillX = Integer.parseInt(String.valueOf(fillPiece.charAt(0)));
			fillY = Integer.parseInt(String.valueOf(fillPiece.charAt(1)));

			boardStr = StringUtils.replaceCharAt(boardStr, "" + fillPiece.charAt(2),
					fillX + fillY * BOARD_COL);
		}

		Piece king = isRed ? board.blackKing : board.redKing;
		Vector<Piece> enemies = new Vector<>();
		enemies.addAll(board.canons);
		enemies.addAll(board.horses);
		enemies.addAll(board.pawns);
		enemies.addAll(board.rooks);
		Piece ourKing = !isRed ? board.blackKing : board.redKing;

		// Check if the king is seeing each other :))
		if (ourKing.location.x == king.location.x) {
			int startIdx = Math.min(ourKing.location.y, king.location.y) + 1;
			int endIdx = Math.max(ourKing.location.y, king.location.y) - 1;

			boolean blocked = false;
			for (int i = startIdx; i < endIdx; i++) {
				PieceType piece = Piece.getPieceType(board.getBoard(), ourKing.location.x, i);
				if (piece != PieceType.Empty) {
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

			String move =
					generateRawMove(value.type, value.location.x, value.location.y, value.isRed, boardStr);

			String[] arr = move.split("/");
			for (int j = 0; j < arr.length; j++) {
				var str = arr[j];
				if (!str.equals("")
						&& String.valueOf(str.charAt(2)).toLowerCase().equals(PieceType.King.getValue()))
					return true;
			}
		}

		return false;
	}
}
