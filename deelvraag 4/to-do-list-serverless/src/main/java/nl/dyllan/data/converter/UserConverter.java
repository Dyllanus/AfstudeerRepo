package nl.dyllan.data.converter;

import java.util.Set;

public class UserConverter {

  public static Set<String> fromDynamoDbToUsers(String users){
    return Set.of(users.split("&"));
  }

  public static String fromUsersToDynamoDb(Set<String> users){
    return String.join("&", users);
  }
}
