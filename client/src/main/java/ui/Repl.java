package ui;
import model.GameData;
import model.LoginRequest;
import model.UserData;

import java.util.Scanner;

public class Repl {
        private final Scanner scanner;
        private boolean isLoggedIn;
        private boolean isInAGame; //If user is in a game
        private int gameID; //ID of the game the user is in
        private final ChessClient client;

        public Repl (String URL){
            client  = new ChessClient(URL);
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
                        case "register"-> {
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
                        case "login"-> {
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
                        case "quit"-> {
                            System.out.println("Exiting program.");
                            return;
                        }
                        default-> {
                            System.out.println("Unknown command.");
                        }
                    }
                } else {
                    switch (command) {
                        case "help" -> {
                            System.out.println("Available commands:");
                            System.out.println("help - Show this message");
                            System.out.println("logout - Log out from your account");
                            System.out.println("create game - Create a new game");
                            System.out.println("list games - List all available games");
                            System.out.println("play game - Join an existing game");
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
                            int gameId = Integer.parseInt(scanner.nextLine());
                            System.out.print("Enter player type (white/black): ");
                            String playerType = scanner.nextLine().toUpperCase();
                            client.joinGame(gameId, playerType);
                            System.out.println("Joined game.");
                            isInAGame = true;
                            gameID = gameId;
                        }
                        case "logout" -> {
                            client.logout();
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
    }
