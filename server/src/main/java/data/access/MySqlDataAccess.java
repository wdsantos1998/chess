package data.access;
import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess implements DataAccess {
    private final String USER_TABLE = "users";
    private final String GAME_DATA_TABLE = "game_data";
    private final String AUTH_TOKEN_TABLE = "auth_token";
    private final String[] TABLES = {USER_TABLE,GAME_DATA_TABLE,AUTH_TOKEN_TABLE};
    private static final Gson gson = new Gson();

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
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, userData.username(), hashPassword(userData.password()) , userData.email());
        return true;
    }

    @Override
    public UserData getUser(String username) throws DataAccessExceptionHTTP {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password,email FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessExceptionHTTP(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }

    @Override
    public AuthData getAuthData(String authData) throws DataAccessExceptionHTTP {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auth_token WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authData);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuthData(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessExceptionHTTP(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    private AuthData readAuthData(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var authToken = rs.getString("authToken");
        return new AuthData(username, authToken);
    }

    @Override
    public boolean isValidAuthToken(String authToken) throws DataAccessExceptionHTTP {
        AuthData authTokenFromDatabase = getAuthData(authToken);
        return authTokenFromDatabase != null && authTokenFromDatabase.authToken().equalsIgnoreCase(authToken);
    }

    @Override
    public AuthData createAuthData(String username) throws DataAccessExceptionHTTP {
        var statement = "INSERT INTO auth_token (authToken, username) VALUES (?, ?)";
        AuthData authToken = new AuthData(username);
        executeUpdate(statement,authToken.authToken(), authToken.username());
        return authToken;
    }

    @Override
    public boolean deleteAuthToken(String autToken) throws DataAccessExceptionHTTP {
        var statement = "DELETE FROM auth_token WHERE authToken=?";
        executeUpdate(statement, autToken);
        return true;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessExceptionHTTP {
        int gameID = gameData.gameID();
        var statement = "DELETE FROM game_data WHERE gameID=?";
        //Deleting current information
        executeUpdate(statement, gameID);
        //Inserting new record with update information
        createGame(gameData);
    }

    @Override
    public GameData createGame(GameData gameData) throws DataAccessExceptionHTTP {
        var statement = "INSERT INTO game_data (gameID, whiteUsername,blackUsername,gameName,game) VALUES (?, ?, ?, ?, ?)";
        executeUpdate(statement,gameData.gameID(),gameData.whiteUsername(),gameData.blackUsername(),gameData.gameName(),gson.toJson(gameData.game()));
        return gameData;
    }

    @Override
    public GameData getGameData(int gameId) throws DataAccessExceptionHTTP {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername,blackUsername,gameName, game FROM game_data WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameId);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGameData(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessExceptionHTTP(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    private GameData readGameData(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var gameJson = rs.getString("game");
        ChessGame game = gson.fromJson(gameJson, ChessGame.class);
        return new GameData(whiteUsername,blackUsername,gameName,gameID,game);
    }



    @Override
    public List<GameListData> listGames() throws DataAccessExceptionHTTP {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game_data";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    List<GameListData> gameDataList = new ArrayList<>();
                    while (rs.next()) {
                        GameData gameDataFromDatabase = readGameData(rs);
                        GameListData gameListData = new GameListData(gameDataFromDatabase.gameID(),gameDataFromDatabase.whiteUsername(),gameDataFromDatabase.blackUsername(),gameDataFromDatabase.gameName());
                        gameDataList.add(gameListData);
                    }
                    return gameDataList;
                }
            }
        } catch (Exception e) {
            throw new DataAccessExceptionHTTP(500, String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public boolean clear() throws DataAccessExceptionHTTP {
        try {
            for (String table : TABLES) {
                String statement = "TRUNCATE TABLE " + table;
                executeUpdate(statement);
            }
            return true;
        } catch (DataAccessExceptionHTTP e) {
            throw new DataAccessExceptionHTTP(e.getStatusCode(), e.getMessage());
        }
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

    public String hashPassword(String password){
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    public Boolean verifyPassword(String clearPassword, String hashedPassword){
        return BCrypt.checkpw(clearPassword, hashedPassword);
    }
}
