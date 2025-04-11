package ui;
import model.GameData;
import model.LoginRequest;
import model.UserData;
import websocket.NotificationHandler;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
        private final Scanner scanner;
        private boolean isLoggedIn;
        private boolean isInAGame;
        private int gameID;
        private final ChessClient client;

        public Repl (String url){
            try {
            client  = new ChessClient(url, this);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            scanner = new Scanner(System.in);
            isLoggedIn = false;
            isInAGame = false;
        }
        public void run() throws Exception {
            System.out.println("Welcome to Chess!");
            processCommand("help");
            String input;
            while (true) {
                System.out.print("Enter command: ");
                input = scanner.nextLine();
                if ("quit".equalsIgnoreCase(input)) {
                    break;
                }
                processCommand(input);
            }
        }

        private void processCommand(String input) throws Exception {
            String[] parts = input.split(" ");
            String command = parts[0].toLowerCase();
            try {
                if (!isLoggedIn) {
                    switch (command) {
                        case "help" -> {
                            System.out.println("help - Show this message");
                            System.out.println("register - Register a new account");
                            System.out.println("login - Log in to your account");
                            System.out.println("quit - Exit the program");
                        }
                        case "register" -> {
                            System.out.print("Enter username: ");
                            String regUsername = scanner.nextLine();
                            System.out.print("Enter password: ");
                            String regPassword = scanner.nextLine();
                            System.out.print("Enter email: ");
                            String email = scanner.nextLine();
                            if (client.register(new UserData(regUsername, regPassword, email))) {
                                System.out.println("Registration successful.");
                                isLoggedIn = client.isClientLoggedIn();
                            } else {
                                System.out.println("Registration failed.");
                            }
                        }
                        case "login" -> {
                            System.out.print("Enter username: ");
                            String username = scanner.nextLine();
                            System.out.print("Enter password: ");
                            String password = scanner.nextLine();
                            if (client.login(new LoginRequest(username, password))) {
                                System.out.println("Login successful.");
                                isLoggedIn = client.isClientLoggedIn();
                            } else {
                                System.out.println("Login failed.");
                            }
                        }
                        case "quit" -> {
                            System.out.println("Exiting program.");
                            return;
                        }
                        default -> {
                            System.out.println("Unknown command.");
                        }
                    }
                } else {
                    if (!isInAGame) {
                        switch (command) {
                            case "help" -> {
                                System.out.println("Available commands:");
                                System.out.println("help - Show this message");
                                System.out.println("logout - Log out from your account");
                                System.out.println("create game - Create a new game");
                                System.out.println("list games - List all available games");
                                System.out.println("play game - Join an existing game");
                                System.out.println("observe game - Join game as an observer");
                            }
                            case "list", "list games" -> {
                                client.listGames().forEach(game -> System.out.println("ID: " + game.gameID() + ", Game Name: " + game.gameName() + ", WHITE: " + game.whiteUsername() + ", BLACK: " + game.blackUsername()));
                            }
                            case "create", "create game" -> {
                                System.out.print("Enter the name of the game: ");
                                String gameName = scanner.nextLine();
                                GameData newGame = client.createGame(gameName);
                                System.out.println("Created ID: " + newGame.gameID() + ", Game Name: " + newGame.gameName());
                            }
                            case "play", "play game" -> {
                                System.out.print("Enter Game ID to join: ");
                                String observeString = scanner.nextLine();
                                try {
                                    int gameId = Integer.parseInt(observeString);
                                    this.gameID = gameId;
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid input. Please enter a valid integer.");
                                    return;
                                }
                                System.out.print("Enter player type (white/black): ");
                                String playerType = scanner.nextLine().toUpperCase();
                                if (!playerType.equals("WHITE") && !playerType.equals("BLACK")) {
                                    System.out.println("Invalid player type. Please enter 'white' or 'black'.");
                                    return;
                                }
                                client.joinGame(this.gameID, playerType);
                                System.out.println("Joined game.");
                                PrintChessBoard.printGenericBoard(playerType);
                                isInAGame = true;
                            }
                            case "observe", "observe game" -> {
                                System.out.print("Enter Game ID to observe: ");
                                String observeString = scanner.nextLine();
                                try {
                                    this.gameID = Integer.parseInt(observeString);
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid input. Please enter a valid integer.");
                                    return;
                                }
                                System.out.println("Observing as WHITE.");
                                PrintChessBoard.printGenericBoard("white");
                                isInAGame = true;
                            }
                            case "logout" -> {
                                client.logout();
                                isInAGame = false;
                                System.out.println("Logged out successfully.");
                                isLoggedIn = client.isClientLoggedIn();
                            }
                            case "quit" -> {
                                System.out.println("Exiting program.");
                                return;
                            }
                            default -> {
                                System.out.println("Unknown command.");
                            }
                        }
                    } else {
                        switch (command) {
                            case "help" -> {
                                System.out.println("Available commands:");
                                System.out.println("help - Show this message");
                                System.out.println("redraw - Redraw Chess Board");
                                System.out.println("leave - Leave current game");
                                System.out.println("make move - Make move in current game");
                                System.out.println("resign - User forfeits the game and the game is over");
                                System.out.println("legal moves - Highlight Legal Moves");
                            }
                            case "leave" -> {
                                client.leaveGame(this.gameID);
                                isInAGame = false;
                                System.out.println("Left game successfully.");
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        public static void main(String[] args) throws Exception {
            Repl repl = new Repl("http://localhost:8080");
            repl.run();
        }

    @Override
    public void notify(Notification notification) {
        System.out.println("\n" +">>>"+ SET_TEXT_COLOR_BLUE + notification.getMessage() + SET_TEXT_COLOR_BLUE+"<<<");
    }

    @Override
    public void load(LoadGame loadGame) {
        //printBoard with the specific game
    }

    @Override
    public void warn(Error error) {
        System.out.println("\n" +">>>"+ SET_TEXT_COLOR_RED + error.getMessage() + SET_TEXT_COLOR_RED+"<<<");
    }
}
