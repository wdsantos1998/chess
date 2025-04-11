package websocket.commands;

public class LoadGameDataCommand extends UserGameCommand {
    public LoadGameDataCommand(String authToken, Integer gameID) {
        super(CommandType.LOAD_GAME_DATA, authToken, gameID);
    }

}
