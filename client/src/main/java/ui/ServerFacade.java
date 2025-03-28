package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class ServerFacade {
    private final String serverUrl;
    private static HttpClient httpClient;
    private final Gson gson;


    public ServerFacade(String URLrequest ){
        this.serverUrl = URLrequest;
        this.httpClient = httpClient.newHttpClient();
        this.gson = new Gson();
    }

    public AuthData register(UserData newUser) throws ExceptionResponse {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/user"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newUser)))
                .header("Content-Type", "application/json")
                .build();
        return getAuthData(request);
    }

    public AuthData login(LoginRequest user) throws ExceptionResponse {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(user)))
                .header("Content-Type", "application/json")
                .build();
        return getAuthData(request);
    }

    public void logout(String authToken) throws ExceptionResponse {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .DELETE()
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .build();
        voidFunctionValidation(request);
    }

    private void voidFunctionValidation(HttpRequest request) throws ExceptionResponse {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ExceptionResponse(response.statusCode(), response.body());
            }
        }
        catch (IOException | InterruptedException e) {
            throw new ExceptionResponse(500, e.getMessage());
        }
    }

    private AuthData getAuthData(HttpRequest request) throws ExceptionResponse {
        try{
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return gson.fromJson(response.body(), AuthData.class);
            }
            else{
                throw new ExceptionResponse(response.statusCode(), response.body());
            }
        }
        catch (IOException | InterruptedException e) {
            throw new ExceptionResponse(500, e.getMessage());
        }
    }

    public GameData createGame(String gameName, String authToken) throws ExceptionResponse {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(Map.of("gameName", gameName))))
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                //Numeric values in JSON are by default Doubles. So, that's why I am casting it to Double and then to int
                Map jsonMap = gson.fromJson(responseBody, Map.class);
                Double gameIdDouble = (Double) jsonMap.get("gameID");
                int gameID = gameIdDouble.intValue();
                return new GameData(null, null, gameName, gameID, new ChessGame());
            }
            else{
                throw new ExceptionResponse(response.statusCode(), response.body());
            }
        }
        catch (IOException | InterruptedException e) {
            throw new ExceptionResponse(500, e.getMessage());
        }
    }

    public void joinGame(JoinGameRequest joinGameRequest) throws ExceptionResponse {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(Map.of("gameID", joinGameRequest.gameID(), "playerColor", joinGameRequest.playerColor())))
                )
                .header("Content-Type", "application/json")
                .header("Authorization", joinGameRequest.authToken())
                .build();
        voidFunctionValidation(request);
    }
    public List<GameListData> listGames(String authToken) throws ExceptionResponse {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .GET()
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                Type responseType = new TypeToken<Map<String, List<GameListData>>>(){}.getType();
                Map<String, List<GameListData>> parsed = gson.fromJson(response.body(), responseType);
                List<GameListData> games = parsed.get("games");
                if (games == null) {
                    throw new ExceptionResponse(403, "Error: Invalid format returned from server");
                }
                return games;
            }
            else{
                throw new ExceptionResponse(response.statusCode(), response.body());
            }
        }
        catch (IOException | InterruptedException e) {
            throw new ExceptionResponse(500, e.getMessage());
        }
    }

    public void clearData() throws ExceptionResponse {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/db"))
                .DELETE()
                .header("Content-Type", "application/json")
                .build();
        voidFunctionValidation(request);
    }



}
