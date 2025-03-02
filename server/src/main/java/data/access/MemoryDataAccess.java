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
    public boolean addUser(User user) throws DataAccessException {
        userDataMap.put(user.getUsername(), user);
        if(!userDataMap.containsValue(user)){
            throw new DataAccessException("Error in adding user. Please try again.");
        }
        return true;
    }

    @Override
    public User getUser(User user) throws DataAccessException {
        User memoryUser = userDataMap.get(user.getUsername());
        if(memoryUser == null){
            throw new DataAccessException("User not found. Please try again.");
        }
        return memoryUser;
    }

    @Override
    public User getUser(authData authData) throws DataAccessException {
        User memoryUser = userDataMap.get(authData.getUsername());
        if(memoryUser == null){
            throw new DataAccessException("User not found. Please try again.");
        }
        return memoryUser;
    }

    @Override
    public authData getAuthData(User user) throws DataAccessException {
        authData memoryAuthToken = authDataMap.get(user.getUsername());
        if(memoryAuthToken == null){
            throw new DataAccessException("Token not found. Please try again.");
        }
        return memoryAuthToken;
    }

    @Override
    public boolean createAuthData(User user) throws DataAccessException {
        String randomToken = UUID.randomUUID().toString();
        authData newAuthData =  new authData(randomToken, user.getUsername());
        authDataMap.put(user.getUsername(),newAuthData);
        if(!authDataMap.containsValue(newAuthData)){
            throw new DataAccessException("Error in creating new token. Please try again.");
        }
        return true;
    }

    @Override
    public boolean deleteAuthToken(User user) throws DataAccessException {
        authDataMap.remove(user.getUsername());
        if(authDataMap.containsKey(user.getUsername())){
            throw new DataAccessException("Error in deleting token. Please try again.");
        }
        return true;
    }

    @Override
    public boolean updateGame(Game game) throws DataAccessException {
        Game gameOldVersion = gameDataMap.get(game.getGameId());
        gameDataMap.replace(game.getGameId(), game);
        if(gameOldVersion.equals(gameDataMap.get(game.getGameId()))){
            throw new DataAccessException("Error in updating game settings. Please try again.");
        }
        return true;
    }

    @Override
    public boolean createGame(Game game) throws DataAccessException {
        gameDataMap.put(game.getGameId(), game);
        if(!gameDataMap.containsValue(game)){
            throw new DataAccessException("Error in creating new game. Please try again.");
        }
        return true;
    }

    @Override
    public Game getGameData(int gameId) throws DataAccessException {
        Game memoryGame = gameDataMap.get(gameId);
        if(memoryGame == null){
            throw new DataAccessException("Error in returning game. Game not found. Please try again.");
        }
        return memoryGame;
    }

    @Override
    public List<Game> listGames() throws DataAccessException {
        List<Game> listOfGames = new ArrayList<>(gameDataMap.values());
        if(listOfGames.isEmpty()){
            throw new DataAccessException("Error in returning list of games. List is empty. Please try again.");
        }
        return listOfGames;
    }

    @Override
    public boolean clear() throws DataAccessException {
        userDataMap.clear();
        authDataMap.clear();
        gameDataMap.clear();
        if (!userDataMap.isEmpty() || !authDataMap.isEmpty() || !gameDataMap.isEmpty()) {
            throw new DataAccessException("Error in clearing memory. Please try again.");
        }
        return true;
    }
}
