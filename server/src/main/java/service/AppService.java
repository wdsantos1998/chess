package service;
import data.access.DataAccess;
import data.access.DataAccessException;
import data.access.DataAccessExceptionHTTP;
import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppService {

    private final DataAccess dataAccess;

    public AppService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public authData register(User user) throws DataAccessExceptionHTTP {
        if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null ) {
            throw new DataAccessExceptionHTTP(400,"Error: bad request");
        }
            User isDuplicated = dataAccess.getUser(user.getUsername());
        if (isDuplicated != null) {
            throw new DataAccessExceptionHTTP(403,"Error: already taken");
        }
        boolean userCreated = dataAccess.addUser(user);

        if (!userCreated) {
            throw new DataAccessExceptionHTTP(500,"Error: user could not be created. Try again");
        }
        return dataAccess.createAuthData(user.getUsername());
    }

    public authData login(LoginRequest user) throws DataAccessExceptionHTTP {
        try {
            if(user.getUsername() == null || user.getPassword() == null) {
                throw new DataAccessException("Error: bad request");
            }
            User isRegisteredUser = dataAccess.getUser(user.getUsername());
            if( isRegisteredUser != null) {
                LoginRequest loginRequestObject = new LoginRequest(isRegisteredUser.getUsername(), isRegisteredUser.getPassword());
                if (loginRequestObject.equals(user)) {
                    return new authData(user.getUsername());
                }
            }
            throw new DataAccessExceptionHTTP(401, "Error: unauthorized");
        } catch (DataAccessExceptionHTTP e) {
            throw new DataAccessExceptionHTTP(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            throw new DataAccessExceptionHTTP(500, "Internal Server Error");
        }
    }

    public Game createGame(GameRequest gameRequest) throws DataAccessExceptionHTTP {
        try {
            if (gameRequest.getGameName() == null || gameRequest.getAuthToken() == null) {
                throw new DataAccessExceptionHTTP(400, "Error: bad request");
            }
            boolean isTokenValid = dataAccess.isValidAuthToken(gameRequest.getAuthToken());
            if (!isTokenValid) {
                System.out.println("I am sending and unauthorized error for this request "+gameRequest.toString());
                throw new DataAccessExceptionHTTP(401, "Error: unauthorized");
            }
            return dataAccess.createGame(new Game(null, null, gameRequest.getGameName()));
        } catch (DataAccessExceptionHTTP e) {
            throw new DataAccessExceptionHTTP(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            throw new DataAccessExceptionHTTP(500, e.getMessage());
        }
    }

    public void joinGame(JoinGameRequest joinGameRequestData)throws DataAccessExceptionHTTP {
        try{
            boolean isTokenValid = dataAccess.isValidAuthToken(joinGameRequestData.getAuthToken());
            if(!isTokenValid){
                System.out.println("I am getting an unauthorized error");
                throw new DataAccessExceptionHTTP(401,"Error: unauthorized");
            }
            Game gameData = dataAccess.getGameData(joinGameRequestData.getGameId());
            if(gameData == null){
                throw new DataAccessExceptionHTTP(400,"Error: bad request");
            }
            List<String> playableColors = List.of("WHITE","BLACK");
            if(!playableColors.contains(joinGameRequestData.getPlayerColor()) ){
                throw new DataAccessExceptionHTTP(400,"Error: bad request");
            }
            boolean isWhiteTeamAvailable = gameData.getWhiteUsername() == null;
            boolean isBlackTeamAvailable = gameData.getBlackUsername() == null;
            Map<String,Boolean> IsTeamAvailable = new HashMap<>();
            IsTeamAvailable.put("WHITE",isWhiteTeamAvailable);
            IsTeamAvailable.put("BLACK",isBlackTeamAvailable);
            if(!IsTeamAvailable.get(joinGameRequestData.getPlayerColor())){
                throw new DataAccessExceptionHTTP(403,"Error: already taken");
            }
             String usernameRequestingToJoin = dataAccess.getAuthData(joinGameRequestData.getAuthToken()).getUsername();
             Game gameToUpdate = dataAccess.getGameData(joinGameRequestData.getGameId());
             if(joinGameRequestData.getPlayerColor().equalsIgnoreCase("WHITE")){
                 gameToUpdate.setWhiteUsername(usernameRequestingToJoin);
             }
             else {
                 gameToUpdate.setBlackUsername(usernameRequestingToJoin);
             }
               dataAccess.updateGame(gameToUpdate);
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

}
