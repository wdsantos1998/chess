package websocket.commands;

import chess.ChessGame;

public class JoinPlayerCommand extends UserGameCommand {
    private final ChessGame.TeamColor teamColor;

    public JoinPlayerCommand(CommandType commandType, String authToken, Integer gameId, ChessGame.TeamColor teamColor) {
        super(CommandType.JOIN_PLAYER, authToken, gameId);
        this.teamColor = teamColor;
    }
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }
}
