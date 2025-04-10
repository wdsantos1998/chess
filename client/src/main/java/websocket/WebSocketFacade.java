package websocket;

import com.google.gson.Gson;
import ui.ExceptionResponse;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade {
    private Session session;
    private NotificationHandler notificationHandler;
    private final Gson gson = new Gson();


    public WebSocketFacade(NotificationHandler notificationHandler, String serverUrl) throws ExceptionResponse {
        try {
            serverUrl = serverUrl.replace("http", "ws");
            URI socketUri = new URI(serverUrl + "/connect");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketUri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case NOTIFICATION:
                            notificationHandler.notify(gson.fromJson(message, Notification.class));
                            break;
                        case LOAD_GAME:
                            notificationHandler.load(gson.fromJson(message, LoadGame.class));
                            break;
                        default:
                            notificationHandler.warn(gson.fromJson(message, Error.class));
                            break;
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException e) {
            throw new ExceptionResponse(500, e.getMessage());
        }
    }



}
