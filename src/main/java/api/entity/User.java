package api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a User
 *
 * @author Rylan Morrison
 * @since 19 September 2018
 */
@Entity
public class User implements Serializable {

    /*
     Normal @GeneratedValue defaults to TABLE generation causing ids to increment between tables instead
     of each table incrementing its own id
      */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Integer id;
    private String username;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;

    public Integer getId() { return id; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    @JsonProperty("first_name")
    public String getFirstName() { return firstName; }

    @JsonProperty("first_name")
    public void setFirstName(String firstName) { this.firstName = firstName; }

    @JsonProperty("last_name")
    public String getLastName() { return lastName; }

    @JsonProperty("last_name")
    public void setLastName(String lastName) { this.lastName = lastName; }

    /**
     * Updates all fields in this User with whatever fields are present in otherUser
     *
     * @param otherUser - the User containing the fields to updateWith this User with
     */
    public void updateWith(User otherUser) {
        Objects.requireNonNull(otherUser);
        String otherUserUsername = otherUser.getUsername();
        String otherUserFirstName = otherUser.getFirstName();
        String otherUserLastName = otherUser.getLastName();
        if (otherUserUsername != null) {
            this.username = otherUserUsername;
        }
        if (otherUserFirstName != null) {
            this.firstName = otherUserFirstName;
        }
        if (otherUserLastName != null) {
            this.lastName = otherUserLastName;
        }
    }

    @Override
    public String toString() {
        try {
            // try to return JSON representation of this User
            return (new ObjectMapper()).writeValueAsString(this);
        } catch (JsonProcessingException jpe) {
            return String.format(
                    "User { id = %d , username = %s , firstName = %s , lastName = %s }",
                                 id ,      username , firstName, lastName);
        }

    }
}
