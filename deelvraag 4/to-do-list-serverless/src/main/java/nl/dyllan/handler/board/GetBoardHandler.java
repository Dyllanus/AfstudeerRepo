package nl.dyllan.handler.board;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import nl.dyllan.Utils;
import nl.dyllan.data.BoardRepository;
import nl.dyllan.data.UserRepository;

import java.util.UUID;

@Named("getBoard")
public class GetBoardHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  @Inject
  BoardRepository boardRepository;

  @Inject
  UserRepository userRepository;

  ObjectMapper mapper = Utils.mapper;

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
    String owner = apiGatewayProxyRequestEvent.getHeaders().get("username");
    var posUser = userRepository.getUser(owner);
    if (posUser.isEmpty()) return Utils.createResponse(404, "User" + owner + " not found" );

    UUID boardId = UUID.fromString(apiGatewayProxyRequestEvent.getPathParameters().get("boardId"));
    var posBoard = boardRepository.findWholeBoard(boardId);
    if (posBoard.isEmpty()) return Utils.createResponse(404, "Board " + boardId + " not found");

    try {
      return Utils.createResponse(200, mapper.writeValueAsString(posBoard));
    } catch (JsonProcessingException e) {
      return Utils.createResponse(400, "Something went wrong");
    }
  }
}
