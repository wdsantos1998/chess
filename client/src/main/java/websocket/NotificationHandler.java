package websocket;

import websocket.messages.LoadGame;
import websocket.messages.Notification;

public interface NotificationHandler {
    void notify(Notification notification);

    void load(LoadGame loadGame);

    void warn(Error error);
}
