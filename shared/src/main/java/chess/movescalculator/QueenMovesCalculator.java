package chess.movescalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.movescalculator.utils.AbstractSlidingPiecesMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator extends AbstractSlidingPiecesMovesCalculator {

    private static final int[][] QUEEN_MOVES = {
                {1,0},{-1,0},
                {0,1},{0,-1},
                {1,1},{1,-1},
                {-1,1},{-1,-1}
        };

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        return calculateMovesInDirections(board,myPosition,QUEEN_MOVES,true);
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
