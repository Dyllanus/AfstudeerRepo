package nl.dyllan.handler.tags;

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
import nl.dyllan.domain.exceptions.UserNotAssignedToBoardException;
import nl.dyllan.handler.tags.dto.CreateTagDto;
import nl.dyllan.handler.tags.dto.DeleteTagDto;

import java.util.UUID;

@Named("deleteTag")
public class DeleteTagHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
  @Inject
  UserRepository userRepository;

  @Inject
  BoardRepository boardRepository;

  ObjectMapper mapper = Utils.mapper;

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
    String owner = apiGatewayProxyRequestEvent.getHeaders().get("username");
    String jsonBody = apiGatewayProxyRequestEvent.getBody();
    var boardId = UUID.fromString(apiGatewayProxyRequestEvent.getPathParameters().get("boardId"));
    DeleteTagDto dto;
    try {
      dto = mapper.readValue(jsonBody, DeleteTagDto.class);
    } catch (JsonProcessingException e) {
      return Utils.createResponse(400, "error processing json body");
    }

    var posUser = userRepository.getUser(owner);
    if (posUser.isEmpty()) return Utils.createResponse(404, "User" + owner + " not found" );

    var posBoard = boardRepository.findById(boardId);
    if (posBoard.isEmpty()) return Utils.createResponse(404, "Board   " + boardId + " not found");
    var board = posBoard.get();

    try {
      board.checkIfInAssignedUsers(owner);
    } catch (UserNotAssignedToBoardException e) {
      return Utils.createResponse(402, "user not assigned to board");
    }
    board.deleteTagByTitle(dto.tagName());
    boardRepository.save(board);
    try {
      return Utils.createResponse(200, mapper.writeValueAsString(board));
    } catch (JsonProcessingException e) {
      return Utils.createResponse(400, "error creating response");
    }
  }
}
