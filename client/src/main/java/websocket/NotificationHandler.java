package websocket;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public interface NotificationHandler {
    void notify(NotificationMessage notificationMessage) throws Exception;

    void load(LoadGameMessage loadGameMessage) throws Exception;

    void warn(ErrorMessage errorMessage)  throws Exception;
}
