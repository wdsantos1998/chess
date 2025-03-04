package service;
import data.access.DataAccess;
import data.access.DataAccessExceptionHTTP;
import model.*;

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
                throw new DataAccessExceptionHTTP(400, "Error: bad request");
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
            boolean isValidToken = dataAccess.isValidAuthToken(gameRequest.getAuthToken());
            if (!isValidToken) {
                throw new DataAccessExceptionHTTP(401, "Error: unauthorized");
            }
            return dataAccess.createGame(new Game(null, null, gameRequest.getGameName()));
        } catch (DataAccessExceptionHTTP e) {
            throw new DataAccessExceptionHTTP(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            throw new DataAccessExceptionHTTP(500, e.getMessage());
        }
    }

    public void logout(String authToken) throws DataAccessExceptionHTTP {
        try {
            if (authToken == null) {
                throw new DataAccessExceptionHTTP(400, "Error: bad request");
            }
            boolean isValidToken = dataAccess.isValidAuthToken(authToken);
            if (!isValidToken) {
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
