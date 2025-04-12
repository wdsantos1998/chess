package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    private final ChessMove move;
    private final String moveStringRepresentation;
    public MakeMoveCommand(String authToken, Integer gameID, ChessMove move, String moveStringRepresentation) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
        this.moveStringRepresentation = moveStringRepresentation;
    }

    public ChessMove getMove() {
        return move;
    }
    public String getMoveStringRepresentation() {
        return moveStringRepresentation;
    }
}
