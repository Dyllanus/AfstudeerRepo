package nl.taskmate.userservice.application;

import jakarta.transaction.Transactional;
import nl.taskmate.userservice.data.UserRepository;
import nl.taskmate.userservice.domain.User;
import nl.taskmate.userservice.domain.exceptions.PasswordIncorrectException;
import nl.taskmate.userservice.domain.exceptions.UserAlreadyExistsException;
import nl.taskmate.userservice.domain.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository repository) {
        this.userRepository = repository;
    }

    public void register(String username, String displayName, String password) {
        if (this.userRepository.findByUsername(username).isPresent())
            throw new UserAlreadyExistsException("User with name '" + username + "' already exists.");

        String encodedPassword = PasswordEncoderUtil.encodePassword(password);

        User user = new User(username, displayName, encodedPassword);

        this.userRepository.save(user);
    }

    // Currently quite useless because not actual security like bearer, but hey it's there
    public User login(String username, String password) {
        User user = this.findUserByUsername(username);

        if (!PasswordEncoderUtil.matches(password, user.getPassword()))
            throw new PasswordIncorrectException();

        return user;
    }

    public void checkIfAllUsersExist(Set<String> usernames) {
        Set<User> users = this.userRepository.findUsersByUsernameIn(usernames);
        if (users.size() != usernames.size()) {
            String nonExistingUsers = usernames.stream().filter(username -> !users.stream().map(User::getUsername).toList().contains(username)).collect(Collectors.joining(", "));
            throw new UserNotFoundException("The following usernames has not been found: " + nonExistingUsers);
        }
    }

    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    public void editDisplayName(String username, String newDisplayName) {
        User user = this.findUserByUsername(username);
        user.editDisplayName(newDisplayName);
        this.userRepository.save(user);
    }

    public User findUserByUsername(String username) {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with name '" + username + "' not found."));
    }
}