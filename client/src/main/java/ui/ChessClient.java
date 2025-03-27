package ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import model.*;
import org.glassfish.grizzly.utils.Exceptions;


public class ChessClient {
    private final ServerFacade server;
    private AuthData userToken;
    private Map <Integer, Integer> gameMap;

    public ChessClient(String ServerUrl) {
        server = new ServerFacade(ServerUrl);
        gameMap = new HashMap<>();
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
            return turnGameListIntoSequenceOfIndexes(server.listGames(userToken.authToken()));
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
            return addToGameIndexList(server.createGame(gameName, userToken.authToken()));
        } catch (Exception e) {
            throw new Exception("Failed to create game");
        }
    }

    public void joinGame(int gameID, String playerColor) throws Exception {
        try {
            server.joinGame(new JoinGameRequest(userToken.authToken(), playerColor, gameMap.get(gameID)));
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

    private List<GameListData> turnGameListIntoSequenceOfIndexes(List<GameListData> gameList) {
        List<GameListData> returnGameList = new ArrayList<>();
        gameList.forEach(game -> {
            int mapLength = this.gameMap.size();
            if(mapLength == 0){
                int currentIndexOfGame = mapLength+1;
                this.gameMap.put(currentIndexOfGame, game.gameID());
                returnGameList.add(new GameListData(currentIndexOfGame, game.whiteUsername(), game.blackUsername(), game.gameName()));
            }
            else{
                if(!this.gameMap.containsValue(game.gameID())){
                    int currentIndexOfGame = mapLength+1;
                    this.gameMap.put(currentIndexOfGame, game.gameID());
                    returnGameList.add(new GameListData(currentIndexOfGame, game.whiteUsername(), game.blackUsername(), game.gameName()));
                }
            }
        });
        return returnGameList;
    }

    private GameData addToGameIndexList(GameData gameData) {
        int gameID = gameMap.size() + 1;
        gameMap.put(gameID, gameData.gameID());
        return new GameData(gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), gameID, gameData.game());
    }
}
