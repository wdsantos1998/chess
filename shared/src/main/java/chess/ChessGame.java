package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;
    private boolean isGameOver;

    public ChessGame() {
        this.board = new ChessBoard();
        setGameOver(false);
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
        ChessGame.TeamColor teamColor = myPiece.getTeamColor();
        Collection<ChessMove> validMoves = new ArrayList<>();
        Collection<ChessMove> moves = myPiece.pieceMoves(this.board, startPosition);

        for (ChessMove move : moves) {
                if(!simulatePieceMovementAndCheck(move, teamColor)){
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
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
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
        ChessPosition kingPosition = new ChessPosition(kingLocation[0], kingLocation[1]);
        ChessGame.TeamColor opponentColor = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        Collection<ChessMove> opponentMoves = getTeamPossibleMoves(opponentColor, true);
        for(ChessMove move : opponentMoves){
            if(move.getEndPosition().equals(kingPosition)){
                return true;
            }
        }
        return false;
    }


    /**
     * Returns all possible moves for the opponent of the given team
     *It does not consider if a move is valid or not
     * @param teamColor opponent's team color
     * @return A collection of all possible moves for the opponent
     */
    private Collection<ChessMove> getTeamPossibleMoves(ChessGame.TeamColor teamColor , boolean returnKingMoves){
        Collection<ChessMove> moves = new ArrayList<>();
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPosition position = new ChessPosition(i,j);
                ChessPiece piece = board.getPiece(position);
                if(piece == null){
                    continue;
                }
                if(piece.getTeamColor() != teamColor){
                    continue;
                }
                if (!returnKingMoves && piece.getPieceType() != ChessPiece.PieceType.KING){
                    continue;
                }
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

    private boolean friendlyPieceCanBlockAttacker(ChessPosition kingPosition, ChessGame.TeamColor teamColor) {
        ChessGame.TeamColor opponentColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        Collection<ChessMove> opponentMoves = getTeamPossibleMoves(opponentColor, false);

        List<ChessMove> attackerMoves = new ArrayList<>();
        for (ChessMove move : opponentMoves) {
            if (move.getEndPosition().equals(kingPosition)) {
                attackerMoves.add(move);
            }
        }
        if (attackerMoves.isEmpty()) {
            return false;
        }

        List<ChessPosition> routeToPiece = new ArrayList<>();
        for (ChessMove attackerMove : attackerMoves) {
            ChessPiece attackerPiece = board.getPiece(attackerMove.getStartPosition());
            if (attackerPiece.isSlidingPiece()) {
                routeToPiece.addAll(getAttackerRouteToPiece(attackerMove, kingPosition));
            }
        }
        if (routeToPiece.isEmpty()) {
            return false;
        }

        Collection<ChessMove> friendlyMoves = getTeamPossibleMoves(teamColor, false);

        HashSet<ChessPosition> routeSet = new HashSet<>(routeToPiece);

        for (ChessMove friendlyMove : friendlyMoves) {
            if (routeSet.contains(friendlyMove.getEndPosition())) {
                return true;
            }
        }

        return false;
    }

    private List<ChessPosition> getAttackerRouteToPiece(ChessMove attackerMove, ChessPosition pieceUnderAttack){
        //This function was built for the purpose of finding the route of the attacker to the king,
        // but can be used to track the route of any piece to another piece

        List<ChessPosition> route = new ArrayList<>();
        ChessBoard board = getBoard();
        ChessPiece attackerPiece = board.getPiece(attackerMove.getStartPosition());
        if(attackerPiece.getPieceType() == ChessPiece.PieceType.KNIGHT || attackerPiece.getPieceType() == ChessPiece.PieceType.PAWN){
            //These pieces cannot be blocked
            return route;
        }
        ChessPosition attackerPosition = attackerMove.getStartPosition();

        //Getting direction of movement
        int directionRow = Integer.compare(pieceUnderAttack.getRow(), attackerPosition.getRow());
        int directionColumn = Integer.compare(pieceUnderAttack.getColumn(), attackerPosition.getColumn());

        int rowTowardsKing = attackerPosition.getRow() + directionRow;
        int colTowardsKing = attackerPosition.getColumn() + directionColumn;

        while(rowTowardsKing != pieceUnderAttack.getRow() && colTowardsKing != pieceUnderAttack.getColumn()){
            route.add(new ChessPosition(rowTowardsKing, colTowardsKing));
            rowTowardsKing += directionRow;
            colTowardsKing += directionColumn;
        }
        return route;
    }

    private boolean friendlyPieceCanCaptureAttacker(ChessPosition kingPosition, ChessGame.TeamColor teamColor) {
        ChessGame.TeamColor opponentColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        Collection<ChessMove> opponentMoves = getTeamPossibleMoves(opponentColor, true);
        Collection<ChessMove> myTeamMoves = getTeamPossibleMoves(teamColor, true);

        HashSet<ChessPosition> attackerPositions = new HashSet<>();
        HashSet<ChessMove> defenderMoves = new HashSet<>();

        for (ChessMove opponentMove : opponentMoves) {
            if (opponentMove.getEndPosition().equals(kingPosition)) {
                attackerPositions.add(opponentMove.getStartPosition());
            }
        }

        // Identify all moves that could capture an attacker
        for (ChessMove teamMove : myTeamMoves) {
            if (attackerPositions.contains(teamMove.getEndPosition())) {
                defenderMoves.add(teamMove);
            }
        }

        for (ChessMove teamMove : defenderMoves) {
            if (!simulatePieceMovementAndCheck(teamMove, teamColor)) {
                return true;
            }
        }
        return false;
    }



    private boolean kingHasValidMoves(ChessPosition kingPosition){
        ChessPiece kingPiece = board.getPiece(kingPosition);
        ChessGame.TeamColor teamColor = kingPiece.getTeamColor();
        Collection<ChessMove> kingMoves = kingPiece.pieceMoves(board, kingPosition);
        for(ChessMove move : kingMoves){
            if(!simulatePieceMovementAndCheck(move, teamColor)){
                return true;
            }
        }
        return false;
    }

    public boolean simulatePieceMovementAndCheck(ChessMove move, TeamColor teamColor) {
        ChessPiece pieceOnTargetPosition = board.getPiece(move.getEndPosition());
        ChessPiece pieceAtStartingPosition = board.getPiece(move.getStartPosition());

        board.addPiece(move.getEndPosition(), pieceAtStartingPosition);
        board.addPiece(move.getStartPosition(), null);

        boolean isKingAtRisk = isInCheck(teamColor);

        board.addPiece(move.getStartPosition(), pieceAtStartingPosition);
        board.addPiece(move.getEndPosition(), pieceOnTargetPosition);

        return isKingAtRisk;
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
        if(isInCheck(teamColor)){
            return false;
        }
        Collection<ChessMove> teamMoves = getTeamPossibleMoves(teamColor,true);
        for(ChessMove move: teamMoves){
            if(!simulatePieceMovementAndCheck(move, teamColor)){
                return false;
            }
        }
            return true;
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
