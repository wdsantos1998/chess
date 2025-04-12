package server.websocket;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;

import javax.websocket.*;

import data.access.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;

import data.access.MySqlDataAccess;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.ConnectionManager;
import service.AppService;
import websocket.commands.*;
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
        AuthData userAuthData = null;
        GameData gameData = null;
        switch (command.getCommandType()) {
            case JOIN_PLAYER:
                JoinPlayerCommand joinPlayerCommand = gson.fromJson(message, JoinPlayerCommand.class);
                connectionManager.addConnection(joinPlayerCommand.getAuthToken(), joinPlayerCommand.getGameID(), session);
                userAuthData = dataAccess.getAuthData(joinPlayerCommand.getAuthToken());
                gameData = dataAccess.getGameData(joinPlayerCommand.getGameID());
                String messageToSend = String.format("User %s joined game %s", userAuthData.username(), gameData.gameName());
                connectionManager.broadcast(joinPlayerCommand.getAuthToken(), command.getGameID(), new Notification(messageToSend));
                break;
            case LEAVE:
                LeaveCommand leaveCommand = gson.fromJson(message, LeaveCommand.class);
                userAuthData = dataAccess.getAuthData(leaveCommand.getAuthToken());
                gameData = dataAccess.getGameData(leaveCommand.getGameID());
                if(gameData.whiteUsername().equals(userAuthData.username())) {
                    dataAccess.updateGame(new GameData(null, gameData.blackUsername(),gameData.gameName(), gameData.gameID(), gameData.game()));
                    ChessBoard game = gameData.game().getBoard();
                }
                else{
                    dataAccess.updateGame(new GameData(gameData.whiteUsername(), null, gameData.gameName(), gameData.gameID(), gameData.game()));
                }
                connectionManager.removeConnections(leaveCommand.getAuthToken());
                String leaveMessage = String.format("User %s left game %s", userAuthData.username(), gameData.gameName());
                connectionManager.broadcast(leaveCommand.getAuthToken(), leaveCommand.getGameID(), new Notification(leaveMessage));
                break;
            case LOAD_GAME_DATA:
                LoadGameDataCommand loadGameDataCommand = gson.fromJson(message, LoadGameDataCommand.class);
                gameData = dataAccess.getGameData(loadGameDataCommand.getGameID());
                session.getRemote().sendString(gson.toJson(new LoadGame(gameData.game())));
                break;
            case JOIN_OBSERVER:
                JoinObserverCommand joinObserverCommand = gson.fromJson(message, JoinObserverCommand.class);
                connectionManager.addConnection(joinObserverCommand.getAuthToken(), joinObserverCommand.getGameID(), session);
                userAuthData = dataAccess.getAuthData(joinObserverCommand.getAuthToken());
                gameData = dataAccess.getGameData(joinObserverCommand.getGameID());
                String observeMessage = String.format("User %s joined game %s as observer", userAuthData.username(), gameData.gameName());
                connectionManager.broadcast(joinObserverCommand.getAuthToken(), joinObserverCommand.getGameID(), new Notification(observeMessage));
                break;
            default:
                throw new IllegalArgumentException("Unknown command type: " + command.getCommandType());
        }
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

