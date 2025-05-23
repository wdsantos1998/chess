package server.websocket;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;

import data.access.MySqlDataAccess;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.ConnectionManager;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.Collection;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final Gson gson = new Gson();
    private MySqlDataAccess dataAccess;

    public void setMySqlDataAccess(MySqlDataAccess  dataAccess) {
        this.dataAccess = dataAccess;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT:
                connectToGame(gson.fromJson(message, ConnectCommand.class), session);
                break;
            case LEAVE:
                leaveGame(gson.fromJson(message, LeaveCommand.class), session);
                break;
            case MAKE_MOVE:
                makeMove(gson.fromJson(message, MakeMoveCommand.class), session);
                break;
            case RESIGN:
                resignGame(gson.fromJson(message, ResignCommand.class), session);
                break;
            default:
                throw new IllegalArgumentException("Unknown command type: " + command.getCommandType());
        }
    }

    public void makeMove(MakeMoveCommand makeMoveCommand, Session session) throws Exception {
        try {
            AuthData userAuthData = dataAccess.getAuthData(makeMoveCommand.getAuthToken());
            GameData gameData = dataAccess.getGameData(makeMoveCommand.getGameID());
            ChessGame chessGameData = gameData.game();
            if(chessGameData.isGameOver()){
                session.getRemote().sendString(new Gson().toJson(new ErrorMessage("Game is over.")));
                return;
            }
            if(!isUserAuthorized(makeMoveCommand.getAuthToken(), makeMoveCommand.getGameID())) {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("You are not a player, so you cannot make a move in the game.")));
                return;
            }
            ChessMove move = makeMoveCommand.getMove();
            if(!isValidMove(move, gameData, userAuthData.username())) {
                String errorMessage = "Invalid move.";
                session.getRemote().sendString(gson.toJson(new ErrorMessage(errorMessage)));
                return;
            }
            chessGameData.makeMove(move);
            GameData updatedGameData = new GameData(gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameData.gameID(), chessGameData);
            dataAccess.updateGame(updatedGameData);
            String moveStartPosition = chessPositionToString(move.getStartPosition());
            String moveEndPosition = chessPositionToString(move.getEndPosition());
            String moveMessage = String.format("User %s made a move from %s to %s in game %s.", userAuthData.username(),moveStartPosition , moveEndPosition, gameData.gameName());
            connectionManager.broadcastNotification(makeMoveCommand.getAuthToken(), makeMoveCommand.getGameID(), new NotificationMessage(moveMessage));

            ChessGame.TeamColor opponentColor = gameData.whiteUsername().equals(userAuthData.username())? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            String opponentUsername = gameData.whiteUsername().equals(userAuthData.username()) ? gameData.blackUsername() : gameData.whiteUsername();
            session.getRemote().sendString(gson.toJson(new LoadGameMessage(chessGameData)));
            connectionManager.broadcastGame(makeMoveCommand.getAuthToken(), makeMoveCommand.getGameID(), new LoadGameMessage(chessGameData));
            notifyIfChecked(gameData,opponentUsername,opponentColor,makeMoveCommand,session);
        }
        catch (Exception e) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage(e.getMessage())));
        }
    }

    public void connectToGame(ConnectCommand connectCommand, Session session) throws Exception {
        try {
            AuthData userAuthData = dataAccess.getAuthData(connectCommand.getAuthToken());
            GameData gameData = dataAccess.getGameData(connectCommand.getGameID());
            if(gameData.game() == null){
                System.out.println("Sending error to the frontend");
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Game not found. Bad game ID")));
                return;
            }
            if(connectCommand.getAuthToken() == null) {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("User not found.")));
                return;
            }
            connectionManager.addConnection(connectCommand.getAuthToken(), connectCommand.getGameID(), session);

            String playerText;
            if (gameData.whiteUsername().equals(userAuthData.username())) {
                playerText = "white player";
            }
            else if (gameData.blackUsername().equals(userAuthData.username())) {
                playerText = "black player";
            }
            else {
                playerText = "an observer";
            }
            String messageToSend = String.format("User %s joined game as %s", userAuthData.username(), playerText);
            connectionManager.broadcastNotification(connectCommand.getAuthToken(), connectCommand.getGameID(), new NotificationMessage(messageToSend));
            session.getRemote().sendString(gson.toJson(new LoadGameMessage(gameData.game())));
        }
        catch (Exception e) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage(e.getMessage())));
        }
    }

    public void leaveGame(LeaveCommand leaveCommand, Session session) throws Exception {
        try {
            AuthData userAuthData = dataAccess.getAuthData(leaveCommand.getAuthToken());
            GameData gameData = dataAccess.getGameData(leaveCommand.getGameID());
            if (isUserAuthorized(leaveCommand.getAuthToken(), leaveCommand.getGameID())) {
                if (gameData.whiteUsername().equals(userAuthData.username())) {
                    dataAccess.updateGame(new GameData(null, gameData.blackUsername(), gameData.gameName(), gameData.gameID(), gameData.game()));
                } else {
                    dataAccess.updateGame(new GameData(gameData.whiteUsername(), null, gameData.gameName(), gameData.gameID(), gameData.game()));
                }
            }
            connectionManager.removeConnections(leaveCommand.getAuthToken());
            String leaveMessage = String.format("User %s left game %s", userAuthData.username(), gameData.gameName());
            connectionManager.broadcastNotification(userAuthData.authToken(), gameData.gameID(), new NotificationMessage(leaveMessage));
        }
        catch (Exception e) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage(e.getMessage())));
        }
    }

    public void resignGame(ResignCommand resignCommand, Session session) throws Exception {
        try {
            AuthData userAuthData = dataAccess.getAuthData(resignCommand.getAuthToken());
            GameData gameData = dataAccess.getGameData(resignCommand.getGameID());
            if(userAuthData == null || gameData == null){
                session.getRemote().sendString(gson.toJson(new ErrorMessage("We couldn't find the game data")));
                return;
            }
            if(gameData.game().isGameOver()){
                session.getRemote().sendString(gson.toJson(new ErrorMessage("You cannot resign a game that is over.")));
                return;
            }

            if (!isUserAuthorized(resignCommand.getAuthToken(), resignCommand.getGameID())) {
                String errorMessage = "You are not a player, so you cannot resign from the game.";
                session.getRemote().sendString(gson.toJson(new ErrorMessage(errorMessage)));
                return;
            }
            ChessGame chessGame = gameData.game();
            chessGame.setGameOver(true);
            dataAccess.updateGame(new GameData(gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameData.gameID(), chessGame));
            String resignMessage = String.format("User %s resigned from game. You won", userAuthData.username());
            connectionManager.broadcastNotification(resignCommand.getAuthToken(), resignCommand.getGameID(), new NotificationMessage(resignMessage));
            session.getRemote().sendString(gson.toJson(new NotificationMessage("You resigned the game.")));
        }
        catch (Exception e) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage(e.getMessage())));
        }
    }

    public boolean isUserAuthorized(String authToken, Integer gameID) throws Exception {
        AuthData userAuthData = dataAccess.getAuthData(authToken);
        GameData gameData = dataAccess.getGameData(gameID);
        return userAuthData.username().equals(gameData.whiteUsername()) || userAuthData.username().equals(gameData.blackUsername());
    }

    public boolean isValidMove(ChessMove move, GameData gameData, String playerUsername) {
        ChessGame.TeamColor currentTurn = gameData.game().getTeamTurn();
        ChessGame.TeamColor userMovingColor = null;
        if(gameData.whiteUsername().equals(playerUsername)){
            userMovingColor = ChessGame.TeamColor.WHITE;
        }
        else{
            userMovingColor = ChessGame.TeamColor.BLACK;
        }
        if(userMovingColor != currentTurn ){
            return false;
        }
        ChessBoard board = gameData.game().getBoard();
        ChessGame.TeamColor pieceMovingColor = board.getPiece(move.getStartPosition()).getTeamColor();
        if (pieceMovingColor != currentTurn) {
            return false;
        }
        Collection<ChessMove> validMoves = gameData.game().validMoves(move.getStartPosition());
        for (ChessMove validMove : validMoves) {
            if (validMove.getEndPosition().equals(move.getEndPosition())) {
                return true;
            }
        }
        return false;
    }

    private void notifyIfChecked(GameData gameData, String opponentUsername ,ChessGame.TeamColor opponentColor, MakeMoveCommand makeMoveCommand, Session session)  throws Exception {
        try {
            if (gameData.game().isInCheckmate(opponentColor)) {
                gameData.game().setGameOver(true);
                dataAccess.updateGame(new GameData(gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(),gameData.gameID(),gameData.game()));
            }
        }
        catch (Exception e){
            session.getRemote().sendString(gson.toJson(new ErrorMessage(e.getMessage())));
        }
    }


    private String chessPositionToString(ChessPosition position){
        char file = (char) ('a' + (position.getColumn() - 1));

        int rank = position.getRow();

        return "" + file + rank;
    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        System.out.println("WebSocket connection opened");
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("WebSocket closed: " + reason + " (Code " + statusCode + ")");
    }
}

