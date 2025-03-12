package data.access;

import model.AuthData;
import model.GameData;
import model.GameListData;
import model.UserData;

import java.sql.SQLException;
import java.util.List;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess implements DataAccess {
    public MySqlDataAccess() {
        try {
            configureDatabase();
        }
        catch (DataAccessExceptionHTTP e){
           System.out.println("Error: "+e.getMessage());
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

    private int executeUpdate(String statement, Object... params) throws DataAccessExceptionHTTP {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case UserData p -> ps.setString(i + 1, p.toString());
                        case GameData p -> ps.setString(i + 1, p.toString());
                        case AuthData p -> ps.setString(i + 1, p.toString());
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {
                        }
                    }
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (DataAccessExceptionHTTP | SQLException e) {
            throw new DataAccessExceptionHTTP(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private void configureDatabase() throws DataAccessExceptionHTTP{
        try  {
            DatabaseManager.createDatabase();
        }
        catch (DataAccessExceptionHTTP e) {
            throw new DataAccessExceptionHTTP(500, "Error: Unable to configure database");
        }
    }
}
