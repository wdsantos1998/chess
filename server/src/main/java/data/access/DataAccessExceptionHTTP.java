package data.access;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessExceptionHTTP extends Exception{
    private final int statusCode;
    public DataAccessExceptionHTTP(int statusCode,String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode(){
        return this.statusCode;
    }
}
