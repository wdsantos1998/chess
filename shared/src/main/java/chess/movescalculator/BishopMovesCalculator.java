package chess.movescalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import chess.movescalculator.utils.AbstractSlidingPiecesMovesCalculator;

import java.util.Collection;

public class BishopMovesCalculator extends AbstractSlidingPiecesMovesCalculator {

    private static final int[][] BISHOP_MOVES = {
                {-1, -1},
                {-1, 1},
                {1, -1},
                {1, 1},
        };


    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        return calculateMovesInDirections(board,myPosition,BISHOP_MOVES,true);
    }
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
