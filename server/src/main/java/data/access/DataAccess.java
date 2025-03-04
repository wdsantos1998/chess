package data.access;

import model.Game;
import model.User;
import model.authData;

import java.util.List;

public interface DataAccess {

    /**
     * This function add a new user to the memory
     * @param user of type User
     * @return boolean value to indicate success.
     * @throws DataAccessExceptionHTTP in case of any error
     */
    boolean addUser(User user) throws DataAccessExceptionHTTP;

    /**
     * Function user to get user information
     * @param username of type String
     * @return user associated to information given.
     * @throws DataAccessExceptionHTTP in case of any errors
     */
    User getUser(String username) throws DataAccessExceptionHTTP;

    /**
     * Function user to return authData from a user
     * @param authData of type String
     * @return authToken associated to given user.
     * @throws DataAccessExceptionHTTP is any error occurs
     */
    authData getAuthData(String authData)  throws DataAccessExceptionHTTP;

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
    authData createAuthData(String username) throws DataAccessExceptionHTTP;


    /**
     *Function to delete autoToken
     * @param autToken of type String. This parameter indicates which authToken we must delete.
     * @return boolean value
     * @throws DataAccessExceptionHTTP in case of any error
     */
    boolean deleteAuthToken(String autToken) throws DataAccessExceptionHTTP;

    /**
     *Function to update chess game.
     * @param game of type String. This parameter is used to extract the gameId and update the information passed in the corresponding gameId
     * @return boolean value
     * @throws DataAccessExceptionHTTP in case of any error
     */
    boolean updateGame(Game game) throws DataAccessExceptionHTTP;

    /**
     *Function to create a chess game.
     * @param game of type Game.
     * @return boolean value
     * @throws DataAccessExceptionHTTP in case of any error
     */
    Game createGame(Game game ) throws DataAccessExceptionHTTP;


    /**
     *Function to return specific game based on gameId
     * @param gameId of type int. It points to the gameId stored in the system.
     * @return game
     * @throws DataAccessExceptionHTTP in case of any error
     */
    Game getGameData(int gameId) throws DataAccessExceptionHTTP;

    /**
     *Function to return list of games in memory
     * @return list of games stored in memory
     * @throws DataAccessExceptionHTTP in case of any error
     */
    List<Game> listGames() throws DataAccessExceptionHTTP;

    /**
     *Function to clear all the memory in games, users and authTokens
     * @return a boolean values as a sign that the operation was concluded.
     * @throws DataAccessExceptionHTTP in case of any error
     */
    boolean clear()  throws DataAccessExceptionHTTP;

}
