package api.service;


import api.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Defines operations that can be performed on {@link User}s
 *
 * @author Rylan Morrison
 * @since 22 September 2018
 */
public interface UserService {

    /**
     * Retrieves the List of all Users
     * @return List containing all Users
     */
    List<User> getAllUsers();

    /**
     * Adds a User
     * @param user - the User to add
     * @return the added User
     */
    User addUser(User user);

    /**
     * Retrieves a User by its id
     * @param id - the id of the User to retrieve
     * @return Optional containing the User if found, Optional.empty() otherwise
     */
    Optional<User> getUserById(Integer id);

    /**
     * Updates a User with the values in newUser
     * @param id - the id of the User to update
     * @param newUser - User containing the values to update the User with
     * @return Optional containing the updated User if successful, Optional.empty() otherwise
     */
    Optional<User> updateUser(Integer id, User newUser);

    /**
     * Used to check if a User with the give user_id exists
     * @param user_id - the id of the User to check for
     * @return true if the User exists, false otherwise
     */
    boolean userExists(Integer user_id);
}
