package dataaccess;

import model.Game;
import model.User;
import model.authData;

import java.util.List;

public interface DataAccess {

    /**
     * This function add a new user to the memory
     * @param user of type User
     * @return boolean value to indicate success.
     * @throws DataAccessException in case of any error
     */
    boolean addUser(User user) throws DataAccessException;

    /**
     * Function user to get user information
     * @param user of type User
     * @return user associated to information given.
     * @throws DataAccessException in case of any errors
     */
    User getUser(User user) throws DataAccessException;

    /**
     * Function user to return user based on authData information
     * @param authData of type authData
     * @return a user based on the authData information.
     * @throws DataAccessException in case of any error
     */
    User getUser(authData authData) throws DataAccessException;

    /**
     * Function user to return authData from a user
     * @param user of type User
     * @return authToken associated to given user.
     * @throws DataAccessException is any error occurs
     */
    authData getAuthData(User user)  throws DataAccessException;


    /**
     *Function to create autoToken
     * @param user of type user. This parameter indicated to which username the authToken will be associated to.
     * @return boolean value
     * @throws DataAccessException in case of any error
     */
    boolean createAuthData(User user) throws DataAccessException;


    /**
     *Function to delete autoToken
     * @param user of type user. This parameter indicates to which user the authToken is associated to.
     * @return boolean value
     * @throws DataAccessException in case of any error
     */
    boolean deleteAuthToken(User user) throws DataAccessException;

    /**
     *Function to update chess game.
     * @param game of type Game. This parameter is used to extract the gameId and update the information passed in the corresponding gameId
     * @return boolean value
     * @throws DataAccessException in case of any error
     */
    boolean updateGame(Game game) throws DataAccessException;

    /**
     *Function to create a chess game.
     * @param game of type Game.
     * @return boolean value
     * @throws DataAccessException in case of any error
     */
    boolean createGame(Game game ) throws DataAccessException;

    /**
     *Function to return list of games in memory
     * @return list of games stored in memory
     * @throws DataAccessException in case of any error
     */
    List<Game> listGames() throws DataAccessException;

    /**
     *Function to clear all the memory in games, users and authTokens
     * @return a boolean values as a sign that the operation was concluded.
     * @throws DataAccessException in case of any error
     */
    boolean clear()  throws DataAccessException;

}
