package nl.dyllan.handler.board;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import nl.dyllan.Utils;
import nl.dyllan.data.BoardRepository;
import nl.dyllan.data.UserRepository;

import java.util.UUID;

@Named("deleteUserFromBoard")
public class DeleteUserFromBoardHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  @Inject
  UserRepository userRepository;

  @Inject
  BoardRepository boardRepository;

  ObjectMapper mapper = Utils.mapper;


  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
    String owner = apiGatewayProxyRequestEvent.getHeaders().get("username");
    UUID boardId = UUID.fromString(apiGatewayProxyRequestEvent.getPathParameters().get("boardId"));
    String assignee = apiGatewayProxyRequestEvent.getPathParameters().get("assignee");

    var posOwner = userRepository.getUser(owner);
    if (posOwner.isEmpty()) return Utils.createResponse(404, "User" + owner + " not found" );

    var posAssignee = userRepository.getUser(assignee);
    if (posAssignee.isEmpty()) return Utils.createResponse(404, "User" + assignee + " not found" );

    var posBoard = boardRepository.findById(boardId);
    if (posBoard.isEmpty()) return Utils.createResponse(404, "Board " + boardId + " not found" );
    var board = posBoard.get();
    if (!board.getOwner().equals(owner)) return Utils.createResponse(401, "Not owner of board");
    board.deleteAssignee(assignee);

    boardRepository.save(board);
    return Utils.createResponse(200, "Added user" +assignee + "to board");
  }
}
