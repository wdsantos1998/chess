package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;
    private boolean isGameOver = false;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        setTeamTurn(TeamColor.WHITE);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }


    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece myPiece = board.getPiece(startPosition);
        if (myPiece == null) {
            return null;
        }
        Collection<ChessMove> moves = myPiece.pieceMoves(this.board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        for(ChessMove move : moves){
            if(isMoveValid(move)){
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    /**
     * Locates the king of the given team
     *
     * @param teamColor which team to find the king for
     * @return the location of the king
     * or null if the king is not found
     */

    private int[] getKingLocation(ChessGame.TeamColor teamColor) {
        ChessPiece.PieceType king = ChessPiece.PieceType.KING;
        int [] KingLocation = new int[2];
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                ChessPosition position = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(position);
                if(piece == null){
                    continue;
                }
                if(piece.getPieceType() == king && piece.getTeamColor() == teamColor){
                    KingLocation = new int[] {i,j};
                }
            }
        }
        return KingLocation;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        int[] kingLocation = getKingLocation(teamColor);
        ChessGame.TeamColor opponentColor = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        Collection<ChessMove> opponentMoves = getTeamPossibleMoves(opponentColor);
        for(ChessMove move : opponentMoves){
            if(move.getEndPosition().getRow() == kingLocation[0] && move.getEndPosition().getColumn() == kingLocation[1]){
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all possible moves for the opponent of the given team
     *
     * @param teamColor opponent's team color
     * @return A collection of all possible moves for the opponent
     */
    private Collection<ChessMove> getTeamPossibleMoves(ChessGame.TeamColor teamColor){
        Collection<ChessMove> moves = new ArrayList<>();
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                ChessPosition position = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(position);
                if(piece == null){
                    continue;
                }
                if(piece.getTeamColor() != teamColor){
                    continue;
                }
                //I need and edge case to when the piece is the King

//                if(piece.getPieceType() == ChessPiece.PieceType.KING){
//                    getKingPossibleMoves(piece);
//                }
                moves.addAll(piece.pieceMoves(board, position));
            }
        }
        return moves;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //This function needs to call for game over if the king is in checkmate
        //I need to check for:
        //1. If the king is in check (Done)
        //2. If the king has no legal moves (Done)
        //3. No friendly pieces can block the check
        //4. No friendly pieces can capture the attacking piece (Done)
        boolean inCheck = isInCheck(teamColor);
        if(!inCheck){
            return false;
        }
        int[] kingLocation = getKingLocation(teamColor);
        ChessPosition kingPosition = new ChessPosition(kingLocation[0], kingLocation[1]);
        if(kingHasValidMoves(kingPosition)){
            return false;
        }
        if(friendlyPieceCanCaptureAttacker(kingPosition,teamColor)){
            return false;
        }
        if(friendlyPieceCanBlockAttacker(kingPosition,teamColor)){
            return false;
        }
        return true;
    }

    private boolean friendlyPieceCanBlockAttacker(ChessPosition kingPosition, ChessGame.TeamColor teamColor){

        return false;
    }

    private boolean friendlyPieceCanCaptureAttacker(ChessPosition kingPosition, ChessGame.TeamColor teamColor){
        ChessGame.TeamColor opponentColor = teamColor = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        Collection<ChessMove> opponentMoves = getTeamPossibleMoves(opponentColor);
        Collection<ChessMove> myTeamMoves = getTeamPossibleMoves(teamColor);
        for(ChessMove opponentMove : opponentMoves){
            ChessPosition attackerTargetPosition = opponentMove.getEndPosition();
            if(attackerTargetPosition.getRow() == kingPosition.getRow() && attackerTargetPosition.getColumn() == kingPosition.getColumn()){
                for(ChessMove teamMove : myTeamMoves){
                    ChessPosition defenderTargetPosition = teamMove.getEndPosition();
                    if (defenderTargetPosition.getRow() == attackerTargetPosition.getRow() && attackerTargetPosition.getColumn() == defenderTargetPosition.getColumn()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean kingHasValidMoves(ChessPosition kingPosition){
        ChessPiece kingPiece = board.getPiece(kingPosition);
        ChessGame.TeamColor teamColor = kingPiece.getTeamColor();
        Collection<ChessMove> kingMoves = kingPiece.pieceMoves(board, kingPosition);
        for(ChessMove move : kingMoves){
           ChessPosition kingTargetPosition = move.getEndPosition();
              ChessPiece targetPiece = board.getPiece(kingTargetPosition);
                if(targetPiece == null && !isInCheck(teamColor)){
                   return true;
                }
                if(targetPiece != null && targetPiece.getTeamColor() != teamColor && !isInCheck(teamColor)){
                    return true;
                }
        }
        return false;
    }


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */

    //If the king is not in check, but the player has no legal moves, then it's a stalemate.
    public boolean isInStalemate(TeamColor teamColor) {
        //I need to set a draw condition if the king is not in check but has no legal moves
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return isGameOver == chessGame.isGameOver && Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn, isGameOver);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", teamTurn=" + teamTurn +
                ", isGameOver=" + isGameOver +
                '}';
    }
}
