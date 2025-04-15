package chess.movescalculator.utils;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.movescalculator.PieceMovesCalculator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractSlidingPiecesMovesCalculator implements PieceMovesCalculator {
    private boolean capturedPiece = false;

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
        for (int[] move : directions) {
            setCapturedPiece(false);
            int currentRow = start.getRow();
            int currentCol = start.getColumn();
            while (true) {
                {
                    currentRow += move[0];
                    currentCol += move[1];
                    ChessPosition newPos = new ChessPosition(currentRow, currentCol);

                    if (isValidMove(board, start, newPos)) {
                        validMoves.add(new ChessMove(start, newPos, null));
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

    public boolean isCapturedPiece() {
        return capturedPiece;
    }

    public void setCapturedPiece(boolean capturedPiece) {
        this.capturedPiece = capturedPiece;
    }
}
