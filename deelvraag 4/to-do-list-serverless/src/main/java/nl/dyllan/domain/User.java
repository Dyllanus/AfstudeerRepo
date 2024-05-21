package nl.dyllan.domain;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.*;

@Getter
@Setter
@RegisterForReflection
public class User extends BaseEntity {
  private String name;
  private Set<UUID> ownerBoard = new HashSet<>();
  private Set<UUID> involvedBoards = new HashSet<>();

  public User(String name) {
    super();
    this.name = name;
  }

  @Builder
  public User(UUID id, Long version, Instant created, Instant lastModified, String name, Set<UUID> ownerBoard, Set<UUID> involvedBoards) {
    super(id, version, created, lastModified);
    this.name = name;
    this.ownerBoard = ownerBoard;
    this.involvedBoards = involvedBoards;
  }

  public void addOwnerBoard(UUID boardId) {
    ownerBoard.add(boardId);
  }

  public void addInvolvedBoard(UUID boardId) {
    involvedBoards.add(boardId);
  }

  public void removeOwnerBoard(UUID boardId) {
    ownerBoard.remove(boardId);
  }
  public void removeInvolvedBoard(UUID boardId) {
    involvedBoards.remove(boardId);
  }
}
