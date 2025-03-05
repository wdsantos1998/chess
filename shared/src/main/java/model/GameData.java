package model;

import chess.ChessGame;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public record GameData(@Nullable String whiteUsername, @Nullable String blackUsername, String gameName, int gameID, ChessGame game) {
    public GameData(@Nullable String whiteUsername, @Nullable String blackUsername, String gameName) {
        this(whiteUsername, blackUsername, gameName,  Math.abs(new Random().nextInt()), new ChessGame());
    }

    public GameData setWhiteUsername(String username) {
        return new GameData(username, this.blackUsername, this.gameName, this.gameID, this.game);
    }

    public GameData setBlackUsername(String username) {
        return new GameData(this.whiteUsername, username, this.gameName, this.gameID, this.game);
    }
}