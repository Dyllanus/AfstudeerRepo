package nl.taskmate.boardservice.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.taskmate.boardservice.data.BoardRepository;
import nl.taskmate.boardservice.domain.Board;
import nl.taskmate.boardservice.domain.Task;
import nl.taskmate.boardservice.domain.TaskList;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskListService {
    private final BoardRepository boardRepository;
    private final BoardService boardService;

    public TaskList create(UUID boardId, String username, String title, String description) {
        Board board = this.boardService.findBoardById(boardId);
        board.checkIfInAssignedUsers(username);
        TaskList taskList = new TaskList(title, description);
        board.addTaskList(taskList);
        this.boardRepository.save(board);
        return taskList;
    }

    public TaskList update(UUID boardId, UUID taskListId, String username, String title, String description) {
        Board board = this.boardService.findBoardById(boardId);
        board.checkIfInAssignedUsers(username);
        TaskList taskList = board.getTaskListById(taskListId);
        taskList.setTitle(title);
        taskList.setDescription(description);
        this.boardRepository.save(board);
        return taskList;
    }

    public TaskList moveTask(UUID boardId, String username, UUID oldTaskListId, UUID newTaskListId, UUID taskId) {
        Board board = this.boardService.findBoardById(boardId);
        board.checkIfInAssignedUsers(username);
        TaskList oldtaskList = board.getTaskListById(oldTaskListId);

        Task task = oldtaskList.getTaskById(taskId);
        oldtaskList.deleteTask(task);

        // Flushing here is required. Without it, the repo will throw a unique index error.
        this.boardRepository.save(board);
        this.boardRepository.flush();

        board.getTaskListById(newTaskListId).addTask(task);
        return this.boardRepository.save(board).getTaskListById(newTaskListId);
    }

    public void delete(UUID boardId, UUID taskListId, String username) {
        Board board = this.boardService.findBoardById(boardId);
        board.checkIfInAssignedUsers(username);
        TaskList taskList = board.getTaskListById(taskListId);
        board.deleteTaskList(taskList);
        this.boardRepository.save(board);
    }
}
