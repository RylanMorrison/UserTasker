package api.repository;

import api.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Provides database operations on {@link Task}s using JPA
 *
 * @author Rylan Morrison
 * @since 19 September 2018
 */
public interface TaskRepository extends JpaRepository<Task, Integer> {

    // Returns a List of all Tasks associated with {userId} User
    List<Task> findAllByUserId(Integer userId);
}
