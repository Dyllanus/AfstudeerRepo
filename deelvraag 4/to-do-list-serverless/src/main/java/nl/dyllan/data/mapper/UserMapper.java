package nl.dyllan.data.mapper;

import com.amazonaws.services.dynamodbv2.document.Item;
import nl.dyllan.domain.User;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class UserMapper {
  private static final String PK = "PK";
  private static final String SK = "SK";
  private static final String VERSION = "version";
  private static final String LASTMODIFIED = "lastModified";
  private static final String CREATED = "created";
  private static final String ID = "id";
  private static final String NAME = "name";
  private static final String OWNERBOARDS = "ownerboards";
  private static final String INVOLVEDBOARDS = "involvedboards";


  public static User userFromDynamoDB(Item item) {
    return User.builder()
            .id(UUID.fromString(item.getString(ID)))
            .version(item.getLong(VERSION))
            .lastModified(Instant.parse(item.getString(LASTMODIFIED)))
            .created(Instant.parse(item.getString(CREATED)))
            .name(item.getString(NAME))
            .involvedBoards(idSetFromString(item.getString(OWNERBOARDS)))
            .ownerBoard(idSetFromString(item.getString(INVOLVEDBOARDS)))
            .build();
  }

  public static Item userToDynamoDB(User user) {
    return Item.fromMap(
            Map.of(
                    PK, "USER#" + user.getName(),
                    SK, "USER#" + user.getName(),
                    VERSION, user.getVersion(),
                    LASTMODIFIED, user.getLastModified().toString(),
                    CREATED, user.getCreated().toString(),
                    ID, user.getId().toString(),
                    NAME, user.getName(),
                    OWNERBOARDS, idSetToString(user.getOwnerBoard()),
                    INVOLVEDBOARDS, idSetToString(user.getInvolvedBoards())
            )
    );
  }

  private static String idSetToString(Set<UUID> idList) {
    if (idList.isEmpty()) {
      return "";
    }
    return idList.stream().map(UUID::toString).collect(Collectors.joining("&"));
  }

  private static Set<UUID> idSetFromString(String idList) {
    if (idList.isEmpty()) {
      return new HashSet<>();
    }
    return Arrays.stream(idList.split("&")).map(UUID::fromString).collect(Collectors.toSet());
  }
}
