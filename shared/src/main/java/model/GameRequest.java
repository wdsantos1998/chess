package model;

public class GameRequest {
    private String gameName;
    private String authToken;

    public GameRequest(String gameName, String authToken) {
        this.gameName = gameName;
        this.authToken = authToken;
    }

    public String getGameName() {
        return gameName;
    }

    public String getAuthToken() {
        return authToken;
    }

    @Override
    public String toString() {
        return "GameRequest{" +
                "gameName='" + gameName + '\'' +
                ", authToken='" + authToken + '\'' +
                '}';
    }
}
