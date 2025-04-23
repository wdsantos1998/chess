package chess.movescalculator.utils;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.movescalculator.PieceMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        return List.of();
    }

    protected Collection<ChessMove> calculateMovesInDirections(
            ChessBoard board,
            ChessPosition start,
            int[][] directions,
            boolean repeat
    ) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (int[] dir : directions) {
            int row = start.getRow();
            int col = start.getColumn();

            while (true) {
                row += dir[0];
                col += dir[1];
                ChessPosition newPos = new ChessPosition(row, col);

                if (!isWithinBoard(newPos)){
                    break;
                }

                if (!isValidMove(board, start, newPos)){
                    break;
                }

                validMoves.add(new ChessMove(start, newPos, null));

                if (board.getPiece(newPos) != null){
                    break; // Stop if capturing
                }

                if (!repeat){
                    break;
                }
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
}
