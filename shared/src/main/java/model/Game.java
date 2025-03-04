package model;
import chess.ChessGame;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class Game {
    private final int  gameId;
    @Nullable
    private String whiteUsername;
    @Nullable
    private String blackUsername;
    private String gameName;
    private ChessGame game;


    public Game(@Nullable String whiteUsername, @Nullable String blackUsername, String gameName) {
        this.gameId = generateUniqueId();
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = new ChessGame();
    }

    private int generateUniqueId() {
        return UUID.randomUUID().hashCode();
    }

    public int getGameId() {
        return gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    @Nullable
    public String getBlackUsername() {
        return blackUsername;
    }

    @Nullable
    public String getWhiteUsername() {
        return whiteUsername;
    }
    public void setGame(ChessGame game) {
        this.game = game;
    }

    public void setWhiteUsername(@Nullable String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public void setBlackUsername(@Nullable String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public ChessGame getGame() {
        return game;
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameId=" + gameId +
                ", whiteUsername='" + whiteUsername + '\'' +
                ", blackUsername='" + blackUsername + '\'' +
                ", gameName='" + gameName + '\'' +
                ", game=" + game +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Game game1 = (Game) o;
        return gameId == game1.gameId && Objects.equals(whiteUsername, game1.whiteUsername) && Objects.equals(blackUsername, game1.blackUsername) && Objects.equals(gameName, game1.gameName) && Objects.equals(game, game1.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, whiteUsername, blackUsername, gameName, game);
    }
}
