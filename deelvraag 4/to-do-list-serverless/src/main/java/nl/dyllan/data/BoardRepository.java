package nl.dyllan.data;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import nl.dyllan.data.mapper.BoardMapper;
import nl.dyllan.domain.Board;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Singleton
public class BoardRepository {
  private static final Logger logger = LoggerFactory.getLogger(BoardRepository.class);
  public final String BOARD_TABLE_NAME = System.getenv("BOARD_TABLE_NAME");
  public final DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.standard().build());
  public final Table boardTable = dynamoDB.getTable(BOARD_TABLE_NAME);
  {
    System.out.println(boardTable.describe().toString());
  }
  @Inject
  TaskListRepository taskListRepository;

  //todo fix PK of BOARD zodat het alleen nog maar de boardID is we moeten toch een user ophalen om het id te krijgen.

  public void save(Board board){
    boardTable.putItem(BoardMapper.boardToDynamoDB(board));
    if (!board.getTaskLists().isEmpty()) {
      taskListRepository.saveTaskLists(board.getTaskLists());
    }
  }

  public Optional<Board> findById(UUID id) {
    var item = boardTable.getItem(
            "PK", "BOARD#" + id);
    if (item == null) {
      return Optional.empty();
    } else {
      return Optional.of(BoardMapper.boardFromDynamoDB(item));
    }
  }

  public Optional<Board> findWholeBoard(UUID id) {
    var item = boardTable.getItem(
            "PK", "BOARD#" + id);
    if (item == null) {
      return Optional.empty();
    } else {
      var board = BoardMapper.boardFromDynamoDB(item);
      var taskLists = taskListRepository.getTaskLists(id);
      board.setTaskLists(taskLists);
      return Optional.of(board);
    }
  }

  public List<Board> findAllBoardsByIds(Set<UUID> ids) {
    PrimaryKey[] primaryKeys = ids.stream().map(id -> new PrimaryKey("PK", "BOARD#" + id, "SK", "BOARD#" + id)).toArray(PrimaryKey[]::new);
    TableKeysAndAttributes tableKeysAndAttributes = new TableKeysAndAttributes(BOARD_TABLE_NAME).withPrimaryKeys(primaryKeys);
    var items = dynamoDB.batchGetItem(tableKeysAndAttributes);
    List<Board> boards = new ArrayList<>();
    for (String tableName : items.getTableItems().keySet()) {
      for (Item item : items.getTableItems().get(tableName)) {
        boards.add(BoardMapper.boardFromDynamoDB(item));
      }
    }
    return boards;
  }

  public void delete(String boardOwner, UUID boardId) {
    taskListRepository.deleteAllTaskLists(boardId);
    boardTable.deleteItem(
            "PK","BOARD#" + boardOwner,
            "SK", "BOARD#" + boardId);
  }

}
