package chess.movescalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class PawnMovesCalculator implements PieceMovesCalculator {

    private boolean isFirstMove = true;

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        ChessGame.TeamColor teamColor = getPawnColor(board, myPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        int direction = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (teamColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promotionRow = (teamColor == ChessGame.TeamColor.WHITE) ? 8 : 1;

        addForwardMoves(validMoves, board, myPosition, direction, startRow, promotionRow);
        addCaptureMoves(validMoves, board, myPosition, direction, promotionRow);

        return validMoves;
    }

    private void addForwardMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition, int direction, int startRow, int promotionRow) {
        int forwardRow = myPosition.getRow() + direction;
        int col = myPosition.getColumn();
        ChessPosition newPosition = new ChessPosition(forwardRow, col);

        if (isValidMove(board, myPosition, newPosition) && board.getPiece(newPosition) == null) {
            if (forwardRow == promotionRow) {
                addPromotionMoves(moves, myPosition, newPosition);
            } else {
                moves.add(new ChessMove(myPosition, newPosition, null));
            }

            if (myPosition.getRow() == startRow) {
                ChessPosition newPositionTwo = new ChessPosition(myPosition.getRow() + 2 * direction, col);
                if (board.getPiece(newPositionTwo) == null && board.getPiece(new ChessPosition(myPosition.getRow() + direction, col)) == null) {
                    moves.add(new ChessMove(myPosition, newPositionTwo, null));
                }
            }
        }
    }

    private void addCaptureMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition myPosition, int direction, int promotionRow) {
        int forwardRow = myPosition.getRow() + direction;
        int[] captureCols = {myPosition.getColumn() - 1, myPosition.getColumn() + 1};

        for (int col : captureCols) {
            if (isWithinBoard(new ChessPosition(forwardRow, col))) {
                ChessPosition newPosition = new ChessPosition(forwardRow, col);
                ChessPiece pieceAtDestination = board.getPiece(newPosition);
                if (pieceAtDestination != null && pieceAtDestination.getTeamColor() != getPawnColor(board, myPosition)) {
                    if (forwardRow == promotionRow) {
                        addPromotionMoves(moves, myPosition, newPosition);
                    } else {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }
        }
    }

    private void addPromotionMoves(Collection<ChessMove> moves, ChessPosition startPos, ChessPosition endPos) {
        moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(startPos, endPos, ChessPiece.PieceType.ROOK));
    }

    @Override
    public boolean isValidMove(ChessBoard board, ChessPosition myPosition, ChessPosition targetPosition) {
        if (!isWithinBoard(targetPosition)) {
            return false;
        }

        ChessPiece targetPiece = board.getPiece(targetPosition);
        return targetPiece == null || targetPiece.getTeamColor() != getPawnColor(board, myPosition);
    }

    private boolean isWithinBoard(ChessPosition position) {
        return position.getRow() >= 1 && position.getRow() <= 8 && position.getColumn() >= 1 && position.getColumn() <= 8;
    }

    public ChessGame.TeamColor getPawnColor(ChessBoard board, ChessPosition myPosition) {
        return board.getPiece(myPosition).getTeamColor();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        PawnMovesCalculator that = (PawnMovesCalculator) o;
        return isFirstMove == that.isFirstMove;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(isFirstMove);
    }
}
