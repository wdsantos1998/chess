package model;

import chess.ChessGame;

import java.util.UUID;

import static java.lang.Integer.parseInt;

public class Game {
    private int gameId;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;
    private ChessGame game;


    public Game(String whiteUsername, String blackUsername, String gameName) {
        this.gameId = parseInt(UUID.randomUUID().toString());
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = new ChessGame();
    }
    public int getGameId() {
        return gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }
    public void setGame(ChessGame game) {
        this.game = game;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public ChessGame getGame() {
        return game;
    }

}
