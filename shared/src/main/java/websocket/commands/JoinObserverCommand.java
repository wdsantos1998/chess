package websocket.commands;

public class JoinObserverCommand extends UserGameCommand {
    public JoinObserverCommand(String authToken, Integer gameId) {
        super(CommandType.JOIN_OBSERVER, authToken, gameId);
    }
}

