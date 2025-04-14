package server;

import com.google.gson.Gson;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import org.eclipse.jetty.websocket.api.Session;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void addConnection(String authToken, int gameID, Session session) {
        connections.put(authToken, new Connection(authToken, gameID, session));
    }

    public void removeConnections(String authToken) {
        connections.remove(authToken);
    }

    /*
        * This method is used to send notifications to all connected users, except the one with the given authToken.
     */
    public void broadcastNotification(String excludeAuthToken, Integer gameID, NotificationMessage notificationMessage) throws Exception {
        if (connections.isEmpty()) {
            System.out.println("No sessions found for token");
            return;
        }
        for (var c : connections.values()) {
            if (c != null && c.session.isOpen() && !c.authToken.equals(excludeAuthToken) && Objects.equals(c.gameID, gameID)) {
                c.sendMessage(gson.toJson(notificationMessage));
            }
        }
    }
    /*
        * This method is used to load game updates to all connected users, except the one with the given authToken.
     */
    public void broadcastGame(String excludeAuthToken, Integer gameID, LoadGameMessage loadGameMessage) throws Exception {
        if (connections.isEmpty()) {
            System.out.println("No sessions found for");
            return;
        }
        for (var c : connections.values()) {
            if (c != null && c.session.isOpen() && !c.authToken.equals(excludeAuthToken) && Objects.equals(c.gameID, gameID)) {
                c.session.getRemote().sendString(gson.toJson(new LoadGameMessage(loadGameMessage.getGame())));
            }
        }
    }
}
