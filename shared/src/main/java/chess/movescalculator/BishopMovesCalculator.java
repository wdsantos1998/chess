package chess.movescalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator {

    private boolean capturedPiece = false;

    private int[][] getBishopMoves() {
        return new int[][]{
                {-1, -1},
                {-1, 1},
                {1, -1},
                {1, 1},
        };
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] bishopMoves = getBishopMoves();
        Collection<ChessMove> validMoves = new ArrayList<>();
        for (int[] move : bishopMoves) {
            setCapturedPiece(false);
            int currentRow = myPosition.getRow();
            int currentCol = myPosition.getColumn();
            while (true) {
                {
                    currentRow += move[0];
                    currentCol += move[1];
                    ChessPosition newPos = new ChessPosition(currentRow, currentCol);

                    if (isValidMove(board, myPosition, newPos)) {
                        validMoves.add(new ChessMove(myPosition, newPos, null));
                        if (isCapturedPiece()) {
                            break;
                        }
                    }
                    else {
                        break;
                    }
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
        else if (myPiece != null && targetPiece.getTeamColor() != myPiece.getTeamColor()) {
            setCapturedPiece(true);
            return true;
        }

        return true;
    }

    private boolean isWithinBoard(ChessPosition targetPosition) {
        return targetPosition.getRow() >= 1 && targetPosition.getRow() <= 8 && targetPosition.getColumn() >= 1 && targetPosition.getColumn() <= 8;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public boolean isCapturedPiece() {
        return capturedPiece;
    }

    public void setCapturedPiece(boolean capturedPiece) {
        this.capturedPiece = capturedPiece;
    }
}
