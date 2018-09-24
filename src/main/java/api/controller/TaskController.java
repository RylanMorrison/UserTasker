package api.controller;

import api.entity.Task;
import api.entity.User;
import api.service.TaskService;
import api.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

/**
 * Controller for receiving and sending HTTP requests that allow {@link Task}s for a specific {@link User}
 * to be added, updated, removed and queried.
 *
 * @author Rylan Morrison
 * @since 19 September 2018
 */
@RestController
@RequestMapping("/api/user/{user_id}/task")
@EnableScheduling
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    // pattern for date_time strings
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    // HTTP error messages
    private static final String NO_USER = "User (ID: {0}) does not exist";
    private static final String NO_TASK = "Invalid Task (ID: {0}) for User (ID: {1})";

    // platform independent line separator (for formatting HTTP responses)
    private static final String LINE_SEP = System.lineSeparator();

    /**
     * GET request to /api/user/{user_id}/task returns list of all Tasks for {user_id} User
     *
     * @return the List of Tasks or an error message
     */
    @GetMapping()
    public ResponseEntity<?> getTasksForUser(@PathVariable int user_id) {

        if (!userService.userExists(user_id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(MessageFormat.format(NO_USER, user_id) + LINE_SEP);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.getAllTasksForUser(user_id) + LINE_SEP);
    }

    /**
     * POST request to /api/user/{user_id}/task creates a new task for {user_id} User
     *
     * @param newTask - the Task to create for the User
     * @return The Task or an error message
     */
    @PostMapping(consumes = "application/JSON")
    public ResponseEntity<?> createTask(@PathVariable int user_id, @RequestBody Task newTask) {

        if (!userService.userExists(user_id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(MessageFormat.format(NO_USER, user_id) + LINE_SEP);
        }

        try {
            Task task = taskService.addTask(user_id, newTask);
            return ResponseEntity.status(HttpStatus.CREATED).body("Created task " + task + LINE_SEP);

        } catch (IllegalArgumentException iae) {
            // date_time in newTask is in the wrong format
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid date format for date_time of " + newTask.getDateTime()
                            + ". Please use " + DATE_TIME_PATTERN + LINE_SEP);
        } catch (DateTimeParseException dtpe) {
            // date_time in newTask could not be parsed
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Could not parse date_time string " + newTask.getDateTime()
                            + ". Please use " + DATE_TIME_PATTERN + LINE_SEP);
        }
    }

    /**
     * PUT request to /api/user/{user_id}/task/{task_id} updates info of {task_id} Task for {user_id} User
     *
     * @param task_id - the id of the Task to updateWith
     * @param newTask - Task object containing updated values
     * @return The updated Task or an error message
     */
    @PutMapping(value = "/{task_id}", consumes = "application/JSON")
    @Transactional
    public ResponseEntity<?> updateTask(@PathVariable int user_id, @PathVariable int task_id, @RequestBody Task newTask) {

        if (!userService.userExists(user_id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(MessageFormat.format(NO_USER, user_id) + LINE_SEP);
        } else if (!taskService.taskExists(task_id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(MessageFormat.format(NO_TASK, task_id, user_id) + LINE_SEP);
        }

        Optional<Task> task = taskService.updateTask(user_id, task_id, newTask);

        if (task.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body("Updated Task " + task.get() + LINE_SEP);

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(MessageFormat.format(NO_TASK, task_id, user_id) + LINE_SEP);
        }
    }

    /**
     * GET request to /api/user/{user_id}/task/{task_id} returns the details of {task_id} Task for {user_id} User
     *
     * @param task_id - the id of the Task to retrieve info for
     * @return The Task if found, otherwise an error message
     */
    @GetMapping(value = "/{task_id}", produces = "application/JSON")
    public ResponseEntity<?> getTaskInfo(@PathVariable int user_id, @PathVariable int task_id) {

        if (!userService.userExists(user_id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(MessageFormat.format(NO_USER, user_id) + LINE_SEP);
        } else if (!taskService.taskExists(task_id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(MessageFormat.format(NO_TASK, task_id, user_id) + LINE_SEP);
        }

        Optional<Task> task = taskService.getTaskInfo(user_id, task_id);
        if (task.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(task.get() + LINE_SEP);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(MessageFormat.format(NO_TASK, task_id, user_id) + LINE_SEP);
        }
    }

    /**
     * DELETE request to /api/user/{user_id}/task/{task_id} deletes {task_id} Task of {user_id} User
     *
     * @param task_id - the id of the Task to delete
     * @return The deleted Task or an error message
     */
    @DeleteMapping("/{task_id}")
    public ResponseEntity<?> deleteTask(@PathVariable int user_id, @PathVariable int task_id) {

        if (!userService.userExists(user_id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(MessageFormat.format(NO_USER, user_id) + LINE_SEP);
        } else if (!taskService.taskExists(task_id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(MessageFormat.format(NO_TASK, task_id, user_id) + LINE_SEP);
        }

        Optional<Task> task = taskService.deleteTaskById(task_id);
        if (task.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body("Deleted Task " + task.get() + LINE_SEP);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(MessageFormat.format(NO_TASK, task_id, user_id) + LINE_SEP);
        }
    }
}
