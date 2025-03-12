package data.access;

import model.AuthData;
import model.GameData;
import model.GameListData;
import model.UserData;

import java.util.List;

public class MySqlDataAccess implements DataAccess {
    public MySqlDataAccess() throws DataAccessExceptionHTTP {
        try {
            configureDatabase();
        }
        catch (DataAccessExceptionHTTP e){
            throw new DataAccessExceptionHTTP(e.getStatusCode(),e.getMessage());
        }

    }
    @Override
    public boolean addUser(UserData userData) throws DataAccessExceptionHTTP {
        return false;
    }

    @Override
    public UserData getUser(String username) throws DataAccessExceptionHTTP {
        return null;
    }

    @Override
    public AuthData getAuthData(String authData) throws DataAccessExceptionHTTP {
        return null;
    }

    @Override
    public boolean isValidAuthToken(String authToken) throws DataAccessExceptionHTTP {
        return false;
    }

    @Override
    public AuthData createAuthData(String username) throws DataAccessExceptionHTTP {
        return null;
    }

    @Override
    public boolean deleteAuthToken(String autToken) throws DataAccessExceptionHTTP {
        return false;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessExceptionHTTP {

    }

    @Override
    public GameData createGame(GameData gameData) throws DataAccessExceptionHTTP {
        return null;
    }

    @Override
    public GameData getGameData(int gameId) throws DataAccessExceptionHTTP {
        return null;
    }

    @Override
    public List<GameListData> listGames() throws DataAccessExceptionHTTP {
        return List.of();
    }

    @Override
    public boolean clear() throws DataAccessExceptionHTTP {
        return false;
    }

    private void configureDatabase() throws DataAccessExceptionHTTP{
        DatabaseManager.createDatabase();
        try  {
            //Creating tables
            DatabaseManager.createDataBaseTables();
        }
        catch (DataAccessExceptionHTTP e){
            throw new DataAccessExceptionHTTP(500, "Error: Unable to configure database");
        }
    }
}
