package nl.taskmate.userservice.data;

import nl.taskmate.userservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;


public interface UserRepository extends JpaRepository<User, UUID> {
    Set<User> findUsersByUsernameIn(Set<String> username);

    Optional<User> findByUsername(String username);
}