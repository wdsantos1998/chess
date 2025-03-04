package service;
import com.google.gson.JsonSyntaxException;
import data.access.DataAccess;
import data.access.DataAccessException;
import data.access.DataAccessExceptionHTTP;
import model.*;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.StringUtil;

public class AppService {

    private final DataAccess dataAccess;

    public AppService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public authData register(User user) throws DataAccessExceptionHTTP {
        if (StringUtil.isEmpty(user.getUsername()) || StringUtil.isEmpty(user.getPassword()) || StringUtil.isEmpty( user.getEmail()) ) {
            throw new DataAccessExceptionHTTP(400,"Error: bad request");
        }
            User isDuplicated = dataAccess.getUser(user.getUsername());
        if (!StringUtil.isEmpty(String.valueOf(isDuplicated))) {
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
            if(StringUtil.isEmpty(user.getUsername()) || StringUtil.isEmpty(user.getPassword())) {
                throw new DataAccessExceptionHTTP(400, "Error: bad request");
            }
            User isRegisteredUser = dataAccess.getUser(user.getUsername());
            if( !StringUtil.isEmpty(String.valueOf(isRegisteredUser))) {
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
            try {
                authData authToken = dataAccess.getAuthData(gameRequest.getAuthToken());
                if (StringUtil.isEmpty(String.valueOf(authToken))) {
                    throw new DataAccessExceptionHTTP(401, "Error: unauthorized");
                }
                return dataAccess.createGame(new Game(null,null,gameRequest.getGameName()));
            } catch (DataAccessExceptionHTTP e) {
                throw new DataAccessExceptionHTTP(e.getStatusCode(), e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("THis is the error: " + e.getMessage());
            throw new DataAccessExceptionHTTP(500, e.getMessage());
        }
    }


    public boolean clearApplication() throws DataAccessExceptionHTTP {
        return dataAccess.clear();
    }





}
