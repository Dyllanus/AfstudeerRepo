package nl.dyllan.data.mapper;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.dyllan.Utils;
import nl.dyllan.domain.Task;
import nl.dyllan.domain.TaskList;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TaskListMapper {

  private static final String PK = "PK";
  private static final String SK = "SK";
  private static final String VERSION = "version";
  private static final String LASTMODIFIED = "lastModified";
  private static final String CREATED = "created";
  private static final String BOARDID = "boardId";
  private static final String ID = "id";
  private static final String TITLE = "title";
  private static final String DESCRIPTION = "description";
  private static final String TASKS = "tasks";

  // Tasks are also saved in the TaskList itself. A Task object will be saved as an JSON object.

  private static final ObjectMapper mapper = Utils.mapper;


  public static TaskList taskListFromDynamoDB(Item item){
    try {
      return TaskList.builder()
              .id(UUID.fromString(item.getString(ID)))
              .version(item.getLong(VERSION))
              .lastModified(Instant.parse(item.getString(LASTMODIFIED)))
              .created(Instant.parse(item.getString(CREATED)))
              .boardId(UUID.fromString(item.getString(BOARDID)))
              .title(item.getString(TITLE))
              .description(item.getString(DESCRIPTION))
              .tasks(mapper.readValue(item.getString(TASKS), mapper.getTypeFactory().constructCollectionType(Set.class, Task.class)))
              .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static Item taskListToDynamoDB(TaskList taskList){
    try {
      return Item.fromMap(
        Map.of(
                PK, "BOARD#"+ taskList.getBoardId(),
                SK, "TASKLIST#"+ taskList.getId(),
                ID, taskList.getId().toString(),
                VERSION, taskList.getVersion(),
                LASTMODIFIED, taskList.getLastModified().toString(),
                CREATED, taskList.getLastModified().toString(),
                BOARDID, taskList.getBoardId().toString(),
                TITLE, taskList.getTitle(),
                DESCRIPTION, taskList.getDescription(),
                TASKS, mapper.writeValueAsString(taskList.getTasks())
        )
      );
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
