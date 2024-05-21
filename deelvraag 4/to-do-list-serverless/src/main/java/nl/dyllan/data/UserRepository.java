package nl.dyllan.data;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import nl.dyllan.data.mapper.UserMapper;
import nl.dyllan.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Singleton
public class UserRepository {
  private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
  private final String BOARD_TABLE_NAME = System.getenv("BOARD_TABLE_NAME");
  public final DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.standard().build());
  public final Table boardTable = dynamoDB.getTable(BOARD_TABLE_NAME);
  {
    System.out.println(boardTable.describe().toString());
  }


  public void save(final User user) {
    boardTable.putItem(UserMapper.userToDynamoDB(user));
  }

  public void delete(final String username) {
    boardTable.deleteItem("PK", username);
  }

  public Optional<User> getUser(final String username) {
    var tableResponse = boardTable.getItem("PK", "USER#"+username, "SK", "USER#"+username );
    if (tableResponse == null) {
      return Optional.empty();
    } else {
      return Optional.of(UserMapper.userFromDynamoDB(tableResponse));
    }
  }

}
