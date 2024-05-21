package nl.dyllan.handler.tasklist;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import nl.dyllan.Utils;
import nl.dyllan.data.BoardRepository;
import nl.dyllan.data.UserRepository;

import java.util.UUID;


@Named("deleteTaskList")
public class DeleteTaskListHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  @Inject
  BoardRepository boardRepository;

  @Inject
  UserRepository userRepository;

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
    String boardId = apiGatewayProxyRequestEvent.getPathParameters().get("boardId");
    String taskListId = apiGatewayProxyRequestEvent.getPathParameters().get("taskListId");

    String owner = apiGatewayProxyRequestEvent.getHeaders().get("username");
    var posUser = userRepository.getUser(owner);
    if (posUser.isEmpty()) return Utils.createResponse(404, "User" + owner + " not found" );

    var posBoard = boardRepository.findWholeBoard(UUID.fromString(boardId));
    if (posBoard.isEmpty()) return Utils.createResponse(404, "Board " + boardId + " not found");
    var board = posBoard.get();

    board.deleteTaskList(UUID.fromString(taskListId));
    boardRepository.save(board);

    return Utils.createResponse(204, "Board deleted");
  }
}
