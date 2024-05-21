package nl.taskmate.userservice.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import nl.taskmate.userservice.application.PasswordEncoderUtil;
import nl.taskmate.userservice.data.UserRepository;
import nl.taskmate.userservice.domain.User;
import nl.taskmate.userservice.presentation.dto.LoginDto;
import nl.taskmate.userservice.presentation.dto.RegistrationDto;
import nl.taskmate.userservice.presentation.dto.UpdateDisplayNameDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private User user, user2;

    @BeforeEach
    void init() {
        this.user = new User("Dyllan", null, PasswordEncoderUtil.encodePassword("coolpassword"));
        this.user2 = new User("Stije", null, PasswordEncoderUtil.encodePassword("Bread"));
        this.userRepository.save(user);
        this.userRepository.save(user2);
    }

    @AfterEach
    void breakdown() {
        userRepository.deleteAll();
    }

    @Test
    void getAllUsers() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/users");

        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(hasSize(2))));
    }

    @Test
    void findUserByUsername() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/users/" + user.getUsername());

        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().toString())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.displayName", is(user.getDisplayName())));
    }

    @Test
    void testFindUserNotFound() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/users/" + "NOTANUSER");

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void editDisplayName() throws Exception {
        UpdateDisplayNameDto dto = new UpdateDisplayNameDto("Dyllie");
        RequestBuilder request = MockMvcRequestBuilders
                .patch("/users/" + user.getUsername())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto));

        this.mockMvc.perform(request)
                .andExpect(status().isNoContent());
        assertEquals("Dyllie", userRepository.findById(user.getId()).get().getDisplayName());
    }

    @Test
    void testLogin() throws Exception {
        LoginDto dto = new LoginDto(user.getUsername(), "coolpassword");
        RequestBuilder request = MockMvcRequestBuilders
                .post("/users/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto));

        var response = this.mockMvc.perform(request)
                .andExpect(status().isOk()).andReturn();

        assertEquals("User 'Dyllan' logged in successfully. Id: " + user.getId(), response.getResponse().getContentAsString());
    }

    @Test
    void testLoginWrongPassword() throws Exception {
        LoginDto dto = new LoginDto(user.getUsername(), "WrongPassword");
        RequestBuilder request = MockMvcRequestBuilders
                .post("/users/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto));

        this.mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void checkIfUsernamesExists() throws Exception {
        Set<String> usernames = new HashSet<>(List.of(user.getUsername(), user2.getUsername()));
        RequestBuilder request = MockMvcRequestBuilders
                .head("/users?usernames=" + String.join(",", usernames));

        this.mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void register() throws Exception {
        RegistrationDto dto = new RegistrationDto("Arjen", "CodeManger", "23i27");
        RequestBuilder request = MockMvcRequestBuilders
                .post("/users/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto));

        this.mockMvc.perform(request)
                .andExpect(status().isOk());
        assertTrue(this.userRepository.findByUsername("Arjen").isPresent());
    }

    @Test
    void registerSameUserThrowsException() throws Exception {
        RegistrationDto dto = new RegistrationDto("Dyllan", "CodeManger", "23i27");
        RequestBuilder request = MockMvcRequestBuilders
                .post("/users/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto));

        this.mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

}