package ui;

import java.util.List;

import model.*;


public class ChessClient {
    private final ServerFacade server;
    private AuthData userToken;

    public ChessClient(String ServerUrl) {
        server = new ServerFacade(ServerUrl);
    }

    public boolean login(LoginRequest loginRequest) throws Exception {
        try {
            this.userToken = server.login(loginRequest);
            return this.userToken != null && this.userToken.authToken() != null;
        }
        catch (Exception e) {
            throw new Exception("Failed to login");
        }

    }

    public boolean register(UserData userData) throws Exception {
        try {
            this.userToken = server.register(userData);
            return this.userToken != null && this.userToken.authToken() != null;
        }
        catch (Exception e) {
            throw new Exception("Failed to register");
        }
    }

    public List<GameListData> listGames() throws Exception {
        try {
            return server.listGames(userToken.authToken());
        } catch (Exception e) {
           throw new Exception("Failed to list games");
        }
    }

    public void logout() throws Exception {
        try {
            server.logout(userToken.authToken());
            userToken = null;
        } catch (Exception e) {
            throw new Exception("Failed to logout");
        }
    }

    public GameData createGame(String gameName) throws Exception {
        try {
            return server.createGame(gameName, userToken.authToken());
        } catch (Exception e) {
            throw new Exception("Failed to create game");
        }
    }

    public void joinGame(int gameID, String playerColor) throws Exception {
        try {
            server.joinGame(new JoinGameRequest(userToken.authToken(), playerColor, gameID));
        } catch (Exception e) {
            throw new Exception("Failed to join game");
        }
    }

    public boolean isClientLoggedIn(){
        if (userToken == null || userToken.authToken() == null) {
            return false;
        }
        return true;
    }
}
