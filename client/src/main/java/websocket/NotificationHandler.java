package websocket;

import websocket.messages.LoadGame;
import websocket.messages.Notification;

public interface NotificationHandler {
    void notify(Notification notification) throws Exception;

    void load(LoadGame loadGame) throws Exception;

    void warn(Error error)  throws Exception;
}
