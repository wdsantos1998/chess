package chess.movescalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public interface PieceMovesCalculator {

    Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition);

    boolean isValidMove(ChessBoard board, ChessPosition myPosition, ChessPosition targetPosition);
}
