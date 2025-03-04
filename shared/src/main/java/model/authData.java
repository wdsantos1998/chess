package model;
import java.util.Objects;
import java.util.UUID;

public class authData {
    private final String authToken;
    private final String username;


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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        authData authData = (authData) o;
        return Objects.equals(authToken, authData.authToken) && Objects.equals(username, authData.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authToken, username);
    }

    @Override
    public String toString() {
        return "authData{" +
                "authToken='" + authToken + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
