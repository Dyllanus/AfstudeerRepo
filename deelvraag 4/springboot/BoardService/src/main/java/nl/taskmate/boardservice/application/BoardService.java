package nl.taskmate.boardservice.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.taskmate.boardservice.data.BoardRepository;
import nl.taskmate.boardservice.domain.Board;
import nl.taskmate.boardservice.domain.Tag;
import nl.taskmate.boardservice.domain.exceptions.BoardNotFoundException;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Set;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    private final SendRequestToUserService requestToUserService;

    // When the user is not the owner the method should only return the boards which are assigned to the user.
    // If the user is the owner of the boards the method should return all available boards to the user.
    public Set<Board> getBoardsThatUserIsInvolvedIn(String username, boolean ownerOnly) {
        return ownerOnly ? this.boardRepository.findBoardsByOwnerIgnoreCase(username)
                : this.boardRepository.findBoardsByAssignedUsers(username);
    }

    public Board get(UUID boardId, String username) {
        Board board = this.findBoardById(boardId);
        board.checkIfInAssignedUsers(username);
        return board;
    }

    public Board create(String title, String description, String owner) {
        return this.boardRepository.save(new Board(title, description, owner));
    }

    public Board update(UUID boardId, String title, String description, String owner) {
        Board board = this.findBoardById(boardId);
        board.checkIfIsOwner(owner);
        board.setDescription(description);
        board.setTitle(title);
        this.boardRepository.save(board);
        return board;
    }

    public void delete(UUID boardId, String owner) {
        Board board = this.findBoardById(boardId);
        board.checkIfIsOwner(owner);
        this.boardRepository.delete(board);
    }

    public Board updatedAssignedUsers(String owner, UUID boardId, Set<String> usersToAssign) {
        Board board = this.findBoardById(boardId);
        board.checkIfIsOwner(owner);
        this.requestToUserService.checkIfUsersExist(usersToAssign);
        board.setUsers(usersToAssign);
        this.boardRepository.save(board);
        return board;
    }

    public Tag createTag(String username, UUID boardId, String title, Color color) {
        Board board = this.findBoardById(boardId);
        board.checkIfInAssignedUsers(username);
        Tag tag = new Tag(title, color);
        board.addTag(tag);
        this.boardRepository.save(board);
        return tag;
    }

    public Tag updateTag(String username, UUID boardId, String oldTitle, String newTitle, Color color) {
        Board board = this.findBoardById(boardId);
        board.checkIfInAssignedUsers(username);
        Tag tag = board.updateTagByTitle(oldTitle, newTitle, color);
        this.boardRepository.save(board);
        return tag;
    }

    public void deleteTag(String username, UUID boardId, String title) {
        Board board = this.findBoardById(boardId);
        board.checkIfInAssignedUsers(username);
        board.deleteTagByTitle(title);
        this.boardRepository.save(board);
    }

    public Board findBoardById(UUID boardId) {
        return this.boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board with uuid '" + boardId + "' not found."));
    }

}
