package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.GameData;
import model.LoginRequest;
import model.UserData;
import websocket.NotificationHandler;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ErrorMessage;

import java.util.Collection;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.PrintChessBoard.printHighlightedBoard;

public class Repl implements NotificationHandler {
    private final Scanner scanner;
    private boolean isLoggedIn;
    private boolean isInAGame;
    private boolean isObserver;
    private int gameID;
    private final ChessClient client;
    private ChessGame.TeamColor teamColor;
    private boolean isGameOver = false;
    private ChessGame.TeamColor whosTurn = ChessGame.TeamColor.WHITE;
    private ChessBoard boardData;
    private ChessGame gameData;


    public Repl(String url) {
        try {
            client = new ChessClient(url, this);
        } catch (Exception e) {
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
                                this.gameID = Integer.parseInt(observeString);
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input. Please enter a valid integer.");
                                return;
                            }
                            System.out.print("Enter player type (white/black/observer): ");
                            String playerType = scanner.nextLine().toUpperCase();
                            if (!playerType.equals("WHITE") && !playerType.equals("BLACK") && !playerType.equals("OBSERVER")) {
                                System.out.println("Invalid player type. Please enter 'white' , 'black' or 'observer'.");
                                return;
                            }
                            if (playerType.equals("OBSERVER")) {
                                client.joinGameAsObserver(this.gameID);
                                this.teamColor = ChessGame.TeamColor.WHITE;
                                isObserver = true;
                                System.out.println("Joined game as observer.");
                            } else {
                                if (playerType.equals("WHITE")) {
                                    this.teamColor = ChessGame.TeamColor.WHITE;
                                } else {
                                    this.teamColor = ChessGame.TeamColor.BLACK;
                                }
                                client.joinGame(this.gameID, playerType);
                                System.out.println("Joined game.");
                            }
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
                        case "redraw" -> {
                            redrawBoard();
                            System.out.println("Redrawing board.");
                        }
                        case "leave" -> {
                            client.leaveGame(this.gameID);
                            isInAGame = false;
                            gameID = 0;
                            System.out.println("Left game successfully.");
                        }
                        case "move", "make move" -> {
                            if (!isObserver) {
                                if (isGameOver) {
                                    System.out.println("Game is over. You cannot make a move.");
                                    return;
                                }
                                if (whosTurn != this.teamColor) {
                                    System.out.println("It's not your turn. Please wait for your opponent to make a move.");
                                    return;
                                }
                                System.out.print("Enter move (e.g., e2 e4): ");
                                String move = scanner.nextLine();
                                String[] partsMove = move.split(" ");
                                if (partsMove.length != 2) {
                                    System.out.println("Invalid move format. Please enter a valid move.");
                                    return;
                                }
                                ChessPosition from = parseChessPosition(partsMove[0].trim().toLowerCase());
                                ChessPosition to = parseChessPosition(partsMove[1].trim().toLowerCase());
                                client.makeMove(this.gameID, from, to);
                            } else {
                                System.out.println("You cannot make a move as an observer");
                            }
                        }
                        case "resign" -> {
                            if (!isObserver) {
                                if (isGameOver) {
                                    System.out.println("Game is over. You cannot resign. You may leave the game.");
                                    return;
                                }
                                System.out.print("Are you sure you want to resign? (yes/no): ");
                                String confirm = scanner.nextLine();
                                if (!confirm.equalsIgnoreCase("yes")) {
                                    System.out.println("Resignation cancelled.");
                                    return;
                                }
                                client.resignFromGame(this.gameID);
                                System.out.println("Resigned from game successfully. You lost. You may leave the game.");
                            } else {
                                System.out.println("You cannot resign as an observer");
                            }
                        }
                        case "legal", "legal moves" -> {
                            System.out.print("Enter piece position (e.g., e2): ");
                            String move = scanner.nextLine().trim().toLowerCase();
                            if (!move.matches("^[a-h][1-8]$")) {
                                System.out.println("Invalid input. Please enter a position like 'e2' (a-h, 1-8).");
                                return;
                            }
                            ChessPosition from = parseChessPosition(move.trim().toLowerCase());
                            highlightLegalMoves(from);
                        }
                        default -> {
                            System.out.println("Unknown command.");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        Repl repl = new Repl("http://localhost:8080");
        repl.run();
    }


    public ChessPosition parseChessPosition(String input) {
        if (input.length() != 2) {
            return null;
        }

        char colChar = Character.toLowerCase(input.charAt(0));
        char rowChar = input.charAt(1);

        int col = colChar - 'a' + 1;
        int row = rowChar - '0';

        if (col >= 1 && col <= 8 && row >= 1 && row <= 8) {
            return new ChessPosition(row, col);
        } else {
            return null;
        }
    }

    private void redrawBoard() {
        boolean isWhite = this.teamColor != ChessGame.TeamColor.WHITE;
        PrintChessBoard.printChessBoardFromBoardData(boardData, isWhite);
    }

    private void highlightLegalMoves(ChessPosition referencePosition) {
        Collection<ChessMove> validMoves = gameData.validMoves(referencePosition);
        boolean isWhite = this.teamColor != ChessGame.TeamColor.WHITE;
        printHighlightedBoard(boardData, isWhite, referencePosition, validMoves);
    }


    @Override
    public void notify(NotificationMessage notificationMessage) throws Exception {
        System.out.println("\n" + SET_TEXT_COLOR_GREEN + ">>>" + notificationMessage.getMessage() + "<<<" + RESET_TEXT_COLOR);
        System.out.print("Enter command: ");
    }

    @Override
    public void load(LoadGameMessage loadGameMessage) throws Exception {
        boolean isWhite = this.teamColor != ChessGame.TeamColor.WHITE;
        whosTurn = loadGameMessage.getGame().getTeamTurn();
        isGameOver = loadGameMessage.getGame().isGameOver();
        gameData = loadGameMessage.getGame();
        boardData = loadGameMessage.getGame().getBoard();
        PrintChessBoard.printChessBoardFromBoardData(boardData, isWhite);
        System.out.print("Enter command: ");
    }

    @Override
    public void warn(ErrorMessage errorMessage) throws Exception {
        System.out.println("\n" + SET_TEXT_COLOR_GREEN + ">>>" + errorMessage.getErrorMessage() + "<<<" + RESET_TEXT_COLOR);
        System.out.print("Enter command: ");
    }
}
