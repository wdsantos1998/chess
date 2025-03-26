package ui;

public class ExceptionResponse extends Exception {
    private final int statusCode;

    public ExceptionResponse(int statusCode,String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
