package data.access;

import model.Game;
import model.GameListData;
import model.User;
import model.authData;

import java.util.*;
import java.util.stream.Collectors;

public class MemoryDataAccess implements DataAccess{
    Map<String, User> userDataMap = new HashMap<>();
    Map<String, authData> authDataMap = new HashMap<>();
    Map<Integer, Game> gameDataMap = new HashMap<>();
    @Override
    public boolean addUser(User user) throws DataAccessExceptionHTTP {
        userDataMap.put(user.getUsername(), user);
        return userDataMap.containsValue(user);
    }

    @Override
    public User getUser(String username) throws DataAccessExceptionHTTP {
        return userDataMap.get(username);
    }

    @Override
    public authData getAuthData(String authToken) throws DataAccessExceptionHTTP {
        return authDataMap.get(authToken);
    }

    @Override
    public boolean isValidAuthToken(String authToken) throws DataAccessExceptionHTTP {
        return authDataMap.get(authToken) != null && authDataMap.get(authToken).getAuthToken().equalsIgnoreCase(authToken);
    }

    @Override
    public authData createAuthData(String username) throws DataAccessExceptionHTTP {
        authData newAuthData =  new authData(username);
        authDataMap.put(newAuthData.getAuthToken(),newAuthData);
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
    public void updateGame(Game game) throws DataAccessExceptionHTTP {
        int gameId = game.getGameId();
        gameDataMap.remove(gameId);
        gameDataMap.put(gameId, game);
    }

    @Override
    public Game createGame(Game game) throws DataAccessExceptionHTTP {
        gameDataMap.put(game.getGameId(), game);
        if(!gameDataMap.containsValue(game)){
            throw new DataAccessExceptionHTTP(500,"Error in creating new game. Please try again.");
        }
        return game;
    }

    @Override
    public Game getGameData(int gameId) throws DataAccessExceptionHTTP {
        return gameDataMap.get(gameId);
    }

    @Override
    public List<GameListData> listGames() throws DataAccessExceptionHTTP {
        return gameDataMap.values().stream().map(game ->  new GameListData(
                        game.getGameId(),
                        game.getWhiteUsername(),
                        game.getBlackUsername(),
                        game.getGameName()))
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
