package nl.dyllan.handler.tasks;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import nl.dyllan.Utils;
import nl.dyllan.data.BoardRepository;
import nl.dyllan.domain.exceptions.UserNotAssignedToBoardException;

import java.util.UUID;

@Named("deleteTask")
public class DeleteTaskHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  @Inject
  BoardRepository boardRepository;

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
    String username = apiGatewayProxyRequestEvent.getHeaders().get("username");
    UUID taskListId = UUID.fromString(apiGatewayProxyRequestEvent.getPathParameters().get("taskListId"));
    UUID taskId = UUID.fromString(apiGatewayProxyRequestEvent.getPathParameters().get("taskId"));
    UUID boardId = UUID.fromString(apiGatewayProxyRequestEvent.getPathParameters().get("boardId"));

    var optBoard = boardRepository.findWholeBoard(boardId);
    if (optBoard.isEmpty()) return Utils.createResponse(404, "Board not found");
    var board = optBoard.get();

    try {
      board.checkIfInAssignedUsers(username);
    } catch (UserNotAssignedToBoardException e){
      return Utils.createResponse(401, e.getMessage());
    }
    board.getTaskListById(taskListId).deleteTask(taskId);

    return Utils.createResponse(204, "Tasklist deleted");
  }
}
