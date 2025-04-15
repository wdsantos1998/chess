package ui;

import chess.*;

import java.util.Collection;

public class PrintChessBoard {
    private static final int BOARD_SIZE = 8;

    /**
     * Prints the chessboard from the perspective of the given ChessBoard object.
     *
     * @param board   The ChessBoard object to print.
     * @param reverse If true, prints the board from the perspective of the white player.
     */
    public static void printChessBoardFromBoardData(ChessBoard board, boolean reverse) {
        System.out.println();
        System.out.print(EscapeSequences.RESET_TEXT_COLOR + "   ");
        for (int i = 0; i < BOARD_SIZE; i++) {
            char c = (char) (reverse ? 'H' - i : 'A' + i);
            System.out.print(" " + c + " ");
        }
        System.out.println();

        for (int r = 0; r < BOARD_SIZE; r++) {
            int row = reverse ? r : BOARD_SIZE - 1 - r;
            System.out.print(EscapeSequences.RESET_TEXT_COLOR + " " + (row + 1) + " ");

            for (int c = 0; c < BOARD_SIZE; c++) {
                int col = reverse ? BOARD_SIZE - 1 - c : c;

                if ((row + col) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                }

                ChessPiece piece = board.getPiece(new ChessPosition(row + 1, col + 1));
                if (piece != null) {
                    boolean isWhite = piece.pieceColor == ChessGame.TeamColor.WHITE;
                    System.out.print(getPieceRepresentation(piece.type, isWhite));
                } else {
                    System.out.print(EscapeSequences.EMPTY);
                }

                System.out.print(EscapeSequences.RESET_BG_COLOR);
            }

            System.out.println(EscapeSequences.RESET_TEXT_COLOR + " " + (row + 1));
        }

        System.out.print(EscapeSequences.RESET_TEXT_COLOR + "   ");
        for (int i = 0; i < BOARD_SIZE; i++) {
            char c = (char) (reverse ? 'H' - i : 'A' + i);
            System.out.print(" " + c + " ");
        }
        System.out.println();
    }

    private static String getPieceRepresentation(ChessPiece.PieceType type, boolean isWhite) {
        switch (type) {
            case KING:
                return isWhite ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN:
                return isWhite ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case BISHOP:
                return isWhite ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT:
                return isWhite ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case ROOK:
                return isWhite ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case PAWN:
                return isWhite ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
            default:
                return EscapeSequences.EMPTY;
        }
    }

    public static void printHighlightedBoard(ChessBoard board, boolean reverse,
                                             ChessPosition selectedPosition,
                                             Collection<ChessMove> legalMoves) {
        System.out.println();
        System.out.print(EscapeSequences.RESET_TEXT_COLOR + "   ");
        for (int i = 0; i < BOARD_SIZE; i++) {
            char c = (char) (reverse ? 'H' - i : 'A' + i);
            System.out.print(" " + c + " ");
        }
        System.out.println();

        for (int r = 0; r < BOARD_SIZE; r++) {
            int row = reverse ? r : BOARD_SIZE - 1 - r;
            System.out.print(EscapeSequences.RESET_TEXT_COLOR + " " + (row + 1) + " ");

            for (int c = 0; c < BOARD_SIZE; c++) {
                int col = reverse ? BOARD_SIZE - 1 - c : c;
                ChessPosition currentPos = new ChessPosition(row + 1, col + 1);

                boolean isSelected = currentPos.equals(selectedPosition);
                boolean isLegalTarget = legalMoves.stream()
                        .anyMatch(move -> move.getEndPosition().equals(currentPos));

                if (isSelected) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_YELLOW);
                } else if (isLegalTarget) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_GREEN);
                } else if ((row + col) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                }

                ChessPiece piece = board.getPiece(currentPos);
                if (piece != null) {
                    boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
                    System.out.print(getPieceRepresentation(piece.getPieceType(), isWhite));
                } else {
                    System.out.print(EscapeSequences.EMPTY);
                }

                System.out.print(EscapeSequences.RESET_BG_COLOR);
            }

            System.out.println(EscapeSequences.RESET_TEXT_COLOR + " " + (row + 1));
        }

        System.out.print(EscapeSequences.RESET_TEXT_COLOR + "   ");
        for (int i = 0; i < BOARD_SIZE; i++) {
            char c = (char) (reverse ? 'H' - i : 'A' + i);
            System.out.print(" " + c + " ");
        }
        System.out.println();
    }

}