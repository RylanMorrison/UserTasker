package api.repository;

import api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides database operations on {@link User}s using JPA
 *
 * @author Rylan Morrison
 * @since 19 September 2018
 */
public interface UserRepository extends JpaRepository<User, Integer> {
}
