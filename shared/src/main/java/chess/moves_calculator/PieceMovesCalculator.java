package chess.moves_calculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public interface PieceMovesCalculator {

    Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition);

    boolean isValidMove(ChessBoard board, ChessPosition myPosition);

    boolean isEnemy(ChessBoard board, ChessPosition myPosition);

}
