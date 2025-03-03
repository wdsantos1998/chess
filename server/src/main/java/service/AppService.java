package service;
import data.access.DataAccess;
import data.access.DataAccessExceptionHTTP;
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
            User isDuplicated = dataAccess.getUser(user);
        if (isDuplicated != null) {
            throw new DataAccessExceptionHTTP(403,"Error: already taken");
        }
        boolean userCreated = dataAccess.addUser(user);

        if (!userCreated) {
            throw new DataAccessExceptionHTTP(500,"Error: user could not be created. Try again");
        }
        return dataAccess.createAuthData(user);
    }

    public boolean clearApplication() throws DataAccessExceptionHTTP {
        return dataAccess.clear();
    }





}
