package websocket;

import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class WebSocketHandler {
    private Session session;
    private final URI uri;

    public WebSocketHandler(URI uri) {
        this.uri = uri;
    }

    public void connect() throws Exception{
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
    }


    public void sendMessage(String message) throws Exception {
        session.getBasicRemote().sendText(message);
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
    }

    @OnClose
    public void onClose() {
        System.out.println("Connection closed.");
    }

    public void disconnect() throws Exception {
        session.close();
    }
}
