package model;
import java.util.UUID;

public class authData {
    private String authToken;
    private String username;


    public authData(String username) {
        this.authToken = UUID.randomUUID().toString() + username;
        this.username = username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "authData{" +
                "authToken='" + authToken + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
