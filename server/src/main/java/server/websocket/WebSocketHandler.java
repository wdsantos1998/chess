package server.websocket;

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
import websocket.commands.JoinPlayerCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.UserGameCommand;
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
                connectionManager.removeConnections(leaveCommand.getAuthToken());
                userAuthData = dataAccess.getAuthData(leaveCommand.getAuthToken());
                gameData = dataAccess.getGameData(leaveCommand.getGameID());
                String leaveMessage = String.format("User %s left game %s", userAuthData.username(), gameData.gameName());
                connectionManager.broadcast(leaveCommand.getAuthToken(), leaveCommand.getGameID(), new Notification(leaveMessage));
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

