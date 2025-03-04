package model;

public class JoinGameRequest {
    private final String authToken;
    private final String playerColor;
    private final int gameId;


    public JoinGameRequest(String authToken, String playerColor, int gameId) {
        this.authToken = authToken;
        this.playerColor = playerColor;
        this.gameId = gameId;
    }


    public String getAuthToken() {
        return authToken;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public int getGameId() {
        return gameId;
    }

    @Override
    public String toString() {
        return "JoinGameRequest{" +
                "authToken='" + authToken + '\'' +
                ", playerColor='" + playerColor + '\'' +
                ", gameId=" + gameId +
                '}';
    }
}
