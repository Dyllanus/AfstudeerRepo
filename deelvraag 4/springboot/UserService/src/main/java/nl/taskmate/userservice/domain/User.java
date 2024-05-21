package nl.taskmate.userservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity(name = "users") // user is a reserved keyword for the database.
@NoArgsConstructor
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, length = 20)
    private String username = "";

    @Column(length = 20)
    private String displayName = "";
    private String password = "";

    public User(String username, String displayName, String encodedPassword) {
        this.username = username;
        this.displayName = (displayName != null && !displayName.isEmpty()) ? displayName : username;
        this.password = encodedPassword;
    }

    public void editDisplayName(String newName) {
        this.displayName = newName;
    }
}
