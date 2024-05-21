package nl.taskmate.boardservice.presentation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import nl.taskmate.boardservice.data.BoardRepository;
import nl.taskmate.boardservice.domain.Board;
import nl.taskmate.boardservice.domain.Task;
import nl.taskmate.boardservice.domain.TaskList;
import nl.taskmate.boardservice.presentation.dto.TagDto;
import nl.taskmate.boardservice.presentation.dto.TaskRequestDto;
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
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TaskControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String ownerDyllan = "Dyllan";
    private final String assigneeStije = "Stije";
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MockMvc mockMvc;
    private Board boardWithOwnerDyllan;
    private TaskList standardTasklist;
    private UUID boardUuid;
    private UUID tasklistUuid;

    @BeforeEach
    void init() {
        this.boardWithOwnerDyllan = new Board("Cool board", "Bread board", ownerDyllan);
        this.boardWithOwnerDyllan.setUsers(new HashSet<>(List.of(assigneeStije)));
        this.standardTasklist = new TaskList("cool title", "Cool description");
        this.boardWithOwnerDyllan.setTaskLists(new HashSet<>(List.of(this.standardTasklist)));

        this.boardRepository.save(this.boardWithOwnerDyllan);
        this.tasklistUuid = this.standardTasklist.getId();
        this.boardUuid = boardWithOwnerDyllan.getId();
    }

    @AfterEach
    void breakdown() {
        boardRepository.deleteAll();
    }

    @Test
    void createTask() throws Exception {
        TagDto tagDto = new TagDto("newTag", "#f66f52");
        TaskRequestDto dto = new TaskRequestDto("Cool task", "coolio", "25-10-2023", new HashSet<>(List.of(tagDto)), new HashSet<>(List.of(assigneeStije)));
        RequestBuilder request = MockMvcRequestBuilders
                .post("/boards/" + boardUuid + "/tasklists/" + tasklistUuid + "/tasks")
                .header("userdata", this.ownerDyllan)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(dto));

        this.mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.title", is(dto.title())))
                .andExpect(jsonPath("$.description", is(dto.description())))
                .andExpect(jsonPath("$.deadline", is(notNullValue())))
                .andExpect(jsonPath("$.tags", is(hasSize(1))))
                .andExpect(jsonPath("$.tags[0].name", is(tagDto.name())))
                .andExpect(jsonPath("$.tags[0].color", is(tagDto.color())))
                .andExpect(jsonPath("$.assignedUsers", is(hasSize(1))))
                .andExpect(jsonPath("$.assignedUsers[0]", is(assigneeStije)));
    }

    @Test
    void createTaskNoTagsAndNoDeadline() throws Exception {
        TaskRequestDto dto = new TaskRequestDto("Cool task", "coolio", "", new HashSet<>(), new HashSet<>(List.of(assigneeStije)));
        RequestBuilder request = MockMvcRequestBuilders
                .post("/boards/" + boardUuid + "/tasklists/" + tasklistUuid + "/tasks")
                .header("userdata", this.ownerDyllan)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(dto));

        this.mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.title", is(dto.title())))
                .andExpect(jsonPath("$.description", is(dto.description())))
                .andExpect(jsonPath("$.deadline", is("")))
                .andExpect(jsonPath("$.tags", is(hasSize(0))))
                .andExpect(jsonPath("$.assignedUsers", is(hasSize(1))))
                .andExpect(jsonPath("$.assignedUsers[0]", is(assigneeStije)));
    }

    @Test
    void createTaskNoAssignedUsers() throws Exception {
        TagDto tagDto = new TagDto("newTag", "#f66f52");
        TaskRequestDto dto = new TaskRequestDto("Cool task", "coolio", "25-10-2023", new HashSet<>(List.of(tagDto)), new HashSet<>());
        RequestBuilder request = MockMvcRequestBuilders
                .post("/boards/" + boardUuid + "/tasklists/" + tasklistUuid + "/tasks")
                .header("userdata", this.ownerDyllan)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(dto));

        this.mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.title", is(dto.title())))
                .andExpect(jsonPath("$.description", is(dto.description())))
                .andExpect(jsonPath("$.deadline", is(notNullValue())))
                .andExpect(jsonPath("$.tags", is(hasSize(1))))
                .andExpect(jsonPath("$.tags[0].name", is(tagDto.name())))
                .andExpect(jsonPath("$.tags[0].color", is(tagDto.color())))
                .andExpect(jsonPath("$.assignedUsers", is(hasSize(0))));
    }

    @Test
    void updateTask() throws Exception {
        Task task = new Task("coderen", "meer tests");
        this.boardWithOwnerDyllan.getTaskListById(tasklistUuid).addTask(task);
        this.boardRepository.save(boardWithOwnerDyllan);
        TagDto tagDto = new TagDto("newTag", "#f66f52");
        TaskRequestDto dto = new TaskRequestDto("Cool task", "coolio", "26-10-2023", new HashSet<>(List.of(tagDto)), new HashSet<>(List.of(assigneeStije, ownerDyllan)));
        RequestBuilder request = MockMvcRequestBuilders
                .put("/boards/" + boardUuid + "/tasklists/" + tasklistUuid + "/tasks/" + task.getId())
                .header("userdata", this.ownerDyllan)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(dto));

        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.title", is(dto.title())))
                .andExpect(jsonPath("$.description", is(dto.description())))
                .andExpect(jsonPath("$.deadline", is(notNullValue())))
                .andExpect(jsonPath("$.tags", is(hasSize(1))))
                .andExpect(jsonPath("$.tags[0].name", is(tagDto.name())))
                .andExpect(jsonPath("$.tags[0].color", is(tagDto.color())))
                .andExpect(jsonPath("$.assignedUsers", is(hasSize(2))));
    }

    @Test
    void updateTaskNoDeadlineAndNoAssignedUsers() throws Exception {
        Task task = new Task("coderen", "meer tests");
        this.boardWithOwnerDyllan.getTaskListById(tasklistUuid).addTask(task);
        this.boardRepository.save(boardWithOwnerDyllan);
        TagDto tagDto = new TagDto("newTag", "#f66f52");
        TaskRequestDto dto = new TaskRequestDto("Cool task", "coolio", null, new HashSet<>(List.of(tagDto)), new HashSet<>());
        RequestBuilder request = MockMvcRequestBuilders
                .put("/boards/" + boardUuid + "/tasklists/" + tasklistUuid + "/tasks/" + task.getId())
                .header("userdata", this.ownerDyllan)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(dto));

        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.title", is(dto.title())))
                .andExpect(jsonPath("$.description", is(dto.description())))
                .andExpect(jsonPath("$.deadline", is("")))
                .andExpect(jsonPath("$.tags", is(hasSize(1))))
                .andExpect(jsonPath("$.tags[0].name", is(tagDto.name())))
                .andExpect(jsonPath("$.tags[0].color", is(tagDto.color())))
                .andExpect(jsonPath("$.assignedUsers", is(hasSize(0))));
    }

    @Test
    void deleteTask() throws Exception {
        Task task = new Task("coderen", "meer tests");
        this.boardWithOwnerDyllan.getTaskListById(tasklistUuid).addTask(task);
        this.boardRepository.save(boardWithOwnerDyllan);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/boards/" + boardUuid + "/tasklists/" + tasklistUuid + "/tasks/" + task.getId())
                .header("userdata", this.ownerDyllan);

        this.mockMvc.perform(request)
                .andExpect(status().isNoContent());
        assertEquals(0, this.boardRepository.findById(boardUuid).get().getTaskListById(tasklistUuid).getTasks().size());
    }

}