package model;

import java.util.UUID;

public record AuthData(String username, String authToken) {
    public AuthData(String username) {
        this(username, UUID.randomUUID()+ username);
    }
    //Just to commit something 1
}