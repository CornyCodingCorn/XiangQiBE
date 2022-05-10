package com.XiangQi.XiangQiBE.Components;

import java.util.Vector;

import com.XiangQi.XiangQiBE.Components.Piece.PieceType;
import com.XiangQi.XiangQiBE.utils.StringUtils;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Component
public class Board {
  @AllArgsConstructor
  public enum Result {
    CONTINUE(0),
    RED_WIN(1),
    BLACK_WIN(2),
    DRAW(3);

    @Getter
    @Setter
    private int value;
  }

	// feels like somthing wrong :v
  public boolean IsMoveValid(String board, String move) {
		setBoard(board);
		boolean isRed = Piece.isPieceRed(String.valueOf(move.charAt(2)));

    return isKingChecked(this, isRed, move);
  }

  public String UpdateBoard(String board, String move) {
    return board;
  }

  public Result CheckResult(String board, boolean isRedTurn) {
    //Check the result of the board game.
    return Result.CONTINUE;
  }

	public static final int BOARD_COL = 9;
	public static final int BOARD_ROW = 10;

  public Vector<Piece> horses = new Vector<>();
	public Vector<Piece> canons = new Vector<>();
	public Vector<Piece> advisors = new Vector<>();
	public Vector<Piece> elephants = new Vector<>();
	public Vector<Piece> pawns = new Vector<>();
	public Vector<Piece> rooks= new Vector<>();

	public Piece blackKing= new Piece();
	public Piece redKing = new Piece();

  // private static Board _instance = null;
	// public static Board getInstance() {
	// 	if (Board._instance == null) {
	// 		Board._instance = new Board();
	// 	}
	// 	return Board._instance;
	// }

  protected String _board = null;
  public void setBoard(String _board) {
    this._board = _board;
    this._findAllPieces();
  }
  public String getBoard() {
    return _board;
  }

  public void removeAt(int x, int y) {
    this._board = StringUtils.replaceCharAt(this._board, PieceType.Empty.getValue(), x + y * BOARD_COL);
    this._findAllPieces();
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

    board.setBoard(this._board);

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
			PieceType type = PieceType.valueOf(charString.toLowerCase());

			// Ignore empty and same color
			if (type == PieceType.Empty) 
        continue;

			Piece piece = Piece.getPieceObject(this._board, i);
			switch (type) {
				case Cannon:
					this.canons.add(piece);
					break;
				case Horse:
					this.horses.add(piece);
					break;
				case Rook:
					this.rooks.add(piece);
					break;
				case Pawn:
					this.pawns.add(piece);
					break;
				case Advisor:
					this.advisors.add(piece);
					break;
				case Elephant:
					this.elephants.add(piece);
					break;
				case King:
					if (charString.toLowerCase() == charString) {
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

  public String generateRawMove(
		PieceType type,
		int x,
		int y,
		boolean isRed,
    String board
	) {
		//let func: generateMoveFunc | null = null;
		Piece piece;
		String boardPos;
    if (board == null)
      boardPos = getBoard();
    else
      boardPos = board;

		switch (type) {
			case King:
				piece = new King();
				break;
			case Advisor:
				piece = new Advisor();
				break;
			case Elephant:
				piece = new Elephant();
				break;
			case Horse:
        piece = new Horse();
        break;
			case Rook:
        piece = new Rook();
				break;
			case Cannon:
        piece = new Canon();
				break;
			case Pawn:
        piece = new Pawn();
				break;
      default:
        return "";
		}
		
    return piece.generateMove(boardPos, x, y, isRed);
		//return func ? func(board || this.getInstance().getBoard(), x, y, isRed) : "";
	}

  public String generateMove(
		PieceType type,
		int x,
		int y,
		boolean isRed
	) {
		Board board = getInfoOfOneSide(!isRed);
    board.removeAt(x, y);

		String rawMove = generateRawMove(type, x, y, isRed, null);
		String[] arr = rawMove.split("/");
		String result = "";

    for (String value : arr) {
      if (value == "")
        break;
      String fillInStr = value.substring(0, 2) + (isRed ? type.getValue().toUpperCase() : type);

      if (!isKingChecked(board, isRed, fillInStr)) {
        result += "${value}/";
      }
    }

		return result;
	}

	/**
	 * If return something other than "" then the king is checked
	 * @param board The board object created from the normal board but remove all the allies;
	 * @param isRed
	 * @param fillPiece This piece fill get filled in if defined, the board will get changed then restored to normal
	 * @returns
	 */
	public boolean isKingChecked(Board board, boolean isRed, String fillPiece) {
		/*
    - Check the king in vertical line.
    - Check the other horses, canons, pawns, rooks
    - Advisors and elephant can't attack the king anyway.
  */

		// Enemies
		String boardStr = board.getBoard();
		int fillX = -1;
		int fillY = -1;
		if (!StringUtils.isStringEmpty(fillPiece)) {
			// If asked to fill in then it will fill in the boardStr.
			fillX = Integer.parseInt(String.valueOf(fillPiece.charAt(0)));
			fillY = Integer.parseInt(String.valueOf(fillPiece.charAt(1)));

			boardStr = StringUtils.replaceCharAt(
				boardStr, 
				String.valueOf(fillPiece.charAt(2)),
				fillX + fillY * BOARD_COL
			);
		}

		Piece king = isRed ? board.blackKing : board.redKing;
		Vector<Piece> enemies = new Vector<>();
		enemies.addAll(board.canons);
		enemies.addAll(board.horses);
		enemies.addAll(board.pawns);
		enemies.addAll(board.rooks);
		Piece ourKing= !isRed ? board.blackKing : board.redKing;

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
			// If the enemy has the same x and y as the fill in piece then it mean that the enemy is going to be killed
			if (value.location.x == fillX && value.location.y == fillY) continue;

			String move = generateRawMove(
				value.type,
				value.location.x,
				value.location.y,
				value.isRed,
        boardStr
			);

			String[] arr = move.split("/");
			for (int j = 0; j < arr.length; j++) {
				var str = arr[j];
				if (str != "" && String.valueOf(str.charAt(2)).toLowerCase() == PieceType.King.getValue())
				  return true;
			}
		}

		return false;
	}
}
