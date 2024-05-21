package nl.dyllan.data.mapper;

import com.amazonaws.services.dynamodbv2.document.Item;
import nl.dyllan.data.converter.TagConverter;
import nl.dyllan.data.converter.UserConverter;
import nl.dyllan.domain.Board;
import java.time.Instant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class BoardMapper {
  private static final String PK = "PK";
  private static final String SK = "SK";
  private static final String ID = "id";
  private static final String VERSION = "version";
  private static final String LASTMODIFIED = "lastModified";
  private static final String CREATED = "created";
  private static final String TITLE = "title";
  private static final String DESCRIPTION = "description";
  private static final String OWNER = "owner";
  private static final String USERS = "users";
  private static final String TAGS = "tags";
  private static final String TASKLISTSDSIDS = "tasklistsIds";


  public static Board boardFromDynamoDB (Item items) {
    return Board.builder()
            .id(UUID.fromString(items.getString(ID)))
            .version(items.getLong(VERSION))
            .lastModified(Instant.parse(items.getString(LASTMODIFIED)))
            .created(Instant.parse(items.getString(CREATED)))
            .title(items.getString(TITLE))
            .description(items.getString(DESCRIPTION))
            .owner(items.getString(OWNER))
            .users(UserConverter.fromDynamoDbToUsers(items.getString(USERS)))
            .tags(TagConverter.fromDynamoDbToTags(items.getString(TAGS)))
            .tasklistsIds(Arrays.stream(items.getString(TASKLISTSDSIDS).split("&")).map(UUID::fromString).collect(Collectors.toSet()))
            .build();
  }

  public static Item boardToDynamoDB (Board board) {
    String taskListsIds = board.getTaskLists().stream().map(taskList -> taskList.getId().toString()).collect(Collectors.joining("&"));
    var map = new HashMap<String, Object>(
            Map.of(
            PK, "BOARD#" + board.getId(),
            SK, "BOARD#" + board.getId(),
            ID, board.getId().toString(),
            VERSION, board.getVersion(),
            LASTMODIFIED, board.getLastModified().toString(),
            CREATED, board.getLastModified().toString(),
            TITLE, board.getTitle(),
            DESCRIPTION, board.getDescription(),
            OWNER, board.getOwner(),
            TAGS, TagConverter.fromTagsToDynamoDb(board.getTags())
    ));
    map.put(SK, "BOARD#" + board.getId());
    map.put(TASKLISTSDSIDS, taskListsIds);
    map.put(USERS, UserConverter.fromUsersToDynamoDb(board.getUsers()));
    return Item.fromMap(map);
  }
}
