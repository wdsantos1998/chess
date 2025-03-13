package service;
import data.access.DataAccess;
import data.access.DataAccessException;
import data.access.DataAccessExceptionHTTP;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppService {

    private final DataAccess dataAccess;

    public AppService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    //Reverting branch.

    public AuthData register(UserData userData) throws DataAccessExceptionHTTP {
        if (userData.username() == null || userData.password() == null || userData.email() == null ) {
            throw new DataAccessExceptionHTTP(400,"Error: bad request");
        }
            UserData isDuplicated = dataAccess.getUser(userData.username());
        if (isDuplicated != null) {
            throw new DataAccessExceptionHTTP(403,"Error: already taken");
        }
        boolean userCreated = dataAccess.addUser(userData);

        if (!userCreated) {
            throw new DataAccessExceptionHTTP(500,"Error: user could not be created. Try again");
        }
        return dataAccess.createAuthData(userData.username());
    }

    public AuthData login(LoginRequest user) throws DataAccessExceptionHTTP {
        try {
            if(user.username() == null || user.password() == null) {
                throw new DataAccessException("Error: bad request");
            }
            UserData isRegisteredUserData = dataAccess.getUser(user.username());
            if( isRegisteredUserData != null) {
                LoginRequest userFromDatabase = new LoginRequest(isRegisteredUserData.username(), isRegisteredUserData.password());
                if (verifyUser(user,userFromDatabase)) {
                    return dataAccess.createAuthData(user.username());
                }
            }
            throw new DataAccessExceptionHTTP(401, "Error: unauthorized");
        } catch (DataAccessExceptionHTTP e) {
            throw new DataAccessExceptionHTTP(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            throw new DataAccessExceptionHTTP(500, "Internal Server Error");
        }
    }

    public GameData createGame(GameRequest gameRequest) throws DataAccessExceptionHTTP {
        try {
            boolean isTokenValid = dataAccess.isValidAuthToken(gameRequest.authToken());
            if (!isTokenValid) {
                throw new DataAccessExceptionHTTP(401, "Error: unauthorized");
            }
            return dataAccess.createGame(new GameData(null, null, gameRequest.gameName()));
        } catch (DataAccessExceptionHTTP e) {
            throw new DataAccessExceptionHTTP(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            throw new DataAccessExceptionHTTP(500, e.getMessage());
        }
    }

    public void joinGame(JoinGameRequest joinGameRequestData) throws DataAccessExceptionHTTP {
        try{
            boolean isTokenValid = dataAccess.isValidAuthToken(joinGameRequestData.authToken());
            if(!isTokenValid){
                throw new DataAccessExceptionHTTP(401,"Error: unauthorized");
            }
            GameData gameData = dataAccess.getGameData(joinGameRequestData.gameID());

            if(gameData == null){
                throw new DataAccessExceptionHTTP(400,"Error: bad request");
            }
            List<String> playableColors = List.of("WHITE","BLACK");
            if(!playableColors.contains(joinGameRequestData.playerColor()) ){
                throw new DataAccessExceptionHTTP(400,"Error: bad request");
            }
            boolean isWhiteTeamAvailable = gameData.whiteUsername() == null;
            boolean isBlackTeamAvailable = gameData.blackUsername() == null;
            Map<String,Boolean> IsTeamAvailable = new HashMap<>();
            IsTeamAvailable.put("WHITE",isWhiteTeamAvailable);
            IsTeamAvailable.put("BLACK",isBlackTeamAvailable);
            if(!IsTeamAvailable.get(joinGameRequestData.playerColor())){
                throw new DataAccessExceptionHTTP(403,"Error: already taken");
            }
             String usernameRequestingToJoin = dataAccess.getAuthData(joinGameRequestData.authToken()).username();
             GameData gameDataToUpdate = dataAccess.getGameData(joinGameRequestData.gameID());
            GameData updatedGameData = null;
             if(joinGameRequestData.playerColor().equalsIgnoreCase("WHITE")){
                  updatedGameData = gameDataToUpdate.setWhiteUsername(usernameRequestingToJoin);
             }
             else {
                  updatedGameData = gameDataToUpdate.setBlackUsername(usernameRequestingToJoin);
             }
               dataAccess.updateGame(updatedGameData);
        } catch (DataAccessExceptionHTTP e) {
            throw new DataAccessExceptionHTTP(e.getStatusCode(),e.getMessage());
        }
        catch (Exception e){
            throw new DataAccessExceptionHTTP(500,e.getMessage());
        }
    }

    public List<GameListData> listGames(String authToken) throws DataAccessExceptionHTTP {
        try{
            boolean isTokenValid = dataAccess.isValidAuthToken(authToken);
            if(!isTokenValid){
                throw new DataAccessExceptionHTTP(401, "Error: unauthorized");
            }
            return dataAccess.listGames();
        }
        catch( DataAccessExceptionHTTP e){
            throw new DataAccessExceptionHTTP(e.getStatusCode(), e.getMessage());
        }
        catch (Exception e){
            throw new DataAccessExceptionHTTP(500, e.getMessage());
        }
    }

    public void logout(String authToken) throws DataAccessExceptionHTTP {
        try {
            if (authToken == null) {
                throw new DataAccessExceptionHTTP(400, "Error: bad request");
            }
            boolean isTokenValid = dataAccess.isValidAuthToken(authToken);
            if (!isTokenValid) {
                throw new DataAccessExceptionHTTP(401, "Error: unauthorized");
            }
            dataAccess.deleteAuthToken(authToken);
        } catch (DataAccessExceptionHTTP e) {
            throw new DataAccessExceptionHTTP(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            throw new DataAccessExceptionHTTP(500, e.getMessage());
        }
    }

    public void clearApplication() throws DataAccessExceptionHTTP {
        try {
            dataAccess.clear();
        }
        catch (DataAccessExceptionHTTP e){
            throw new DataAccessExceptionHTTP(e.getStatusCode(), e.getMessage());
        }
    }
    private Boolean verifyPassword(String clearPassword, String hashedPassword){
        return BCrypt.checkpw(clearPassword, hashedPassword);
    }

    private Boolean verifyUser(LoginRequest userA, LoginRequest userB){
       return userA.username().equals(userB.username()) && verifyPassword(userA.password(),userB.password());
    }
}
