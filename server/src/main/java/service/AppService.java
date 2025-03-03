package service;
import com.google.gson.JsonSyntaxException;
import data.access.DataAccess;
import data.access.DataAccessException;
import data.access.DataAccessExceptionHTTP;
import model.LoginRequest;
import model.User;
import model.authData;
import org.eclipse.jetty.http.HttpStatus;

public class AppService {

    private final DataAccess dataAccess;

    public AppService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public authData register(User user) throws DataAccessExceptionHTTP {
        if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
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
        return dataAccess.createAuthData(user);
    }

    public authData login(LoginRequest user) throws DataAccessExceptionHTTP {
        try {
            if(user.getUsername() == null || user.getPassword() == null) {
                throw new DataAccessExceptionHTTP(400, "Error: bad request");
            }
            User isRegisteredUser = dataAccess.getUser(user.getUsername());
            if(isRegisteredUser != null) {
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


    public boolean clearApplication() throws DataAccessExceptionHTTP {
        return dataAccess.clear();
    }





}
