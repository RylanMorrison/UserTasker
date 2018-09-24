package api.controller;

import api.entity.User;
import api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.Optional;

/**
 * Controller for HTTP requests that allow Users to be added, modified and queried
 *
 * @author Rylan Morrison
 * @since 19 September 2018
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // Http error messages
    private static final String NO_USER = "User (ID: {0}) does not exist";

    // platform independent line separator (for formatting HTTP responses)
    private static final String LINE_SEP = System.lineSeparator();

    /**
     * GET request to /api/user returns a List of all Users
     *
     * @return - List of User objects
     */
    @GetMapping()
    public ResponseEntity<?> listAllUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers() + LINE_SEP);
    }

    /**
     * POST request to /api/user creates a new User
     *
     * @param newUser - the User to add
     */
    @PostMapping(consumes = "application/JSON")
    public ResponseEntity<String> createUser(@RequestBody User newUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Created User " + userService.addUser(newUser) + LINE_SEP);
    }

    /**
     * GET request to /api/user/{id} returns the info of the {id} User
     *
     * @param id - the id of the User to return info for
     * @return - User object containing info
     */
    @GetMapping(value = "{id}", produces="application/JSON")
    public ResponseEntity<?> getUserInfo(@PathVariable int id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(user.get() + LINE_SEP);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(MessageFormat.format(NO_USER, id) + LINE_SEP);
        }
    }

    /**
     * PUT request to /api/user/{id} updates {id} User with request body values
     *
     * @param id - the id of the user to updateWith
     * @param newUser - User object containing new values
     */
    @PutMapping(value = "{id}", consumes = "application/JSON")
    @Transactional
    public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody User newUser) {
        Optional<User> currentUser = userService.updateUser(id, newUser);
        if (currentUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body("Updated User " + currentUser.get() + LINE_SEP);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MessageFormat.format(NO_USER, id) + LINE_SEP);
        }
    }





}
