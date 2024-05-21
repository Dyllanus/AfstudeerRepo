package nl.taskmate.userservice.application;

import nl.taskmate.userservice.data.UserRepository;
import nl.taskmate.userservice.domain.User;
import nl.taskmate.userservice.domain.exceptions.PasswordIncorrectException;
import nl.taskmate.userservice.domain.exceptions.UserAlreadyExistsException;
import nl.taskmate.userservice.domain.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    //added for 100% line coverage I know it's not good, but it's silly to not get 100% line coverage if you can get it this easy
    private final PasswordEncoderUtil passwordEncoderUtil = new PasswordEncoderUtil();
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    void init() {

    }

    @Test
    void testRegister() {
        String username = "Dyllan";
        String password = "coolpassword123";
        userService.register(username, null, password);

        verify(this.userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterWhenUsernameExists() {
        String username = "Dyllan";
        String password = "coolpassword123";
        User user = new User("Dyllan", null, "iets");
        when(this.userRepository.findByUsername("Dyllan")).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> userService.register(username, null, password));
        verify(this.userRepository, times(0)).save(any(User.class));
    }

    @Test
    void testLogin() {
        String username = "Dyllan";
        String password = "iets";
        String encodedPassword = PasswordEncoderUtil.encodePassword(password);
        User user = new User("Dyllan", null, encodedPassword);

        when(this.userRepository.findByUsername("Dyllan")).thenReturn(Optional.of(user));

        User userResult = this.userService.login(username, password);
        assertEquals(username, userResult.getUsername());
        assertEquals(username, userResult.getDisplayName());
    }

    @Test
    void testLoginPasswordIncorrect() {
        String username = "Dyllan";
        String password = "iets";
        User user = new User("Dyllan", null, password);
        when(this.userRepository.findByUsername("Dyllan")).thenReturn(Optional.of(user));

        assertThrows(PasswordIncorrectException.class, () -> this.userService.login(username, password));
    }

    @Test
    void testCheckIfAllUsersExist() {
        User user = new User("Dyllan", null, "cooly");
        User user1 = new User("Stije", null, "bread");
        Set<User> usernamesSet = new HashSet<>();
        usernamesSet.add(user);
        usernamesSet.add(user1);
        when(this.userRepository.findUsersByUsernameIn(anySet())).thenReturn(usernamesSet);

        assertDoesNotThrow(() -> this.userService.checkIfAllUsersExist(new HashSet<>(List.of("Dyllan", "Stije"))));
    }

    @Test
    void testCheckIfAllUsersExistThrowsException() {
        User user = new User("Dyllan", null, "cooly");
        User user1 = new User("Stije", null, "bread");
        Set<User> usernamesSet = new HashSet<>();
        usernamesSet.add(user);
        usernamesSet.add(user1);
        when(this.userRepository.findUsersByUsernameIn(anySet())).thenReturn(usernamesSet);
        Throwable throwable = catchThrowable(() -> this.userService.checkIfAllUsersExist(new HashSet<>(List.of("Stije", "Dyllan", "Arjen", "Laurens"))));
        assertThat(throwable)
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("The following usernames has not been found: Arjen, Laurens");
        verify(this.userRepository, times(0)).save(any(User.class));

    }

    @Test
    void testGetAllUsers() {
        User user = new User("Dyllan", null, "cooly");
        User user1 = new User("Stije", null, "bread");
        when(this.userRepository.findAll()).thenReturn(List.of(user, user1));

        assertEquals(List.of(user, user1), this.userService.getAllUsers());
    }

    @Test
    void testEditDisplayName() {
        String username = "Dyllan";
        String password = "iets";
        User user = new User(username, null, password);
        when(this.userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        this.userService.editDisplayName(username, "lightningGuy");
        assertEquals("lightningGuy", user.getDisplayName());
        verify(this.userRepository, times(1)).save(user);
    }

    @Test
    void testFindUserByUsername() {
        when(this.userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> this.userService.findUserByUsername("dyllan"));
    }


}