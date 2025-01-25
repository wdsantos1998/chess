package chess.moves_calculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {

    private int[][] getKingMoves() {
        return new int[][]{
                //{row, col}
                {-1, -1}, //Diagonal-left (Moving Down)
                {-1, 0}, //Down
                {-1, 1},//Diagonal-right (Moving Down)
                {0, -1}, //Left
                {0, 1},// Right
                {1, -1}, //Diagonal-left (Moving Up)
                {1, 0}, //Up
                {1, 1}, //Diagonal-right (Moving Up)
        };
    }

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] kingMoves = getKingMoves();
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (int[] move : kingMoves) {
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

    @Override
    public boolean isEnemy(ChessBoard board, ChessPosition myPosition) {
        return false;
    }

    private boolean isWithinBoard(ChessPosition targetPosition) {
        return targetPosition.getRow() >= 1 && targetPosition.getRow() <= 8 && targetPosition.getColumn() >= 1 && targetPosition.getColumn() <= 8;
    }

    public boolean isSlidingPiece() {
        return false;
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
