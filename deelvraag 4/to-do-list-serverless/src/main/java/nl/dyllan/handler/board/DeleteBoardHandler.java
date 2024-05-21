package nl.dyllan.handler.board;

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

@Named("DeleteBoard")
public class DeleteBoardHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  @Inject
  BoardRepository boardRepository;

  @Inject
  UserRepository userRepository;


  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
    String owner = apiGatewayProxyRequestEvent.getHeaders().get("username");
    UUID boardId = UUID.fromString(apiGatewayProxyRequestEvent.getPathParameters().get("boardId"));

    var posUser = userRepository.getUser(owner);
    if (posUser.isEmpty()) return Utils.createResponse(404, "User" + owner + " not found");
    var user = posUser.get();
    user.removeOwnerBoard(boardId);

    var posBoard = boardRepository.findById(boardId);
    if (posBoard.isEmpty()) return Utils.createResponse(404, "Board with owner " + owner + " and boardId: "+ boardId +" not found");
    boardRepository.delete(owner, boardId);


    return Utils.createResponse(204, "Board with owner " + owner + " deleted successfully");
  }
}
