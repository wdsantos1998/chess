package model;

import chess.ChessGame;

import java.util.Random;

public record GameData( String whiteUsername, String blackUsername, String gameName, int gameID, ChessGame game) {
    public GameData( String whiteUsername, String blackUsername, String gameName) {
        this(whiteUsername, blackUsername, gameName,  Math.abs(new Random().nextInt()), new ChessGame());
    }

    public GameData setWhiteUsername(String username) {
        return new GameData(username, this.blackUsername, this.gameName, this.gameID, this.game);
    }

    public GameData setBlackUsername(String username) {
        return new GameData(this.whiteUsername, username, this.gameName, this.gameID, this.game);
    }
}