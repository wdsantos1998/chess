package chess.movescalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.movescalculator.utils.AbstractMovesCalculator;

import java.util.Collection;

public class KnightMovesCalculator extends AbstractMovesCalculator {

    private static final int[][] KNIGHT_MOVES = {
                {1,2},{1,-2},
                {-1,2},{-1,-2},
                {2,1},{2,-1},
                {-2,1},{-2,-1}
    };

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        return calculateMovesInDirections(board,myPosition,KNIGHT_MOVES,false);
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
