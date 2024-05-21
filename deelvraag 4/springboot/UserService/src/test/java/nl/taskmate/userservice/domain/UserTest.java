package nl.taskmate.userservice.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    @Test
    void testConstructor() {
        User user = new User("Dyllan", "", "cheebs");
        assertEquals("cheebs", user.getPassword());
        assertEquals("Dyllan", user.getUsername());
        assertEquals("Dyllan", user.getDisplayName());
    }

    @Test
    void testConstructorWithDisplayName() {
        User user = new User("Dyllan", "Dyllie", "cheebs");
        assertEquals("cheebs", user.getPassword());
        assertEquals("Dyllan", user.getUsername());
        assertEquals("Dyllie", user.getDisplayName());
    }

    @Test
    void editDisplayName() {
        User user = new User("Stije", "Stije", "bread");
        user.editDisplayName("Cheese Enjoyer");
        assertEquals("Cheese Enjoyer", user.getDisplayName());
    }
}