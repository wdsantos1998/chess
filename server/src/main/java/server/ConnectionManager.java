package server;

import com.google.gson.Gson;
import websocket.messages.Notification;

import javax.websocket.Session;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void addConnection(String authToken, int gameID, Session session) {
        connections.put(authToken, new Connection(authToken, gameID, session));
    }

    public void removeConnections(String authToken) {
        connections.remove(authToken);
    }

    public void broadcast(String excludeAuthToken, Integer gameID, Notification notification) throws Exception {
        for (var c : connections.values()) {
            if (c.session.isOpen() && !c.authToken.equals(excludeAuthToken) && Objects.equals(c.gameID, gameID)) {
                c.sendMessage(new Gson().toJson(notification));
            }

        }
    }
}
