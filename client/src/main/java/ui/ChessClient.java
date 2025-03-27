package ui;

import java.util.List;

import model.*;


public class ChessClient {
    private final ServerFacade Server;
    private AuthData userToken;

    public ChessClient(String ServerUrl) {
        Server = new ServerFacade(ServerUrl);
    }

    public boolean login(LoginRequest loginRequest) throws Exception {
        this.userToken = Server.login(loginRequest);
        return this.userToken != null && this.userToken.authToken() != null;
    }

    public boolean register(UserData userData) throws Exception {
        this.userToken = Server.register(userData);
        return this.userToken != null && this.userToken.authToken() != null;
    }

    public List<GameListData> listGames() throws Exception {
        try {
            return Server.listGames(userToken.authToken());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void logout() throws Exception {
        try {
            Server.logout(userToken.authToken());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public GameData createGame(String gameName) throws Exception {
        try {
            return Server.createGame(gameName, userToken.authToken());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void joinGame(int gameID, String playerColor) throws Exception {
        try {
            Server.joinGame(new JoinGameRequest(userToken.authToken(), playerColor, gameID));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean isClientLoggedIn() throws ExceptionResponse {
        if (userToken == null || userToken.authToken() == null) {
            System.out.println("You must be logged in to perform this action.");
            return false;
        }
        return true;
    }
}
