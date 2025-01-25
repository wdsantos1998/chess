package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition startPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition endPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType promotionPiece() {
        return promotionPiece;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        System.out.println("chessMove: " + chessMove.toString());
        boolean startPosition = this.startPosition.equals(chessMove.startPosition);
        System.out.println("startPosition: " + startPosition);
        boolean endPosition = this.endPosition.equals(chessMove.endPosition);
        System.out.println("endPosition: " + endPosition);
        boolean promotionPiece = Objects.equals(this.promotionPiece, chessMove.promotionPiece);
        System.out.println("promotionPiece: " + promotionPiece);
        boolean returnValue = startPosition && endPosition && promotionPiece;
        System.out.println("returnValue: " + returnValue);
        return returnValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }

    @Override
    public String toString() {
        return "ChessMove{" +
                "startPosition=" + "("+ startPosition.getRow() +","+ startPosition.getColumn()+")" +
                ", endPosition=" + "("+ endPosition.getRow() +","+endPosition.getColumn() +")" +
                ", promotionPiece=" + promotionPiece +
                '}';
    }
}
