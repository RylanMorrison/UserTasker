package api.service;

import api.entity.Task;

import java.util.List;
import java.util.Optional;

/**
 * Defines operations that can be performed on {@link Task}s
 *
 * @author Rylan Morrison
 * @since 22 September 2018
 */
public interface TaskService {

    /**
     * Adds a Task for a User
     * @param user_id - the id of the User
     * @param task - the task to add
     * @return the Task that was added
     */
    Task addTask(Integer user_id, Task task);

    /**
     * Updates a Task for a User
     * @param user_id - the id of the User
     * @param task_id - the id of the Task
     * @param newTask - the Task containing the values to update
     * @return Optional containing the updated Task if successful, Optional.empty() otherwise
     */
    Optional<Task> updateTask(Integer user_id, Integer task_id, Task newTask);

    /**
     * Deletes a Task
     * @param id - the id of the Task to delete
     * @return Optional containing the deleted Task if successful, Optional.empty() otherwise
     */
    Optional<Task> deleteTaskById(Integer id);

    /**
     * Retrieves a Task's information for a User
     * @param user_id - the id of the User
     * @param task_id - the id of the Task
     * @return Optional containing the Task if successful, Optional.empty() otherwise
     */
    Optional<Task> getTaskInfo(Integer user_id, Integer task_id);

    /**
     * Retrieves a List of Tasks for a User
     * @param user_id - the id of the User
     * @return List containing all tasks associated with the User
     */
    List<Task> getAllTasksForUser(Integer user_id);

    /**
     * Retrieves a List of all Tasks
     * @return - List containing all Tasks
     */
    List<Task> getAllTasks();

    /**
     * Used to check if a Task with the given id exists
     * @param id - the id of the Task to check for
     * @return true if the Task exists, false otherwise
     */
    boolean taskExists(Integer id);

}
