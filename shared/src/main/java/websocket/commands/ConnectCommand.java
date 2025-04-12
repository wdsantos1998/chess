package websocket.commands;

public class ConnectCommand  extends UserGameCommand {
    private final String playerTypeOrColor;
    public ConnectCommand(String authToken, Integer gameID, String playerTypeOrColor) {
        super(CommandType.CONNECT, authToken, gameID);
        this.playerTypeOrColor = playerTypeOrColor;
    }

    public String getPlayerTypeOrColor() {
        return playerTypeOrColor;
    }
}
