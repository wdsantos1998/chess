package websocket;

import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import ui.ExceptionResponse;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ClientEndpoint
public class WebSocketFacade {
    private final Session session;
    private final NotificationHandler notificationHandler;
    private final Gson gson = new Gson();


    public WebSocketFacade(NotificationHandler notificationHandler, String serverUrl) throws ExceptionResponse {
        try {
            serverUrl = serverUrl.replace("http", "ws");
            URI socketUri = new URI(serverUrl + "/connect");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketUri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                public void onMessage(String message) {
                    ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case NOTIFICATION:
                            try{
                            notificationHandler.notify(gson.fromJson(message, NotificationMessage.class));
                            }
                            catch (Exception e){
                                System.out.println("Error in notification handler: " + e.getMessage());
                            }
                            break;
                        case LOAD_GAME:
                            try {
                                notificationHandler.load(gson.fromJson(message, LoadGameMessage.class));
                            }
                            catch (Exception e){
                                System.out.println("Error in notification handler: " + e.getMessage());
                            }
                            break;
                        default:
                            try {
                                notificationHandler.warn(gson.fromJson(message, ErrorMessage.class));
                            }
                            catch (Exception e){
                                System.out.println("Error in notification handler: " + e.getMessage());
                            }
                            break;
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException e) {
            throw new ExceptionResponse(500, e.getMessage());
        }
    }

    public void connectToGame(String authToken, Integer gameID) throws ExceptionResponse {
        try {
            ConnectCommand command = new ConnectCommand(authToken, gameID);
            this.session.getBasicRemote().sendText(gson.toJson(command));
        } catch (IOException e) {
            throw new ExceptionResponse(500, e.getMessage());
        }
    }

    public void leaveGame(String authToken, Integer gameID) throws ExceptionResponse {
        try {
            LeaveCommand command = new LeaveCommand(authToken, gameID);
            this.session.getBasicRemote().sendText(gson.toJson(command));
        } catch (IOException e) {
            throw new ExceptionResponse(500, e.getMessage());
        }
    }

    public void makeMove(String authToken, Integer gameID, ChessPosition from, ChessPosition to) throws ExceptionResponse {
        try {
            MakeMoveCommand command = new MakeMoveCommand(authToken, gameID, new ChessMove(from, to, null));
            this.session.getBasicRemote().sendText(gson.toJson(command));
        } catch (IOException e) {
            throw new ExceptionResponse(500, e.getMessage());
        }
    }
    public void resignFromGame(String authToken, Integer gameID) throws ExceptionResponse {
        try {
            ResignCommand command = new ResignCommand(authToken, gameID);
            this.session.getBasicRemote().sendText(gson.toJson(command));
        } catch (IOException e) {
            throw new ExceptionResponse(500, e.getMessage());
        }
    }

}
