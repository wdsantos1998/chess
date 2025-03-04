package model;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameRequest that = (GameRequest) o;
        return Objects.equals(gameName, that.gameName) && Objects.equals(authToken, that.authToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameName, authToken);
    }

    @Override
    public String toString() {
        return "GameRequest{" +
                "gameName='" + gameName + '\'' +
                ", authToken='" + authToken + '\'' +
                '}';
    }
}
