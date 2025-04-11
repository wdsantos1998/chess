package websocket.commands;

import chess.ChessGame;

public class JoinPlayerCommand extends UserGameCommand {
    private final String teamColor;

    public JoinPlayerCommand(String authToken, Integer gameId, String teamColor) {
        super(CommandType.JOIN_PLAYER, authToken, gameId);
        this.teamColor = teamColor;
    }
    public String getTeamColor() {
        return teamColor;
    }
}
