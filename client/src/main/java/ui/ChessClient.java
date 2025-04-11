package ui;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.*;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;


public class ChessClient {
    private final ServerFacade server;
    private WebSocketFacade webSocket;
    private AuthData userToken;
    private Map<Integer, Integer> gameMap;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) throws Exception {
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
        server = new ServerFacade(serverUrl);
        gameMap = new HashMap<>();
    }

    public boolean login(LoginRequest loginRequest) throws Exception {
        try {
            this.userToken = server.login(loginRequest);
            return this.userToken != null && this.userToken.authToken() != null;
        } catch (ExceptionResponse e) {
            throw new Exception(handleException(e.getMessage()));
        }

    }

    public boolean register(UserData userData) throws Exception {
        try {
            this.userToken = server.register(userData);
            return this.userToken != null && this.userToken.authToken() != null;
        } catch (Exception e) {
            throw new Exception(handleException(e.getMessage()));
        }
    }

    public List<GameListData> listGames() throws Exception {
        try {
            return turnGameListIntoSequenceOfIndexes(server.listGames(userToken.authToken()));
        } catch (Exception e) {
            throw new Exception(handleException(e.getMessage()));
        }
    }

    public void logout() throws Exception {
        try {
            server.logout(userToken.authToken());
            userToken = null;
        } catch (Exception e) {
            throw new Exception(handleException(e.getMessage()));
        }
    }

    public GameData createGame(String gameName) throws Exception {
        try {
            return addToGameIndexList(server.createGame(gameName, userToken.authToken()));
        } catch (Exception e) {
            throw new Exception(handleException(e.getMessage()));
        }
    }

    public void joinGame(int gameID, String playerColor) throws Exception {
        try {
            turnGameListIntoSequenceOfIndexes(server.listGames(userToken.authToken()));
            if(!gameMap.containsKey(gameID)){
                throw new Exception("Error: gameID not found");
            }
                server.joinGame(new JoinGameRequest(userToken.authToken(),playerColor, gameMap.get(gameID)));
            try{
                webSocket = new WebSocketFacade(notificationHandler, serverUrl);
            }
            catch (Exception e){
                throw new Exception(handleException(e.getMessage()));
            }
            webSocket.joinPlayer(userToken.authToken(), gameMap.get(gameID), playerColor);
        } catch (Exception e) {
            throw new Exception(handleException(e.getMessage()));
        }
    }

    public void joinGameAsObserver(int gameID) throws Exception {
        try {
            turnGameListIntoSequenceOfIndexes(server.listGames(userToken.authToken()));
            if(!gameMap.containsKey(gameID)){
                throw new Exception("Error: gameID not found");
            }
            try{
                webSocket = new WebSocketFacade(notificationHandler, serverUrl);
            }
            catch (Exception e){
                throw new Exception(handleException(e.getMessage()));
            }
            webSocket.joinGameAsObserver(userToken.authToken(), gameMap.get(gameID));
        } catch (Exception e) {
            throw new Exception(handleException(e.getMessage()));
        }
    }

    public void leaveGame(int gameID) throws Exception {
        try {
            webSocket.leaveGame(userToken.authToken(), gameMap.get(gameID));
        } catch (Exception e) {
            throw new Exception(handleException(e.getMessage()));
        }
    }
    public void redrawBoard(int gameID) throws Exception {
        try {
            webSocket.redrawGame(userToken.authToken(), gameMap.get(gameID));
        } catch (Exception e) {
            throw new Exception(handleException(e.getMessage()));
        }
    }

    public boolean isClientLoggedIn() {
        if (userToken == null || userToken.authToken() == null) {
            return false;
        }
        return true;
    }

    private List<GameListData> turnGameListIntoSequenceOfIndexes(List<GameListData> gameList) {
        this.gameMap.clear();

        List<GameListData> returnGameList = new ArrayList<>();
        int index = 1;

        for (GameListData game : gameList) {
            this.gameMap.put(index, game.gameID());
            returnGameList.add(new GameListData(index, game.whiteUsername(), game.blackUsername(), game.gameName()));
            index++;
        }

        return returnGameList;
    }

    private GameData addToGameIndexList(GameData gameData) {
        int gameID = gameMap.size() + 1;
        gameMap.put(gameID, gameData.gameID());
        return new GameData(gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameID, gameData.game());
    }

    private String handleException(String e) {
        try {
            JsonObject obj = JsonParser.parseString(e).getAsJsonObject();
            return obj.get("message").getAsString();
        } catch (Exception parseEx) {
            //If the exception is not a JSON object, then it is a string
            return e;
        }
    }
}
