package api.service;

import api.entity.User;
import api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of {@link UserService} that performs operations on {@link User}s
 *
 * @author Rylan Morrison
 * @since 22 September 2018
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    private Logger logger = LoggerFactory.getLogger("UserService");

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepo.findAll();
        logger.info("All Users: " + users);
        return users;
    }

    @Override
    public User addUser(User user) {
        Objects.requireNonNull(user);
        userRepo.save(user);
        logger.info("Created User: " + user);
        return user;
    }

    @Override
    public Optional<User> getUserById(final Integer id) {
        Optional<User> user = userRepo.findById(id);
        user.ifPresent(u -> logger.info("Returned User: " + u));
        return user;
    }

    @Override
    @Transactional
    public Optional<User> updateUser(final Integer id, User newUser) {
        Optional<User> currentUser = userRepo.findById(id);
        if (currentUser.isPresent()) {
            User user = currentUser.get();
            logger.info("Updating User: " + user);
            user.updateWith(newUser);
            logger.info("-> " + user);
            return currentUser;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean userExists(final Integer user_id) {
        return userRepo.existsById(user_id);
    }
}
