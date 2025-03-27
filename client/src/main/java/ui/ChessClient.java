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
        this.userToken = server.login(loginRequest);
        return this.userToken != null && this.userToken.authToken() != null;
    }

    public boolean register(UserData userData) throws Exception {
        this.userToken = server.register(userData);
        return this.userToken != null && this.userToken.authToken() != null;
    }

    public List<GameListData> listGames() throws Exception {
        try {
            return server.listGames(userToken.authToken());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void logout() throws Exception {
        try {
            server.logout(userToken.authToken());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public GameData createGame(String gameName) throws Exception {
        try {
            return server.createGame(gameName, userToken.authToken());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void joinGame(int gameID, String playerColor) throws Exception {
        try {
            server.joinGame(new JoinGameRequest(userToken.authToken(), playerColor, gameID));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean isClientLoggedIn(){
        if (userToken == null || userToken.authToken() == null) {
            return false;
        }
        return true;
    }
}
