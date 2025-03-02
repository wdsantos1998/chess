package data.access;

import model.Game;
import model.User;
import model.authData;

import java.util.*;

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
    public authData getAuthData(User user) throws DataAccessExceptionHTTP {
        authData memoryAuthToken = authDataMap.get(user.getUsername());
        if(memoryAuthToken == null){
            throw new DataAccessExceptionHTTP(400,"Token not found. Please try again.");
        }
        return memoryAuthToken;
    }

    @Override
    public authData createAuthData(User user) throws DataAccessExceptionHTTP {
        authData newAuthData =  new authData(user.getUsername());
        authDataMap.put(user.getUsername(),newAuthData);
        if(!authDataMap.containsValue(newAuthData)){
            throw new DataAccessExceptionHTTP(500,"Error in creating new token. Please try again.");
        }
        return newAuthData;
    }

    @Override
    public boolean deleteAuthToken(User user) throws DataAccessExceptionHTTP {
        authDataMap.remove(user.getUsername());
        if(authDataMap.containsKey(user.getUsername())){
            throw new DataAccessExceptionHTTP(500,"Error in deleting token. Please try again.");
        }
        return true;
    }

    @Override
    public boolean updateGame(Game game) throws DataAccessExceptionHTTP {
        Game gameOldVersion = gameDataMap.get(game.getGameId());
        gameDataMap.replace(game.getGameId(), game);
        if(gameOldVersion.equals(gameDataMap.get(game.getGameId()))){
            throw new DataAccessExceptionHTTP(500,"Error in updating game settings. Please try again.");
        }
        return true;
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
        Game memoryGame = gameDataMap.get(gameId);
        if(memoryGame == null){
            throw new DataAccessExceptionHTTP(400,"Error in returning game. Game not found. Please try again.");
        }
        return memoryGame;
    }

    @Override
    public List<Game> listGames() throws DataAccessExceptionHTTP {
        List<Game> listOfGames = new ArrayList<>(gameDataMap.values());
        if(listOfGames.isEmpty()){
            throw new DataAccessExceptionHTTP(500,"Error in returning list of games. List is empty. Please try again.");
        }
        return listOfGames;
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
