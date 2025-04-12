package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class PrintChessBoard {
    private static final int BOARD_SIZE = 8;

    public static void printGenericBoard(String color) {
        if (color.equalsIgnoreCase("black")) {
            System.out.println("Displaying the chessboard from the perspective of the " + color + " player");
            //Drawing board from the perspective of the black player
            String[][] board = {
                    // row 0 - black main pieces
                    {EscapeSequences.BLACK_ROOK, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_BISHOP,
                            EscapeSequences.BLACK_QUEEN,EscapeSequences.BLACK_KING,  EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_ROOK},


                    // row 1 - black pawns
                    {EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN,
                            EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN},


                    // row 2–5 - empty squares
                    {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY,
                            EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                    {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY,
                            EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                    {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY,
                            EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                    {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY,
                            EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},


                    // row 6 - white pawns
                    {EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN,
                            EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN},


                    // row 7  white main pieces
                    {EscapeSequences.WHITE_ROOK, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_BISHOP,
                             EscapeSequences.WHITE_QUEEN,EscapeSequences.WHITE_KING, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_ROOK}
            };
            printBoard(board, true);
        } else {

            System.out.println("Displaying the chessboard from the perspective of the " + color + " player");

            //Drawing board from the perspective of the white player
            String[][] board = {
                    // row 0 - white main pieces
                    {EscapeSequences.WHITE_ROOK, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_QUEEN,
                            EscapeSequences.WHITE_KING, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_ROOK},


                    // row 1 - black pawns
                    {EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN,
                            EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN},


                    // row 2–5 - empty squares
                    {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY,
                            EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                    {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY,
                            EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                    {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY,
                            EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                    {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY,
                            EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},


                    // row 6 - black pawns
                    {EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN,
                            EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN},


                    // row 7  black main pieces
                    {EscapeSequences.BLACK_ROOK, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_BISHOP,EscapeSequences.BLACK_QUEEN, EscapeSequences.BLACK_KING,
                            EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_ROOK},
            };
            printBoard(board, false);
        }
    }

    private static void printBoard(String[][] board, boolean reverse) {
        System.out.print(EscapeSequences.RESET_TEXT_COLOR + "   ");
        for (int i = 0; i < BOARD_SIZE; i++) {
            char c = (char) (reverse ? 'H' - i : 'A' + i);
            System.out.print(" " + c + " "); // 3 boxes wide column to align with the pieces
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

                System.out.print(board[row][col]);
                System.out.print(EscapeSequences.RESET_BG_COLOR);
            }

            System.out.println(EscapeSequences.RESET_TEXT_COLOR + " " + (row + 1));
        }

        System.out.print(EscapeSequences.RESET_TEXT_COLOR + "   ");
        for (int i = 0; i < BOARD_SIZE; i++) {
            char c = (char) (reverse ? 'H' - i : 'A' + i);
            System.out.print(" " + c + " "); // Wide column to align with the pieces
        }
        System.out.println();
    }

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

}