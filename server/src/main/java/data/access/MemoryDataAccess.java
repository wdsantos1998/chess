package data.access;

import model.GameData;
import model.GameListData;
import model.UserData;
import model.AuthData;

import java.util.*;
import java.util.stream.Collectors;

public class MemoryDataAccess implements DataAccess{
    private final Map<String, UserData> userDataMap = new HashMap<>();
    private final  Map<String, AuthData> authDataMap = new HashMap<>();
    private final Map<Integer, GameData> gameDataMap = new HashMap<>();
    @Override
    public boolean addUser(UserData userData) throws DataAccessExceptionHTTP {
        userDataMap.put(userData.username(), userData);
        return userDataMap.containsValue(userData);
    }

    @Override
    public UserData getUser(String username) throws DataAccessExceptionHTTP {
        return userDataMap.get(username);
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessExceptionHTTP {
        return authDataMap.get(authToken);
    }

    @Override
    public boolean isValidAuthToken(String authToken) throws DataAccessExceptionHTTP {
        return authDataMap.get(authToken) != null && authDataMap.get(authToken).authToken().equalsIgnoreCase(authToken);
    }

    @Override
    public AuthData createAuthData(String username) throws DataAccessExceptionHTTP {
        AuthData newAuthData =  new AuthData(username);
        authDataMap.put(newAuthData.authToken(),newAuthData);
        if(!authDataMap.containsValue(newAuthData)){
            throw new DataAccessExceptionHTTP(500,"Error in creating new token. Please try again.");
        }
        return newAuthData;
    }

    @Override
    public boolean deleteAuthToken(String authToken) throws DataAccessExceptionHTTP {
        authDataMap.remove(authToken);
        if(authDataMap.containsKey(authToken)){
            throw new DataAccessExceptionHTTP(500,"Error in deleting token. Please try again.");
        }
        return true;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessExceptionHTTP {
        int gameId = gameData.gameID();
        gameDataMap.remove(gameId);
        gameDataMap.put(gameId, gameData);
    }

    @Override
    public GameData createGame(GameData gameData) throws DataAccessExceptionHTTP {
        gameDataMap.put(gameData.gameID(), gameData);
        if(!gameDataMap.containsValue(gameData)){
            throw new DataAccessExceptionHTTP(500,"Error in creating new game. Please try again.");
        }
        return gameData;
    }

    @Override
    public GameData getGameData(int gameId) throws DataAccessExceptionHTTP {
        return gameDataMap.get(gameId);
    }

    @Override
    public List<GameListData> listGames() throws DataAccessExceptionHTTP {
        return gameDataMap.values().stream().map(game ->  new GameListData(
                        game.gameID(),
                        game.whiteUsername(),
                        game.blackUsername(),
                        game.gameName()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean clear() throws DataAccessExceptionHTTP {
        userDataMap.clear();
        authDataMap.clear();
        gameDataMap.clear();
        if (!userDataMap.isEmpty() || !authDataMap.isEmpty() || !gameDataMap.isEmpty()) {
            throw new DataAccessExceptionHTTP(500,"Error in clearing memory. Please try again.");
        }
        return true;
    }
}
