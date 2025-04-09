package websocket.messages;

public class Error extends ServerMessage{
    private final String message;

    public Error(String message){
        super(ServerMessageType.ERROR);
        this.message = message;
    }

    //Requirement in the specification
    public String getMessage(){
        return String.format("Error: %s", message);
    }
}
