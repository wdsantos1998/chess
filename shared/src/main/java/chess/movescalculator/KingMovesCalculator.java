package chess.movescalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.movescalculator.utils.AbstractSlidingPiecesMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {

    private static final int[][] KING_MOVES = {
                {-1, -1},
                {-1, 0},
                {-1, 1},
                {0, -1},
                {0, 1},
                {1, -1},
                {1, 0},
                {1, 1},
        };

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (int[] move : KING_MOVES) {
            int newRow = myPosition.getRow() + move[0];
            int newCol = myPosition.getColumn() + move[1];
            ChessPosition newPos = new ChessPosition(newRow, newCol);

            if (isValidMove(board, myPosition, newPos)) {
                validMoves.add(new ChessMove(myPosition, newPos, null));
            }
        }
        return validMoves;
    }

    @Override
    public boolean isValidMove(ChessBoard board, ChessPosition myPosition, ChessPosition targetPosition) {
        if (!isWithinBoard(targetPosition)) {
            return false;
        }
        ChessPiece myPiece = board.getPiece(myPosition);
        ChessPiece targetPiece = board.getPiece(targetPosition);

        if (targetPiece == null) {
            return true;
        }

        else if (myPiece != null && targetPiece.getTeamColor() == myPiece.getTeamColor()) {
            return false;
        }

        return true;
    }

    private boolean isWithinBoard(ChessPosition targetPosition) {
        return targetPosition.getRow() >= 1 && targetPosition.getRow() <= 8 && targetPosition.getColumn() >= 1 && targetPosition.getColumn() <= 8;
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
