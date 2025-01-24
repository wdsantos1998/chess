package chess;

import chess.moves_calculator.PieceMovesCalculator;

import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece typeOfPiece = board.getPiece(myPosition);
        switch (typeOfPiece) {
            case PieceType.KING:
                return PieceMovesCalculator.pieceMoves(board, myPosition);
            case PieceType.QUEEN:
                return PieceMovesCalculator.pieceMoves(board, myPosition);
            case PieceType.BISHOP:
                return PieceMovesCalculator.pieceMoves(board, myPosition);
            case PieceType.KNIGHT:
                return PieceMovesCalculator.pieceMoves(board, myPosition);
            case PieceType.ROOK:
                return PieceMovesCalculator.pieceMoves(board, myPosition);
            case PieceType.PAWN:
                return PieceMovesCalculator.pieceMoves(board, myPosition);
            default:
                throw new RuntimeException("Unknown piece type");
        }
//        use switch cases
//        consider color
//        consider piece type

    }
}
