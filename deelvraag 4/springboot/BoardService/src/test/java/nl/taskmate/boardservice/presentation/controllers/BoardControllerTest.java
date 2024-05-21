package nl.taskmate.boardservice.presentation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import nl.taskmate.boardservice.data.BoardRepository;
import nl.taskmate.boardservice.domain.Board;
import nl.taskmate.boardservice.domain.Tag;
import nl.taskmate.boardservice.presentation.dto.CreateBoardDto;
import nl.taskmate.boardservice.presentation.dto.SetUsersDto;
import nl.taskmate.boardservice.presentation.dto.TagDto;
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

import java.awt.*;
import java.util.List;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BoardControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String ownerDyllan = "Dyllan";
    private final String ownerStije = "Stije";
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MockMvc mockMvc;
    private Board boardWithOwnerDyllan, boardWithOwnerStije;
    private UUID uuid;

    @BeforeEach
    void init() {
        this.boardWithOwnerDyllan = new Board("Cool board", "Bread board", ownerDyllan);
        this.boardWithOwnerStije = new Board("Bread making", "A Board to the steps for bread", ownerStije);

        this.boardWithOwnerDyllan.setUsers(new HashSet<>(List.of(ownerStije)));
        this.boardRepository.save(this.boardWithOwnerDyllan);
        this.boardRepository.save(this.boardWithOwnerStije);
        this.uuid = boardWithOwnerDyllan.getId();
    }


    @AfterEach
    void breakdown() {
        boardRepository.deleteAll();
    }


    @Test
    void testGetBoardsThatUserIsInvolvedIn() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/boards")
                .header("userdata", this.ownerStije);

        this.mockMvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", is(hasSize(2))));
    }

    @Test
    void testGetBoardThatUserIsInvolvedInOwnerTrue() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/boards?ownerOnly=true")
                .header("userdata", this.ownerStije);

        this.mockMvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$", is(hasSize(1))))
                .andExpect(jsonPath("$[0].id", is(this.boardWithOwnerStije.getId().toString())))
                .andExpect(jsonPath("$[0].title", is(this.boardWithOwnerStije.getTitle())))
                .andExpect(jsonPath("$[0].description", is(this.boardWithOwnerStije.getDescription())))
                .andExpect(jsonPath("$[0].owner", is(this.boardWithOwnerStije.getOwner())))
                .andExpect(jsonPath("$[0].taskLists", is(hasSize(3))))
                .andExpect(jsonPath("$[0].tags", is(hasSize(6))));

    }

    @Test
    void testGetBoards() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/boards/" + this.uuid)
                .header("userdata", this.ownerDyllan);

        this.mockMvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(this.uuid.toString())))
                .andExpect(jsonPath("$.title", is(this.boardWithOwnerDyllan.getTitle())))
                .andExpect(jsonPath("$.description", is(this.boardWithOwnerDyllan.getDescription())))
                .andExpect(jsonPath("$.owner", is(this.boardWithOwnerDyllan.getOwner())))
                .andExpect(jsonPath("$.taskLists", is(hasSize(3))))
                .andExpect(jsonPath("$.assignedUsers", is(hasSize(1))))
                .andExpect(jsonPath("$.tags", is(hasSize(6))));

    }

    @Test
    void testGetBoardsWithWrongId() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/boards/" + UUID.randomUUID())
                .header("userdata", this.ownerDyllan);

        this.mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetBoardWithWrongName() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/boards/" + uuid.toString())
                .header("userdata", "NotAUser");

        this.mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateBoard() throws Exception {
        CreateBoardDto dto = new CreateBoardDto("balloon board", "bla bla bla");
        RequestBuilder request = MockMvcRequestBuilders
                .post("/boards")
                .header("userdata", ownerDyllan)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(dto));

        this.mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.title", is(dto.title())))
                .andExpect(jsonPath("$.description", is(dto.description())))
                .andExpect(jsonPath("$.owner", is(ownerDyllan)))
                .andExpect(jsonPath("$.taskLists", is(hasSize(3))))
                .andExpect(jsonPath("$.tags", is(hasSize(6))))
                .andExpect(jsonPath("$.assignedUsers", is(hasSize(0))));
    }

    @Test
    void testUpdateBoard() throws Exception {
        CreateBoardDto dto = new CreateBoardDto("balloon board", "bla bla bla");
        RequestBuilder request = MockMvcRequestBuilders
                .put("/boards/" + uuid.toString())
                .header("userdata", ownerDyllan)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(dto));

        this.mockMvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(uuid.toString())))
                .andExpect(jsonPath("$.title", is(dto.title())))
                .andExpect(jsonPath("$.description", is(dto.description())))
                .andExpect(jsonPath("$.owner", is(ownerDyllan)))
                .andExpect(jsonPath("$.taskLists", is(hasSize(3))))
                .andExpect(jsonPath("$.tags", is(hasSize(6))))
                .andExpect(jsonPath("$.assignedUsers", is(hasSize(1))));
    }

    @Test
    void testDeleteBoard() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/boards/" + uuid.toString())
                .header("userdata", ownerDyllan);

        this.mockMvc.perform(request)
                .andExpect(status().isNoContent());
        assertThrows(NoSuchElementException.class, () -> this.boardRepository.findById(this.uuid).get());
    }

    @Test
    void testAssignUsersToBoardThrowsException() throws Exception {
        Set<String> users = new HashSet<>(List.of("Arjen", "Laurens", this.ownerStije, "NONONONOONONONONNOUSER"));
        SetUsersDto dto = new SetUsersDto(users);
        RequestBuilder request = MockMvcRequestBuilders
                .put("/boards/" + uuid.toString() + "/users")
                .header("userdata", ownerDyllan)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto));

        this.mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }


    @Test
    void testCreateTag() throws Exception {
        TagDto dto = new TagDto("Bug", "#ff0000");
        RequestBuilder request = MockMvcRequestBuilders
                .post("/boards/" + uuid.toString() + "/tags")
                .header("userdata", ownerDyllan)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(dto));

        this.mockMvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name", is(dto.name())))
                .andExpect(jsonPath("$.color", is(dto.color())));

        assertEquals(7, this.boardRepository.findById(this.uuid).get().getTags().size());
    }

    @Test
    void testCreateTagWrongColorFormat() throws Exception {
        TagDto dto = new TagDto("Bug", "#dd");
        RequestBuilder request = MockMvcRequestBuilders
                .post("/boards/" + uuid.toString() + "/tags")
                .header("userdata", ownerDyllan)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(dto));

        this.mockMvc.perform(request)
                .andExpect(status().isBadRequest());
        assertEquals(6, this.boardRepository.findById(this.uuid).get().getTags().size());
    }

    @Test
    void testUpdateTag() throws Exception {
        Tag tag = new Tag("Bug", Color.RED);
        this.boardWithOwnerDyllan.addTag(tag);
        this.boardRepository.save(this.boardWithOwnerDyllan);
        TagDto dto = new TagDto("NotABug", "#00ff00");
        RequestBuilder request = MockMvcRequestBuilders
                .put("/boards/" + uuid.toString() + "/tags/" + tag.getTitle())
                .header("userdata", ownerDyllan)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(dto));

        this.mockMvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name", is(dto.name())))
                .andExpect(jsonPath("$.color", is(dto.color())));
        assertEquals(7, this.boardRepository.findById(this.uuid).get().getTags().size());
    }

    @Test
    void testUpdateTagWrongColorFormat() throws Exception {
        Tag tag = new Tag("Bug", Color.RED);
        this.boardWithOwnerDyllan.addTag(tag);
        this.boardRepository.save(this.boardWithOwnerDyllan);
        TagDto dto = new TagDto("NotABug", "#00f00");
        RequestBuilder request = MockMvcRequestBuilders
                .put("/boards/" + uuid.toString() + "/tags/" + tag.getTitle())
                .header("userdata", ownerDyllan)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(dto));

        this.mockMvc.perform(request)
                .andExpect(status().isBadRequest());
        assertEquals(7, this.boardRepository.findById(this.uuid).get().getTags().size());
    }

    @Test
    void testDeleteTag() throws Exception {
        Tag tag = new Tag("Bug", Color.RED);
        this.boardWithOwnerDyllan.addTag(tag);
        this.boardRepository.save(this.boardWithOwnerDyllan);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/boards/" + uuid.toString() + "/tags/" + tag.getTitle())
                .header("userdata", ownerDyllan);

        this.mockMvc.perform(request)
                .andExpect(status().isNoContent());
        assertEquals(6, this.boardRepository.findById(this.uuid).get().getTags().size());
    }

}