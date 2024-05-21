package nl.taskmate.boardservice.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.taskmate.boardservice.data.BoardRepository;
import nl.taskmate.boardservice.domain.Board;
import nl.taskmate.boardservice.domain.Tag;
import nl.taskmate.boardservice.domain.Task;
import nl.taskmate.boardservice.domain.TaskList;
import nl.taskmate.boardservice.domain.exceptions.TaskNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {
    private final BoardService boardService;
    private final BoardRepository boardRepository;

    public Task create(UUID boardId, String username, UUID taskListId, String title, String description,
                       String deadline, Set<Tag> tags, Set<String> assignedUsers) {
        Board board = this.boardService.findBoardById(boardId);
        board.checkIfInAssignedUsers(username);
        TaskList taskList = board.getTaskListById(taskListId);
        Task task = new Task(title, description);

        if (deadline != null && !deadline.isEmpty()) task.setDeadline(DateUtils.convertStringToDate(deadline));
        if (!assignedUsers.isEmpty()) {
            board.checkIfInAssignedUsers(assignedUsers);
            task.assignUsers(assignedUsers);
        }
        if (!tags.isEmpty()) {
            board.addTags(tags);
            task.addTags(tags);
        }
        taskList.addTask(task);
        this.boardRepository.save(board);
        return task;
    }

    public Task update(UUID boardId, String username, UUID taskListId, UUID taskId, String title, String description,
                       String deadline, Set<Tag> tags, Set<String> assignedUsers) {
        Board board = this.boardService.findBoardById(boardId);
        board.checkIfInAssignedUsers(username);
        TaskList taskList = board.getTaskListById(taskListId);
        Task task = this.getTaskOutTaskList(taskList, taskId);
        if (deadline != null && !deadline.isEmpty()) task.setDeadline(DateUtils.convertStringToDate(deadline));
        if (!assignedUsers.isEmpty()) {
            board.checkIfInAssignedUsers(assignedUsers);
            task.setAssignedUsers(assignedUsers);
        }

        task.setTitle(title);
        task.setDescription(description);

        if (!tags.isEmpty())
            board.addTags(tags);
        task.setTags(tags);

        this.boardRepository.save(board);
        return task;
    }

    public void deleteTask(UUID boardId, String username, UUID taskListId, UUID taskId) {
        Board board = this.boardService.findBoardById(boardId);
        board.checkIfInAssignedUsers(username);
        TaskList taskList = board.getTaskListById(taskListId);
        Task task = this.getTaskOutTaskList(taskList, taskId);
        taskList.deleteTask(task);
        this.boardRepository.save(board);
    }


    private Task getTaskOutTaskList(TaskList taskList, UUID taskId) {
        return taskList.getTasks().stream()
                .filter(t -> t.getId().equals(taskId)).findFirst()
                .orElseThrow(() -> new TaskNotFoundException("Task with uuid '" + taskId + "' was not found"));
    }

}
