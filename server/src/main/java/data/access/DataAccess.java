package data.access;

import model.GameData;
import model.GameListData;
import model.UserData;
import model.AuthData;

import java.util.List;

public interface DataAccess {

    /**
     * This function add a new user to the memory
     * @param userData of type User
     * @return boolean value to indicate success.
     * @throws DataAccessExceptionHTTP in case of any error
     */
    boolean addUser(UserData userData) throws DataAccessExceptionHTTP;

    /**
     * Function user to get user information
     * @param username of type String
     * @return user associated to information given.
     * @throws DataAccessExceptionHTTP in case of any errors
     */
    UserData getUser(String username) throws DataAccessExceptionHTTP;

    /**
     * Function user to return authData from a user
     * @param authData of type String
     * @return authToken associated to given user.
     * @throws DataAccessExceptionHTTP is any error occurs
     */
    AuthData getAuthData(String authData)  throws DataAccessExceptionHTTP;

    /**
     * Function to verify authToken
     * @param authToken of type String
     * @return true/false if the authToken is valid
     * @throws DataAccessExceptionHTTP is any error occurs
     */
    boolean isValidAuthToken(String authToken)  throws DataAccessExceptionHTTP;

    /**
     *Function to create autoToken
     * @param username of type user. This parameter indicated to which username the authToken will be associated to.
     * @return boolean value
     * @throws DataAccessExceptionHTTP in case of any error
     */
    AuthData createAuthData(String username) throws DataAccessExceptionHTTP;


    /**
     *Function to delete autoToken
     * @param autToken of type String. This parameter indicates which authToken we must delete.
     * @return boolean value
     * @throws DataAccessExceptionHTTP in case of any error
     */
    boolean deleteAuthToken(String autToken) throws DataAccessExceptionHTTP;

    /**
     * Function to update chess game.
     *
     * @param gameData of type String. This parameter is used to extract the gameId and update the information passed in the corresponding gameId
     * @throws DataAccessExceptionHTTP in case of any error
     */
    void updateGame(GameData gameData) throws DataAccessExceptionHTTP;

    /**
     *Function to create a chess game.
     * @param gameData of type Game.
     * @return boolean value
     * @throws DataAccessExceptionHTTP in case of any error
     */
    GameData createGame(GameData gameData) throws DataAccessExceptionHTTP;


    /**
     *Function to return specific game based on gameId
     * @param gameId of type int. It points to the gameId stored in the system.
     * @return game
     * @throws DataAccessExceptionHTTP in case of any error
     */
    GameData getGameData(int gameId) throws DataAccessExceptionHTTP;

    /**
     *Function to return list of games in memory
     * @return list of games stored in memory
     * @throws DataAccessExceptionHTTP in case of any error
     */
    List<GameListData> listGames() throws DataAccessExceptionHTTP;

    /**
     *Function to clear all the memory in games, users and authTokens
     * @return a boolean values as a sign that the operation was concluded.
     * @throws DataAccessExceptionHTTP in case of any error
     */
    boolean clear()  throws DataAccessExceptionHTTP;

}
