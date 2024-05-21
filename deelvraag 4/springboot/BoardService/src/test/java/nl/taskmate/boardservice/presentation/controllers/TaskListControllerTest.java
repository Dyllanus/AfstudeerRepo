package nl.taskmate.boardservice.presentation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import nl.taskmate.boardservice.data.BoardRepository;
import nl.taskmate.boardservice.domain.Board;
import nl.taskmate.boardservice.domain.Task;
import nl.taskmate.boardservice.domain.TaskList;
import nl.taskmate.boardservice.presentation.dto.CreateTaskListDto;
import nl.taskmate.boardservice.presentation.dto.MoveTaskRequestDto;
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
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TaskListControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String ownerDyllan = "Dyllan";
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private MockMvc mockMvc;
    private Board boardWithOwnerDyllan;
    private UUID uuid;

    @BeforeEach
    void init() {
        this.boardWithOwnerDyllan = new Board("Cool board", "Bread board", ownerDyllan);
        this.boardWithOwnerDyllan.setTaskLists(new HashSet<>());

        this.boardRepository.save(this.boardWithOwnerDyllan);
        this.uuid = boardWithOwnerDyllan.getId();
    }

    @AfterEach
    void breakdown() {
        boardRepository.deleteAll();
    }

    @Test
    void testCreateTaskList() throws Exception {
        CreateTaskListDto dto = new CreateTaskListDto("Taskylisty", "my first tasklist");
        RequestBuilder request = MockMvcRequestBuilders
                .post("/boards/" + uuid + "/tasklists")
                .header("userdata", this.ownerDyllan)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(dto));

        this.mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is(dto.title())))
                .andExpect(jsonPath("$.description", is(dto.description())))
                .andExpect(jsonPath("$.tasks", is(hasSize(0))));
        assertEquals(1, this.boardRepository.findById(uuid).get().getTaskLists().size());
    }

    @Test
    void updateTaskList() throws Exception {
        CreateTaskListDto dto = new CreateTaskListDto("Taskylisty", "my first tasklist");
        TaskList taskList = new TaskList("oldtaskListname", "old description");
        this.boardWithOwnerDyllan.addTaskList(taskList);
        this.boardRepository.save(this.boardWithOwnerDyllan);

        RequestBuilder request = MockMvcRequestBuilders
                .put("/boards/" + uuid + "/tasklists/" + taskList.getId())
                .header("userdata", this.ownerDyllan)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(dto));

        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(dto.title())))
                .andExpect(jsonPath("$.description", is(dto.description())))
                .andExpect(jsonPath("$.tasks", is(hasSize(0))));
        assertEquals(1, this.boardRepository.findById(uuid).get().getTaskLists().size());
    }

    @Test
    void testMoveTask() throws Exception {
        Task taskToMove = new Task("tasktomove", "cool description");
        TaskList oldTasklist = new TaskList("oldtaskListname", "old description");
        oldTasklist.addTask(taskToMove);
        TaskList newTasklist = new TaskList("newtasklist", "new description");
        this.boardWithOwnerDyllan.addTaskList(oldTasklist);
        this.boardWithOwnerDyllan.addTaskList(newTasklist);
        this.boardRepository.save(this.boardWithOwnerDyllan);
        MoveTaskRequestDto dto = new MoveTaskRequestDto(newTasklist.getId(), taskToMove.getId());

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/boards/" + uuid + "/tasklists/" + oldTasklist.getId())
                .header("userdata", this.ownerDyllan)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(dto));

        this.mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(newTasklist.getTitle())))
                .andExpect(jsonPath("$.description", is(newTasklist.getDescription())))
                .andExpect(jsonPath("$.tasks", is(hasSize(1))));
        assertEquals(2, this.boardRepository.findById(uuid).get().getTaskLists().size());
        assertEquals(0, this.boardRepository.findById(uuid).get().getTaskListById(oldTasklist.getId()).getTasks().size());
        assertEquals(1, this.boardRepository.findById(uuid).get().getTaskListById(newTasklist.getId()).getTasks().size());
    }

    @Test
    void testDeleteTaskList() throws Exception {
        TaskList taskList = new TaskList("oldtaskListname", "old description");
        this.boardWithOwnerDyllan.addTaskList(taskList);
        this.boardRepository.save(this.boardWithOwnerDyllan);
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/boards/" + uuid + "/tasklists/" + taskList.getId())
                .header("userdata", this.ownerDyllan);

        this.mockMvc.perform(request)
                .andExpect(status().isNoContent());
        assertEquals(0, this.boardRepository.findById(uuid).get().getTaskLists().size());
    }

}