package nl.dyllan.data;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import nl.dyllan.data.mapper.TaskListMapper;
import nl.dyllan.domain.TaskList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class TaskListRepository {
  private static final Logger logger = LoggerFactory.getLogger(TaskListRepository.class);
  public final String BOARD_TABLE_NAME = System.getenv("BOARD_TABLE_NAME");
  public final DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.standard().build());
  public final Table boardTable = dynamoDB.getTable(BOARD_TABLE_NAME);
  {
    System.out.println(boardTable.describe().toString());
  }


  public void saveTaskLists(Set<TaskList> taskLists) {
    dynamoDB.batchWriteItem(new TableWriteItems(BOARD_TABLE_NAME)
            .withItemsToPut(taskLists.stream()
                    .map(TaskListMapper::taskListToDynamoDB)
                    .toList()));
  }

  public void saveTaskList (TaskList taskList) {
    boardTable.putItem(TaskListMapper.taskListToDynamoDB(taskList));
  }

  public Set<TaskList> getTaskLists(UUID boardId) {
    var queryRes = boardTable.query("PK", "BOARD#" + boardId.toString());
    var listItems = queryRes.getLastLowLevelResult().getItems();
    return listItems.stream().map(TaskListMapper::taskListFromDynamoDB).collect(Collectors.toSet());
  }


  public void deleteTaskList(UUID boardId, UUID taskListId) {
    boardTable.deleteItem("PK", "BOARD#" + boardId.toString(),
            "SK", "TASKLIST#" + taskListId);
  }

  public void deleteAllTaskLists(UUID boardId) {
    dynamoDB.batchWriteItem(new TableWriteItems(BOARD_TABLE_NAME)
            .addPrimaryKeyToDelete(new PrimaryKey("PK", "BOARD#" + boardId.toString())));
  }

}
