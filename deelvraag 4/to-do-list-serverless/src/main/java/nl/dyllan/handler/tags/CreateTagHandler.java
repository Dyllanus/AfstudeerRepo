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
import nl.dyllan.domain.Tag;
import nl.dyllan.domain.exceptions.UserNotAssignedToBoardException;
import nl.dyllan.handler.tags.dto.CreateTagDto;

import java.awt.*;
import java.util.UUID;

@Named("createTag")
public class CreateTagHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

  @Inject
  BoardRepository boardRepository;

  @Inject
  UserRepository userRepository;

  ObjectMapper mapper = Utils.mapper;

  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
    String owner = apiGatewayProxyRequestEvent.getHeaders().get("username");
    var boardId = UUID.fromString(apiGatewayProxyRequestEvent.getPathParameters().get("boardId"));
    String jsonBody = apiGatewayProxyRequestEvent.getBody();

    CreateTagDto tagDto;
    try {
      tagDto = mapper.readValue(jsonBody, CreateTagDto.class);
    } catch (JsonProcessingException e) {
      return Utils.createResponse(400, "invalid json body");
    }
    var tag = new Tag(tagDto.tagName(), Color.decode(tagDto.hexcode()));

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

    board.addTag(tag);
    boardRepository.save(board);
    try {
      return Utils.createResponse(200, mapper.writeValueAsString(board));
    } catch (JsonProcessingException e) {
      return Utils.createResponse(400, "error creating response body");
    }
  }
}
