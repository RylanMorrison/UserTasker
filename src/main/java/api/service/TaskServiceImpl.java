package api.service;

import api.entity.Task;
import api.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of {@link TaskService} that performs operations on {@link Task}s
 *
 * @author Rylan Morrison
 * @since 22 September 2018
 */
@Service("taskService")
@EnableScheduling
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepo;

    private Logger logger = LoggerFactory.getLogger("TaskService");

    // pattern for date_time strings
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Override
    public Task addTask(final Integer user_id, Task task) throws IllegalArgumentException, DateTimeParseException {

        Objects.requireNonNull(task);
        String date_time = task.getDateTime();
        // make sure that the date_time from the request is in the correct format to avoid parsing errors later on
        logger.debug("Checking date_time " + date_time);
        if (date_time == null) {
            throw new IllegalArgumentException("date_time must not be null");
        }

        LocalDateTime now = LocalDateTime.now();
        // throws IllegalArgumentException or DateTimeParse exception if date_time is not correct
        // better to check the String with a giant regex?
        LocalDateTime taskTime = LocalDateTime.parse(date_time, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));

        task.setUserId(user_id);
        // set Task status depending on date_time
        task.setStatus((now.compareTo(taskTime) >= 0) ? Task.Status.DONE : Task.Status.PENDING);
        taskRepo.saveAndFlush(task);
        logger.info("Added Task " + task);
        return task;
    }

    @Override
    @Transactional
    public Optional<Task> updateTask(final Integer user_id, final Integer id, Task newTask) {

        Optional<Task> task = taskRepo.findById(id);
        if (task.isPresent()) {
            Task currentTask = task.get();
            if (currentTask.getUserId().equals(user_id)) {
                // only update the Task if it is associated with {user_id} User
                logger.info("Updating Task: " + currentTask);
                currentTask.updateWith(newTask);
                logger.info("-> " + currentTask);
                return Optional.of(currentTask);
            }
        }
        logger.info("Could not find Task (@" + id +") to update for User (@" + user_id + ")");
        return Optional.empty();
    }

    @Override
    public Optional<Task> deleteTaskById(final Integer id) {
        Optional<Task> task = taskRepo.findById(id);
        if (task.isPresent()) {
            taskRepo.deleteById(id);
            logger.info("Deleted Task: " + task.get());
            return task;
        }
        logger.info("Could not find Task (@" + id + ") to delete");
        return Optional.empty();
    }

    @Override
    public Optional<Task> getTaskInfo(final Integer user_id, final Integer id) {

        Optional<Task> task = taskRepo.findById(id);
        if (task.isPresent()) {
            Task result = task.get();
            if (result.getUserId().equals(user_id)) {
                // only return Task for {user_id} User
                logger.info("Retrieved Task: " + task.get());
                return task;
            }
        }
        logger.info("Could not find Task (@" + id + ") for User (@" + user_id + ")");
        return Optional.empty();
    }

    @Override
    public List<Task> getAllTasksForUser(final Integer user_id) {
        List<Task> tasks = taskRepo.findAllByUserId(user_id);
        logger.info("All Tasks for User (@" + user_id + "): " + tasks);
        return tasks;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> tasks = taskRepo.findAll();
        logger.debug("All Tasks: " + tasks);
        return tasks;
    }

    @Override
    public boolean taskExists(final Integer id) {
        return taskRepo.existsById(id);
    }

    /**
     * Checks all Tasks once every 30 seconds to see if there are any that have a status of PENDING and a date_time
     * that is after the current date and time. If such a Task is found it is printed to the console and its
     * status is changed to DONE. Alternatively, ensure that all DONE Tasks that have a date_time that is before the
     * current date and time have their status set to PENDING.
     */
    @Scheduled(fixedRate=30000)
    @Transactional
    public void checkTasks() {
        List<Task> tasks = getAllTasks();
        final LocalDateTime now = LocalDateTime.now();

        for (Task task : tasks) {
            String dateTimeString = task.getDateTime();

            LocalDateTime taskTime = LocalDateTime.parse(dateTimeString,
                    DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));

            if (now.compareTo(taskTime) >= 0) {
                if (task.getStatus() == Task.Status.PENDING) {
                    task.setStatus(Task.Status.DONE);
                    logger.info("Task completed: " + task);
                }
            } else if (task.getStatus() != Task.Status.PENDING) {
                task.setStatus(Task.Status.PENDING);
                logger.info("Updated unfinished Task to PENDING: " + task);
            }
        }
    }
}
