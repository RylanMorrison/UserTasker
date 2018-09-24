package api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a {@link User}'s Task
 *
 * @author Rylan Morrison
 * @since 19 September 2018
 */
@Entity
public class Task implements Serializable {

    public enum Status {
        PENDING,
        DONE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Integer id;
    @Column(name = "user_id")
    private Integer userId;
    private String name;
    private String description;
    @Column(name = "date_time")
    private String dateTime;
    @Enumerated(EnumType.STRING) // use the enum's name
    private Status status;

    public Integer getId() { return id; }

    public Integer getUserId() { return userId; }

    public void setUserId(Integer userId) { this.userId = userId; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    @JsonProperty("date_time")
    public String getDateTime() { return dateTime; }

    @JsonProperty("date_time")
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status; }

    /**
     * Updates all fields in this Task with whatever fields are present in newTask
     * @param newTask - the Task containing the fields to update this Task with
     */
    public void updateWith(Task newTask) {
        Integer newUserId = newTask.getUserId();
        String newName = newTask.getName();
        String newDesc = newTask.getDescription();
        String newDT = newTask.getDateTime();
        Status newStatus = newTask.getStatus();
        if (newUserId != null) {
            this.userId = newUserId;
        }
        if (newName != null) {
            this.name = newName;
        }
        if (newDesc != null) {
            this.description = newDesc;
        }
        if (newDT != null && ! newDT.equals("null")) {
            this.dateTime = newDT;
        }
        if (newStatus != null) {
            this.status = newStatus;
        }
    }

    @Override
    public String toString() {
        try {
            // try to return JSON representation of this Task
            return (new ObjectMapper()).writeValueAsString(this);
        } catch (JsonProcessingException jpe) {
            return String.format(
                "Task { ID = %d , UserID = %d , Name = %s , Description = %s , Date_Time = %s , Status = %s }",
                             id ,      userId ,      name ,      description , dateTime, status.name());
        }

    }
}
