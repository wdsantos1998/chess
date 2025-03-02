package dataaccess;

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
        return true;
    }

    @Override
    public User getUser(User user) throws DataAccessException {
        return userDataMap.get(user.getUsername());
    }

    @Override
    public User getUser(authData authData) throws DataAccessException {
        return userDataMap.get(authData.getUsername());
    }

    @Override
    public authData getAuthData(User user) throws DataAccessException {
        return authDataMap.get(user.getUsername());
    }

    @Override
    public boolean createAuthData(User user) throws DataAccessException {
        String randomToken = UUID.randomUUID().toString();
        authDataMap.put(user.getUsername(), new authData(randomToken, user.getUsername()));
        return true;
    }

    @Override
    public boolean deleteAuthToken(User user) throws DataAccessException {
        authDataMap.remove(user.getUsername());
        return true;
    }

    @Override
    public boolean updateGame(Game game) throws DataAccessException {
        gameDataMap.replace(game.getGameId(), game);
        return true;
    }

    @Override
    public boolean createGame(Game game) throws DataAccessException {
        gameDataMap.put(game.getGameId(), game);
        return true;
    }

    @Override
    public List<Game> listGames() throws DataAccessException {
        return new ArrayList<>(gameDataMap.values());
    }

    @Override
    public boolean clear() throws DataAccessException {
        userDataMap.clear();
        authDataMap.clear();
        gameDataMap.clear();
        return true;
    }
}
