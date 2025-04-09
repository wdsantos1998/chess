package websocket.commands;

public class ConnectCommand  extends UserGameCommand {
    public ConnectCommand(String authToken, Integer gameID, String username) {
        super(CommandType.CONNECT, authToken, gameID);
    }
}
