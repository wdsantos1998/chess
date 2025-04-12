package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import data.access.DataAccessExceptionHTTP;
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
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;

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
            case JOIN_PLAYER:
                joinGame(gson.fromJson(message, JoinPlayerCommand.class), session);
                break;
            case LEAVE:
                leaveGame(gson.fromJson(message, LeaveCommand.class), session);
                break;
            case LOAD_GAME_DATA:
                loadGameData(gson.fromJson(message, LoadGameDataCommand.class), session);
                break;
            case JOIN_OBSERVER:
                joinGameAsObserver(gson.fromJson(message, JoinObserverCommand.class), session);
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
        if(!isUserAuthorized(makeMoveCommand.getAuthToken(), makeMoveCommand.getGameID())) {
            String errorMessage = "Error: You are not a player, so you cannot make a move in the game.";
            session.getRemote().sendString(gson.toJson(new Error(errorMessage)));
            return;
        }
        AuthData userAuthData = dataAccess.getAuthData(makeMoveCommand.getAuthToken());
        GameData gameData = dataAccess.getGameData(makeMoveCommand.getGameID());
        ChessGame chessGameData = gameData.game();
        try {
            chessGameData.makeMove(makeMoveCommand.getMove());
            GameData updatedGameData = new GameData(gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameData.gameID(), chessGameData);
            dataAccess.updateGame(updatedGameData);
            String[] partsMove = makeMoveCommand.getMoveStringRepresentation().split(" ");
            String moveMessage = String.format("User %s made a move from %s to %s in game %s. Redraw board to update.", userAuthData.username(),partsMove[0].trim().toLowerCase(),partsMove[1].trim().toLowerCase(), gameData.gameName());
            session.getRemote().sendString(gson.toJson(new LoadGame(updatedGameData.game())));
            connectionManager.broadcast(makeMoveCommand.getAuthToken(), makeMoveCommand.getGameID(), new Notification(moveMessage));
        }
        catch (Exception e) {
            String errorMessage = String.format("Error: %s", e.getMessage());
            session.getRemote().sendString(gson.toJson(new Error(errorMessage)));
        }
    }

    public void joinGameAsObserver(JoinObserverCommand joinObserverCommand, Session session) throws Exception {
        connectionManager.addConnection(joinObserverCommand.getAuthToken(), joinObserverCommand.getGameID(), session);
        AuthData userAuthData = dataAccess.getAuthData(joinObserverCommand.getAuthToken());
        String observeMessage = String.format("User %s joined game as an observer", userAuthData.username());
        connectionManager.broadcast(joinObserverCommand.getAuthToken(), joinObserverCommand.getGameID(), new Notification(observeMessage));
    }

    public void loadGameData(LoadGameDataCommand loadGameDataCommand, Session session) throws Exception {
        GameData gameData = dataAccess.getGameData(loadGameDataCommand.getGameID());
        session.getRemote().sendString(gson.toJson(new LoadGame(gameData.game())));
    }

    public void joinGame(JoinPlayerCommand joinPlayerCommand, Session session) throws Exception {
        connectionManager.addConnection(joinPlayerCommand.getAuthToken(), joinPlayerCommand.getGameID(), session);
        AuthData userAuthData = dataAccess.getAuthData(joinPlayerCommand.getAuthToken());
        GameData gameData = dataAccess.getGameData(joinPlayerCommand.getGameID());
        String playerColor;
        if (gameData.whiteUsername().equals(userAuthData.username())) {
            playerColor = "white";
        } else {
            playerColor = "black";
        }
        String messageToSend = String.format("User %s joined game as %s player", userAuthData.username(), playerColor);
        connectionManager.broadcast(joinPlayerCommand.getAuthToken(), joinPlayerCommand.getGameID(), new Notification(messageToSend));
    }

    public void leaveGame(LeaveCommand leaveCommand, Session session) throws Exception {
        if(!isUserAuthorized(leaveCommand.getAuthToken(), leaveCommand.getGameID())) {
            String errorMessage = "Error: You are not a player, so you cannot leave the game.";
            session.getRemote().sendString(gson.toJson(new Error(errorMessage)));
            return;
        }
        AuthData userAuthData = dataAccess.getAuthData(leaveCommand.getAuthToken());
        GameData gameData = dataAccess.getGameData(leaveCommand.getGameID());
        if (gameData.whiteUsername().equals(userAuthData.username())) {
            dataAccess.updateGame(new GameData(null, gameData.blackUsername(), gameData.gameName(), gameData.gameID(), gameData.game()));
        } else {
            dataAccess.updateGame(new GameData(gameData.whiteUsername(), null, gameData.gameName(), gameData.gameID(), gameData.game()));
        }
        connectionManager.removeConnections(leaveCommand.getAuthToken());
        String leaveMessage = String.format("User %s left game %s", userAuthData.username(), gameData.gameName());
        connectionManager.broadcast(leaveCommand.getAuthToken(), leaveCommand.getGameID(), new Notification(leaveMessage));
    }

    public void resignGame(ResignCommand resignCommand, Session session) throws Exception {
        if(!isUserAuthorized(resignCommand.getAuthToken(), resignCommand.getGameID())) {
            String errorMessage = "Error: You are not a player, so you cannot resign from the game.";
            session.getRemote().sendString(gson.toJson(new Error(errorMessage)));
            return;
        }
        AuthData userAuthData = dataAccess.getAuthData(resignCommand.getAuthToken());
        GameData gameData = dataAccess.getGameData(resignCommand.getGameID());
        ChessGame chessGame = gameData.game();
        chessGame.setGameOver(true);
        dataAccess.updateGame(new GameData(gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameData.gameID(), chessGame));
        String resignMessage = String.format("User %s resigned from game. You won", userAuthData.username());
        connectionManager.broadcast(resignCommand.getAuthToken(), resignCommand.getGameID(), new Notification(resignMessage));
    }

    public boolean isUserAuthorized(String authToken, Integer gameID) throws Exception {
        AuthData userAuthData = dataAccess.getAuthData(authToken);
        GameData gameData = dataAccess.getGameData(gameID);
        return userAuthData.username().equals(gameData.whiteUsername()) || userAuthData.username().equals(gameData.blackUsername());
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

