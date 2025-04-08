package server;

import javax.websocket.Session;

public class Connection {
    public String authToken;
    public int gameID;
    public Session session;

    public Connection(String authToken, int gameID, Session session) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.session = session;
    }

    public void sendMessage(String msg) throws Exception
    {
        session.getBasicRemote().sendText(msg);
    }
}
