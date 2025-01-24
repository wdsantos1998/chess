package chess.moves_calculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.List;

public class QueenMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        return List.of();
    }

    @Override
    public boolean isValidMove(ChessBoard board, ChessPosition myPosition, ChessPosition targetPosition) {
        return false;
    }

    @Override
    public boolean isEnemy(ChessBoard board, ChessPosition myPosition) {
        return false;
    }
}
